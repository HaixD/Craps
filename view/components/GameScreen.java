package view.components;

import javax.swing.JPanel;

import libraries.panellist.PanelList;

/**
 * 
 */
public class GameScreen extends PanelList {
    /** All panels in the top half of this GameScreen. */
    final private PanelList topHalf;
    /** All panels in the bottom half of this GameScreen. */
    final private PanelList bottomHalf;
    
    /**
     * Constructs a GameScreen object.
     */
    public GameScreen() {
        super(new PanelList.Layout()
            .fill(PanelList.Direction.BOTH)
            .fillBeyond(PanelList.Direction.BOTH)
            .complete()
        );

        this.topHalf = new PanelList(new PanelList.Layout()
            .direction(PanelList.Direction.HORIZONTAL)
            .fill(PanelList.Direction.BOTH)
            .fillBeyond(PanelList.Direction.BOTH)
            .complete()
        );
        this.bottomHalf = new PanelList(new PanelList.Layout()
            .direction(PanelList.Direction.HORIZONTAL)
            .fill(PanelList.Direction.BOTH)
            .fillBeyond(PanelList.Direction.BOTH)
            .complete()
        );
    }

    /**
     * Automatically sets the (border) name of the given PanelList if any.
     * 
     * @param name The name of the PanelList.
     * @param panelList The PanelList to be modified.
     */
    private void adjustPanelList(String name, PanelList panelList) {
        if (name != null) {
            panelList.getMyLayout()
                .title(name);
        }
    }

    /**
     * Adds the given PanelList to the top half of this GameScreen.
     * 
     * @param name The name of the PanelList (null if there shouldn't be a name).
     * @param panelList The PanelList to add.
     * @return This GameScreen.
     */
    public GameScreen addTop(String name, PanelList panelList) {
        this.adjustPanelList(name, panelList);
        
        this.topHalf.addComponent(panelList.build());
        
        return this;
    }

    /**
     * Adds the given PanelList to the bottom half of this GameScreen.
     * 
     * @param name The name of the PanelList (null if there shouldn't be a name).
     * @param panelList The PanelList to add.
     * @return This GameScreen.
     */
    public GameScreen addBottom(String name, PanelList panelList) {
        this.adjustPanelList(name, panelList);
        
        this.bottomHalf.addComponent(panelList.build());

        return this;
    }

    @Override
    public JPanel build() {
        super.addComponent(this.topHalf.build());
        super.addComponent(this.bottomHalf.build());

        return super.build();
    }
}
