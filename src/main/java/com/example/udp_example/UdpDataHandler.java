package com.example.udp_example;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpDataHandler {

    private static final Logger LOGGER = Logger.getLogger(UdpDataHandler.class.getName());
    
    private final int bufferSize;
    private final BufferedInputStream bufferedInputStream;
    
    public UdpDataHandler(final int bufferSize, final BufferedInputStream bufferedInputStream) {
        
        this.bufferSize = bufferSize;
        this.bufferedInputStream = Objects.requireNonNull(bufferedInputStream);
    }
    
    public synchronized void read() {
        
        byte[] readData;
        
        try {
            
            readData = new byte[bufferSize];
            
            bufferedInputStream.read(readData);
            
            System.out.println(Arrays.toString(readData));
            
        } catch (final IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
