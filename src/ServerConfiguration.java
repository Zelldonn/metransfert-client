

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class ServerConfiguration {

    // [SERVICE]

    /**
     * A string representing the path the root directory (i.e where everything will be stored/read from
     * <p>
     * NOTE : The string should end with a forward slash '/'. If the given string does not end properly, a '/' will be appended to the end upon creation
     */
    public final Path rootDirectory;
    public final Path storeDirectory;
    public final int defaultLeaseTime; //in seconds
    public final int IDlength;

    public final String charSet;

    //[NETWORK]
    public final int serverPort;

    private ServerConfiguration(Path rootDirectory, Path storeDir, int defaultLeaseTime, int IDlength, int port, String charSet) {
        this.rootDirectory = rootDirectory;
        this.storeDirectory = storeDir;
        this.defaultLeaseTime = defaultLeaseTime;
        this.IDlength = IDlength;
        this.serverPort = port;
        this.charSet = charSet;
    }

    public static ServerConfiguration loadFromFile(Path config_file) throws FileNotFoundException {
        Properties prop = new Properties();

        InputStream is = new FileInputStream(config_file.toFile());

        try {
            prop.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path root = Paths.get(System.getProperty("user.dir"));

        Path store = root ;
        //Path root = (Path) prop.get("ROOT_DIR");
        //Path store = (Path) prop.get("STORE_DIR");
        int lease = Integer.parseInt((String)prop.get("DEFAULT_LEASE_TIME"));
        int id = Integer.parseInt((String)prop.get("ID_LENGTH"));
        int port = Integer.parseInt((String)prop.get("SERVER_PORT"));
        String charset = (String) prop.get("CHARSET");

        ServerConfiguration config = new ServerConfiguration(root, store, lease, id, port, charset);

        return config;
    }

    private String directorify(String dirPath){
        if(dirPath.endsWith("/") == false)
            dirPath += "/";
        return dirPath;
    }
}