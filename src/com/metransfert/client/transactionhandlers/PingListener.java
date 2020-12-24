package com.metransfert.client.transactionhandlers;

import com.metransfert.client.Status;

public interface PingListener {
    void onStatusChanged(Status status);
}
