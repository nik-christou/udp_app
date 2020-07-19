package com.example.udp_example;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpServer {

    private static final Logger LOGGER = Logger.getLogger(UdpServer.class.getName());
    
    private final int port;
    private final String ipAddress;
    private final int bufferSize;
    
    private final AtomicBoolean receiveData = new AtomicBoolean(true);
    
    private final PipedOutputStream pipedOutputStream;
    
    private DatagramSocket datagramSocket;
    
    public UdpServer(final String ipAddress, 
            final int port, 
            final int bufferSize, 
            final PipedOutputStream pipedOutputStream) {
        
        this.port = port;
        this.bufferSize = bufferSize;
        this.ipAddress = Objects.requireNonNull(ipAddress);
        this.pipedOutputStream = Objects.requireNonNull(pipedOutputStream);
        
        connect();
    }
    
    private void connect() {
        
         try {
            
            final InetAddress inetAddress = Inet4Address.getByName(ipAddress);
            final InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
            
            datagramSocket = new DatagramSocket(socketAddress);
            
            LOGGER.log(Level.INFO, "UDP server started at address: [{0}] and port [{1}]", new Object[]{inetAddress.toString(), port});
            
        } catch (final SocketException | UnknownHostException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop() {
        receiveData.set(false);
        datagramSocket.close();
    }
    
    public void start() {
        receiveData.set(true);
        connect();
        receiveData();
    }
    
    private void receiveData() {
        
        byte[] buffer;
        DatagramPacket datagramPacket;

        while(receiveData.get()) {
              
            try {
                
                buffer = new byte[bufferSize];
                datagramPacket = new DatagramPacket(buffer, buffer.length);
                
                datagramSocket.receive(datagramPacket);
                
                pipedOutputStream.write(buffer, 0, buffer.length);
                
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
