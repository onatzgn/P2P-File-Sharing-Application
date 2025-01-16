package p2p.gui;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {
        super(parent, "About", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("P2P File Sharing Application");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel studentName = new JLabel("Developer: Onat Özgen");
        studentName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel studentNumber = new JLabel("Number: 20200702051");
        studentNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); 
        panel.add(studentName);
        panel.add(Box.createRigidArea(new Dimension(0, 5))); 
        panel.add(studentNumber);
        
        getContentPane().add(panel, BorderLayout.CENTER);
    }
}