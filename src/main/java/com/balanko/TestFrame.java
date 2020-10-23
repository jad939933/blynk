/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import java.net.InetAddress;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

/**
 *
 * @author dev
 */
public class TestFrame {

    static Controller joystick = null;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        System.out.println("Starting...");
        InetAddress myIP = InetAddress.getLocalHost();
        System.out.println("ip " + myIP.getHostAddress());
        System.out.println("hostname sentry");

        for (int i = 0; i < args.length; i++) {
            String p = args[i];
            int c = p.indexOf("=");
            if (c > 0) {
                String k = p.substring(0, c);
                if (k.startsWith("--")) {
                    String v = p.substring(c + 1);
                    System.out.println("config " + k + "=" + v);
                    System.setProperty(k.substring(2), v);
                }
            }
        }

        Blynk blynk = new Blynk();

        float[] x = {0};
        float[] y = {0};

        new Thread() {
            @Override
            public void run() {
                try {

                    Event event = new Event();

                    long start = System.currentTimeMillis();

                    while (true) {

                        int eventCount = 0;

                        if (joystick != null) {

                            /* Remember to poll each one */
                            joystick.poll();

                            /* Get the controllers event queue */
                            EventQueue queue = joystick.getEventQueue();

                            while (queue.getNextEvent(event)) {

                                eventCount++;

                                Component comp = event.getComponent();
                                switch (comp.getIdentifier().getName().toLowerCase()) {
                                    case "x": {
                                        x[0] = comp.getPollData();
                                    }
                                    break;
                                    case "y": {
                                        y[0] = comp.getPollData();
                                    }
                                    break;
                                }
                            }
                        }

                        Thread.sleep(18);
                        /**
                         *
                         */
                        if (System.currentTimeMillis() - start > 10_000) {
                            if (eventCount == 0) {
                                Controller[] list = ControllerEnvironment.getDefaultEnvironment().getControllers();
                                for (int i = 0; i < list.length; i++) {
                                    Controller c = list[i];
                                    System.out.println(c.getPortType() + ":" + c.getName());
                                    if (c.getPortType().equals(Controller.PortType.UNKNOWN) && c.getName().equalsIgnoreCase("Wireless Controller")) {
                                        joystick = list[i];
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {

                    Event event = new Event();

                    while (true) {

                        blynk.send("move", String.valueOf((int) (x[0] * 1000)), String.valueOf((int) (y[0] * 1000)));

                        Thread.sleep(1_000);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

//           new Thread() {
//            @Override
//            public void run() {
//
//                try{
//                
//                    /**
//                     * start web server
//                     */
//                    Server server = new Server();
//
//                    // Handler
//                    server.setHandler(new WebHandler(Blynk.this));
//
//                    // HTTP Configuration
//                    HttpConfiguration httpConfig = new HttpConfiguration();
//                    // httpConfig.setOutputBufferSize(32 * 1024);
//                    // httpConfig.setRequestHeaderSize(8 * 1024);
//                    // httpConfig.setResponseHeaderSize(8 * 1024);
//                    httpConfig.setSendServerVersion(false);
//                    httpConfig.setSendDateHeader(false);
//
//                    // === jetty-http.xml ===
//                    ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
//                    connector.setPort(8080);
//                    connector.setIdleTimeout(30_000);
//                    server.addConnector(connector);
//
//                    server.start();
//                    server.join();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }.start();
    }

}
