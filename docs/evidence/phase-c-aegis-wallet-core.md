# Phase C — Aegis On-Chain Wallet Core

Status: PASSED

## Network
- Blockchain: FISCO BCOS
- Version: 3.17.1
- Group: group0
- Consensus: PBFT
- Nodes: 4

## Contracts

### AegisToken
- Address: `0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085`
- Deployment TxHash: `0xf1b468f50a3337e02afb800e5f487186aaca7406f1f20975c1b5323b19c64213`
- Source SHA-256: `8cc40173400e7fc651f8fec922aa57f237ffac3a829ebd3e47a4370a68bff6c9`

### PoIRegistry
- Address: `0xd24180cc0fef2f3e545de4f9aafc09345cd08903`
- Deployment TxHash: `0xcb363284d72f062427ff7985c70a0e1b35ec5f9d655da2a8e00128b097d57cc6`
- Source SHA-256: `38eaf1b9181c463f0374e3943637b680f3de927600b87cf641fbda8405e321a8`

### RiskCircuitBreaker
- Address: `0x37a44585bf1e9618fdb4c62c4c96189a07dd4b48`
- Deployment TxHash: `0x47f8953a479ec6f39ebc7ea5655a9dbf09b8ea671dc360931a92dcad12ed2567`
- Source SHA-256: `9420c2be2733e75663a623278c92487f9caef2a5957fec848a416e66a9dcd655`

### AgentWallet
- Address: `0x31ed5233b81c79d5adddeef991f531a9bbc2ad01`
- Deployment TxHash: `0xace42d97e1d84f462d67c0916ff79c52c008f6df486ccbf74bf075ed151a2bed`
- Source SHA-256: `dfc9bdf74480c69fe1952fecfa013d246e707682225ffc55fe0275ba16451fac`

## Financial Configuration
- Token: AegisCNY (ACNY)
- Decimals: 2
- Maximum transaction: 500.00 ACNY (50,000 units)
- Maximum daily exposure: 2,000.00 ACNY (200,000 units)
- Maximum risk: 25% (2,500 BPS)

## Wallet Funding
- Initial Wallet Balance: 100,000.00 ACNY (10,000,000 units)
- Mint TxHash: `0x8f50edfa7f153e0d52cb74c2cb88445f7a431c772e366937ac159423d09e6108`

## First Aegis Wallet Transaction
- Amount: 200.00 ACNY (20,000 units)
- Risk Score: 12% (1,200 BPS)
- Recipient: `0x1000000000000000000000000000000000000001`
- PoI Hash: `0xef58da3bb7e0b0099b7b82b70ef835078a45e6ab314b4f582afd614bc59b4de8`
- Intent ID: `0x4289244d22763a9577761d242c5362457f5984ec49b1c08d34e51408d0fa4029`
- Policy Hash: `0x6ca710f8fe94a676b0694e78abe67eac82e34cee08ed0d6c88f133db6b54fed2`
- Transaction TxHash: `0x11f55a16da41a07819aeb01e8425e85bd1e832e301fd3e91427d5d269bd8a800`
- Status: SUCCESS (status = 0)
- Block Number: 11
- Atomic Events: `TransactionRiskApproved`, `PoIRecorded`, `Transfer`, `AutonomousTransferExecuted`

## Post-Transaction State
- Wallet Balance: 99,800.00 ACNY (9,980,000 units)
- Recipient Balance: 200.00 ACNY (20,000 units)
- Daily Spent: 200.00 ACNY (20,000 units)
- PoI Registered: TRUE

## Negative Security Tests
- Duplicate PoI: REJECTED (`AEGIS: POI_ALREADY_EXISTS`, status 16)
- High Risk (30% > 25% max): REJECTED (`AEGIS: RISK_TOO_HIGH`, status 16)
- Transaction Limit Exceeded (600.00 > 500.00 max): REJECTED (`AEGIS: TX_LIMIT_EXCEEDED`, status 16)
- Blocked Recipient: REJECTED (`AEGIS: RECIPIENT_BLOCKED`, status 16)
- Emergency Pause: REJECTED (`AEGIS: SYSTEM_PAUSED`, status 16)

## Distributed Verification
- All four PBFT nodes synchronized (Block 20)
- Raw mTLS RPC transaction receipt verified
- PoI persisted on-chain with cryptographic proof
- Financial balances updated atomically

Result: PASS
