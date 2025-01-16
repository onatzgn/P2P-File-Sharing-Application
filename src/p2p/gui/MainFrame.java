package p2p.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

import p2p.file.ChunkInfo;
import p2p.file.DownloadManager;
import p2p.file.FileManager;
import p2p.network.P2PNetworkManager;

public class MainFrame extends JFrame {

    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu;
    private JMenuItem connectItem, disconnectItem, exitItem, aboutItem;
    
    private JLabel lblSharedFolder;
    private JTextField txtSharedFolder;
    private JButton btnChooseShared;
    
    private JLabel lblDestinationFolder;
    private JTextField txtDestinationFolder;
    private JButton btnChooseDestination;
    
    private JCheckBox chkOnlyRoot;
    private DefaultListModel<String> maskListModel;
    private JList<String> maskList;
    private JButton btnAddMask, btnDelMask;
    private JTextField txtNewMask;
    
    private DefaultListModel<String> folderListModel;
    private JList<String> folderList;
    private JButton btnAddFolder, btnDelFolder;
    private JTextField txtNewFolder;
    
    private JTextField txtSearch;
    private JButton btnSearch;
    
    private JTextArea txtAreaFoundFiles;
    private JTextArea txtAreaDownloadStatus;
    
    private P2PNetworkManager networkManager;
    private FileManager fileManager;
    private DownloadManager downloadManager;

    private JTable downloadTable;
    private DefaultTableModel downloadTableModel;

