package p2p.app;

import p2p.network.P2PNetworkManager;
import p2p.file.FileManager;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Starting P2P Node in Backend Mode...");

        FileManager fileManager = new FileManager();
        P2PNetworkManager networkManager = new P2PNetworkManager(fileManager);

        networkManager.connect();
        System.out.println("Node connected. Listening for requests...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            networkManager.disconnect();
            System.out.println("Node disconnected.");
        }));
    }
}