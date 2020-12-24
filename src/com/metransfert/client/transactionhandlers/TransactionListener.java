package com.metransfert.client.transactionhandlers;

public interface TransactionListener {
    void onTransactionStart();
    void onTransactionFinish(TransactionResult result);
}
