package com.example.betaversion;

public class MusicNoteCircle {
    private float x;
    private float y;
    private char special;

    MusicNoteCircle(float x, float y, char sprcial)
    {
        this.x = x;
        this.y = y;
        this.special = sprcial;
    }

    public char getSprcial() {
        return this.special;
    }

    public void setSprcial(char sprcial) {
        this.special = sprcial;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }
}
