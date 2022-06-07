package com.example.betaversion;

import java.util.Dictionary;
import java.util.Hashtable;

public class Note {
    public float duration;
    public float freqency;
    public String name;
    private Dictionary notesAndFrequ = new Hashtable();

    public Note(String name, float duration, float freqency) {
        this.duration = duration;
        this.name = name;
        this.freqency = freqency;
    }

    public Note() {
    }

    public static float takeFreqency(String name)
    {
        return 440;
    }

    @Override
    public String toString() {
        return "Note{" +
                "duration=" + duration +
                ", freqency=" + freqency +
                ", name='" + name + '\'' +
                '}';
    }

    public double getDuration()
    {
        return duration;
    }

    public double getFreqency()
    {
        return freqency;
    }

    public String getName()
    {
        return name;
    }


    public void setDuration(float dur)
    {
         duration = dur;
    }

    public void setFreqency(float fre)
    {
        freqency = fre;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
