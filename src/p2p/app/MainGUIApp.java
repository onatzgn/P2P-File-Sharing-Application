package p2p.app;

import javax.swing.*;

import p2p.gui.MainFrame;

public class MainGUIApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}