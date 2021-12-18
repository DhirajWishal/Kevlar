package com.kevlar;

import java.io.*;
import java.net.*;

public class Sender {
    private String head;
    private String server;
    private String date;
    private String content;
    private String length;
    private String response;

    /**
     * Constructor.
     * This will create a new socket and will send a http request to the server.
     *
     * @param xml       The xml data to send.
     * @param encrypted Whether or not the data is encrypted.
     * @throws MalformedURLException This constructor can throw a Malformed URL Exception.
     */
    public Sender(String xml, boolean encrypted) throws MalformedURLException {
        URL url = new URL("http://localhost");
        try (Socket socket = new Socket(url.getHost(), 2255)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("POST / HTTP/1.1");
            writer.println("Host: " + url.getHost());
            writer.println("Encrypted: " + ((encrypted) ? "1" : "0"));
            writer.println("Content-Type: text/xml");
            writer.println("Content-Length: " + xml.length());
            writer.println();
            writer.println(xml);

            //socket is an instance of Socket
            InputStream input = socket.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            //code to read and print headers
            head = bufferedReader.readLine();
            server = bufferedReader.readLine();
            date = bufferedReader.readLine();
            content = bufferedReader.readLine();
            length = bufferedReader.readLine();
            bufferedReader.readLine();

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
     * Get the head from the header.
     *
     * @return The header.
     */
    public String getHead() {
        return head;
    }

    /**
     * Get the server from the header.
     *
     * @return The server.
     */
    public String getServer() {
        return server;
    }

    /**
     * Get the date from the header.
     *
     * @return The date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the content type from the header.
     *
     * @return The content type.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the content length from the header.
     *
     * @return The content length.
     */
    public String getLength() {
        return length;
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
