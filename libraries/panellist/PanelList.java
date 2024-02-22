package libraries.panellist;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * PanelList is a wrapper for GridBagLayout. It assumes all of its items should
 * have the same layout/format.
 */
public class PanelList {
    /** The components that are a part of this PanelList. */
    private final List<TaggedComponent> myPanelItems = new ArrayList<>();
    /** The layout of this PanelList. */
    private PanelList.Layout myLayout;
    
    /**
     * Constructs a PanelList with the given layout.
     * 
     * @param layout The layout of this PanelList.
     */
    public PanelList(Layout layout) {
        this.myLayout = layout.complete();
    }

    /**
     * Returns the layout of this PanelList.
     * 
     * @return The layout of this PanelList.
     */
    public PanelList.Layout getMyLayout() {
        return this.myLayout;
    }

    /**
     * Updates the layout of this PanelList with another layout. A Layout option
     * in this PanelList will not change if the option is not defined in the other
     * layout.
     * 
     * @param layout The layout to update this PanelList's layout with.
     */
    public void setMyLayout(final PanelList.Layout layout) {
        this.myLayout.update(Objects.requireNonNull(layout));
    }

    /**
     * Adds the given tagged component to this PanelList.
     * 
     * @param item The component to add.
     * @param tag The component's tag.
     * @return This PanelList.
     */
    public PanelList addComponent(Component item, String tag) {
        this.myPanelItems.add(new PanelList.TaggedComponent(item, tag));
        
        return this;
    }

    /**
     * Adds the given tagged component to this PanelList.
     * 
     * @param item The component to add.
     * @return This PanelList.
     */
    public PanelList addComponent(Component item) {
        return this.addComponent(item, null);
    }

    /**
     * Shorthand for adding JButton with a listener to this PanelList.
     * 
     * @param component The JButton to add.
     * @param listener The listener of the JButton.
     * @param tag The tag affiliated with the JButton.
     * @return This PanelList.
     */
    public PanelList addComponent(final JButton component, ActionListener listener, final String tag) {
        component.addActionListener(listener);
        this.addComponent((Component) component, tag);

        return this;
    }
    
    /**
     * Shorthand for adding JButton with a listener to this PanelList.
     * 
     * @param component The JButton to add.
     * @param listener The listener of the JButton.
     * @return This PanelList.
     */
    public PanelList addComponent(final JButton component, ActionListener listener) {
        component.addActionListener(listener);
        return this.addComponent((Component) component, null);
    }
    
    /**
     * Adds the given PanelList to this PanelList. The given PanelList will be built.
     * 
     * @param item The PanelList to add.
     * @return Tis PanelList.
     */
    public PanelList addComponent(PanelList item) {
        return this.addComponent(item.build(), null);
    }

    /**
     * Adds all of the stored components to the given JPanel.
     * 
     * @param parent The parent of the components to add.
     * @return The parent.
     */
    private JPanel buildComponents(final JPanel parent) {
        if (!parent.getLayout().getClass().equals(GridBagLayout.class)) {
            throw new IllegalArgumentException("Parent should be using GridBagLayout");
        }

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = this.myLayout.myFill.asGBC();

        switch (this.myLayout.myDirection) {
            case VERTICAL:
                constraints.gridx = 0;
                break;
            case HORIZONTAL:
                constraints.gridy = 0;
                break;
            case BOTH:
                break;
            case NONE:
                break;
        }

        switch (this.myLayout.myFillBeyond) {
            case VERTICAL:
                constraints.weighty = 1.0;
                break;
            case HORIZONTAL:
                constraints.weightx = 1.0;
                break;
            case BOTH:
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
                break;
            case NONE:
                break;
        }

        constraints.insets = new Insets(
            this.myLayout.myMargin.top, 
            this.myLayout.myMargin.left, 
            this.myLayout.myMargin.bottom, 
            this.myLayout.myMargin.right
        );

        for (TaggedComponent taggedComponent : this.myPanelItems) {
            if ((taggedComponent.myTag != null) && this.myLayout.myTaggedComponentHandler.containsKey(taggedComponent.myTag)) {
                taggedComponent.myComponent = this.myLayout.myTaggedComponentHandler.get(taggedComponent.myTag)
                    .apply(taggedComponent.myComponent);
            } else if (this.myLayout.myComponentHandler.containsKey(taggedComponent.myComponent.getClass())) {
                taggedComponent.myComponent = this.myLayout.myComponentHandler.get(taggedComponent.myComponent.getClass())
                    .apply(taggedComponent.myComponent);
            }

            parent.add(taggedComponent.myComponent, constraints);
        }

        return parent;
    }

