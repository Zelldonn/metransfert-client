package com.metransfert.client.controller;

import com.metransfert.client.Channel;
import com.metransfert.client.gui.GUI;
import com.metransfert.client.gui.UploadTabListener;

import java.util.ArrayList;

public class ClientController extends Address {
    private static ClientController instance;

    private GUI gui;

    private Channel mainChannel;

    private ArrayList<Address> addresses = new ArrayList<>();

    public ClientController(GUI g, Channel c) {
        super("MeTransfer", c.getIp(), c.getPort());
        if(instance == null)
            instance = this;
        else throw new RuntimeException("There cannot be more than one instance of MeTransfer client controller");
        mainChannel = c;
        gui = g;
        gui.getUploadTab().addUploadTabListener(new UploadTabListener() {
            @Override
            public void onUploadButtonClicked() {
                System.out.println("ButtonClicked");
            }
        });
    }

    public void populateDefaultAddresses(){
        Address s1 = new Address("MeTransfer official server", "metransfer.ddns.net", 7999);
        Address s2 = new Address("Localhost", "localhost", 7999);
        addresses.add(s1);
        addresses.add(s2);
    }
    public static ClientController getInstance() {
        return instance;
    }
}
