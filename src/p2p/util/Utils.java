package p2p.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Utils {
    
    public static String getFileHash(File file) {
        if (file == null || !file.exists()) return null;
        
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean matchesMask(String fileName, String mask) {
        if (mask == null || mask.isEmpty()) return false;
        String regex = mask.replace(".", "\\.").replace("*", ".*");
        return fileName.matches(regex);
    }
}