package com.company.config.unleash.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.logging.Logger;

public class UnleashSocketConnector {
    private static final Logger logger = Logger.getLogger(UnleashSocketConnector.class.getName());

    private final Socket socket;

    public UnleashSocketConnector() {
        socket = new Socket();
    }

    public boolean isUnleashServerReachable(URI unleashApi) {
        String host = unleashApi.getHost();
        int port = unleashApi.getPort();
        int timeout = 2000; // Timeout of 2 seconds

        try {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            logger.severe("Can't open socket connection: " + e);
            closeSocket();
            return false;
        }
    }

    public void closeSocket() {
        try {
            logger.info("Closing socket connection");
            socket.close();
        } catch (IOException e) {
            logger.severe("Cant close the socket connection: " +e);
        }
    }
}

