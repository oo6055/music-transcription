package com.example.betaversion;

/**
 * The MusicNoteCircle class
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  help to the viewer
 */
public class MusicNoteCircle {
    private float x;
    private float y;
    private char special;

    /**
     * Instantiates a new Music note circle.
     *
     * @param x       the x cord
     * @param y       the y cord
     * @param sprcial the sprcial
     */
    MusicNoteCircle(float x, float y, char sprcial)
    {
        this.x = x;
        this.y = y;
        this.special = sprcial;
    }

    /**
     * Gets sprcial.
     *
     * @return the sprcial
     */
    public char getSpecial() {
        return this.special;
    }

    /**
     * Sets sprcial.
     *
     * @param sprcial the sprcial
     */
    public void setSpecial(char sprcial) {
        this.special = sprcial;
    }

    /**
     * Gets y.
     *
     * @return the y cord
     */
    public float getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y cord
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets x.
     *
     * @return the x cord
     */
    public float getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x cord
     */
    public void setX(float x) {
        this.x = x;
    }
}
