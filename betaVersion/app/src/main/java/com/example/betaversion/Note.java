package com.example.betaversion;

public class Note {
    private double duration;
    private double freqency;
    private String name;

    public Note(String name, double duration, double freqency)
    {
        this.duration = duration;
        this.name = name;
        this.freqency = freqency;
    }
}