    public MainFrame() {
        super("P2P File Sharing Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        fileManager = new FileManager();
        networkManager = new P2PNetworkManager(fileManager);
        downloadManager = new DownloadManager();
        
        initMenu();
        initUI();
    }
    
    private void initMenu() {
        menuBar = new JMenuBar();
        
        fileMenu = new JMenu("Files");
        helpMenu = new JMenu("Help");
        
        connectItem = new JMenuItem("Connect");
        connectItem.addActionListener((ActionEvent e) -> onConnect());
        
        disconnectItem = new JMenuItem("Disconnect");
        disconnectItem.addActionListener((ActionEvent e) -> onDisconnect());
        
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener((ActionEvent e) -> onExit());
        
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener((ActionEvent e) -> onAbout());
        
        fileMenu.add(connectItem);
        fileMenu.add(disconnectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void initUI() {
        getContentPane().setLayout(new BorderLayout());
        
        JPanel topPanel = createTopPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel settingsPanel = createSettingsPanel();
        JPanel bottomPanel = createBottomPanel();
        
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(settingsPanel, BorderLayout.EAST);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        
        JPanel sharedPanel = new JPanel(new BorderLayout());
        lblSharedFolder = new JLabel("Root of the P2P shared folder: ");
        txtSharedFolder = new JTextField();
        btnChooseShared = new JButton("Set");
        btnChooseShared.addActionListener(e -> chooseSharedFolder());
        
        sharedPanel.add(lblSharedFolder, BorderLayout.WEST);
        sharedPanel.add(txtSharedFolder, BorderLayout.CENTER);
        sharedPanel.add(btnChooseShared, BorderLayout.EAST);
        
        JPanel destPanel = new JPanel(new BorderLayout());
        lblDestinationFolder = new JLabel("Destination folder: ");
        txtDestinationFolder = new JTextField();
        btnChooseDestination = new JButton("Set");
        btnChooseDestination.addActionListener(e -> chooseDestinationFolder());
        
        destPanel.add(lblDestinationFolder, BorderLayout.WEST);
        destPanel.add(txtDestinationFolder, BorderLayout.CENTER);
        destPanel.add(btnChooseDestination, BorderLayout.EAST);
        
        topPanel.add(sharedPanel);
        topPanel.add(destPanel);
        
        return topPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onSearch());
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        txtAreaFoundFiles = new JTextArea(8, 40);
        txtAreaFoundFiles.setEditable(false);
        JScrollPane scrollFound = new JScrollPane(txtAreaFoundFiles);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollFound, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        
        chkOnlyRoot = new JCheckBox("Check new files only in the root");
        chkOnlyRoot.addActionListener(e -> fileManager.setOnlyRootChecked(chkOnlyRoot.isSelected()));
        
        settingsPanel.add(chkOnlyRoot);
        settingsPanel.add(Box.createVerticalStrut(10));
        
        // exclude
        JPanel folderExcludePanel = new JPanel(new BorderLayout());
        folderExcludePanel.setBorder(BorderFactory.createTitledBorder("Exclude folders under these"));
        
        folderListModel = new DefaultListModel<>();
        folderList = new JList<>(folderListModel);
        JScrollPane folderScroll = new JScrollPane(folderList);
        
        JPanel folderButtonPanel = new JPanel(new GridLayout(2, 1));
        txtNewFolder = new JTextField();
        btnAddFolder = new JButton("Add");
        btnAddFolder.addActionListener(e -> {
            String folderName = txtNewFolder.getText().trim();
            if (!folderName.isEmpty()) {
                folderListModel.addElement(folderName);
                fileManager.addExcludedSubfolder(folderName);
                txtNewFolder.setText("");
            }
        });
        btnDelFolder = new JButton("Del");
        btnDelFolder.addActionListener(e -> {
            String selected = folderList.getSelectedValue();
            if (selected != null) {
                folderListModel.removeElement(selected);
                fileManager.removeExcludedSubfolder(selected);
            }
        });
        
        folderButtonPanel.add(btnAddFolder);
        folderButtonPanel.add(btnDelFolder);
        
        folderExcludePanel.add(txtNewFolder, BorderLayout.NORTH);
        folderExcludePanel.add(folderScroll, BorderLayout.CENTER);
        folderExcludePanel.add(folderButtonPanel, BorderLayout.EAST);
        
        settingsPanel.add(folderExcludePanel);
        settingsPanel.add(Box.createVerticalStrut(10));
        
        // filemask
        JPanel maskPanel = new JPanel(new BorderLayout());
        maskPanel.setBorder(BorderFactory.createTitledBorder("Exclude files matching these masks"));
        
        maskListModel = new DefaultListModel<>();
        maskList = new JList<>(maskListModel);
        JScrollPane maskScroll = new JScrollPane(maskList);
        
        JPanel maskButtonPanel = new JPanel(new GridLayout(2, 1));
        txtNewMask = new JTextField();
        btnAddMask = new JButton("Add");
        btnAddMask.addActionListener(e -> {
            String mask = txtNewMask.getText().trim();
            if (!mask.isEmpty()) {
                maskListModel.addElement(mask);
                fileManager.addExcludeMask(mask);
                txtNewMask.setText("");
            }
        });
        btnDelMask = new JButton("Del");
        btnDelMask.addActionListener(e -> {
            String selected = maskList.getSelectedValue();
            if (selected != null) {
                maskListModel.removeElement(selected);
                fileManager.removeExcludeMask(selected);
            }
        });
        
        maskButtonPanel.add(btnAddMask);
        maskButtonPanel.add(btnDelMask);
        
        maskPanel.add(txtNewMask, BorderLayout.NORTH);
        maskPanel.add(maskScroll, BorderLayout.CENTER);
        maskPanel.add(maskButtonPanel, BorderLayout.EAST);
        
        settingsPanel.add(maskPanel);
        
        return settingsPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Download Status"));
        
        txtAreaDownloadStatus = new JTextArea(4, 80);
        txtAreaDownloadStatus.setEditable(false);
        JScrollPane scrollDownloads = new JScrollPane(txtAreaDownloadStatus);
        
        bottomPanel.add(scrollDownloads, BorderLayout.CENTER);
        
        return bottomPanel;
    }
    
    // menubar
    private void onConnect() {
        networkManager.connect();
        JOptionPane.showMessageDialog(this, "Connected to P2P Overlay.");
    }
    
    private void onDisconnect() {
        networkManager.disconnect();
        JOptionPane.showMessageDialog(this, "Disconnected from P2P Overlay.");
    }
    
    private void onExit() {
        System.exit(0);
    }
    
    private void onAbout() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }
    
    
    private void chooseSharedFolder() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            txtSharedFolder.setText(selected.getAbsolutePath());
            fileManager.setSharedFolder(selected.getAbsolutePath());
        }
    }
    
    private void chooseDestinationFolder() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            txtDestinationFolder.setText(selected.getAbsolutePath());
            fileManager.setDestinationFolder(selected.getAbsolutePath());
        }
    }
    
    private void onSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter something to search.");
            return;
        }

        java.util.List<String> foundFiles = networkManager.searchFiles(query);

        txtAreaFoundFiles.setText("");
        for (String f : foundFiles) {
            txtAreaFoundFiles.append(f + "\n");
        }

        File[] localFiles = fileManager.listSharedFiles();
        txtAreaFoundFiles.append("\n--- Local Shared Files ---\n");
        Arrays.stream(localFiles).forEach(file ->
                txtAreaFoundFiles.append(file.getName() + "\n"));

        if (!foundFiles.isEmpty()) {
            String firstFile = foundFiles.get(0);
            txtAreaDownloadStatus.append("Started download: " + firstFile + "\n");

            long fileSize = networkManager.getFileSize(firstFile, "192.168.1.16", 5050); 
            if (fileSize > 0) {
                int totalChunks = (int) Math.ceil((double) fileSize / (256 * 1024)); 

                downloadManager.downloadFileInChunks(
                        firstFile,
                        totalChunks,
                        fileManager.getDestinationFolder(),
                        "255.255.255.255", // WRITE YOUR SOURCE IP
                        5050 
                );
                DownloadProgressDialog progressDialog = new DownloadProgressDialog(this, downloadManager);
                progressDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve file size from source node.");
            }
        }
    }
    
    private void initDownloadTable() {
        String[] columns = {"File Name", "Progress (%)"};
        downloadTableModel = new DefaultTableModel(columns, 0);
        downloadTable = new JTable(downloadTableModel);
        JScrollPane scrollPane = new JScrollPane(downloadTable);
        getContentPane().add(scrollPane, BorderLayout.SOUTH);
    }

    public void updateDownloadProgress(String fileName, double progress) {
        if (downloadTableModel == null) return;
        
        for (int i = 0; i < downloadTableModel.getRowCount(); i++) {
            if (downloadTableModel.getValueAt(i, 0).equals(fileName)) {
                downloadTableModel.setValueAt(progress, i, 1);
                return;
            }
        }
        downloadTableModel.addRow(new Object[]{fileName, progress});
    }
    
}