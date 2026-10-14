package com.aegis.bridge;

public final class HexUtil {

    private HexUtil() {}

    public static boolean isAddress(String value) {
        return value != null && value.matches("^0x[0-9a-fA-F]{40}$");
    }

    public static boolean isBytes32(String value) {
        return value != null && value.matches("^0x[0-9a-fA-F]{64}$");
    }

    public static boolean isTxHash(String value) {
        return isBytes32(value);
    }

    public static byte[] bytes32(String value) {
        if (!isBytes32(value)) {
            throw new IllegalArgumentException(
                "Expected 0x-prefixed 32-byte hex value"
            );
        }
        String hex = value.substring(2);
        byte[] output = new byte[32];
        for (int i = 0; i < 32; i++) {
            int index = i * 2;
            output[i] = (byte) Integer.parseInt(
                hex.substring(index, index + 2), 16
            );
        }
        return output;
    }

    public static String hex(byte[] value) {
        StringBuilder out = new StringBuilder("0x");
        for (byte b : value) {
            out.append(String.format("%02x", b & 0xff));
        }
        return out.toString();
    }
}
