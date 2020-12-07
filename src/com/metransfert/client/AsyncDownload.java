package com.metransfert.client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.metransfert.common.MeTransfertPacketTypes;
import com.packeteer.network.PacketHeader;
import com.packeteer.network.PacketInputStream;
import com.packeteer.network.PacketOutputStream;
import com.packeteer.network.PacketUtils;

public class AsyncDownload extends AsyncTransfer {
	
	private final int BLOCK_SIZE = 10*1024;
	
	private Path downloadedFile = null;
	private String filename = null;
	
	/**
	 * 
	 * @param in
	 * @param out
	 * @param pathToFile A path representing the file that will be downloaded. If the file does not exist, it will be created
	 * @throws IOException 
	 */
	public AsyncDownload(PacketInputStream in, PacketOutputStream out, Path pathToFile) throws IOException{
		super(in, out);
	
		this.downloadedFile = pathToFile; 
		if(Files.exists(downloadedFile) == false)
			Files.createFile(downloadedFile);
		
	}
	
	@Override
	public void run() {
		FileOutputStream fos = null;
		try{
			PacketHeader header = in.readHeader();
			
			if(header.type != MeTransfertPacketTypes.FILEUPLOAD)
				throw new RuntimeException("invalid packet type read by Async downloader");
			
			this.filename = in.readString();
			fos = new FileOutputStream(downloadedFile.toFile());
			int fileLen = header.payloadLength - PacketUtils.calculateNetworkStringLength(filename);
			this.expectedBytes = fileLen;
			byte[] buffer = new byte[BLOCK_SIZE];
			int count=0;
			while( (count = in.read(buffer)) > 0 ){
				fos.write(buffer, 0, count);				
				this.transferredBytes += count;
			}
			this.finished = true;
			
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getFilename(){
		return this.filename;
	}

}