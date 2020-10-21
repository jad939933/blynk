/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author dev
 */
public class TestFrame {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        System.out.println("Starting...");

        InetAddress myIP = InetAddress.getLocalHost();

        /* public String getHostAddress(): Returns the IP 
       * address string in textual presentation.
         */
        System.out.println("My IP Address is:");
        System.out.println(myIP.getHostAddress());

        ExecutorService exec = Executors.newFixedThreadPool(1);

        Blynk blynk = new Blynk();

        for (int i = 0; i < args.length; i++) {
            String p = args[i];
            int c = p.indexOf("=");
            if (c > 0) {
                String k = p.substring(0, c);
                if (k.startsWith("--")) {
                    String v = p.substring(c + 1);
                    System.out.println("config " + k + ": " + v);
                    System.setProperty(k.substring(2), v);
                }
            }
        }

        exec.submit(() -> {
            try {
                blynk.send("on");
                blynk.send("move", Coordinates.position(2));
                blynk.send("light", 20_000);
                blynk.send("move", Coordinates.position(1));
                blynk.sendAndGetResponse("off");

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
            }
        });

    }
}
