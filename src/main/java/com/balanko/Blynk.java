package com.balanko;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import jssc.SerialPort;
import jssc.SerialPortEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class C {

    CountDownLatch received = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(1);
    String value;
}

public class Blynk {

    private final LinkedHashMap<String, C> callbacks = new LinkedHashMap(100) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100;
        }
    };

    SerialPort port;

    private StringBuilder str = new StringBuilder();

    public Blynk() throws Exception {

        port = new SerialPort(System.getProperty("port"));

        System.out.println("port open: " + port.openPort());

        port.setParams(115200, 8, 1, 0, true, true);

        port.addEventListener((SerialPortEvent spe) -> {
            try {
                byte bytes[] = port.readBytes();
                if (bytes != null) {
                    for (byte b : bytes) {
                        if (b == 10) {
                            String e = str.toString().trim();
                            System.out.println(">>" + e);
                            if (e.startsWith("[")) {
                                if (e.startsWith("[complete ")) {
                                    String s[] = e.split(" ");
                                    String txid = s[1];
                                    C c = callbacks.get(txid);
                                    if (c != null) {
                                        c.value = e;
                                        c.completed.countDown();
                                    }
                                } else if (e.startsWith("[received ")) {
                                    String s[] = e.split(" ");
                                    String txid = s[1];
                                    C c = callbacks.get(txid);
                                    if (c != null) {
                                        c.value = e;
                                        c.received.countDown();
                                    }
                                } else if (e.startsWith("[error ")) {
                                    String s[] = e.split(" ");
                                    String txid = s[1];
                                    C c = callbacks.get(txid);
                                    if (c != null) {
                                        c.value = "error!!";
                                        c.received.countDown();
                                    }
                                }
                            }

                            str.delete(0, str.length() - 1);
                        } else {
                            str.append((char) b);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    long counter = 0;

    /**
     *
     * @param cmd
     * @param params
     * @return
     * @throws Exception
     */
    synchronized C send(String cmd, Object... params) throws Exception {

        String txid = Long.toHexString(counter++);

        C c = new C();
        callbacks.put(txid, c);

        StringBuilder command = new StringBuilder();
        command.append(txid);
        command.append(" ");
        command.append(cmd);
        if (params == null || params.length == 0) {
            params = new Object[]{"0", "0"};
        }
        for (Object param : params) {
            command.append(" ").append(param);
        }
        System.out.println("SEND: " + command);
        port.writeString(command.toString() + "\r\n");

//        c.received.await(1, TimeUnit.MINUTES);
        return c;

    }

    /**
     *
     * @param cmd
     * @param params
     * @return
     * @throws Exception
     */
    synchronized String
            sendAndGetResponse(String cmd, Object... params) throws Exception {
        C c = send(cmd, params);
        c.completed.await(5, TimeUnit.MINUTES);

        return c.value;
    }

    /**
     *
     * @throws Exception
     */
    public synchronized void close() throws Exception {
        System.out.println("closing...");
        port.closePort();
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            String p = args[i];
            int c = p.indexOf("=");
            if (c > 0) {
                String k = p.substring(0, c);
                if (k.startsWith("--")) {
                    String v = p.substring(c + 1);
                    System.out.println("Config " + k + ": " + v);
                    System.setProperty(k.substring(2), v);
                }
            }
        }

        Blynk b = new Blynk();
        b.send("light", 2000);
        b.send("light", 2000);
        System.out.println("REC:: " + b.sendAndGetResponse("light", 2000));

        b.send("move", 10000, 1000);
        b.send("move", 20000, 1000);
        System.out.println(b.sendAndGetResponse("move", 1000, 1200));

        System.out.println(b.sendAndGetResponse("home", 1000, 1200));

        Thread.sleep(15_000);

        b.close();
    }

}
