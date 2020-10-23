/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import java.net.InetAddress;
import net.java.games.input.Controller;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

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

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//
//                    Event event = new Event();
//
//                    long start = 0;
//
//                    while (true) {
//
//                        if (joystick != null) {
//
//                            /* Remember to poll each one */
//                            joystick.poll();
//
//                            /* Get the controllers event queue */
//                            EventQueue queue = joystick.getEventQueue();
//
//                            while (queue.getNextEvent(event)) {
//
//                                start = System.currentTimeMillis();
//
//                                Component comp = event.getComponent();
//                                System.err.println(comp);
//                                switch (comp.getIdentifier().getName().toLowerCase()) {
//                                    case "x": {
//                                        x[0] = comp.getPollData();
//                                    }
//                                    break;
//                                    case "y": {
//                                        y[0] = comp.getPollData();
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//
//                        Thread.sleep(20);
//                        /**
//                         *
//                         */
//                        if (System.currentTimeMillis() - start > 20_000) {
//                            System.out.println("scanning for a joystick...");
//                            start = System.currentTimeMillis();
//                            Controller[] list = ControllerEnvironment.getDefaultEnvironment().getControllers();
//                            for (int i = 0; i < list.length; i++) {
//                                Controller c = list[i];
//                                if (c.getPortType().equals(Controller.PortType.UNKNOWN) && c.getName().equalsIgnoreCase("Wireless Controller")) {
//                                    System.err.println("Adding joystick " + c.getName());
//                                    joystick = list[i];
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }.start();
//
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//
//                    while (true) {
//                        blynk.send("on", String.valueOf((int) (x[0] * 1000)), String.valueOf((int) (y[0] * 1000)));
//                        Thread.sleep(100);
//                    }
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }.start();
        new Thread() {
            @Override
            public void run() {

                try {

                    /**
                     * start web server
                     */
                    Server server = new Server();

                    // Handler
                    server.setHandler(new WebHandler(blynk));

                    // HTTP Configuration
                    HttpConfiguration httpConfig = new HttpConfiguration();
                    // httpConfig.setOutputBufferSize(32 * 1024);
                    // httpConfig.setRequestHeaderSize(8 * 1024);
                    // httpConfig.setResponseHeaderSize(8 * 1024);
                    httpConfig.setSendServerVersion(false);
                    httpConfig.setSendDateHeader(false);

                    // === jetty-http.xml ===
                    ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
                    connector.setPort(8080);
                    connector.setIdleTimeout(30_000);
                    server.addConnector(connector);

                    server.start();
                    server.join();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

}
