package com.metransfert.client.controller;

import com.metransfert.client.controller.exception.ConnectionFailedException;
import com.metransfert.client.controller.transaction.Ping;
import com.metransfert.client.controller.transaction.Upload;
import com.metransfert.client.gui.GUI;
import com.metransfert.client.gui.GUIListener;
import com.metransfert.client.gui.MyFileChooser;
import com.metransfert.client.gui.Status;
import com.metransfert.client.gui.upload.UploadPanel;
import com.metransfert.client.gui.upload.UploadPanelListener;
import com.metransfert.client.gui.upload.UploadTabListener;
import com.metransfert.client.transactionhandlers.PingListener;
import com.metransfert.client.transactionhandlers.TransactionResult;
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.transactionhandlers.UploadInfoResult;
import com.metransfert.client.utils.GuiUtils;
import com.metransfert.client.utils.PathUtils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientController {

    private static ClientController instance;

    private GUI gui;

    private Address address;

    private ArrayList<Address> addresses = new ArrayList<>();

    private Path uploadPath, downloadPath;

    private File[] fileList;

    public ClientController(GUI g, Address a, Path uploadPath, Path downloadPath) {
        if(instance == null)
            instance = this;
        else throw new RuntimeException("There cannot be more than one instance of MeTransfer client controller");
        this.address = a;
        gui = g;
        this.uploadPath = uploadPath;
        this.downloadPath = downloadPath;
        this.fileList = null;
        ping();
    }

    private void ping(){
        Ping ping = new Ping();
        ping.addPingListeners(status -> gui.setConnectionStatusLabel(status, address.getIp(), Integer.toString(address.getPort())));
        Channel testConnection = new Channel(address);
        try {
            testConnection.startTransaction(ping);
        } catch (ConnectionFailedException e) {
            e.printStackTrace();
        }
    }

    public void populateDefaultAddresses(){
        Address s1 = new Address("MeTransfer official server", "metransfer.ddns.net", 7999);
        Address s2 = new Address("Localhost", "localhost", 7999);
        addresses.add(s1);
        addresses.add(s2);
    }
    public void addAddressesToGUI(Address address){
        gui.getSelectServerMenu().add(new JMenuItem(address.name));
    }

    public void initMainChannel(){
    }

    public void initGUI(){
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.pack();
        initUploadTab();
        gui.addGuiListener(new GUIListener() {
            @Override
            public void onRefreshButtonClicked(String ip, String port) {
                try{
                    int p = Integer.parseInt(port);
                    address.setIp(ip);
                    address.setPort(p);
                    ping();

                }catch (NumberFormatException e){
                    //TODO : write error in guilabel  ?
                }
            }
        });
    }

    public void initUploadTab(){
        gui.getUploadTab().setPathTextField(uploadPath.toString());
        gui.getUploadTab().addUploadTabListener(new UploadTabListener() {
            @Override
            public void onUploadButtonClicked() {
                if(fileList != null){
                    Upload up = new Upload(fileList);
                    UploadPanel p = new UploadPanel();
                    p.addUploadPanelListener(new UploadPanelListener() {
                        @Override
                        public void onClosedButtonClicked() {
                            gui.getUploadTab().remove(p);
                            gui.getUploadTab().refreshPanel();
                        }

                        @Override
                        public void onPauseButtonClicked() {
                            //TODO : add a state in the panel and find a way to integrate it with the protocol
                            up.changeRunningState();
                            p.changeRunningStateIcon(up.getRunningState());
                        }

                        @Override
                        public void onStopButtonClicked() {

                        }
                    });
                    gui.getUploadTab().add(p);
                    up.addTransferListeners(new TransferListener() {
                        @Override
                        public void onTransferUpdate(Info info) {
                            float percentage = ((float)info.transferredBytes/(float)info.expectedBytes) * 100F;
                            p.setProgressBarValue((int)percentage);
                            gui.getUploadTab().refreshPanel();
                        }

                        @Override
                        public void onTransactionStart() {
                        }

                        @Override
                        public void onTransactionFinish(String id) {
                            p.setUploadIDTextField(id);
                            p.getUploadIDTextField().setVisible(true);
                            p.getClipboardButton().setVisible(true);
                            p.getProgressBar().setVisible(false);
                            p.getPauseButton().setVisible(false);
                            p.getStopButton().setVisible(false);
                            gui.getUploadTab().refreshPanel();
                        }
                    });
                    Channel c = new Channel(address);
                    try {
                        c.startTransaction(up);
                    } catch (ConnectionFailedException e) {
                        e.printStackTrace();
                    }
                    fileList = null;
                    updateUploadTab();
                }
            }

            @Override
            public void openPathChooserButtonClicked() {
                MyFileChooser mfc = new MyFileChooser();
                boolean result = mfc.openFileChooser(uploadPath, gui.getUploadTab(), JFileChooser.FILES_AND_DIRECTORIES);
                if (result) {
                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            fileList = mfc.getSelectedFiles();

                            if(fileList.length == 1 && fileList[0].isDirectory()){
                                fileList = fileList[0].listFiles(PathUtils.withAtLeastOneFile);
                            }
                            updateUploadTab();
                        }
                    });
                    t.start();
                    uploadPath = mfc.getDirectorySelected().toPath();
                }else
                    fileList = null;

                updateUploadTab();
            }

            @Override
            public void onFilesDragged(File[] fileDraggedList) {
                fileList= fileDraggedList;
                updateUploadTab();
            }
        });
    }

    public void updateGUI(){
        for(Address a : addresses){
            addAddressesToGUI(a);
        }
        gui.setIpLabel(address.getIp());
        gui.setPortLabel(Integer.toString(address.getPort()));
        gui.getUploadTab().refreshPanel();
    }

    public void updateUploadTab(){
        gui.getUploadTab().setPathTextField(uploadPath.toString());
        if(fileList != null){
            gui.getUploadTab().setFileInfoLabel(GuiUtils.displayFileInfo(fileList));
            gui.getUploadTab().getFileInfoLabel().setVisible(true);
        }else{
            gui.getUploadTab().getFileInfoLabel().setVisible(false);
        }
    }


    public static ClientController getInstance() {
        return instance;
    }
}
