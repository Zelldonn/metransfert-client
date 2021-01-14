package com.metransfert.client.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ClientConfiguration {
    private Path uploadPath, downloadPath;
    private String theme;
    private Address address;

    public ClientConfiguration(Address address, String theme, Path uploadPath, Path downloadPath){
        this.address = address;
        this.theme = theme;
        this.uploadPath = uploadPath;
        this.downloadPath = downloadPath;
    }
    public static ClientConfiguration loadFromFile(Path config_file) throws FileNotFoundException {
        Properties prop = new Properties();

        InputStream is = new FileInputStream(config_file.toFile());

        try {
            prop.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String home = System.getProperty("user.home");
        Path uploadPath = Paths.get(home + "/Documents/");
        Path downloadPath = Paths.get(home + "/Downloads/");

        String uploadStr = (String)prop.get("UPLOAD_PATH");
        if(uploadStr.isEmpty())
            uploadPath = Paths.get(home);
        else
            uploadPath = Paths.get(uploadStr);

        String downloadStr = (String)prop.get("DOWNLOAD_PATH");
        if(uploadStr.isEmpty())
            downloadPath = Paths.get(home);
        else
            downloadPath = Paths.get(downloadStr);

        Address address = null;
        String addressStr = (String)prop.get("ADDRESS");
        if(addressStr == null  || addressStr.isEmpty()){
            address = new Address("Local", "localhost", 7999);
        }
        else
            address = resolveAddressFromString(addressStr);

        String theme = (String)prop.get("THEME");
        if(addressStr == null  || addressStr.isEmpty()){
            theme = "LIGHT";
        }

        ClientConfiguration config = new ClientConfiguration(address, theme, uploadPath, downloadPath);

        return config;
    }

    private static Address resolveAddressFromString(String address){
        String ip, name ;
        int port = 0;

        boolean isValidAddress = true;

        int index = address.indexOf(";");
        if(index == -1)
            isValidAddress = false;
        name = address.substring(0, index);

        int nextIndex = address.indexOf(":");
        if(index == -1)
            isValidAddress = false;
        ip = address.substring(index + 1, nextIndex);

        try {
            port = Integer.parseInt(address.substring(nextIndex + 1));
        }catch(NumberFormatException e){
            e.printStackTrace();
            isValidAddress = false;
        }

        if(!isValidAddress)
            return new Address("Local", "localhost", 7999);

        return new Address(name, ip, port);
    }

    public Path getDownloadPath() {
        return downloadPath;
    }

    public Path getUploadPath() {
        return uploadPath;
    }

    public String getTheme() {
        return theme;
    }

    public Address getAddress() {
        return address;
    }
}
