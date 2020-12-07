package com.metransfert.client.transaction;

public interface TransactionListener {
    void onTransactionStart();
    void onTransactionFinish(TransactionResult result);


}
