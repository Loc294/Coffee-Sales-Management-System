package org.example;

import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.view.LoginForm;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}