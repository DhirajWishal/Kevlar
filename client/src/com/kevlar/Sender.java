package com.kevlar;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;

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
     * @param xml      The xml data to send.
     */
    public Sender(String xml) {
        try {
            // Create the trust manager.
            TrustManager[] trustManager = new TrustManager[]{new TrustManager()};

            // Set up the ssl context.
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManager, new java.security.SecureRandom());

            // Create the ssl socket for communication.
            URL url = new URL("https://localhost");
            SSLSocketFactory factory = context.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(url.getHost(), 2255);

            // Start the handshake and set up the security attributes.
            socket.startHandshake();

            // Set up the output stream.
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // Write the http content.
            writer.println("POST / HTTP/1.1");
            writer.println("Host: " + url.getHost());
            writer.println("Content-Type: text/xml");
            writer.println("Content-Length: " + xml.length());
            writer.println();
            writer.println(xml);

            // Get the input stream from the socket.
            InputStream input = socket.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            // Read and print headers
            head = bufferedReader.readLine();
            server = bufferedReader.readLine();
            date = bufferedReader.readLine();
            content = bufferedReader.readLine();
            length = bufferedReader.readLine();
            bufferedReader.readLine();

            // Read the post payload data
            int toCopy = Integer.parseInt(length.replace("Content-Length: ", ""));
            char[] buffer = new char[toCopy];
            bufferedReader.read(buffer, 0, toCopy);
            response = String.valueOf(buffer);
        } catch (IOException | GeneralSecurityException ex) {
            ex.printStackTrace();
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
