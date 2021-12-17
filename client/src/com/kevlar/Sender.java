package com.kevlar;

import java.net.*;
import java.io.*;

public class Sender {
    /**
     * Constructor.
     * This will create a new socket and sill send a http request to the server.
     * @param xml The xml data to send.
     * @throws MalformedURLException
     */
    public Sender(String xml) throws MalformedURLException {
        URL url = new URL("localhost");
        try (Socket socket = new Socket(url.getHost(), 2255)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("POST " + url.getPath() + " HTTP/1.1");
            writer.println("Host: " + url.getHost());
            writer.println("User-Agent: Simple Http Client");
            writer.println("Accept: text/html");
            writer.println("Accept-Language: en-US");
            writer.println("Content-Type: text/xml");
            writer.println("Content-Length: " + xml.length());
            writer.println();
            writer.println(xml);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
