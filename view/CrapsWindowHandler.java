package view;

import javax.swing.*;

import java.awt.*;

/**
 * CrapsWindowHandler manages a JFrame and main JPanel.
 */
public class CrapsWindowHandler {
    /** The JFrame this CrapsWindowHandler manages. */
    private final JFrame frame;
    /** The (main) JPanel this CrapsWindowHandler manages. */
    private final JPanel panel;

    /** The menu bar of the JFrame. */
    private final JMenuBar menuBar;

    /**
     * Constructs a CrapsWindowHandler object.
     * 
     * @param title The title to give the JFrame.
     */
    public CrapsWindowHandler(final String title) {
        this(title, new Dimension(0, 0));
    }
    
    /**
     * Constructs a CrapsWindowHandler object.
     * 
     * @param title The title to give the JFrame.
     * @param windowSize The size of the JFrame.
     */
    public CrapsWindowHandler(final String title, final Dimension windowSize) {
        this.frame = new JFrame(title);
        this.frame.setSize(windowSize);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.panel = new JPanel(new CardLayout());
        this.frame.add(this.panel);

        this.menuBar = new JMenuBar();
        this.frame.setJMenuBar(this.menuBar);
    }

    /**
     * Gets the JFrame.
     * 
     * @return The JFrame.
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Adds the given component to the main JPanel.
     * 
     * @param component The component to add.
     */
    public void addPanel(Component component) {
        this.panel.add(component);
    }

    /**
     * Adds a menu to the menu bar.
     * 
     * @param menu The menu to add.
     */
    public void addMenu(JMenu menu) {
        this.menuBar.add(menu);
    }
    
    /**
     * Renders the JFrame in the middle of the screen.
     */
    public void show() {
        Dimension frameDimension = this.frame.getSize();
        if (frameDimension.width == 0 || frameDimension.height == 0) {
            this.frame.pack();
        }

        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }
}
