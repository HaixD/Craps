package controller;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import libraries.observer.Subject;
import libraries.panellist.PanelList;
import libraries.panellist.PanelList.Direction;
import model.ObservedCrapsPlayer;
import model.WinState;
import view.CrapsWindowHandler;
import view.components.DiePanel;
import view.components.GameScreen;
import view.components.LabeledTextField;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

final public class Main {
    static final String TITLE = "Craps by Hai Duong";
    
    // THEME STANDARDS
    private static final Dimension WINDOW_SIZE = new Dimension(750, 500);

    private static final PanelList.Margin SMALL_BOTH_MARGIN = new PanelList.Margin(10, 10, 10, 10);
    private static final PanelList.Margin MINIMAL_VERTICAL_MARGIN = new PanelList.Margin(5, 0, 5, 0);

    private static final Dimension NORMAL_BUTTON_SIZE = new Dimension(200, 30);

    private static final int SHORT_TEXT_FIELD_COLS = 5;
    private static final int NORMAL_TEXT_FIELD_COLS = 7;
    private static final int LONG_TEXT_FIELD_COLS = 10;
    
    /**
     * Shows a JOptionPane asking if the user would like to exit and disposes
     * the crapsWindow JFrame if so.
     * 
     * @param crapsWindow The CrapsWindowHandler object to dispose.
     */
    /* default */ static void confirmQuitJOption(final CrapsWindowHandler crapsWindow) {
        int result = JOptionPane.showOptionDialog(
            null,
            "Are you sure you would like to quit?",
            "Warning",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new String[] {"Yes", "No"},
            "No"
            );

        if (result == 0) {
            crapsWindow.getFrame().dispose();
        }
    }

    /**
     * Prompts the user if they would like to restart.
     * 
     * @param player The player to restart.
     * @param clearTempData A callback to trigger during the restart process.
     */
    /* default */ static void restartPromptJOption(final ObservedCrapsPlayer player, final Runnable clearTempData) {
        int response = (player.getBank() > 0) ? JOptionPane.showConfirmDialog(
            null, 
            "Are you sure you would like to go Restart? (you will lose your progress)", 
            "Warning", 
            JOptionPane.OK_CANCEL_OPTION
        ) : 0;

        if (response == 0) {
            player.resetPlayer();
            clearTempData.run();
            Main.startPromptJOption(player);
        }
    }
    
