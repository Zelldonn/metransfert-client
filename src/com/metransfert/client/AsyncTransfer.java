package com.metransfert.client;

import com.metransfert.client.transaction.TransferListener;
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
	protected int expectedBytes = 0;
	protected int transferredBytes = 0;
	
	protected AsyncTransfer(PacketInputStream pis, PacketOutputStream pos){
		in = pis;
		out = pos;
	}
	
	public int expectedBytes(){
		return this.expectedBytes;
	}
	
	public int transferredBytes(){
		return this.transferredBytes;
	}
	
	public float throughput(){
		return this.throughput;
	}
	
	public boolean isFinished(){
		return this.finished;
	}
}
