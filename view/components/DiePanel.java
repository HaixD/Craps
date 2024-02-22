package view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * DiePanel represents a JPanel that is responsible for drawing a die.
 */
public class DiePanel extends JPanel {
    /**
     * The appearance of the die when it is a 1.
     * 
     * 0b110_010_000 would show:
     * 1 1 0
     * 0 1 0 the 1 indicates that there should be a circle
     * 0 0 0 whereas 0 means there is nothing drawn at that position.
     */
    private static final int ONE   = 0b000_010_000;
    /** The appearance of the die when it is a 2. */
    private static final int TWO   = 0b001_000_100;
    /** The appearance of the die when it is a 3. */
    private static final int THREE = 0b001_010_100;
    /** The appearance of the die when it is a 4. */
    private static final int FOUR  = 0b101_000_101;
    /** The appearance of the die when it is a 5. */
    private static final int FIVE  = 0b101_010_101;
    /** The appearance of the die when it is a 6. */
    private static final int SIX   = 0b101_101_101;

    /** The number shown on this DiePanel. */
    private int number;
    /** The preferred size of this DiePanel. */
    private final Dimension preferredSize;
    
    /**
     * Constructs a DiePanel.
     */
    public DiePanel() {
        super();

        this.number = 0;

        Dimension size = this.getSize();
        int preferredLength = Math.max(Math.min((int) size.getWidth(), (int) size.getHeight()), 50);

        this.preferredSize = new Dimension(preferredLength, preferredLength);
        super.setPreferredSize(this.preferredSize);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.setBorder(BorderFactory.createLineBorder(Color.black));
        
        super.paintComponent(g);

        int sectionSize = this.getSectionSize();
        int numberCopy = this.number;
        for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
            int row = numberCopy & 0b111;
            numberCopy >>= 3;

            for (int colIndex = 2; colIndex >= 0; colIndex--) {
                int col = row & 0b1;

                if (col == 1) {
                    g.drawOval(sectionSize * colIndex, sectionSize * (2 - rowIndex), sectionSize, sectionSize);
                }

                row >>= 1;
            }
        }
    }

    /**
     * Sets the number shown on this DiePanel and draws it.
     * 
     * @param number The new number.
     */
    public void setNumber(final int number) {
        switch (number) {
            case 0:
                this.number = 0;
                break;
            case 1:
                this.number = DiePanel.ONE;
                break;
            case 2:
                this.number = DiePanel.TWO;
                break;
            case 3:
                this.number = DiePanel.THREE;
                break;
            case 4:
                this.number = DiePanel.FOUR;
                break;
            case 5:
                this.number = DiePanel.FIVE;
                break;
            case 6:
                this.number = DiePanel.SIX;
                break;
            default:
                throw new IllegalArgumentException("Given number is not a valid die number");
        }

        this.paintComponent(super.getGraphics());
    }

    /**
     * Get the height/width of a single circle within the die.
     * 
     * @return The height/width of a single circle within the die.
     */
    private int getSectionSize() {
        return (int) (this.getSize().getWidth() / 3);
    }
}
