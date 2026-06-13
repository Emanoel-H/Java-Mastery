package br.com.javamastery.Tests;

import br.com.javamastery.models.Email;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class EmailMainScreen {
    public static void main(String[] args) {
        String sPassword, sEmail;
        Email email = new Email();
        FlatDarkLaf.setup();
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JLabel label = new JLabel("Enter Email:");
        JTextField field = new JTextField(20);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Login Screen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION)
            sEmail = field.getText();




        JPasswordField passwordField = new JPasswordField();

        int ok = JOptionPane.showConfirmDialog(
                null,
                passwordField,
                "Enter Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (ok == JOptionPane.OK_OPTION)
            sPassword = new String(passwordField.getPassword());

    }
}
