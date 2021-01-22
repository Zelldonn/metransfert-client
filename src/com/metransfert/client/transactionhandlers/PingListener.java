package com.metransfert.client.transactionhandlers;

import com.metransfert.client.gui.common.Status;

public interface PingListener {
    void onStatusChanged(Status status);
}
