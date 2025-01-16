package p2p.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import p2p.file.ChunkInfo;
import p2p.file.DownloadManager;

public class DownloadProgressDialog extends JDialog {

    private JProgressBar progressBar;
    private DownloadManager downloadManager;
    private Timer downloadProgressTimer;

    public DownloadProgressDialog(JFrame parent, DownloadManager downloadManager) {
        super(parent, "Download Progress", true);
        this.downloadManager = downloadManager;

        setSize(400, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); 
        add(progressBar, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            stopDownloadProgressTimer();
            setVisible(false);
        });
        add(cancelButton, BorderLayout.SOUTH);

        startDownloadProgressTimer();
    }

    private void startDownloadProgressTimer() {
        downloadProgressTimer = new Timer(10, e -> { 
            Map<String, ChunkInfo> downloads = downloadManager.getDownloads();
            for (Map.Entry<String, ChunkInfo> entry : downloads.entrySet()) {
                String fileName = entry.getKey();
                ChunkInfo info = entry.getValue();
                double progress = info.getProgressPercent();
                
                progressBar.setValue((int) progress);

                if (progress >= 100.0) {
                    stopDownloadProgressTimer();
                    setVisible(false);
                    JOptionPane.showMessageDialog(this, "Download completed: " + fileName, "Download Complete", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        downloadProgressTimer.start();
    }

    private void stopDownloadProgressTimer() {
        if (downloadProgressTimer != null) {
            downloadProgressTimer.stop();
        }
    }
}