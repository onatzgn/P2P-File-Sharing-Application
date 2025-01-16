package p2p.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownloadManager {

    private Map<String, ChunkInfo> downloads; // key:fileName - value:info
    
    public DownloadManager() {
        downloads = new HashMap<>();
    }
    

    public void downloadFileInChunks(String fileName,
                                     int totalChunks,
                                     String destinationFolder,
                                     String sourceIp,
                                     int sourcePort) {
        File destinationFile = new File(destinationFolder, fileName);
        long totalBytes = totalChunks * 256L * 1024L; // Her chunk 256KB
        downloads.putIfAbsent(fileName, new ChunkInfo(fileName, 0, totalBytes));

        for (int i = 0; i < totalChunks; i++) {
            int chunkIndex = i;
            Thread downloaderThread = new Thread(() -> {
                try {
                    new ChunkDownloader(sourceIp, sourcePort, fileName, chunkIndex, destinationFile).run();

                    synchronized (downloads) {
                        ChunkInfo info = downloads.get(fileName);
                        if (info != null) {
                            long newDownloadedBytes = info.getDownloadedBytes() + (256L * 1024L);
                            if (newDownloadedBytes > info.getTotalBytes()) {
                                newDownloadedBytes = info.getTotalBytes();
                            }
                            info.setDownloadedBytes(newDownloadedBytes);
                            System.out.println("DownloadManager: Updated progress for " + fileName +
                                    " - " + info.getProgressPercent() + "%");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("DownloadManager: Error downloading chunk " + chunkIndex +
                            " of file " + fileName);
                    e.printStackTrace();
                }
            });
            downloaderThread.start();
        }
    }
    
    public void checkDownloadedFile(String fileName, File destinationFile) {
        File downloadedFile = new File(destinationFile.getAbsolutePath());
        if (downloadedFile.exists() && downloadedFile.length() > 0) {
            System.out.println("DownloadManager: File downloaded successfully. Size: " + downloadedFile.length());
        } else {
            System.out.println("DownloadManager: Download failed or file is empty for " + fileName);
        }
    }

    public Map<String, ChunkInfo> getDownloads() {
        return downloads;
    }
}