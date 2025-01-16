package p2p.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import p2p.util.Utils;

public class FileManager {

    private String sharedFolder;
    private String destinationFolder;
    
    private Set<String> excludeMasks;
    private Set<String> excludedSubfolders;
    private boolean onlyRootChecked;
    
    public FileManager() {
        // for docker
        this.sharedFolder = System.getenv("SHARED_FOLDER") != null ? System.getenv("SHARED_FOLDER") : System.getProperty("user.dir") + "/shared";
        this.destinationFolder = System.getenv("DESTINATION_FOLDER") != null ? System.getenv("DESTINATION_FOLDER") : System.getProperty("user.dir") + "/downloads";

        this.excludeMasks = new HashSet<>();
        this.excludedSubfolders = new HashSet<>();
        this.onlyRootChecked = false;

        // log
        System.out.println("FileManager Initialized");
        System.out.println("Shared Folder: " + sharedFolder);
        System.out.println("Destination Folder: " + destinationFolder);
    }
    
    public String getSharedFolder() {
        return sharedFolder;
    }

    public void setSharedFolder(String sharedFolder) {
        this.sharedFolder = sharedFolder;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }
    
    public Set<String> getExcludeMasks() {
        return excludeMasks;
    }
    
    public Set<String> getExcludedSubfolders() {
        return excludedSubfolders;
    }
    
    public boolean isOnlyRootChecked() {
        return onlyRootChecked;
    }
    
    public void setOnlyRootChecked(boolean onlyRootChecked) {
        this.onlyRootChecked = onlyRootChecked;
    }
    
    public void addExcludeMask(String mask) {
        excludeMasks.add(mask);
    }

    public void removeExcludeMask(String mask) {
        excludeMasks.remove(mask);
    }
    
    public void addExcludedSubfolder(String folderName) {
        excludedSubfolders.add(folderName);
    }

    public void removeExcludedSubfolder(String folderName) {
        excludedSubfolders.remove(folderName);
    }
    //filter
    public File[] listSharedFiles() {
        File folder = new File(sharedFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("FileManager: Shared folder does not exist or is not a directory.");
            return new File[0];
        }

        File[] files = folder.listFiles(file -> {
            System.out.println("FileManager: Checking file => " + file.getName());

            if (onlyRootChecked && file.isDirectory()) {
                System.out.println("FileManager: Skipping subfolder => " + file.getName());
                return false;
            }

            if (file.isDirectory() && excludedSubfolders.contains(file.getName())) {
                System.out.println("FileManager: Excluded subfolder => " + file.getName());
                return false;
            }

            for (String mask : excludeMasks) {
                if (Utils.matchesMask(file.getName(), mask)) {
                    System.out.println("FileManager: File excluded by mask => " + file.getName());
                    return false;
                }
            }

            return true;
        });
        //list
        System.out.println("FileManager: Shared files =>");
        for (File file : files) {
            System.out.println(" - " + file.getName());
        }

        return files;
    }
}