import com.packeteer.network.Packet;
import com.packeteer.network.PacketUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gui extends JFrame implements ActionListener{
    Client client ;

    private final JPanel ID_Panel;
    private final JPanel progress_panel;

    private final JMenuItem m11;

    private final JButton file;
    private final JTextField tf_path;

    private final JProgressBar pg;
    private final JLabel l_throughput;

    private final JButton upload;
    private final JTextField generatedID;
    private final JButton copy_clipboard;

    private final JButton show;
    private final JTextField tf_id;

    private int throughput = 100;// KB/S or MB/S

    public Gui(){
        //-------------------Define Frame properties---------------------\\
        this.setTitle("MeTransfer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400,300);
        this.setMinimumSize(new Dimension(400, 300));
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
        generatedID = new JTextField("Ax574");
        generatedID.setEditable(false);

        //-------------Copy to clipboard Button-------------\\
        Icon icon = new ImageIcon("img/clipboard.png");
        img = ((ImageIcon) icon).getImage() ;
        Image new_img = img.getScaledInstance( 16, 16,  java.awt.Image.SCALE_SMOOTH ) ;
        icon = new ImageIcon( new_img );

        copy_clipboard = new JButton(icon);
        copy_clipboard.addActionListener(this);

        //----------Choose file path Button-----------\\
        file = new JButton("Choose a file");
        file.addActionListener(this);

        //------------File path TextField-------------\\
        tf_path = new JTextField(20);
        tf_path.setText("C:/Examples/test/img.txt");

        //----------------Paste-ID & Show File Area----------------\\
        JLabel l_id = new JLabel("Enter ID");
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
        l_throughput = new JLabel(Integer.toString(throughput));

        //Download button

        // Components Added using Flow Layout
        panel.add(l_id);
        panel.add(tf_id);
        panel.add(show);
        panel.add(show);

        progress_panel.add(pg);
        progress_panel.add(l_throughput);

        progress_panel.setVisible(false);

        uploadPanel.add(file);
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
            if(new_statue){
                status = "Connected";
                label_address.setForeground(new Color(89, 160, 66));
            }
            else {
                status = "Trying to connect";
                label_address.setForeground(Color.RED);
            }
            String address = client.getAddress() + ":" + client.getPort();
            label_address.setText(status + " : " + address);
        });

        client.addRequestInfoFinishListeners(new TransactionFinishListener() {
            @Override
            public void onFinish(Packet p) {
                ByteBuffer bf = p.getPayloadBuffer();
                byte result = bf.get();
                int fileSize = bf.getInt();
                String fileName = PacketUtils.readNetworkString(bf);

                //TODO : Format octet display
                String display = fileName + " | " + fileSize;

                l_id.setText(display);
            }
        });

        client.connect();

    }
    private boolean isFile(String path){
            File file = new File(path);
            return file.isFile();
    }
    private String throughput2string(int throughput){
        String s = "";
        if(throughput < 1024)
            s = Float.toString(throughput) + " kB/s";
        else if(throughput >= 1024){
            s =  String.format("%.2f",(float)throughput / 1024) + " MB/s";
        }
        return s;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final Timer t = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                progress_panel.setVisible(true);
                pg.setString("Uploading..." );
                pg.setValue(pg.getValue() + 1);
                throughput += 50;
                l_throughput.setText(throughput2string(throughput));
                if (pg.getValue() == 100) {
                    progress_panel.setVisible(false);
                    ID_Panel.setVisible(true);
                    ((Timer) e.getSource()).stop();
                }
            }
        });

        if(e.getSource() == file){
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
                client.upload(p);
                //t.start();
            }else{
                tf_path.setText("Error: The file specified does not exist");
            }
        }
        else if(e.getSource() == copy_clipboard){
            if(generatedID.isVisible()){
                String myString = generatedID.getText();
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        }else if(e.getSource() == show){
            String id = tf_id.getText();
            client.requestInfo(id);
        }else if(e.getSource() == m11){
            JTextField address = new JTextField();
            JTextField port = new JTextField();
            Object[] message = {
                    "Address:", address,
                    "Port:", port
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Change address", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (true) {
                    client.setAddress(address.getText());
                    client.setPort(Integer.parseInt(port.getText()));

                    client.connect();
                } else {
                    System.out.println("login failed");
                }
            } else {
                System.out.println("Login canceled");
            }
        }
    }
}