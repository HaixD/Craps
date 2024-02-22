package view.components;

import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import libraries.observer.Subject;

/**
 * LabeledTextField represents a JPanel that contains both a JTextField 
 * and a JLabel.
 */
public class LabeledTextField {
    /** The text for the JLabel. */
    private String label;
    /** The amount of space in the JTextField. */
    private int fieldColumns;
    /** 
     * The function to call whenever the keyTyped event triggers 
     * from the JTextField.
     */
    private Consumer<KeyEvent> callable;
    /** The JTextField of this LabeledTextField. */
    private JTextField textField;
    /** Whether or not the JTextField should be enabled when it is made. */
    private boolean queueEnable;
    
    /**
     * Constructs a LabeledTextField.
     * 
     * @param label The text for the JLabel.
     * @param fieldColumns The amount of space in the JTextField.
     */
    public LabeledTextField(String label, int fieldColumns) {
        this(label, fieldColumns, null);
    }

    /**
     * Constructs a LabeledTextField.
     * 
     * @param label The text for the JLabel.
     * @param fieldColumns The amount of space in the JTextField.
     * @param callable The function to call whenever the keyTyped event triggers.
     */
    public LabeledTextField(String label, int fieldColumns, Consumer<KeyEvent> callable) {
        this.label = label;
        this.fieldColumns = fieldColumns;
        this.callable = callable;
        this.queueEnable = true;
    }

    /**
     * Attach an observer to the given subject. Whenever the attached observer
     * is updated, the given string will replace the text in this 
     * LabeledTextField's JTextField.
     * 
     * @param subject The subject to attach the observer to.
     * @return This LabeledTextField.
     */
    public LabeledTextField attachStringObserver(Subject<String> subject) {
        subject.addObserver((String newText) -> this.textField.setText(newText));
        
        return this;
    }

    /**
     * Attach an observer to the given subject. Whenever the attached observer
     * is updated, the given integer will replace the text in this 
     * LabeledTextField's JTextField.
     * 
     * @param subject The subject to attach the observer to.
     * @return This LabeledTextField.
     */
    public LabeledTextField attachIntegerObserver(Subject<Integer> subject) {
        subject.addObserver((Integer newInteger) -> this.textField.setText(Integer.toString(newInteger)));
        
        return this;
    }

    /**
     * Attach an observer to the given subject. Whenever the attached observer
     * is updated, if the given boolean is True then the JTextField will be
     * enabled, and disabled otherwise.
     * 
     * @param subject The subject to attach the observer to.
     * @return This LabeledTextField.
     */
    public LabeledTextField attachEnablerObserver(Subject<Boolean> subject) {
        subject.addObserver((Boolean enable) -> this.textField.setEnabled(enable));

        return this;
    }

    /**
     * Makes the JTextField enabled/disabled when it is made. If True is passed
     * then the JTextField will be enabled, otherwise it is disabled.
     * 
     * @param enabled True if the JTextField should be enabled and false otherwise.
     * @return This LabeledTextField.
     */
    public LabeledTextField setEnabled(boolean enabled) {
        this.queueEnable = enabled;
        
        return this;
    }

    /**
     * Creates the JPanel with the JLabel and JTextField and adds it to
     * the given parent.
     * 
     * @param parent The parent to add this LabledTextField to.
     * @return The parent.
     */
    public JPanel build(JPanel parent) {
        this.textField = new JTextField(this.fieldColumns);
        this.textField.setEnabled(this.queueEnable);

        if (this.callable != null) {
            this.textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    callable.accept(e);
                }
            });
        } else {
            this.textField.setEditable(false);
            this.textField.setFocusable(false);
        }
        
        parent.add(new JLabel(this.label));
        parent.add(this.textField);

        return parent;
    }
    
    /**
     * Creates the JPanel with the JLabel and JTextField and adds it to
     * a new parent.
     * 
     * @return The parent.
     */
    public JPanel build() {
        return this.build(new JPanel(new GridBagLayout()));
    }
}
