/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joystick;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

/**
 *
 * @author dev
 */
public class JS {

    private static XBeeDevice uart;

    private static XBee64BitAddress addr_64;

    public static void main(String[] args) throws Exception {

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

        addr_64 = new XBee64BitAddress(System.getProperty("xbee.dest_addr_64"));

        uart = new XBeeDevice(System.getProperty("xbee.port"), Integer.parseInt(System.getProperty("xbee.baud_rate")));
        uart.open();

        System.out.println("UART open");

        uart.sendData(new RemoteXBeeDevice(uart, addr_64), "|ACC 0 1000|ACC 1 1000|".getBytes());

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        Controller joystick = null;

        for (int i = 0; i < controllers.length; i++) {
            Controller c = controllers[i];
            System.err.println(c.getName() + "...");
            if (c.getName().equalsIgnoreCase(System.getProperty("joystick.name"))) {
                joystick = c;
                System.err.println(c.getName());
            }
        }

        if (joystick == null) {
            System.out.println("Found no controllers.");
            System.exit(0);
        }

        float x[] = {0}, y[] = {0};

        while (true) {

            joystick.poll();

            EventQueue queue = joystick.getEventQueue();

            Event event = new Event();

            while (queue.getNextEvent(event)) {

                Component comp = event.getComponent();
                float value = event.getValue();

                switch (comp.getName().toLowerCase()) {
                    case "x":
                        x[0] = value;
                        break;
                    case "y":
                        y[0] = value;
                        break;
                }

            }

            send(x[0], y[0]);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    static int lastX = 0, lastY = 0;

    public static void send(float x, float y) throws Exception {

        int _x = (int) (x * 512);
        int _y = (int) (y * 512);

        if (lastX == _x && lastY == _y) {
            //skip
            return;
        }

        lastX = _x;
        lastY = _y;

        System.err.println(">>" + _x + ":" + _y);

        String DATA_TO_SEND = "";
        if (_x == 0) {
            DATA_TO_SEND += "|STP 0|";
        } else {
            DATA_TO_SEND += "|SPD 0 " + Math.max(200, (int) (8 * Math.abs(_x))) + "|MV 0 " + ((_x > 0) ? "99999" : "-99999") + "|";
        }

        if (_y == 0) {
            DATA_TO_SEND += "|STP 1|";
        } else {
            DATA_TO_SEND += "|SPD 1 " + Math.max(200, (int) (8 * Math.abs(_y))) + "|MV 1 " + ((_y > 0) ? "99999" : "-99999") + "|";
        }

//        myDevice.sendBroadcastData(dataToSend);
        uart.sendData(new RemoteXBeeDevice(uart, addr_64), DATA_TO_SEND.getBytes());
    }
}
