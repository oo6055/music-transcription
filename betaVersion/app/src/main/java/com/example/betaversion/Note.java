package com.example.betaversion;

public class Note {
    public double duration;
    public double freqency;
    public String name;

    public Note(String name, double duration, double freqency)
    {
        this.duration = duration;
        this.name = name;
        this.freqency = freqency;
    }

    public Note()
    {
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


    public void setDuration(double dur)
    {
         duration = dur;
    }

    public void setFreqency(double fre)
    {
        freqency = fre;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
