package com.metransfert.client.transactionhandlers;

public class TransferInfo {
    public final long expectedBytes, transferredBytes, oldTransferredBytes;

    public TransferInfo(long expectedBytes, long transferredBytes, long oldTransferredBytes) {
        this.expectedBytes = expectedBytes;
        this.transferredBytes = transferredBytes;
        this.oldTransferredBytes = oldTransferredBytes;
    }
}
