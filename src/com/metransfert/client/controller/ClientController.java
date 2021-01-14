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
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.utils.Gui;
import com.metransfert.client.utils.Path;

import javax.swing.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class ClientController {

    private static ClientController instance;

    private final GUI gui;

    private Address address;

    private ArrayList<Address> addresses = new ArrayList<>();

    private java.nio.file.Path uploadPath, downloadPath;

    private File[] fileList;

    public ClientController(GUI g, ClientConfiguration config, String[] args) {
        if(instance == null)
            instance = this;
        else throw new RuntimeException("There cannot be more than one instance of MeTransfer client controller");

        this.address = config.getAddress();

        this.uploadPath = config.getUploadPath();
        this.downloadPath = config.getDownloadPath();

        if(args.length >= 1){
            //This is from a right-click -> upload
            fileList = new File[args.length];
            for(int i = 0 ; i < args.length; i++){
                //Populating fileList
                fileList[i] = new File(args[i]);
                System.out.println(fileList[i]);
            }
        }else{
            this.fileList = null;
        }

        gui = g;
        gui.setIpLabel(address.getIp());
        gui.setPortLabel(Integer.toString(address.getPort()));
        gui.setTheme(config.getTheme());

        updateUploadTabInfo();
        ping();
    }

    /**
     * Create and start a ping transaction using a channel. This will result by displaying connection's infos on the GUI's menu bar
     */
    private void ping(){
        Ping ping = new Ping();

        //Forcing GUI to display "DISCONNECTED" as no ping will be sent if the socket could not be connected
        gui.setConnectionStatusLabel(Status.TRYING, address.getIp(), Integer.toString(address.getPort()));

        //Waiting to be triggered by a ping listeners to update and display status on the GUI (this will look like this for example : (CONNECTED : localhost:7999)
        ping.addPingListeners(status -> gui.setConnectionStatusLabel(status, address.getIp(), Integer.toString(address.getPort())));

        Channel c = new Channel(address);
        Status s = null;
        try {
            s = c.startTransaction(ping);
        } catch (ConnectionFailedException e) {
            e.printStackTrace();
        }
        if(s == Status.DISCONNECTED)
            gui.setConnectionStatusLabel(s, address.getIp(), Integer.toString(address.getPort()));
    }

    /**
     * This is useless for the moment.
     */
    public void populateDefaultAddresses(){
        Address s1 = new Address("MeTransfer official server", "metransfer.ddns.net", 7999);
        Address s2 = new Address("Localhost", "localhost", 7999);
        addresses.add(s1);
        addresses.add(s2);
    }
    public void addAddressesToGUI(Address address){
        gui.getSelectServerMenu().add(new JMenuItem(address.name));
    }

    /**
     * Init main behavior of GUI by adding listeners for GUI's buttons and setting up basic swing parameters
     */
    public void initGUI(){
        //Swing Parameters
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.pack();
        //Listeners
        gui.addGuiListener(new GUIListener() {
            @Override
            public void onRefreshButtonClicked(String ip, String port) {

                int p;
                try{
                    p = Integer.parseInt(port);
                    if(p < 1 || p > 65565){
                        gui.setPortLabel("");
                    }else{
                        address.setPort(p);
                        address.setIp(ip);
                        ping();
                    }
                }catch (NumberFormatException e){
                    gui.setPortLabel("");
                }
            }

            @Override
            public void onGuiClosed() {
                java.nio.file.Path userDir = Paths.get(System.getProperty("user.dir"));
                try {
                    saveConfig(userDir.resolve("client.properties"));
                } catch (FileNotFoundException e) {
                    System.err.println("Could not load save config");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Init main behavior of the UploadTab panel by adding listeners for it's buttons and provides informations about the default upload path
     */
    public void initUploadTab(){
        //Providing upload path to the GUI
        gui.getUploadTab().setPathTextField(uploadPath.toString());

        //Listeners
        gui.getUploadTab().addUploadTabListener(new UploadTabListener() {
            @Override
            public void onUploadButtonClicked() {
                //TODO : Display a pop up to notify client if they are not connected ?
                if(fileList != null){
                    doUpload();
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
                                fileList = fileList[0].listFiles(Path.withAtLeastOneFile);
                            }
                            updateUploadTabInfo();
                        }
                    });
                    t.start();
                    uploadPath = mfc.getSelectedDirectory().toPath();
                }else
                    fileList = null;

                updateUploadTabInfo();
            }

            @Override
            public void onFilesDragged(File[] fileDraggedList) {
                fileList = fileDraggedList;
                updateUploadTabInfo();
            }
        });
    }

    public void refreshGUI(){
        gui.getUploadTab().refreshPanel();
    }

    /**
     * Update informations about the upload panel (upload path and decide whether it displays file info or not (if fileList is not null))
     */
    public void updateUploadTabInfo(){
        gui.getUploadTab().setPathTextField(uploadPath.toString());
        if(fileList != null){
            gui.getUploadTab().setFileInfoLabel(Gui.displayFileInfo(fileList));
            gui.getUploadTab().getFileInfoLabel().setVisible(true);
        }else{
            gui.getUploadTab().getFileInfoLabel().setVisible(false);
        }
    }

    private void doUpload(){
        Upload up = new Upload(fileList);
        UploadPanel p = new UploadPanel();
        p.addUploadPanelListener(new UploadPanelListener() {
            @Override
            public void onClosedButtonClicked() {
                //TODO : verify if there is no more thing to clean ?
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
                long throughput = 0;
                p.setProgressBarValue((int)percentage);
                p.setProgressBarText(Gui.byte2Readable(throughput) + "/s");
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
        updateUploadTabInfo();
    }

    public void saveConfig(java.nio.file.Path config_file) throws IOException {
        Properties prop = new Properties();

        InputStream is = new FileInputStream(config_file.toFile());

        try {
            prop.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        prop.setProperty("ADDRESS", address.name+";"+address.ip+":"+address.getPort());
        prop.setProperty("THEME",gui.getTheme());
        prop.setProperty("UPLOAD_PATH", uploadPath.toString());
        prop.setProperty("DOWNLOAD_PATH", downloadPath.toString());

        prop.store(new FileOutputStream(config_file.toFile()), null);
    }
}
