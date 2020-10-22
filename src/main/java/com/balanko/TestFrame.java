/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        ExecutorService exec = Executors.newFixedThreadPool(1);

        Blynk blynk = new Blynk();

        ControllerEnvironment.getDefaultEnvironment().addControllerListener(new ControllerListener() {
            @Override
            public void controllerRemoved(ControllerEvent ce) {
                System.out.println("removed controoller " + ce.getController().getName());

            }

            @Override
            public void controllerAdded(ControllerEvent ce) {
                System.out.println("added controoller " + ce.getController().getName());

                Controller c = ce.getController();
                if (c.getPortType().equals(Controller.PortType.UNKNOWN) && c.getName().equalsIgnoreCase("Wireless Controller")) {
                    joystick = ce.getController();
                }
            }
        });

        Controller[] list = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int i = 0; i < list.length; i++) {
            Controller c = list[i];
            System.out.println(c.getPortType() + ":" + c.getName());
            if (c.getPortType().equals(Controller.PortType.UNKNOWN) && c.getName().equalsIgnoreCase("Wireless Controller")) {
                joystick = list[i];
            }
        }

        new Thread() {
            @Override
            public void run() {
                try {

                    Event event = new Event();

                    while (true) {

                        if (joystick != null) {

                            /* Remember to poll each one */
                            joystick.poll();

                            /* Get the controllers event queue */
                            EventQueue queue = joystick.getEventQueue();

                            float x = -1;
                            float y = -1;

                            /* For each object in the queue */
                            while (queue.getNextEvent(event)) {
                                /* Get event component */
                                Component comp = event.getComponent();

                                System.err.println(comp.getIdentifier().getName() + ":" + comp.getPollData());

                                switch (comp.getIdentifier().getName().toLowerCase()) {
                                    case "x": {
                                        x = comp.getPollData();
                                    }
                                    break;
                                    case "y": {
                                        y = comp.getPollData();
                                    }
                                    break;
                                }
                            }

                            if (x != -1 && y != -1) {
                                blynk.send("acc", String.valueOf(x), String.valueOf(y));
                            }else if(x!=-1){
                                blynk.send("accx", String.valueOf(x));
                            }else if(y!=-1){
                                blynk.send("accy", String.valueOf(y));
                            }

                        }
                        Thread.sleep(18);
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
