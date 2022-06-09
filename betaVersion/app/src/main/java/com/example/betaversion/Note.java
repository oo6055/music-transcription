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

    public static float takeFreqency(String note)
    {
        String[] notes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};

        if (note.contains("-"))
        {
            int indexOfNote = java.util.Arrays.asList(notes).indexOf(note.substring(0, note.length() - 2).toUpperCase()) - 1;
            String octave = note.length() == 3 ? String.valueOf(note.charAt(2)) : String.valueOf(note.charAt(1));
            note = indexOfNote < 0 ? notes[notes.length + indexOfNote] : notes[indexOfNote];
            note += octave;
        }
        int octave = note.length() == 3 ? Integer.parseInt(String.valueOf(note.charAt(2))) : Integer.parseInt(String.valueOf(note.charAt(1)));
        int keyNumber = java.util.Arrays.asList(notes).indexOf(note.substring(0, note.length() - 1).toUpperCase());

        if (keyNumber < 3) {
            keyNumber = keyNumber + 12 + ((octave - 1) * 12) + 1;
        } else {
            keyNumber = keyNumber + ((octave - 1) * 12) + 1;
        }

        // Return frequency of note
        return (float) (440 * Math.pow(2, (float)(keyNumber - 49) / 12));
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
