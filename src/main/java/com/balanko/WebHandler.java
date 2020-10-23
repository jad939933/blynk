/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

/**
 *
 * @author dev
 */
public class WebHandler extends AbstractHandler {

    private final Blynk blynk;

    /**
     *
     * @param blynk
     * @throws java.lang.Exception
     */
    public WebHandler(Blynk blynk) throws Exception {
        this.blynk = blynk;
    }

    /**
     *
     * @param requestPath
     * @param req
     * @param servletRequest
     * @param servletResponse
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(String requestPath, Request req, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException {

        try {

            if (requestPath.startsWith("/exit")) {

                System.exit(0);

                byte bytes[] = new byte[]{};

                servletResponse.setStatus(200);
                servletResponse.setContentLength(bytes.length);

                try (OutputStream out = servletResponse.getOutputStream()) {
                    out.write(bytes);
                    out.flush();
                }

            } else if (requestPath.startsWith("/move")) {

                int x = Integer.parseInt(req.getParameter("x"));
                int y = Integer.parseInt(req.getParameter("y"));

//                blynk.sendAndGetResponse("on");
                blynk.send("move", String.valueOf(x), String.valueOf(y));
//                blynk.sendAndGetResponse("off");

                JSONObject obj = new JSONObject();

                byte bytes[] = obj.toString().getBytes();

                servletResponse.setStatus(200);
                servletResponse.setContentLength(bytes.length);

                try (OutputStream out = servletResponse.getOutputStream()) {
                    out.write(bytes);
                    out.flush();
                }
            } else {
                throw new Exception("Unexpected " + requestPath);
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            servletResponse.setStatus(500);

            try (OutputStream out = servletResponse.getOutputStream()) {
                ex.printStackTrace(new PrintWriter(out));
                out.flush();
            }
        }
    }

}
