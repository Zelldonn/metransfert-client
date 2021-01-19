package com.metransfert.client.controller;

import com.metransfert.client.controller.exception.ConnectionFailedException;
import com.metransfert.client.controller.transaction.Download;
import com.metransfert.client.controller.transaction.Ping;
import com.metransfert.client.controller.transaction.RequestInfo;
import com.metransfert.client.controller.transaction.Upload;
import com.metransfert.client.gui.Gui;
import com.metransfert.client.gui.GUIListener;
import com.metransfert.client.gui.MyFileChooser;
import com.metransfert.client.gui.Status;
import com.metransfert.client.gui.download.DownloadPanel;
import com.metransfert.client.gui.download.DownloadPanelListener;
import com.metransfert.client.gui.download.DownloadTabListener;
import com.metransfert.client.gui.upload.UploadPanel;
import com.metransfert.client.gui.upload.PanelListener;
import com.metransfert.client.gui.upload.UploadTabListener;
import com.metransfert.client.transactionhandlers.DownloadListener;
import com.metransfert.client.transactionhandlers.RequestListener;
import com.metransfert.client.transactionhandlers.TransferInfo;
import com.metransfert.client.transactionhandlers.TransferListener;
import com.metransfert.client.utils.Path;

import javax.swing.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class ClientController {

    private static ClientController instance;

    private final Gui gui;

    private Address address;

    private ArrayList<Address> addresses = new ArrayList<>();

    private java.nio.file.Path uploadPath, downloadPath;

    private File[] fileList;

    private String requestedID;

    boolean canDownload = false;

    public ClientController(Gui g, ClientConfiguration config, String[] args) {
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
     * Create and start a ping transaction using a channel. This will result by displaying connection's information on the GUI's menu bar
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
                    gui.getUploadTab().setPathTextField(mfc.getSelectedDirectory().toString());
                    uploadPath = mfc.getCurrentDir();
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

    public void initDownloadTab(){

        gui.getDownloadTab().setPathTextField(downloadPath.toString());

        gui.getDownloadTab().addDownloadTabListeners(new DownloadTabListener() {
            @Override
            public void onDownloadButtonClicked() {
                if(canDownload){
                    doDownload();
                }else{
                    requestedID = gui.getDownloadTab().getIdTextField();
                    Channel c = new Channel(address);
                    RequestInfo ri = new RequestInfo(requestedID);
                    ri.addRequestListeners(new RequestListener() {
                        @Override
                        public void onTransactionFinish(int fileNumber, long expectedBytes) {
                            if(fileNumber == 0 && expectedBytes == 0){
                                gui.getDownloadTab().setFileInfoLabel("This ID is not valid");
                                canDownload = false;
                            }else{
                                gui.getDownloadTab().setFileInfoLabel(fileNumber +" file(s) ("+ com.metransfert.client.utils.Gui.byte2Readable(expectedBytes)+")");
                                setDownloadable(true);
                            }
                            gui.getDownloadTab().setFileInfoLabelVisible(true);
                        }

                        @Override
                        public void onTransactionStart() {
                            gui.getDownloadTab().setFileInfoLabelVisible(false);
                        }

                        @Override
                        public void onTransactionFinish(String id) {

                        }
                    });
                    try {
                        c.startTransaction(ri);
                    } catch (ConnectionFailedException e) {
                        e.printStackTrace();
                    }
                }
                //Check if can dl

                //if not
                //Send requestInfo

                //Disp Info and set canDl to true

                //if canDL
                //doDownload

            }

            @Override
            public void onPathTextFieldChanged(java.nio.file.Path p) {
                downloadPath = p;
            }

            @Override
            public void onPathChooserClicked() {
                MyFileChooser mfc = new MyFileChooser();
                boolean result = mfc.openFileChooser(downloadPath, gui.getDownloadTab(), JFileChooser.DIRECTORIES_ONLY);
                if (result){
                    downloadPath = mfc.getSelectedDirectory();
                    gui.getDownloadTab().setPathTextField(mfc.getSelectedDirectory().toString());
                }
            }

            @Override
            public void onIDTextFieldChanged() {
                setDownloadable(false);
            }
        });
    }

    public void setDownloadable(boolean b){
        gui.getDownloadTab().setIdTextField("");
        if(!b)
            gui.getDownloadTab().setDownloadButton("Show");
        else
            gui.getDownloadTab().setDownloadButton("Download");
        canDownload = b;
        gui.getDownloadTab().setFileInfoLabelVisible(b);
    }

    public void refreshGUI(){
        gui.getUploadTab().refreshPanel();
    }

    /**
     * Update informations about the upload panel (upload path and decide whether it displays file info or not (if fileList is not null))
     */
    public void updateUploadTabInfo(){
        if(fileList != null){
            gui.getUploadTab().setFileInfoLabel(com.metransfert.client.utils.Gui.displayFileInfo(fileList));
            gui.getUploadTab().getFileInfoLabel().setVisible(true);
        }else{
            gui.getUploadTab().getFileInfoLabel().setVisible(false);
        }
    }

    private void doDownload(){
        Download dl = new Download(downloadPath, requestedID);
        DownloadPanel p = new DownloadPanel(downloadPath);
        p.addDownloadPanelListeners(new DownloadPanelListener() {
            @Override
            public void onOpenDownloadPathClicked() {
                //TODO : try to open the true windows explorer
            }

            @Override
            public void onClosedButtonClicked() {
                gui.getDownloadTab().remove(p);
                gui.getDownloadTab().refreshPanel();
            }

            @Override
            public void onPauseButtonClicked() {

            }

            @Override
            public void onStopButtonClicked() {

            }
        });
        gui.getDownloadTab().add(p);
        dl.addDownloadListeners(new DownloadListener() {
            @Override
            public void onTransferFinish() {
                p.getProgressBar().setVisible(false);
                p.getPauseButton().setVisible(false);
                p.getStopButton().setVisible(false);
                gui.getDownloadTab().refreshPanel();
                p.getFileInfoLabel().setText("Files saved in "+downloadPath);
                p.getFileInfoLabel().setVisible(true);
                setDownloadable(false);
            }

            @Override
            public void onTransferUpdate(TransferInfo info) {
                float percentage = ((float)info.transferredBytes/(float)info.expectedBytes) * 100F;
                long throughput = 0;
                p.setProgressBarValue((int)percentage);
                p.setProgressBarText(com.metransfert.client.utils.Gui.byte2Readable(throughput) + "/s");
                gui.getDownloadTab().refreshPanel();
            }

            @Override
            public void onTransactionStart() {

            }

            @Override
            public void onTransactionFinish(String id) {

            }
        });
        Channel c = new Channel(address);
        try {
            c.startTransaction(dl);
        } catch (ConnectionFailedException e) {
            e.printStackTrace();
        }
    }

    private void doUpload(){
        Upload up = new Upload(fileList);
        UploadPanel p = new UploadPanel();
        p.addPanelListener(new PanelListener() {
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
            public void onTransferUpdate(TransferInfo info) {
                float percentage = ((float)info.transferredBytes/(float)info.expectedBytes) * 100F;
                long throughput = 0;
                p.setProgressBarValue((int)percentage);
                p.setProgressBarText(com.metransfert.client.utils.Gui.byte2Readable(throughput) + "/s");
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
