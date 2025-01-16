package p2p.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import p2p.file.FileManager;

public class P2PNetworkManager {
    
    private boolean connected;
    private UDPFlooder flooder;
    private List<NodeInfo> knownNodes;
    private FileServer fileServer;
    private FileManager fileManager;
    
    private static final int FILE_SERVER_PORT = 5050; 
    
    public P2PNetworkManager(FileManager fileManager) {
        this.fileManager = fileManager;  
        
        this.flooder = new UDPFlooder(this.fileManager);
        this.knownNodes = new ArrayList<>();
        this.connected = false;
    }
    
    public void connect() {
        connected = true;
        // start server
        fileServer = new FileServer(fileManager, FILE_SERVER_PORT);
        fileServer.start();

        // start flooder
        flooder.startListening();
        System.out.println("P2PNetworkManager: connected.");
    }
    
    public void disconnect() {
        connected = false;
        // stop server
        if (fileServer != null) {
            fileServer.stopServer();
        }
        // stop flooder
        flooder.stopListening();
        System.out.println("P2PNetworkManager: disconnected.");
    }
    
    
    public boolean isConnected() {
        return connected;
    }

    public List<String> searchFiles(String query) {
        if (!connected) {
            System.out.println("Not connected to the P2P network.");
            return new ArrayList<>();
        }

        List<String> results = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            // search
            String searchRequest = "SEARCH:" + query;
            byte[] buffer = searchRequest.getBytes();

            // broadcast
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName("255.255.255.255"), 4445);
            socket.send(packet);
            System.out.println("Search request sent: " + query);

            socket.setSoTimeout(5000);
            byte[] responseBuffer = new byte[256];
            while (true) {
                try {
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                    socket.receive(responsePacket);
                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    if (response.startsWith("RESULT:")) {
                        String fileName = response.substring(7).trim();
                        results.add(fileName);
                        System.out.println("File found: " + fileName 
                                           + " from " + responsePacket.getAddress());
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
    public long getFileSize(String fileName, String sourceIp, int sourcePort) {
        try (Socket socket = new Socket(sourceIp, sourcePort);
             InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            String request = "GET_FILE_SIZE:" + fileName + "\n";
            output.write(request.getBytes());
            output.flush();

            byte[] buffer = new byte[1024];
            int bytesRead = input.read(buffer);
            if (bytesRead > 0) {
                String response = new String(buffer, 0, bytesRead).trim();
                if (response.startsWith("FILE_SIZE:")) {
                    return Long.parseLong(response.substring("FILE_SIZE:".length()).trim());
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve file size: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; 
    }
    
    public List<NodeInfo> getKnownNodes() {
        return knownNodes;
    }
}