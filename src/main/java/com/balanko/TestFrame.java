/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import java.lang.ModuleLayer.Controller;
import java.net.InetAddress;
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
