import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import com.metransfert.common.MeTransfertPacketTypes;
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
		//Va
		FileInputStream fis = null;
		int oldTransferredBytes = 0;
		
		try{
			File file = sourceFile.toFile();
			int totalLen = (int) file.length();
			fis = new FileInputStream(file);
			
			this.expectedBytes = totalLen;
			String fileName = file.getName();
			PacketHeader fileHeader = new PacketHeader( totalLen + PacketUtils.calculateNetworkStringLength(fileName) , 
					MeTransfertPacketTypes.FILE);
			out.writeAndFlush(fileHeader);
			//send file name
			out.writeAndFlush(fileName);
			
			//send file data
			byte[] data = new byte[BLOCK_SIZE];
			int count;
	        while ((count = fis.read(data)) > 0) {
	            out.write(data, 0, count);
	            out.flush();

	            //TODO : fix "transferedBytes" being negative (while count is never)

				this.transferredBytes += count;

				if(transferredBytes != oldTransferredBytes){
					for (TransferListener listener : transferListeners) {
						listener.onTransferUpdate(new TransferListener.Info(expectedBytes, transferredBytes, throughput));
					}
				}
				oldTransferredBytes = transferredBytes;
	        }
	        this.finished = true;
	        fis.close(); //TODO close file stream in "finally" clause

			Packet answer = in.readPacket();
			for(TransferListener listener : transferListeners){
				listener.onTransactionFinish(new TransactionListener.TransactionResult(answer));
			}
		}
		catch(IOException e){
			//throw e;
			e.printStackTrace();
		}

	}
	
}
