package p2p.file;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class ChunkDownloader implements Runnable {

    private String sourceNodeIp;
    private int sourceNodePort; 
    private String fileName;
    private int chunkIndex;
    private File destinationFile;

    public ChunkDownloader(String sourceNodeIp, int sourceNodePort, 
                           String fileName, int chunkIndex, File destinationFile) {
        this.sourceNodeIp = sourceNodeIp;
        this.sourceNodePort = sourceNodePort;
        this.fileName = fileName;
        this.chunkIndex = chunkIndex;
        this.destinationFile = destinationFile;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(sourceNodeIp, sourceNodePort);
             InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            System.out.println("ChunkDownloader: Requesting chunk " + chunkIndex + 
                    " of file " + fileName + " from " + sourceNodeIp);
            
            String request = "GET_CHUNK:" + fileName + ":" + chunkIndex + "\n";
            output.write(request.getBytes());
            output.flush();

            try (RandomAccessFile raf = new RandomAccessFile(destinationFile, "rw")) {
                final int CHUNK_SIZE = 256 * 1024; // 256 kb
                long offset = (long) chunkIndex * CHUNK_SIZE;
                raf.seek(offset);
                
                System.out.println("ChunkDownloader: Writing chunk " + chunkIndex + 
                        " to file " + destinationFile.getAbsolutePath() + 
                        " at offset " + offset);

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    raf.write(buffer, 0, bytesRead);
                }

            }
            File destFile = new File(destinationFile.getAbsolutePath());
            if (destFile.exists() && destFile.length() > 0) {
                System.out.println("ChunkDownloader: File written successfully. Size: " + destFile.length());
            } else {
                System.out.println("ChunkDownloader: File write failed or is empty.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}