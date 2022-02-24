import glob
import pickle
import random
import os
import numpy
import music21
from midi2audio import FluidSynth

from collections import defaultdict
from mido import MidiFile
from pydub import AudioSegment
from pydub.generators import Sine


def midi_to_wav(path, soundfont =r"C:\music-transcription\transcipt\HedSound-Accordeon5c.sf2"):
    fs = FluidSynth(sound_font=soundfont, sample_rate=22050)
    #
    fs.midi_to_audio(path, path[:path.find(".")] + ".wav")


def genrateRandomStream() -> music21.stream.Stream:
    """
    this func create a stream of section
    :return: tuple (music stream, transcription)
    """
    s = music21.stream.Stream()
    len_of_seq = random.randrange(3,10)
    notes = []

    for i in range(len_of_seq):
        randomNote = 12 + random.randrange(0,88-12)
        note = music21.note.Note(randomNote)
        d = music21.duration.Duration(random.randrange(2,3))
        note.duration = d
        s.append(note)
        notes.append(note.nameWithOctave)

    return s, notes


def create_midi(path):
    """
    create midi and convert it to wav
    :param num: the num of the file
    :return: transcript, name of file
    """
    stream, transcript = genrateRandomStream()
    mf = music21.midi.translate.streamToMidiFile(stream)

    mf.open(str(path) + '.mid', 'wb')
    mf.write()
    mf.close()
    midi_to_wav(str(path) + '.mid')
    os.remove(str(path) + '.mid')
    return transcript,str(path) + '.wav'


def gerate_data_dir(dir, instument_id, set_id, num_of_files):
    start_path = dir +"/" + str(instument_id)
    try:
        os.mkdir(start_path)
    except:
        pass

    start_path = dir +"/" + str(instument_id) + "/" + str(set_id)

    try:
        os.mkdir(start_path)
    except:
        pass


    path = dir +"/" + str(instument_id) + "/"+ str(set_id) + "/"
    transcripts = open(path + str(instument_id) +"-" + str(set_id) + ".trans.txt", "w")
    for i in range(num_of_files):
        transcript, _ = create_midi(path + "/"+str(instument_id) + "-"+ str(set_id) + "-" + str(i).zfill(4))
        transcripts.write(str(instument_id) + "-"+ str(set_id) + "-" + str(i).zfill(4) + " " + " ".join(transcript) + "\n")
    transcripts.close()


def main():
    gerate_data_dir("data/train", "2", "22", 14)



if __name__ == "__main__":
    main()