import glob
import pickle
import numpy
import music21
from midi2audio import FluidSynth

from collections import defaultdict
from mido import MidiFile
from pydub import AudioSegment
from pydub.generators import Sine


def note_to_freq(note, concert_A=440.0):
    '''
    from wikipedia: http://en.wikipedia.org/wiki/MIDI_Tuning_Standard#Frequency_values
    '''
    return (2.0 ** ((note - 69) / 12.0)) * concert_A

def midi_to_wav(path):
    mid = MidiFile(path)
    output = AudioSegment.silent(mid.length * 1000.0)

    tempo = 100  # bpm
    # inner function
    def ticks_to_ms(ticks):
        tick_ms = (60000.0 / tempo) / mid.ticks_per_beat
        return ticks * tick_ms

    for track in mid.tracks:
        # position of rendering in ms
        current_pos = 0.0

        current_notes = defaultdict(dict)
        # current_notes = {
        #   channel: {
        #     note: (start_time, message)
        #   }
        # }

        for msg in track:
            current_pos += ticks_to_ms(msg.time)

            if msg.type == 'note_on':
                current_notes[msg.channel][msg.note] = (current_pos, msg)

            if msg.type == 'note_off':
                start_pos, start_msg = current_notes[msg.channel].pop(msg.note)

                duration = current_pos - start_pos

                signal_generator = Sine(note_to_freq(msg.note))
                rendered = signal_generator.to_audio_segment(duration=duration - 50, volume=-20).fade_out(100).fade_in(
                    30)

                output = output.overlay(rendered, start_pos)

    output.export("animal.wav", format="wav")

def get_notes():
    """ Get all the notes and chords from the midi files in the ./midi_songs directory """
    notes = []

    for file in glob.glob("midi/*.mid"):
        midi = music21.converter.parse(file)

        print("Parsing %s" % file)

        notes_to_parse = None

        try: # file has instrument parts
            s2 = music21.instrument.partitionByInstrument(midi)
            notes_to_parse = s2.parts[0].recurse()
        except: # file has notes in a flat structure
            notes_to_parse = midi.flat.notes
        print()
        for element in notes_to_parse:
            if isinstance(element, music21.note.Note):
                notes.append(str(element.pitch))
            elif isinstance(element, music21.chord.Chord):
                print(element.notes)
                notes.append('.'.join(str(n) for n in element.normalOrder))

    with open('data/notes', 'wb') as filepath:
        pickle.dump(notes, filepath)

    return notes
sc = music21.scale.PhrygianScale('g')
s = music21.stream.Stream()
s.append(music21.note.Note("A1"))
s.append(music21.note.Note("b1"))
s.append(music21.note.Note("c1"))
mf = music21.midi.translate.streamToMidiFile(s)
mf.open('midi.mid', 'wb')
mf.write()
mf.close()

# using the default sound font in 44100 Hz sample rate
fs = FluidSynth(sound_font='weedsgm3.sf2',sample_rate=22050)

fs.midi_to_audio(r"C:\music-transcription\transcipt\midi.mid", 'output.wav')

# FLAC, a lossless codec, is supported as well (and recommended to be used)