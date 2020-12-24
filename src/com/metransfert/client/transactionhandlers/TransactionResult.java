package com.metransfert.client.transactionhandlers;

import com.packeteer.network.Packet;

public class TransactionResult {
    public final Packet packet;

    public TransactionResult(Packet p) {
        this.packet = p;
    }
}
