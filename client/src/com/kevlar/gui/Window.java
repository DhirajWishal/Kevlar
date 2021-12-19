package com.kevlar.gui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    /**
     * Default constructor.
     */
    public Window() {
        super("Kevlar");

        // This will make sure that the frame will get disposed when closed.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new CardLayout());
    }

    /**
     * Create a window with a custom title.
     *
     * @param title The title of the window.
     */
    public Window(String title) {
        super("Kevlar - " + title);

        // This will make sure that the frame will get disposed when closed.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Dispose the window.
     */
    public void close() {
        if (isVisible())
            dispose();
    }
}
