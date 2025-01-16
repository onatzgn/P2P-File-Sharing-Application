package p2p.file;

public class ChunkInfo {
    
    private String fileName;
    private long downloadedBytes;
    private long totalBytes;
    
    public ChunkInfo(String fileName, long downloadedBytes, long totalBytes) {
        this.fileName = fileName;
        this.downloadedBytes = downloadedBytes;
        this.totalBytes = totalBytes;
    }
    
    public String getFileName() {
        return fileName;
    }
    public long getDownloadedBytes() {
        return downloadedBytes;
    }
    public long getTotalBytes() {
        return totalBytes;
    }
    
    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }
    
    public double getProgressPercent() {
        if(totalBytes == 0) return 0;
        return (downloadedBytes * 100.0) / totalBytes;
    }
}