    /**
     * Adds all of the stored components to the given JPanel.
     * 
     * @param parent The parent of the components to add.
     * @return The parent.
     */
    public JPanel build(final JPanel parent) {
        if (this.myLayout.myTitle != null) {
            parent.setBorder(BorderFactory.createTitledBorder(this.myLayout.myTitle));
        }
        
        return this.buildComponents(parent);
    }

    /**
     * Adds all of the stored components to a new JPanel parent.
     *  
     * @return The parent.
     */
    public JPanel build() {
        JPanel parent = new JPanel(new GridBagLayout());

        return this.build(parent);
    }

    /**
     * TaggedComponent represents a component that is linked to a String tag.
     */
    protected static class TaggedComponent {
        /** The Component this TaggedComponent wraps. */
        public Component myComponent;
        /** The tag affiliated with the component. */
        public String myTag;

        /**
         * Constructs a TaggedComponent.
         * 
         * @param component The Component this TaggedComponent wraps.
         * @param tag The tag affiliated with the component.
         */
        TaggedComponent(Component component, String tag) {
            this.myComponent = component;
            this.myTag = tag;
        }
    }

    /**
     * Direction represents a direction that something can expand in.
     */
    public static enum Direction {
        HORIZONTAL (GridBagConstraints.HORIZONTAL),
        VERTICAL   (GridBagConstraints.VERTICAL),
        NONE       (GridBagConstraints.NONE),
        BOTH       (GridBagConstraints.BOTH);

        /** The GridBagConstraints value */
        private int value;
        
        /**
         * Constructs a Direction.
         * 
         * @param value The GridBagConstraints value
         */
        private Direction(final int value) {
            this.value = value;
        }

        /**
         * Returns the GridBagConstraints value.
         * 
         * @return The GridBagConstraints value.
         */
        public int asGBC() {
            return this.value;
        }
    }

    /**
     * Cardinal represents a direction that something can go towards.
     */
    public static enum Cardinal {
        CENTER    (GridBagConstraints.CENTER),
        NORTH     (GridBagConstraints.NORTH),
        NORTHEAST (GridBagConstraints.NORTHEAST),
        EAST      (GridBagConstraints.EAST),
        SOUTHEAST (GridBagConstraints.SOUTHEAST),
        SOUTH     (GridBagConstraints.SOUTH),
        SOUTHWEST (GridBagConstraints.SOUTHWEST),
        WEST      (GridBagConstraints.WEST),
        NORTHWEST (GridBagConstraints.NORTHWEST);

        /** The GridBagConstraints value */
        private int value;

        /**
         * Constructs a Cardinal.
         * 
         * @param value The GridBagConstraints value
         */
        private Cardinal(final int value) {
            this.value = value;
        }

        /**
         * Returns the GridBagConstraints value.
         * 
         * @return The GridBagConstraints value.
         */
        public int asGBC() {
            return this.value;
        }
    }

    /**
     * Margin represents the spacing between a component and its neighbors.
     */
    public record Margin(int top, int left, int bottom, int right) {
        /**
         * Constructs a "nonexistent" Margin.
         */
        public Margin() {
            this(0, 0, 0, 0);
        }

        /**
         * Copy constructory for Margin.
         * 
         * @param other The Margin to copy.
         */
        public Margin(final Margin other) {
            this(other.top, other.left, other.bottom, other.right);
        }
    }

    /**
     * Layout represents the layout of a PanelList.
     */
    public static class Layout {
        /** The title of the border. */
        public String myTitle = null;
        
        /** THe direction to expand in. */
        public PanelList.Direction myDirection = null;
        /** The Margin of each component. */
        public PanelList.Margin myMargin = null;
        /** The direction for each item to fill. */
        public PanelList.Direction myFill = null;
        /** The direction for this PanelList to expand. */
        public PanelList.Direction myFillBeyond = null;

        /** The definition of each (type/class) tag. */
        public Map<Class<? extends Component>, Function<Component, Component>> myComponentHandler = null;
        /** The definition of each (String) tag. */
        public Map<String, Function<Component, Component>> myTaggedComponentHandler = null;

        /** 
         * Constructs a blank Layout object. 
         */
        public Layout() { }
                
        /** 
         * Copy constructor for Layout.
         */
        public Layout(final Layout other) {
            other.complete();
            
            this
                .title(other.myTitle)
                .direction(other.myDirection)
                .margin(other.myMargin.top, other.myMargin.left, other.myMargin.bottom, other.myMargin.right)
                .fill(other.myFill)
                .fillBeyond(other.myFillBeyond)
                .complete();
                
            this.myComponentHandler.putAll(other.myComponentHandler);
            this.myTaggedComponentHandler.putAll(other.myTaggedComponentHandler);
        }
        
