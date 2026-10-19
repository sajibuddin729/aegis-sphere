package com.aegis.bridge;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PersistentIntentReplayGuard {

    private final Path ledgerPath;
    private final Set<String> executedIntents = Collections.synchronizedSet(new HashSet<>());
    private final Object writeLock = new Object();

    public PersistentIntentReplayGuard(Path ledgerPath) {
        this.ledgerPath = ledgerPath;
        initLedger();
    }

    private void initLedger() {
        try {
            if (ledgerPath.getParent() != null) {
                Files.createDirectories(ledgerPath.getParent());
            }
            if (Files.exists(ledgerPath)) {
                List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
                for (String line : lines) {
                    String trimmed = line.trim().toLowerCase();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                        executedIntents.add(trimmed);
                    }
                }
            } else {
                Files.createFile(ledgerPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize PersistentIntentReplayGuard ledger: " + ledgerPath, e);
        }
    }

    public void checkBeforeExecution(String intentId) {
        if (intentId == null || intentId.isBlank()) {
            throw new IllegalArgumentException("intentId must not be null or blank");
        }
        String normalized = intentId.trim().toLowerCase();
        synchronized (writeLock) {
            if (executedIntents.contains(normalized)) {
                throw new IllegalStateException("AEGIS: INTENT_ALREADY_EXECUTED (" + intentId + ")");
            }
        }
    }

    public void recordConsumed(String intentId) {
        if (intentId == null || intentId.isBlank()) {
            return;
        }
        String normalized = intentId.trim().toLowerCase();
        synchronized (writeLock) {
            if (!executedIntents.contains(normalized)) {
                executedIntents.add(normalized);
                try (FileOutputStream fos = new FileOutputStream(ledgerPath.toFile(), true);
                     OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                     BufferedWriter writer = new BufferedWriter(osw)) {
                    writer.write(normalized);
                    writer.newLine();
                    writer.flush();
                    fos.getFD().sync(); // Ensure fsync to disk
                } catch (IOException e) {
                    throw new RuntimeException("Failed to persist consumed intentId to ledger: " + intentId, e);
                }
            }
        }
    }

    public boolean isConsumed(String intentId) {
        if (intentId == null) {
            return false;
        }
        return executedIntents.contains(intentId.trim().toLowerCase());
    }

    public int size() {
        return executedIntents.size();
    }
}
