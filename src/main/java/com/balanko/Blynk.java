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

    CountDownLatch latch = new CountDownLatch(1);
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
                                        c.latch.countDown();
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
    synchronized C
            send(String cmd, Object... params) throws Exception {

        String txid = Long.toHexString(counter++);

        C c = new C();
        callbacks.put(txid, c);

        StringBuilder command = new StringBuilder();
        command.append(txid);
        command.append(" ");
        command.append(cmd);
        for (Object param : params) {
            command.append(" ").append(param);
        }
        System.out.println(">>" + command);
        port.writeString(command.toString());

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
        c.latch.await(5, TimeUnit.MINUTES);

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

    public static void main(String[] args) throws Exception {

        System.setProperty("port", "/dev/ttyACM0");

        Blynk b = new Blynk();
        System.out.println("REC:: " + b.sendAndGetResponse("light", 2000));

        Thread.sleep(15_000);

        b.close();
    }

}
