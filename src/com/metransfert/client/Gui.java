package com.metransfert.client;

import com.metransfert.client.transaction.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gui extends JFrame implements ActionListener{

    public class TransferPanel extends JPanel{

        TransferPanel(){

            JProgressBar progressBar = new JProgressBar();
            progressBar.setValue(0);
            progressBar.setStringPainted(true);

            JLabel throughput = new JLabel();

            this.add(progressBar);
            this.add(throughput);
            this.setVisible(false);

        }
    }

    public class RequestInfoButton extends JButton{
        Boolean canDownload;
        String fileName;
        RequestInfoButton(){
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

    private final JPanel ID_Panel;
    private final JPanel transferPanel;
    private final JPanel bottomPanel;

    private final JMenuItem m11;

    private final JButton chooseFile_b;
    private final JTextField path_tf;

    private final JProgressBar pg;
    private final JLabel l_throughput;

    private final JButton upload_b;
    private final JTextField generatedID;
    private final JButton copy_clipboard;

    private final JButton requestIDInfo_b;
    private final JTextField requestID_tf;

    private final JLabel requestedInfo_l;
    private Image bg = null;

    private final RequestInfoButton requestInfo_b;

    public Gui(){
        //-------------------Define Frame properties---------------------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(450,300);
        this.setMinimumSize(this.getSize());
        Image img = Toolkit.getDefaultToolkit().getImage("img/logo.png");
        this.setIconImage(img);
        try {
            bg = ImageIO.read(new File("img/back_ground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Move focus to the next item (Avoid first button to be focused)
        setFocusable(true);

        //Set Windows look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //--------Creating the panels and adding components------\\
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(60, 63, 65));/*{
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bg, 0, 0, getWidth(),getHeight(),this);
            }
        };*/
        JPanel uploadPanel = new JPanel();
        uploadPanel.setBackground(new Color(60, 63, 65));

        bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(60, 63, 65));

        transferPanel = new JPanel();
        transferPanel.setBackground(new Color(60, 63, 65));

        ID_Panel = new JPanel();
        ID_Panel.setBackground(new Color(60, 63, 65));



        //-------Creating the MenuBar and adding components------\\
        JMenuBar mb = new JMenuBar();
        JLabel label_address = new JLabel("Not Connected");
        label_address.setForeground(Color.GRAY);
        JMenu m1 = new JMenu("Server");
        JMenu m2 = new JMenu("Client");
        m11 = new JMenuItem("Change address");
        m11.addActionListener(this);

        mb.add(m1);
        mb.add(m2);
        m1.add(m11);
        mb.add(label_address);

        //--------------Generated ID TextField---------------\\
        generatedID = new JTextField("Ax574", 4);
        generatedID.setEditable(false);

        //-------------Copy to clipboard Button-------------\\
        Icon icon = new ImageIcon("img/clipboard.png");
        img = ((ImageIcon) icon).getImage() ;
        Image new_img = img.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH ) ;
        icon = new ImageIcon( new_img );

        copy_clipboard = new JButton(icon);
        copy_clipboard.addActionListener(this);

        //----------Choose file path Button-----------\\
        chooseFile_b = new JButton("Choose a file");
        chooseFile_b.addActionListener(this);

        //------------File path TextField-------------\\
        path_tf = new JTextField(25);
        path_tf.setText("E:/Téléchargements/c.c");
        path_tf.setDragEnabled(true);
        path_tf.setBackground(new Color(151, 151, 151));

        //----------------Paste-ID & Show File Area----------------\\
        requestedInfo_l = new JLabel("Enter ID");
        requestID_tf = new JTextField(8); //Display up to 8 characters


        requestIDInfo_b = new JButton("Show file");
        requestInfo_b = new RequestInfoButton();
        requestInfo_b.addActionListener(this);

        //Drag n Drop box ?

        //------------------ProgressBar-----------------\\
        pg = new JProgressBar();
        pg.setValue(0);
        pg.setStringPainted(true);

        //----------------Upload button-----------------\\
        upload_b = new JButton("Upload file");
        upload_b.addActionListener(this);

        //------------Throughput Label-----------------\\
        l_throughput = new JLabel();

        //Download button

        // Components Added using Flow Layout

        bottomPanel.add(requestedInfo_l);
        bottomPanel.add(requestID_tf);
        bottomPanel.add(requestInfo_b);

        transferPanel.add(pg);
        transferPanel.add(l_throughput);
        transferPanel.setVisible(false);

        uploadPanel.add(chooseFile_b);
        uploadPanel.add(path_tf);
        uploadPanel.add(upload_b);

        ID_Panel.add(generatedID);
        ID_Panel.add(copy_clipboard);
        ID_Panel.setVisible(false);

        mainPanel.add(uploadPanel);
        mainPanel.add(transferPanel);
        mainPanel.add(ID_Panel);

        //Adding Components to the frame.
        this.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
        this.getContentPane().add(BorderLayout.PAGE_START, mb);
        this.getContentPane().add(BorderLayout.CENTER, mainPanel);

        //Extra important layout settings
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                mainPanel.repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        mainChannel = new Channel();

        mainChannel.addStatusChangeListeners(new_statue -> {
            String status;

            if(new_statue == Status.CONNECTED){
                status = "Connected";
                label_address.setForeground(new Color(89, 160, 66));
            } else if(new_statue == Status.TRYING){
                status = "Trying to connect";
                label_address.setForeground(new Color(212, 63, 35));
            }else if(new_statue == Status.TIMEOUT){
                status = "Timeout";
                label_address.setForeground(new Color(205, 25, 25));
            }else
                status = "UNSPECIFIED ERROR";

            String address = mainChannel.getIp() + ":" + mainChannel.getPort();

            label_address.setText(status + " : " + address);
        });
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
                mainChannel.upload(p, new TransferListener() {
                    @Override
                    public void onTransactionStart() {
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

                        float throughput = 528_152F;
                        l_throughput.setText(byte2Readable(throughput) + "/s");
                    }
                });
            }else{
                path_tf.setText("Error: The file specified does not exist");
            }
        }
        else if(e.getSource() == requestInfo_b){
            if(requestInfo_b.canDownload){
                //Open path dialogue
                String filename = requestInfo_b.fileName;
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int i = fc.showOpenDialog(this);
                Path downloadPath = null;
                if(i == JFileChooser.APPROVE_OPTION){
                    String temp = fc.getSelectedFile().toPath().toString() + "/" + filename;
                    downloadPath = Paths.get(temp) ;
                }
                try {
                    TransferPanel dl = new TransferPanel();
                    bottomPanel.add(dl);
                    String id = requestID_tf.getText();
                    mainChannel.download(id, downloadPath, new TransferListener() {
                        @Override
                        public void onTransferUpdate(Info info) {

                        }

                        @Override
                        public void onTransactionStart() {

                        }

                        @Override
                        public void onTransactionFinish(TransactionResult result) {

                        }
                    });
                } catch (IOException ioException) {
                    ioException.printStackTrace();
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
        else if(e.getSource() == m11){
            ChangeAddressPopUp p = new ChangeAddressPopUp();
            mainChannel.setAddress(p.ip, p._port);
            //TODO : make a ping request to update status in GUI
        }
        else if(e.getSource() == copy_clipboard){
            copyToClipBoard(generatedID.getText());
        }
    }

    //TODO : make this fck thing working
    /*private float calculateThroughput(){
        float timeNow = System.nanoTime();
        float delta = (timeNow - time);
        throughput = ((float)transferredBytes - (float)oldTransferredBytes) / (delta / 1_000_000_000F);
        time = timeNow;

        System.out.println(throughput);
    }*/

    //TODO: reload ID in memory after crash
    private boolean isFile(String path){
        File file = new File(path);
        return file.isFile();
    }

    /**
     * Converts byte to string with appropriate unit  * @param throughput
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
            s = String.format("%.2f",_byte/(mebibyte * mebibyte)) + " GB";
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
                //TODO: check if address and port may be valid
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