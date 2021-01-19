package com.metransfert.client.utils;

public class TransactionType {
    public static final byte PING = 0x0;

    public static final byte PONG = 0x1;

    public static final byte UPLOAD = 0x2;

    public static final byte FILE = 0x3;

    public static final byte DIRECTORY = 0x4;

    public static final byte NEXT_FILE = 0x12;

    public static final byte FILE_INFO = 0x5;

    public static final byte INFO_OK = 0x6;

    public static final byte INFO_ERR = 0x7;

    public static final byte DOWNLOAD = 0x8;
}
