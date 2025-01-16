package p2p.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import p2p.file.FileManager;

public class FileServer extends Thread {

    private static final int CHUNK_SIZE = 256 * 1024; 

    private ServerSocket serverSocket;
    private FileManager fileManager;
    private boolean running = true;

    public FileServer(FileManager fileManager, int listenPort) {
        this.fileManager = fileManager;
        try {
            serverSocket = new ServerSocket(listenPort);
            System.out.println("FileServer started on port " + listenPort + "...");
        } catch (IOException e) {
            e.printStackTrace();
            running = false; 
        }
    }

    @Override
    public void run() {
        if (serverSocket == null) {
            System.out.println("ServerSocket is null. Stopping FileServer thread.");
            return; 
        }
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                         new InputStreamReader(clientSocket.getInputStream()));
                 OutputStream out = clientSocket.getOutputStream()) {

                String requestLine = reader.readLine(); 
                if (requestLine != null) {
                    if (requestLine.startsWith("GET_CHUNK:")) {
                        handleChunkRequest(requestLine, out);
                    } else if (requestLine.startsWith("GET_FILE_SIZE:")) {
                        handleFileSizeRequest(requestLine, out);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleChunkRequest(String requestLine, OutputStream out) {
        try {
            String[] parts = requestLine.split(":");
            if (parts.length == 3) {
                String fileName = parts[1];
                int chunkIndex = Integer.parseInt(parts[2]);

                System.out.println("FileServer: GET_CHUNK request for " + fileName + 
                        ", chunkIndex=" + chunkIndex);

                File sharedFolder = new File(fileManager.getSharedFolder());
                File targetFile = new File(sharedFolder, fileName);

                if (targetFile.exists()) {
                    sendChunk(targetFile, chunkIndex, out);
                } else {
                    System.out.println("FileServer: file not found => " + targetFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFileSizeRequest(String requestLine, OutputStream out) {
        try {
            String fileName = requestLine.substring("GET_FILE_SIZE:".length()).trim();
            File sharedFolder = new File(fileManager.getSharedFolder());
            File targetFile = new File(sharedFolder, fileName);

            if (targetFile.exists() && targetFile.isFile()) {
                long fileSize = targetFile.length();
                String response = "FILE_SIZE:" + fileSize + "\n";
                out.write(response.getBytes());
                System.out.println("FileServer: Sent file size for " + fileName + ": " + fileSize + " bytes.");
            } else {
                String errorResponse = "ERROR:File not found\n";
                out.write(errorResponse.getBytes());
                System.out.println("FileServer: File not found for GET_FILE_SIZE request: " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendChunk(File file, int chunkIndex, OutputStream out) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = file.length();
            long chunkStart = (long) chunkIndex * CHUNK_SIZE;

            System.out.println("FileServer: Sending chunk " + chunkIndex + " of " + file.getName() +
                    " (start=" + chunkStart + ", length=" + fileLength + ")");

            if (chunkStart >= fileLength) {
                System.out.println("FileServer: Invalid chunk index. No data to send.");
                return;
            }

            raf.seek(chunkStart);
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead = raf.read(buffer);

            if (bytesRead > 0) {
                out.write(buffer, 0, bytesRead);
                out.flush();
                System.out.println("FileServer: Sent chunk " + chunkIndex + " of " + file.getName());
            }
        }
    }
    
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}