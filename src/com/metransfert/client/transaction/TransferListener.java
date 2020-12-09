package com.metransfert.client.transaction;

public interface TransferListener extends TransactionListener{

    void onTransferUpdate(Info info);
    class Info{
        public final int expectedBytes, transferredBytes, oldTransferredBytes;

        public Info(int expectedBytes, int transferredBytes, int oldTransferredBytes) {
            this.expectedBytes = expectedBytes;
            this.transferredBytes = transferredBytes;
            this.oldTransferredBytes = oldTransferredBytes;
        }
    }
}
