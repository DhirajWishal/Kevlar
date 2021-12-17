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
     * @throws MalformedURLException This constructor can throw a Malformed URL Exception.
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

            //socket is an instance of Socket
            InputStream input = socket.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            //code to read and print headers
            while ((bufferedReader.readLine()).length() != 0) ;

            //code to read the post payload data
            StringBuilder payload = new StringBuilder();
            while (bufferedReader.ready())
                payload.append((char) bufferedReader.read());
            response = payload.toString();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found!");
        } catch (IOException ex) {
            System.out.println("I/O error!");
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
