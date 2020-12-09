package com.metransfert.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import com.metransfert.client.transaction.TransferListener;
import com.metransfert.client.transaction.UploadInfoResult;
import com.metransfert.common.PacketTypes;
import com.packeteer.network.*;

public class AsyncUpload extends AsyncTransfer {
	
	private final int BLOCK_SIZE = 10*1024;
	
	private Path sourceFile;
	
	public AsyncUpload(PacketInputStream pis, PacketOutputStream pos, Path sourceFile) {
		super(pis, pos);
		
		if(sourceFile == null)	throw new NullPointerException("argument Path cannot be null");
		
		this.sourceFile = sourceFile;
	}

	//TODO : callback for catched exception, probably put it in parent class AsyncTransfert ?
	
	@Override
	public void run(){

		FileInputStream fis;

		int oldTransferredBytes = 0;

		try{
			for(TransferListener listener : transferListeners){
				listener.onTransactionStart();
			}

			File file = sourceFile.toFile();
			fis = new FileInputStream(file);

			int totalLen = (int) file.length();
			this.expectedBytes = totalLen;

			String fileName = file.getName();
			PacketHeader fileHeader = new PacketHeader( totalLen + PacketUtils.calculateNetworkStringLength(fileName) , 
					PacketTypes.FILEUPLOAD);
			out.writeAndFlush(fileHeader);
			//send file name
			out.writeAndFlush(fileName);
			
			//send file data
			byte[] data = new byte[BLOCK_SIZE];
			int count;
	        while ((count = fis.read(data)) > 0) {
	            out.write(data, 0, count);
	            out.flush();

				this.transferredBytes += count;

				if(transferredBytes != oldTransferredBytes){
					for (TransferListener listener : transferListeners){
						listener.onTransferUpdate(new TransferListener.Info(expectedBytes, transferredBytes, oldTransferredBytes));
					}
				}
				oldTransferredBytes = transferredBytes;
	        }
	        this.finished = true;
	        fis.close(); //TODO close file stream in "finally" clause

			Packet answer = in.readPacket();
			for(TransferListener listener : transferListeners){
				listener.onTransactionFinish(new UploadInfoResult(answer));
			}
		}
		catch(IOException e){
			//throw e;
			e.printStackTrace();
		}

	}
	
}