        /**
         * Updates the layout with another layout. A Layout option
         * will not change if the option is not defined in the other
         * layout.
         * 
         * @param myLayout The layout to update this Layout with.
         * @return This Layout.
         */
        public Layout update(final Layout other) {
            if (other.myTitle != null) {
                this.myTitle = other.myTitle;
            }
            if (other.myDirection != null) {
                this.myDirection = other.myDirection;
            }
            if (other.myMargin != null) {
                this.myDirection = other.myDirection;
            }
            if (other.myFill != null) {
                this.myFill = other.myFill;
            }
            if (other.myFillBeyond != null) {
                this.myFillBeyond = other.myFillBeyond;
            }
            if (other.myComponentHandler != null) {
                this.myComponentHandler = other.myComponentHandler;
            }
            if (other.myTaggedComponentHandler != null) {
                this.myTaggedComponentHandler = other.myTaggedComponentHandler;
            }

            return this;
        }

        /**
         * Completes this Layout with valid non-null values. Updating another
         * Layout with this Layout will replace all of the other Layout's values
         * (with the exception of the tag definitions).
         * 
         * @return This Layout.
         */
        public Layout complete() {
            if (this.myDirection == null) {
                this.myDirection = PanelList.Direction.VERTICAL;
            }
            if (this.myMargin == null) {
                this.myMargin = new PanelList.Margin();
            }
            if (this.myFill == null) {
                this.myFill = PanelList.Direction.NONE;
            }
            if (this.myFillBeyond == null) {
                this.myFillBeyond = PanelList.Direction.NONE;
            }
            if (this.myComponentHandler == null) {
                this.myComponentHandler = new HashMap<>();
            }
            if (this.myTaggedComponentHandler == null) {
                this.myTaggedComponentHandler = new HashMap<>();
            }
            
            return this;
        }
        
        /**
         * Sets the direction of this Layout.
         * 
         * @param direction The new direction.
         * @return This Layout.
         */
        public Layout direction(final PanelList.Direction direction) {
            this.myDirection = direction;
            
            return this;
        }

        /**
         * Sets the margin of this Layout.
         * 
         * @param margin The new margin.
         * @return This Layout.
         */
        public Layout margin(final PanelList.Margin margin) {
            this.myMargin = margin;

            return this;
        }

        /**
         * Sets the margin of this Layout.
         * 
         * @param vertical The vertical margin
         * @param horizontal The horizontal margin.
         * @return This Layout.
         */
        public Layout margin(final int vertical, final int horizontal) {
            this.myMargin = new PanelList.Margin(
                vertical,
                horizontal,
                vertical,
                horizontal
            );

            return this;
        }

        /**
         * Sets the margin of this Layout.
         * 
         * @param top The top margin
         * @param left The left margin.
         * @param bottom The bottom margin.
         * @param right The right margin.
         * @return This Layout.
         */
        public Layout margin(final int top, final int left, final int bottom, final int right) {
            this.myMargin = new PanelList.Margin(top, left, bottom, right);

            return this;
        }

        /**
         * Sets the title of this Layout.
         * 
         * @param title The new title.
         * @return This Layout.
         */
        public Layout title(final String title) {
            this.myTitle = title;

            return this;
        }

        /**
         * Sets the fill direction of this Layout.
         * 
         * @param direction The fill direction.
         * @return This Layout.
         */
        public Layout fill(final PanelList.Direction direction) {
            this.myFill = direction;

            return this;
        }

        /**
         * Sets the fillBeyoud direction of this Layout.
         * 
         * @param direction The fillBeyoud direction.
         * @return This Layout.
         */
        public Layout fillBeyond(final PanelList.Direction direction) {
            this.myFillBeyond = direction;

            return this;
        }
        
        /**
         * Adds a new component/(type/class) tag definition/handler to this Layout.
         * 
         * @param <T> The type of the component.
         * @param component The class object of the component.
         * @param handler The handler of the component.
         * @return This Layout.
         */
        public <T extends Component> Layout addComponentHandler(Class<T> component, Function<Component, Component> handler) {
            this.myComponentHandler.put(component, handler);
            
            return this;
        }

        /**
         * Adds a new component component/(string) tag definition/handler to this layout.
         * 
         * @param tag The String tag of the component.
         * @param handler The handler of the component.
         * @return This Layout.
         */
        public Layout addTaggedComponentHandler(String tag, Function<Component, Component> handler) {
            this.myTaggedComponentHandler.put(tag, handler);

            return this;
        }
    }
}
