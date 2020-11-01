/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;

public class bee {

    static OutputStream out;
    static InputStream in;

    public static void main(String[] args) throws Exception {

        SerialPort port = null;
        for (SerialPort p : SerialPort.getCommPorts()) {
            System.out.println(">>" + p);
            port = p;
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

            exec("+++");

            System.out.println(">>" + exec("AT\r"));

            System.out.println(">>" + exec("ATMY\r"));
            System.out.println(">>" + exec("ATSH\r"));
            System.out.println(">>" + exec("ATSL\r"));
            System.out.println(">>" + exec("ATID\r"));
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
