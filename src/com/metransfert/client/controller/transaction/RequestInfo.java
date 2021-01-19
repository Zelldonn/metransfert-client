package com.metransfert.client.controller.transaction;

import com.metransfert.client.transactionhandlers.RequestListener;
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.utils.TransactionType;

import java.io.IOException;
import java.util.ArrayList;

public class RequestInfo extends Transaction{

    public ArrayList<RequestListener> requestListeners = new ArrayList<>();

    public void addRequestListeners(RequestListener newListener){
        requestListeners.add(newListener);
    }

    private String id;

    protected byte[] data;

    protected long expectedBytes;

    public long getExpectedBytes() {
        return expectedBytes;
    }

    public int getNumberOfFile() {
        return numberOfFile;
    }

    protected int numberOfFile;

    public RequestInfo(String id){
        this.id = id;
    }

    @Override
    public void run() {
        try {
            bos.write(TransactionType.FILE_INFO);
            bos.flush();

            data = id.getBytes();

            dos.writeLong(data.length);
            dos.flush();

            bos.write(data);
            bos.flush();

            byte answer = dis.readByte();

            if(answer == TransactionType.INFO_OK){
                numberOfFile = dis.readInt();
                expectedBytes = dis.readLong();
            }else if(answer == TransactionType.INFO_ERR){
                expectedBytes = 0L;
                numberOfFile = 0;
            }

            for(RequestListener r : requestListeners){
                r.onTransactionFinish(numberOfFile, expectedBytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
