package com.metransfert.client.transaction;

public interface TransferListener extends TransactionListener{

    void onTransferUpdate(Info info);
    class Info{
        public final int expectedBytes;
        public final int transferredBytes;

        public Info(int expectedBytes, int transferredBytes) {
            this.expectedBytes = expectedBytes;
            this.transferredBytes = transferredBytes;
        }
    }
}
