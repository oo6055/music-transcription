package com.example.betaversion;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * the note pbject
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  it represents music note
 */
public class Note {
    private float duration;
    private float freqency;
    private String name;

    /**
     * Instantiates a new Note.
     *
     * @param name     the name
     * @param duration the duration
     * @param freqency the freqency
     */
    public Note(String name, float duration, float freqency) {
        this.duration = duration;
        this.name = name;
        this.freqency = freqency;
    }

    /**
     * Instantiates a new Note.
     */
    public Note() {
    }

    /**
     * return the frequency of a note.
     *
     * @param note the note
     * @return the float
     */
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

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public double getDuration()
    {
        return duration;
    }

    /**
     * Gets freqency.
     *
     * @return the freqency
     */
    public double getFreqency()
    {
        return freqency;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets duration.
     *
     * @param dur the dur
     */
    public void setDuration(float dur)
    {
         duration = dur;
    }

    /**
     * Sets freqency.
     *
     * @param fre the fre
     */
    public void setFreqency(float fre)
    {
        freqency = fre;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
