/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;

public class bee {

    static OutputStream out;
    static InputStream in;

    public static void main(String[] args) throws Exception {
        String PORT = "/dev/ttyUSB0";
        int BAUD_RATE = 9600;

        String DATA_TO_SEND = "|ACC 1 1000|MV 1 1000|";

        XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
        byte[] dataToSend = DATA_TO_SEND.getBytes();

        myDevice.open();

        XBee64BitAddress addr_64 = new XBee64BitAddress("00 13 A2 00 40 30 21 BA".replace(" ", ""));

//        myDevice.sendBroadcastData(dataToSend);
        myDevice.sendData(new RemoteXBeeDevice(myDevice,
                addr_64), dataToSend);
        System.out.println(" >> Success");

        XBeePacket packet = new RemoteATCommandPacket(1, addr_64, new XBee16BitAddress("FFFE"), 0, "SL", new byte[]{});

        XBeePacket rec = myDevice.sendPacket(packet);

        System.out.println(rec.toPrettyString());
        System.out.println(rec.getParameters().get("Response"));

        System.exit(0);
    }

    public static void main3(String[] args) throws Exception {

        SerialPort port = null;
        for (SerialPort p : SerialPort.getCommPorts()) {
            System.out.println(">>" + p);
            if (p.toString().toLowerCase().contains("uart")) {
                port = p;
            }
        }

        if (port == null) {
            System.err.println("no port");
            System.exit(0);
        }

        if (port.openPort(2000)) {

//            port.setBaudRate(9600);
            System.err.println("port open");

            out = port.getOutputStream();

            in = port.getInputStream();

//            exec("+++");
////
//            System.err.println(exec("AT\r"));
//            for (String w : ("ATRE\nATID 3001\n"
//                    + "ATMY 1\n"
//                    + "ATDH 0\n"
//                    + "ATDL 2\n"
//                    + "ATWR\nATAP\n").split("\n")) {
//
//                System.out.println(">>" + w + " " + exec(w + "\r"));
//
//            }
//            for (String w : ("ATRE\nATID 3001\n"
//                    + "ATMY 2\n"
//                    + "ATDH 0\n"
//                    + "ATDL 1\n"
//                    + "ATWR\nATAP\n").split("\n")) {
//
//                System.out.println(">>" + w + " " + exec(w + "\r"));
//
//            }
////
////            System.out.println(">>" + exec("ATDL 403021BA\r"));
////            System.out.println(">>" + exec("ATSL\r"));
//            System.out.println(">>" + exec("ATMY\r"));
//            System.out.println(">>" + exec("ATAP\r"));
//            System.out.println(">>" + exec("ATSH\r"));
//            System.out.println(">>" + exec("ATSL\r"));
////            System.out.println(">>" + exec("ATWR\r"));
//            System.out.println(">>" + exec("ATAP 0\r"));
//            System.out.println(">>" + exec("ATWR\r"));
//            System.out.println(">>" + exec("ATVR\r"));
//            System.err.println(exec("ATCN\r"));
            System.err.println("resuming...");

            while (true) {
//                out.write("|ACC 0 1000|MV 0 1000|ACC 1 1000|MV 1 1000|\r\n".getBytes("UTF-8"));
                out.write("ABC 123\r\n".getBytes("UTF-8"));
                out.flush();
                Thread.sleep(5_000);

                System.err.println("+");

                int c = 0;
                while (in.available() > 0) {
                    System.err.print((char) in.read());
                    c++;
                }
                if (c > 0) {
                    System.err.println();
                }
            }
        }

    }

    /**
     *
     */
    static String exec(String cmd) throws Exception {

        out.write((cmd).getBytes());
        out.flush();

        if (cmd.equals("+++")) {
            Thread.sleep(3_000);
        } else {
            Thread.sleep(1000);
        }

        int r;
        r = in.read();

        StringBuilder res = new StringBuilder();
        while (r != -1 && r != '\r') {
            res.append((char) r);
            r = in.read();
        }

        return res.toString();
    }
}
