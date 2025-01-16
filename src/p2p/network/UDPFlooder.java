package p2p.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;

import p2p.file.FileManager;

public class UDPFlooder {
    
    private boolean listening = false;
    private Thread listenerThread;
    private FileManager fileManager;
    
    public UDPFlooder(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    public void startListening() {
        listening = true;
        listenerThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(4445)) {
                socket.setBroadcast(true);
                
                byte[] buf = new byte[1024];
                while(listening) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("UDPFlooder Received: " + received 
                                       + " from " + packet.getAddress()
                                       + ":" + packet.getPort());
                    
                    if(received.startsWith("P2P_DISCOVERY")) {
                    }
                    else if(received.startsWith("SEARCH:")) {
                        String query = received.substring("SEARCH:".length()).trim();
                        System.out.println("UDPFlooder: Processing SEARCH request for => " + query);

                        File[] sharedFiles = fileManager.listSharedFiles();
                        for(File f : sharedFiles) {
                            // query+
                            if(f.getName().toLowerCase().contains(query.toLowerCase())) {
                                String resultMsg = "RESULT:" + f.getName();
                                byte[] outBuf = resultMsg.getBytes();
                                DatagramPacket outPacket = new DatagramPacket(
                                    outBuf,
                                    outBuf.length,
                                    packet.getAddress(),
                                    packet.getPort()
                                );
                                socket.send(outPacket);
                                System.out.println("UDPFlooder: Sent RESULT => " + f.getName());

                            }
                        }
                    }
                    else if(received.startsWith("RESULT:")) {
                        String fileName = received.substring("RESULT:".length()).trim();
                        System.out.println("UDPFlooder: Found => " + fileName);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
        
        broadcastDiscovery();
    }
    
    public void stopListening() {
        listening = false;
        if(listenerThread != null) {
            listenerThread.interrupt();
        }
    }
    
    private void broadcastDiscovery() {
        String discoveryMsg = "P2P_DISCOVERY";
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = discoveryMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(
                buffer, 
                buffer.length,
                InetAddress.getByName("255.255.255.255"), 
                4445
            );
            socket.send(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}