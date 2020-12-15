package com.metransfert.client.transaction;

import com.metransfert.client.Status;

public interface PingListener {
    void onStatusChanged(Status status);
}
