package com.metransfert.client.transactionhandlers;

public interface TransferListener extends TransactionListener{

    void onTransferUpdate(Info info);
    class Info{
        public final long expectedBytes, transferredBytes, oldTransferredBytes;

        public Info(long expectedBytes, long transferredBytes, long oldTransferredBytes) {
            this.expectedBytes = expectedBytes;
            this.transferredBytes = transferredBytes;
            this.oldTransferredBytes = oldTransferredBytes;
        }
    }
}
