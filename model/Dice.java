package model;

import java.util.Random;

/**
 * Dice represents a static pair of dice.
 */
public class Dice {
    /** Random number generator for all Dice. */
    private static Random RNG = new Random();
    /** Minimum possible number on a die. */
    private static int MIN_DICE_VALUE = 1;
    /** Maximum possible number on a die. */
    private static int MAX_DICE_VALUE = 6;

    /** The number of the first die. */
    public final int myDie1;
    /** The number of the second die. */
    public final int myDie2;
    
    /**
     * Constructs a die with the given numbers.
     * @param die1 The number of the first die.
     * @param die2 The number of the second die.
     */
    public Dice(int die1, int die2) {
        if (die1 < Dice.MIN_DICE_VALUE || die1 > Dice.MAX_DICE_VALUE
        || die2 < Dice.MIN_DICE_VALUE || die2 > Dice.MAX_DICE_VALUE) {
            throw new IllegalArgumentException("Die values passed were invalid");
        }
        
        this.myDie1 = die1;
        this.myDie2 = die2;
    }

    /**
     * Creates a new and random Dice object.
     * @return The Dice object.
     */
    public static Dice newRoll() {
        return new Dice(Dice.RNG.nextInt(Dice.MIN_DICE_VALUE, Dice.MAX_DICE_VALUE + 1), 
                        Dice.RNG.nextInt(Dice.MIN_DICE_VALUE, Dice.MAX_DICE_VALUE + 1));
    }

    /**
     * Gets the sum of the dice.
     * 
     * @return The sum of the dice.
     */
    public int getSum() {
        return this.myDie1 + this.myDie2;
    }
}