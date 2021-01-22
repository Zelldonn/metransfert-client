package com.metransfert.client.transactionhandlers;

public interface TransferListener extends TransactionListener{
    void onTransferUpdate(TransferInfo info);
}
