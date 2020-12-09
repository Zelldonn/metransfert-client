package com.metransfert.client.gui;

import com.metransfert.client.Channel;
import com.metransfert.client.transaction.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gui extends JFrame implements ActionListener{

    public class TransferPanel extends JPanel{
        JProgressBar progressBar;

        TransferPanel(){

            progressBar = new JProgressBar();
            progressBar.setValue(0);
            progressBar.setStringPainted(true);

            JLabel throughput = new JLabel();

            this.add(progressBar);
            this.add(throughput);
        }
        public void update(TransferListener.Info info){
            int percentage = (int)(((double)info.transferredBytes/(double)info.expectedBytes)*100);
            progressBar.setValue(percentage);
            progressBar.setString("Downloading... " +percentage+" %");

            float throughput = getThroughput(info.transferredBytes, info.oldTransferredBytes);

            l_throughput.setText(byte2Readable(throughput) + "/s");
        }
    }

    public class RequestInfoButton extends JButton{
        Boolean canDownload;
        String fileName;
        RequestInfoButton(String title){
            this.setText(title);
            setDownload(false);
            //Dont forrget to add actionlistener
        }

        public void setDownload(Boolean canDownload) {
            this.canDownload = canDownload;
            if(canDownload){
                this.setText("Download");
            }else{
                this.setText("Show");
            }
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    Channel mainChannel;

    private JPanel mainPanel;
    private final JPanel ID_Panel;
    private final JPanel transferPanel;
    private final JPanel bottomPanel;

    private final JMenuItem changeAddressMenuItem;
    private final JMenuItem connectMenuItem;

    private final JButton chooseFile_b;
    private final JTextField path_tf;

    private final JProgressBar pg;
    private final JLabel l_throughput;

    private final JButton upload_b;
    private final JTextField generatedID;
    private final JButton copy_clipboard;

    private final JTextField requestID_tf;

    private final JLabel requestedInfo_l;
    private Image bg = null;

    private final RequestInfoButton requestInfo_b;

    private float throughput = 0F;
    private float oldTime = 0F;

    private JLabel l_downloadConfirmed = new JLabel("");

    JTabbedPane tabbedPane;

    public Gui(){
        //-------------------Define Frame properties---------------------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(550,350);
        this.setMinimumSize(this.getSize());

        ImageIcon img = new ImageIcon("img/logo.png");
        setIconImage(img.getImage());

        //Move focus to the next item (Avoid first button to be focused)
        setFocusable(true);

        //--------Creating the panels and adding components------\\
        JPanel mainPanel = new JPanel();

        JPanel uploadPanel = new JPanel();

        bottomPanel = new JPanel();

        transferPanel = new JPanel();

        ID_Panel = new JPanel();

        mainChannel = new Channel();

        //-------Creating the MenuBar and adding components------\\
        JMenuBar menuBar = new JMenuBar();
        JLabel connectionStatusLabel = new JLabel("Not Connected");
        connectionStatusLabel.setForeground(Color.GRAY);
        JMenu serverMenu = new JMenu("Server");
        JMenu m2 = new JMenu("Client");
        changeAddressMenuItem = new JMenuItem("Change address");
        changeAddressMenuItem.addActionListener(this);

        connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.addActionListener(this);

        serverMenu.add(changeAddressMenuItem);
        menuBar.add(serverMenu);
        m2.add(connectMenuItem);
        menuBar.add(m2);
        menuBar.add(connectionStatusLabel);
        setJMenuBar(menuBar);

        //--------------Generated ID TextField---------------\\
        generatedID = new JTextField("", 4);
        generatedID.setHorizontalAlignment(JTextField.CENTER);
        generatedID.setEditable(false);


        //-------------Copy to clipboard Button-------------\\
        Icon icon = new ImageIcon("img/clipboard.png");
        Image img_ = ((ImageIcon) icon).getImage() ;
        Image new_img = img_.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH) ;
        icon = new ImageIcon( new_img );

        copy_clipboard = new JButton(icon);
        copy_clipboard.addActionListener(this);

        //----------Choose file path Button-----------\\
        chooseFile_b = new JButton("Choose a file");
        chooseFile_b.addActionListener(this);

        //------------File path TextField-------------\\
        path_tf = new JTextField(20);
        path_tf.setText("E:/Téléchargements/c.c");
        path_tf.setDragEnabled(true);

        //----------------Paste-ID & Show File Area----------------\\
        requestInfo_b = new RequestInfoButton("Show file");
        requestInfo_b.addActionListener(this);

        requestedInfo_l = new JLabel("Enter ID");
        requestID_tf = new JTextField(8); //Display up to 8 characters
        requestID_tf.getDocument().addDocumentListener(new MyDocumentListener(requestInfo_b));

        //Drag n Drop box ?

        //------------------ProgressBar-----------------\\
        pg = new JProgressBar();
        pg.setValue(0);
        pg.setStringPainted(true);

        //----------------Upload button-----------------\\
        upload_b = new JButton("Generate ID");
        upload_b.addActionListener(this);

        //------------Throughput Label-----------------\\
        l_throughput = new JLabel();

        //Download button

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(1);
        tabbedPane.addTab("Upload", new UploadTab());
        tabbedPane.addTab("Download", new DownloadTab());
        tabbedPane.setPreferredSize(this.getSize());

        mainPanel = new JPanel();

        mainPanel.add(tabbedPane);

        setContentPane(mainPanel);

        String status = "Connected";
        String address = mainChannel.getIp() + ":" + mainChannel.getPort();
        connectionStatusLabel.setText(status + " : " + address);
        connectionStatusLabel.setForeground(new Color(89, 160, 66));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == chooseFile_b){
            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(this);
            if(i == JFileChooser.APPROVE_OPTION){
                Path filepath = fc.getSelectedFile().toPath();

                path_tf.setText(filepath.toString());
            }
        }
        else if(e.getSource() == upload_b){
            if(isFile(path_tf.getText())){
                Path p = Paths.get(path_tf.getText());
                oldTime = System.nanoTime();
                mainChannel.upload(p, new TransferListener() {
                    @Override
                    public void onTransactionStart() {
                        path_tf.setText("Let's upload again !");
                    }

                    @Override
                    public void onTransactionFinish(TransactionResult result) {
                        UploadInfoResult uploadResult = (UploadInfoResult)result;

                        transferPanel.setVisible(false);
                        ID_Panel.setVisible(true);
                        String ID = uploadResult.id;
                        generatedID.setText(ID);
                    }

                    @Override
                    public void onTransferUpdate(Info info) {
                        transferPanel.setVisible(true);
                        int percentage = (int)(((double)info.transferredBytes/(double)info.expectedBytes)*100);
                        pg.setValue(percentage);
                        pg.setString("Uploading... " +percentage+" %");

                        float throughput = getThroughput(info.transferredBytes, info.oldTransferredBytes);

                        l_throughput.setText(byte2Readable(throughput) + "/s");
                    }
                });
            }else{
                path_tf.setText("Error: The file specified does not exist");
            }
        }
        else if(e.getSource() == requestInfo_b){
            if(requestInfo_b.canDownload){
                //Opening file chooser
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int i = fc.showOpenDialog(this);
                Path downloadPath = null;

                if(i == JFileChooser.APPROVE_OPTION){
                    String filename = requestInfo_b.fileName;
                    String temp = fc.getSelectedFile().toPath().toString() + "/" + filename;
                    downloadPath = Paths.get(temp) ;
                    try {
                        TransferPanel dl = new TransferPanel();
                        bottomPanel.add(dl);
                        String id = requestID_tf.getText();
                        Path finalDownloadPath = downloadPath;
                        mainChannel.download(id, downloadPath, new TransferListener() {
                            @Override
                            public void onTransferUpdate(Info info) {
                                dl.update(info);
                            }

                            @Override
                            public void onTransactionStart() {
                            bottomPanel.remove(l_downloadConfirmed);
                            }

                            @Override
                            public void onTransactionFinish(TransactionResult result) {
                                bottomPanel.remove(dl);
                                l_downloadConfirmed.setText("File saved in : " + finalDownloadPath.toString());
                                l_downloadConfirmed.setBackground(Color.white);
                                bottomPanel.add(l_downloadConfirmed);
                            }
                        });
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

            }
            else {
                String id = requestID_tf.getText();
                mainChannel.requestInfo(id, new TransactionListener() {
                    @Override
                    public void onTransactionStart() {

                    }

                    @Override
                    public void onTransactionFinish(TransactionResult result) {
                        RequestInfoResult infoResult = (RequestInfoResult)result;

                        String display;
                        if(!infoResult.id_exists)
                            display = "File does not exist";
                        else{
                            display = infoResult.fileName + " | " + byte2Readable(infoResult.fileSize);
                            requestInfo_b.setDownload(true);
                            requestInfo_b.setFileName(infoResult.fileName);
                        }

                        requestedInfo_l.setText(display);

                    }
                });
            }
        }
        else if(e.getSource() == changeAddressMenuItem){
            ChangeAddressPopUp p = new ChangeAddressPopUp();
            mainChannel.setAddress(p.ip, p._port);
            //TODO : make a ping request to update status in GUI
        }
        else if(e.getSource() == copy_clipboard){
            copyToClipBoard(generatedID.getText());
        }
    }

    //TODO : make this fck thing working
    private float getThroughput(int transferredBytes, int oldTransferredBytes){
        float timeNow = System.nanoTime();
        float delta = (timeNow - oldTime);
        float temp = ((float)transferredBytes - (float)oldTransferredBytes) / (delta / 10_000_000_000F);
        if(temp < 100000000F)
            throughput = temp;

        oldTime = timeNow;

        return throughput;
    }

    //TODO: reload ID in memory after crash
    private boolean isFile(String path){
        File file = new File(path);
        return file.isFile();
    }

    /**
     * Converts byte to string with appropriate unit  * @param byte
     * @return
     */
    private String byte2Readable(float _byte){
        String s = "";
        float mebibyte = 1_048_576F;

        if(_byte < mebibyte)
            s = String.format("%.2f",_byte/1024F) + " kB";
        else if(_byte < mebibyte * 1024F){
            s = String.format("%.2f",_byte/mebibyte) + " MB";
        }else if(_byte < mebibyte * mebibyte){
            s = String.format("%.2f",_byte/(mebibyte * 1024F)) + " GB";
        }

        return s;
    }

    static class ChangeAddressPopUp {
        int _port;
        String ip;

        public ChangeAddressPopUp(){
            JTextField address = new JTextField();
            JTextField port = new JTextField();
            Object[] message = {
                    "Address:", address,
                    "Port:", port
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Change address", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                ip = address.getText();
                _port = Integer.parseInt(port.getText());
            }
        }
    }

    public static void copyToClipBoard(String text){
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}