package com.balanko;


import jssc.SerialPort;
import jssc.SerialPortEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class Blynk {

    public static void main(String[] args) throws Exception {

        String portName = "/dev/ttyACM1";

        SerialPort port = new SerialPort(portName);

        System.out.println(port.openPort());

        port.setParams(115200, 8, 1, 0, true, true);

        port.addEventListener((SerialPortEvent spe) -> {
            try {
//                System.out.println("received something");
                byte bytes[] = port.readBytes();
                if (bytes != null) {
                    System.out.println(">" + new String(bytes));
                } else {
                    System.out.println("null bytes");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

//        port.writeBytes("100 4000 5000\n".getBytes());
        port.writeBytes("200 4000\n\r".getBytes());

        Thread.sleep(15_000);

        port.closePort();
    }

}
