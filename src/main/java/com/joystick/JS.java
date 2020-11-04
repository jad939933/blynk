/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joystick;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.lang.reflect.Constructor;
import java.util.Set;
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
                    System.err.println("config " + k + "=" + v);
                    System.setProperty(k.substring(2), v);
                }
            }
        }

        addr_64 = new XBee64BitAddress(System.getProperty("xbee.dest_addr_64"));

        uart = new XBeeDevice(System.getProperty("xbee.port"), Integer.parseInt(System.getProperty("xbee.baud_rate")));
        uart.open();

        System.err.println("UART open");

        uart.sendData(new RemoteXBeeDevice(uart, addr_64), "|ACC 0 4000|ACC 1 4000|".getBytes());

        Controller joystick = null;

        float x[] = {0}, y[] = {0}, ratio[] = {0};

        while (true) {

            long now = System.currentTimeMillis();

            if (joystick == null) {

                System.err.println("searching for a controller...");

                Controller[] controllers = createDefaultEnvironment().getControllers();

                for (int i = 0; i < controllers.length; i++) {
                    Controller c = controllers[i];
                    System.err.println(c.getName() + "...");
                    if (c.getName().equalsIgnoreCase(System.getProperty("joystick.name"))) {
                        joystick = c;
                        System.err.println(c.getName());
                        break;
                    }
                }

                if (joystick == null) {
                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } else {

                if (joystick.poll() == false) {
                    System.err.println("breaking...");
                    joystick = null;
                    continue;
                }

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
                        case "rz":
                            ratio[0] = value;
                            break;
                        default:
                            System.err.println(comp.getName() + ":" + value);
                            break;
                    }

                }

                send(x[0], y[0], ratio[0]);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }

    }

    /**
     *
     * @param x
     * @param y
     * @param ratio
     * @throws Exception
     */
    public static void send(float x, float y, float ratio) throws Exception {

        x *= 4000;
        y *= 4000;

        ratio = (ratio + 1) / 2; //normalize between zero (depressed) and one

        if (ratio == 0) {
            ratio = .05f;
        }

        x = x * ratio;
        y = y * ratio;

        String DATA_TO_SEND = "";
        if (x == 0) {
            DATA_TO_SEND += "|STP 0|";
        } else {
            DATA_TO_SEND += "|SPD 0 " + Math.max(20, (int) (Math.abs(x))) + "|MV 0 " + ((x > 0) ? "99999" : "-99999") + "|";
        }

        if (y == 0) {
            DATA_TO_SEND += "|STP 1|";
        } else {
            DATA_TO_SEND += "|SPD 1 " + Math.max(20, (int) (Math.abs(y))) + "|MV 1 " + ((y > 0) ? "99999" : "-99999") + "|";
        }

        if (lastCmd.equalsIgnoreCase(DATA_TO_SEND)) {
            return;
        }
        System.err.println(DATA_TO_SEND);
        lastCmd = DATA_TO_SEND;
//        myDevice.sendBroadcastData(dataToSend);
        uart.sendData(new RemoteXBeeDevice(uart, addr_64), DATA_TO_SEND.getBytes());
    }

    static String lastCmd = "";

    /**
     *
     * @return @throws ReflectiveOperationException
     */
    private static ControllerEnvironment createDefaultEnvironment() throws ReflectiveOperationException {

        final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (final Thread thread : threadSet) {
            final String name = thread.getClass().getName();
            if (name.equals("net.java.games.input.RawInputEventQueue$QueueThread")) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (final InterruptedException e) {
                    thread.interrupt();
                }
            }
        }

        // Find constructor (class is package private, so we can't access it directly)
        Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>) Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

        // Constructor is package private, so we have to deactivate access control checks
        constructor.setAccessible(true);

        // Create object with default constructor
        return constructor.newInstance();
    }
}
