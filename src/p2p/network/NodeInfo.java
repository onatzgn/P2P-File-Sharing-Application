package p2p.network;

public class NodeInfo {
    private String ipAddress;
    private int port;
    
    public NodeInfo(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public int getPort() {
        return port;
    }
    
    @Override
    public String toString() {
        return "NodeInfo [ip=" + ipAddress + ", port=" + port + "]";
    }
}