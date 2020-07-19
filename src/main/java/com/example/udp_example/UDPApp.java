package com.example.udp_example;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPApp {

    private static final Logger LOGGER = Logger.getLogger(UDPApp.class.getName());
    
    private static final int PORT = 52000;
    private static final String IP_ADDRESS = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private static final long READ_INTERNVAL = 50;
    
    private final ExecutorService udpServerExecutorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService readDataExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(final String[] args) {
        final UDPApp udpApp = new UDPApp();
        udpApp.foo();
    }
    
    public void foo() {
        
        try {
            
            final PipedOutputStream pipedOutputStream = new PipedOutputStream();
            final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
            
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(pipedInputStream);
            
            final UdpServer udpServer = new UdpServer(IP_ADDRESS, PORT, BUFFER_SIZE, pipedOutputStream);
            final UdpDataHandler udpDataHandler = new UdpDataHandler(BUFFER_SIZE, bufferedInputStream);
            
            udpServerExecutorService.execute(udpServer::start);
            readDataExecutorService.scheduleAtFixedRate(udpDataHandler::read, 
                    READ_INTERNVAL, READ_INTERNVAL, TimeUnit.MILLISECONDS);
            
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
