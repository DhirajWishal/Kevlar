package com.kevlar;

import java.io.*;
import java.net.*;

public class Sender {
    private String response;

    /**
     * Constructor.
     * This will create a new socket and sill send a http request to the server.
     *
     * @param xml The xml data to send.
     * @throws MalformedURLException
     */
    public Sender(String xml) throws MalformedURLException {
        URL url = new URL("http://localhost");
        try (Socket socket = new Socket(url.getHost(), 2255)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("POST / HTTP/1.1");
            writer.println("Host: " + url.getHost());
            writer.println("Content-Type: text/xml");
            writer.println("Content-Length: " + xml.length());
            writer.println();
            writer.println(xml);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            response = "";

            String line;
            boolean shouldRecord = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("\n"))
                    shouldRecord = true;

                if (shouldRecord)
                    response += line + "\n";
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    /**
     * Get the response from the sender.
     *
     * @return The response including the HTTP header.
     */
    public String getResponse() {
        return response;
    }
}
