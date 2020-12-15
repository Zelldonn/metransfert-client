package com.metransfert.client.gui;

import com.metransfert.client.transaction.TransferListener;

import javax.swing.*;
import java.nio.file.Path;

public class DownloadPanel extends JPanel{

    private JProgressBar progressBar;

    private JLabel downloadResultLabel;

    private final Path fileLocationPath;

    private long throughput, lastNano;

    private JLabel fileInfoLabel;

    public DownloadPanel(Path fileLocationPath){
        this.fileLocationPath = fileLocationPath;
        initComponent();
    }

    public void initComponent(){
        downloadResultLabel = new JLabel();
        progressBar = new JProgressBar();
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);

        lastNano = System.nanoTime();
        throughput = 0L;

        fileInfoLabel =  new JLabel("");

        this.add(fileInfoLabel);
        fileInfoLabel.setVisible(false);
        this.add(progressBar);
        this.add(downloadResultLabel);
    }

    public void update(TransferListener.Info info){
        int percentage = (int)(((double)info.transferredBytes/(double)info.expectedBytes)*100);
        progressBar.setValue(percentage);

        throughput = getThroughput(info.transferredBytes, info.oldTransferredBytes);
        String throughputString = GuiUtils.byte2Readable(throughput) + "/s";

        progressBar.setString("Downloading... " +percentage+" %"+ "("+throughputString+")");
    }

    private long getThroughput(int currentBytes, int lastBytes){
        long currentNano = System.nanoTime();
        long dB = (long)currentBytes - (long)lastBytes;
        long dt = currentNano - lastNano;
        long dt_s = dt/1_000_000_000L;
        if(dB != 0 && dt_s != 0)
            throughput = dB/dt_s;

        lastNano = currentNano;

        return throughput;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getDownloadResultLabel() {
        return downloadResultLabel;
    }

    public Path getFileLocationPath(){
        return fileLocationPath;
    }

    public JLabel getFileInfoLabel(){
        return fileInfoLabel;
    }
}
