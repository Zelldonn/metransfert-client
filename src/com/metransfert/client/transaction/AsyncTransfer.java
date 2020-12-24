package com.metransfert.client.transaction;

import com.metransfert.client.transactionhandlers.TransferListener;
import com.packeteer.network.PacketInputStream;
import com.packeteer.network.PacketOutputStream;

import java.util.ArrayList;

public class AsyncTransfer extends Thread {

	public ArrayList<TransferListener> transferListeners = new ArrayList<>();

	public void addTransferListeners(TransferListener newListener){
		transferListeners.add(newListener);
	}

	protected PacketInputStream in;
	protected PacketOutputStream out;
	protected boolean finished = false;
		
	protected float throughput = 0.0f;
	protected long expectedBytes = 0;
	protected long transferredBytes = 0;
	
	protected AsyncTransfer(PacketInputStream pis, PacketOutputStream pos){
		in = pis;
		out = pos;
	}
	
	public long expectedBytes(){
		return this.expectedBytes;
	}
	
	public long transferredBytes(){
		return this.transferredBytes;
	}
	
	public float throughput(){
		return this.throughput;
	}
	
	public boolean isFinished(){
		return this.finished;
	}


}
