import com.packeteer.network.PacketUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gui extends JFrame implements ActionListener{

    Client client;

    private final JPanel ID_Panel;
    private final JPanel progress_panel;

    private final JMenuItem m11;

    private final JButton chooseFile_b;
    private final JTextField tf_path;

    private final JProgressBar pg;
    private final JLabel l_throughput;

    private final JButton upload;
    private final JTextField generatedID;
    private final JButton copy_clipboard;

    private final JButton show;
    private final JTextField tf_id;

    private final JLabel l_resultID;

    public Gui(){
        //-------------------Define Frame properties---------------------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(450,300);
        this.setMinimumSize(this.getSize());
        Image img = Toolkit.getDefaultToolkit().getImage("img/logo.png");
        this.setIconImage(img);

        //Move focus to the next item (Avoid first button to be focused)
        setFocusable(true);

        //Set Windows look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //--------Creating the panels and adding components------\\
        JPanel panel = new JPanel(); // the panel is not visible in output
        panel.setBackground(new Color(219, 219, 219));
        progress_panel = new JPanel();
        JPanel uploadPanel = new JPanel();
        ID_Panel = new JPanel();
        JPanel panel_main = new JPanel();

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
        JLabel transferStatus= new JLabel("File successfully uploaded");
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
        tf_path = new JTextField(25);
        tf_path.setText("E:/Téléchargements/b.exe");
        tf_path.setDragEnabled(true);

        //----------------Paste-ID & Show File Area----------------\\
        l_resultID = new JLabel("Enter ID");
        tf_id = new JTextField(8); //Display up to 8 characters

        show = new JButton("Show file");
        show.addActionListener(this);

        //Drag n Drop box ?

        //------------------ProgressBar-----------------\\
        pg = new JProgressBar();
        pg.setStringPainted(true);

        //----------------Upload button-----------------\\
        upload = new JButton("Upload file");
        upload.addActionListener(this);

        //------------Throughput Label-----------------\\
        l_throughput = new JLabel();

        //Download button

        // Components Added using Flow Layout
        panel.add(l_resultID);
        panel.add(tf_id);
        panel.add(show);
        panel.add(show);

        progress_panel.add(pg);
        progress_panel.add(l_throughput);

        progress_panel.setVisible(false);

        uploadPanel.add(chooseFile_b);
        uploadPanel.add(tf_path);
        uploadPanel.add(upload);

        ID_Panel.add(transferStatus);
        ID_Panel.add(generatedID);
        ID_Panel.add(copy_clipboard);
        ID_Panel.setVisible(false);

        panel_main.add(uploadPanel);
        panel_main.add(progress_panel);
        panel_main.add(ID_Panel);

        //Adding Components to the frame.
        this.getContentPane().add(BorderLayout.SOUTH, panel);
        this.getContentPane().add(BorderLayout.PAGE_START, mb);
        this.getContentPane().add(BorderLayout.CENTER, panel_main);


        //Extra important layout settings
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        client = new Client();

        client.addStatusChangeListeners(new_statue -> {
            String status;

            if(new_statue == Status.CONNECTED){
                status = "Connected";
                label_address.setForeground(new Color(89, 160, 66));
            } else if(new_statue == Status.TRYING){
                status = "Trying to connect";
                label_address.setForeground(Color.RED);
            }else if(new_statue == Status.TIMEOUT){
                status = "Timeout";
                label_address.setForeground(new Color(222, 95, 10));
            }else
                status = "UNSPECIFIED ERROR";

            String address = client.getIp() + ":" + client.getPort();

            label_address.setText(status + " : " + address);
        });

        client.connect();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == chooseFile_b){
            JFileChooser fc = new JFileChooser();

            int i = fc.showOpenDialog(this);
            if(i == JFileChooser.APPROVE_OPTION){
                Path filepath = fc.getSelectedFile().toPath();
                tf_path.setText(filepath.toString());
            }
        }
        else if(e.getSource() == upload){
            if(isFile(tf_path.getText())){
                //Upload code
                Path p = Paths.get(tf_path.getText());
                try {
                    client.upload(p, new TransferListener() {
                        @Override
                        public void onTransactionStart() {

                        }

                        @Override
                        public void onTransactionFinish(TransactionResult result) {


                            ByteBuffer bf = result.packet.getPayloadBuffer();

                            byte resultCode = bf.get();
                            if(resultCode != 0x0){
                                //TODO : handleError
                            }else{
                                progress_panel.setVisible(false);
                                ID_Panel.setVisible(true);
                                String ID = PacketUtils.readNetworkString(bf);
                                generatedID.setText(ID);
                            }
                        }

                        @Override
                        public void onTransferUpdate(Info info) {
                            progress_panel.setVisible(true);
                            pg.setString("Uploading..." );

                            int percentage = (int)(((double)info.transferredBytes/(double)info.expectedBytes)*100);
                            pg.setValue(percentage);
                            l_throughput.setText(throughput2string(info.throughput));
                        }
                    });
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }else{
                tf_path.setText("Error: The file specified does not exist");
            }
        }
        else if(e.getSource() == copy_clipboard){
            copyToClipBoard(generatedID.getText());
        }else if(e.getSource() == show){
            //TODO : handle unknown id
            String id = tf_id.getText();
            client.requestInfo(id, new TransactionListener() {
                @Override
                public void onTransactionStart() {

                }

                @Override
                public void onTransactionFinish(TransactionResult result) {
                    ByteBuffer bf = result.packet.getPayloadBuffer();
                    byte resultCode = bf.get();
                    int fileSize = bf.getInt();
                    String fileName = PacketUtils.readNetworkString(bf);

                    //TODO : Format octet display
                    String display = fileName + " | " + byte2Readable(fileSize) ;

                    //TODO : rename label
                    l_resultID.setText(display);
                }
            });
        }else if(e.getSource() == m11){
            ChangeAddressPopUp p = new ChangeAddressPopUp();
            client.setPort(p._port);
            client.setIp(p.ip);
            //TODO : make a ping request to update status in GUI
            client.connect();
        }
    }

    private boolean isFile(String path){
        File file = new File(path);
        return file.isFile();
    }

    private String throughput2string(float throughput){
        String s = "";
        if(throughput < 1024)
            s = Float.toString(throughput) + " kB/s";
        else if(throughput >= 1024){
            s =  String.format("%.2f",(float)throughput / 1024) + " MB/s";
        }
        return s;
    }

    //TODO: reload ID in memory after crash
    private String byte2Readable(int _byte_){
        float _byte = (float)_byte_;
        String s = "";
        int mebibyte = 1_048_576;
        if(_byte < mebibyte)
            s = _byte + " B";
        else if(_byte <= mebibyte * 1000){
            s = String.format("%.2f",_byte/(float)1_048_576) + " MB";
        }else if(_byte <= mebibyte * 2000){
            s = String.format("%.2f",_byte/((float)1_048_576 * 1000)) + " GB";
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
                //TODO: check if adress and port may be valid
                if (true) {
                    ip = address.getText();
                    _port = Integer.parseInt(port.getText());
                } else {
                    System.out.println("login failed");
                }
            }
        }
    }

    public static void copyToClipBoard(String text){
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}