    /**
     * Prompts the user for an amount to start their game/bank with.
     * 
     * @param player The player to call setBank on.
     */
    /* default */ static void startPromptJOption(final ObservedCrapsPlayer player) {
        int initialBank = -1;
        while (true) {
            String input = JOptionPane.showInputDialog("Enter a value to start with:");
            
            try {
                initialBank = Integer.parseInt(input);

                if (initialBank <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a positive integer");
                continue;
            }

            break;
        }

        player.reinitialize(initialBank);
    }
    
    /**
     * Shows the craps rules.
     */
    /* default */ static void rulesPromptJOption() {
        JOptionPane.showMessageDialog(null,
            "Two dice are thrown and their sum is calculated.\n"
          + "if the sum is 7 or 11 on the first throw then the roller wins.\n"
          + "if the sum is 2, 3, or 12 then the roller loses.\n"
          + "All other sums will become the roller's target.\n"
          + "The roller must keep rolling until they roll the target sum again without getting a sum of 7.\n"
          + "If a sum of 7 is obtained before the target then the roller loses.");
    }

    /**
     * Shows information regarding the creation of this project.
     */
    /* default */ static void aboutPromptJOption() {
        JOptionPane.showMessageDialog(null, 
            "This craps game project was made by Hai Duong.\n\n"
          + "Game Version: 1.0\n"
          + "Java Version: 21.0.1");
    }
    
    /**
     * Shows the extra keyboard shortcuts.
     */
    /* default */ static void controlsPromptJOption() {
        JOptionPane.showMessageDialog(null, 
            "Hotkeys:\n\n"
          + "alt o (perform initial dice roll with bet)\n"
          + "alt p (perform continued dice roll after game has started)" );
    }
    
    public static void main(String[] args) throws Exception {        
        // MODEL
        ObservedCrapsPlayer player = new ObservedCrapsPlayer();

        Subject<String> setEmptySubject = new Subject<>();
        Subject<Integer> setZeroSubject = new Subject<>();
        final Runnable clearTempData = () -> {
            setEmptySubject.update("");
            setZeroSubject.update(0);
        };
        
        Clip winClip = AudioSystem.getClip();
        Clip loseClip = AudioSystem.getClip();
        Clip rollClip = AudioSystem.getClip();
        
        winClip.open(AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("../assets/sound/win.wav")));
        loseClip.open(AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("../assets/sound/lose.wav")));
        rollClip.open(AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("../assets/sound/roll.wav")));
        
        player.getGameResultSubject().addObserver((WinState winState) -> {
            if (winState == WinState.WON) {
                winClip.setFramePosition(0);
                winClip.start();
                JOptionPane.showMessageDialog(
                    null, 
                    String.format("Congrats! You won $%d ($%d was added to your bank)", player.getMyLastBet(), 2 * player.getMyLastBet())
                );
                winClip.stop();
            } else if (winState == WinState.LOSS) {
                loseClip.setFramePosition(0);
                loseClip.start();
                JOptionPane.showMessageDialog(
                    null, 
                    String.format("Sorry! You lost $%d", player.getMyLastBet())
                );
                loseClip.stop();

                if (player.getBank() <= 0) {
                    JOptionPane.showMessageDialog(
                        null,
                        "It looks like you've lost all your money. Press the restart button if you want to restart"
                    );
                }
            }

            clearTempData.run();
        });

        // VIEW
        CrapsWindowHandler craps = new CrapsWindowHandler(Main.TITLE, Main.WINDOW_SIZE);

        // MAKE MENU
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic('G');

        JMenuItem gameMenuStart = new JMenuItem("Start");
        gameMenuStart.addActionListener(e -> Main.startPromptJOption(player));
        gameMenuStart.setMnemonic('S');

        JMenuItem gameMenuRestart = new JMenuItem("Restart");
        gameMenuRestart.addActionListener(e -> Main.restartPromptJOption(player, clearTempData));

        JMenuItem gameMenuExit = new JMenuItem("Exit");
        gameMenuExit.addActionListener(e -> Main.confirmQuitJOption(craps));
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.addActionListener(e -> Main.aboutPromptJOption());
        helpMenuAbout.setMnemonic('A');
        
        JMenuItem helpMenuRules = new JMenuItem("Help");
        helpMenuRules.addActionListener(e -> Main.rulesPromptJOption());

        JMenuItem helpMenuControls = new JMenuItem("Controls");
        helpMenuControls.addActionListener(e -> Main.controlsPromptJOption());

        craps.addMenu(gameMenu);

        gameMenu.add(gameMenuStart);
        gameMenu.add(gameMenuRestart);
        gameMenu.add(gameMenuExit);

        craps.addMenu(helpMenu);

        helpMenu.add(helpMenuAbout);
        helpMenu.add(helpMenuRules);
        helpMenu.add(helpMenuControls);

        // MAKE PANELS
        craps.addPanel(new GameScreen()
            .addTop("", // RESTART BUTTON
                new PanelList(new PanelList.Layout().complete()
                    .addComponentHandler(JButton.class, (Component component) -> {
                        JButton button = (JButton) component;

                        button.setPreferredSize(Main.NORMAL_BUTTON_SIZE);
                        button.setFocusPainted(false);
                        button.setEnabled(false);

                        player.getGameStateSubject().addObserver((Boolean started) -> button.setEnabled(!started && player.getBank() <= 0));
                        
                        return button;
                    })
                ).addComponent(new JButton("Restart"), e -> Main.restartPromptJOption(player, clearTempData))

            )
            .addTop("Win Totals", 
                new PanelList(new PanelList.Layout().complete())
                .addComponent(new LabeledTextField("Player Win Total: ", Main.LONG_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getGameWonSubject())
                    .build()
                )
                .addComponent(new LabeledTextField("House Win Total: ", Main.LONG_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getGameLossSubject())
                    .build()
                )
            )
            .addTop("Bank", 
                new PanelList(new PanelList.Layout().complete())
                .addComponent(new LabeledTextField("$ ", Main.NORMAL_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getBankSubject())
                    .build()
                )
            )
            .addBottom(null, // ROLL/REROLL BUTTON
                new PanelList(new PanelList.Layout().complete()
                    .title("")
                    .margin(Main.SMALL_BOTH_MARGIN)
                    .addTaggedComponentHandler("STARTBUTTON", (Component component) -> {
                        JButton button = (JButton) component;

                        button.setPreferredSize(Main.NORMAL_BUTTON_SIZE);
                        button.setFocusPainted(false);
                        button.setEnabled(false);
                        button.setMnemonic('O');

                        player.getGameStartableSubject().addObserver((Boolean startable) -> button.setEnabled(startable));

                        return button;
                    })
                    .addTaggedComponentHandler("ROLLBUTTON", (Component component) -> {
                        JButton button = (JButton) component;

                        button.setPreferredSize(Main.NORMAL_BUTTON_SIZE);
                        button.setFocusPainted(false);
                        button.setEnabled(false);
                        button.setMnemonic('P');

                        player.getGameStateSubject().addObserver((Boolean started) -> button.setEnabled(started));

                        return button;
                    })
                )
                .addComponent(new JButton("Start Game"), e -> {
                    if (player.getBet() == 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid bet before playing");
                        
                        return;
                    }

                    rollClip.setFramePosition(0);
                    rollClip.start();
                    player.startGame();
                }, "STARTBUTTON")
                .addComponent(new JButton("Roll Dice"), e -> { 
                    if (rollClip.isActive()) {
                        rollClip.stop();
                    }
                    rollClip.setFramePosition(0);
                    rollClip.start();
                    player.continueGame();
                }, "ROLLBUTTON")
                .addComponent(new LabeledTextField("Point: ", Main.SHORT_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getPointSubject())
                    .attachStringObserver(setEmptySubject)
                    .build()
                )
            )
            .addBottom("Current Roll",
                new PanelList(new PanelList.Layout()
                    .margin(Main.SMALL_BOTH_MARGIN)
                )
                .addComponent(new LabeledTextField("Die 1: ", Main.LONG_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getDie1Subject())
                    .attachStringObserver(setEmptySubject)
                    .build()
                )
                .addComponent(new LabeledTextField("Die 2: ", Main.LONG_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getDie2Subject())
                    .attachStringObserver(setEmptySubject)
                    .build()
                )
                .addComponent(new LabeledTextField("Total: ", Main.LONG_TEXT_FIELD_COLS)
                    .attachIntegerObserver(player.getDiceTotalSubject())
                    .attachStringObserver(setEmptySubject)
                    .build()
                )
                .addComponent(new PanelList(new PanelList.Layout().complete()
                        .margin(Main.SMALL_BOTH_MARGIN)
                        .direction(Direction.HORIZONTAL)
                        .addTaggedComponentHandler("DIE1", (Component component) -> {
                            DiePanel diePanel = (DiePanel) component;

                            player.getDie1Subject().addObserver((Integer value) -> diePanel.setNumber(value));
                            setZeroSubject.addObserver((Integer number) -> diePanel.setNumber(number));

                            return diePanel;
                        })
                        .addTaggedComponentHandler("DIE2", (Component component) -> {
                            DiePanel diePanel = (DiePanel) component;

                            player.getDie2Subject().addObserver((Integer value) -> diePanel.setNumber(value));
                            setZeroSubject.addObserver((Integer number) -> diePanel.setNumber(number));

                            return diePanel;
                        })
                    )
                    .addComponent(new DiePanel(), "DIE1")
                    .addComponent(new DiePanel(), "DIE2")
                    .build()
                )
            )
            .addBottom("Bet", 
                new PanelList(new PanelList.Layout().complete()
                    .margin(Main.MINIMAL_VERTICAL_MARGIN)
                    .addComponentHandler(JButton.class, (Component component) -> {
                        JButton button = (JButton) component;

                        button.setPreferredSize(Main.NORMAL_BUTTON_SIZE);
                        button.setFocusPainted(false);
                        button.setEnabled(false);

                        player.getGameStartableSubject().addObserver((Boolean startable) -> button.setEnabled(startable));
                        
                        return button;
                    })
                )
                .addComponent(new LabeledTextField("$ ", Main.NORMAL_TEXT_FIELD_COLS, (KeyEvent event) -> {
                    JTextField textField = (JTextField) event.getSource();
                    
                    if (textField.getText().isEmpty()) {
                        textField.setText("0");
                        player.setBet(0);
                    } else {
                        int amount = player.getBet();

                        if (Character.isDigit(event.getKeyChar())) {
                            amount = Integer.parseInt(textField.getText() + event.getKeyChar());
                        } else if ((int) event.getKeyChar() == 8 && !textField.getText().isEmpty()) { // handle backspace
                            amount = Integer.parseInt(textField.getText());
                        }

                        player.setBet(amount);
                    }

                    event.consume();
                })
                    .attachIntegerObserver(player.getBetSubject())
                    .attachEnablerObserver(player.getGameStartableSubject())
                    .setEnabled(false)
                    .build()
                )
                .addComponent(new JButton("+$1"), e -> player.incrementBet(1))
                .addComponent(new JButton("+$5"), e -> player.incrementBet(5))
                .addComponent(new JButton("+$10"), e -> player.incrementBet(10))
                .addComponent(new JButton("+$50"), e -> player.incrementBet(50))
                .addComponent(new JButton("+$100"), e -> player.incrementBet(100))
                .addComponent(new JButton("+$500"), e -> player.incrementBet(500))
            )
            .build()
        );

        // START GAME
        craps.show();
    }
}
