package com.metransfert.client.transactionhandlers;

public interface RequestListener extends TransactionListener{
    void onTransactionFinish(int fileNumber, long expectedBytes);
}
