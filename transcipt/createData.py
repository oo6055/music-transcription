import glob
import pickle
import random
import os
import music21
from model import MusicRecognitionModel
from torch import nn as nn
from midi2audio import FluidSynth
import torch
import dataTransform
import torchaudio
from collections import defaultdict
from mido import MidiFile
from pydub import AudioSegment
from pydub.generators import Sine


def midi_to_wav(path, soundfont =r"C:\music-transcription\transcipt\Essential Keys-sforzando-v9.6.sf2"):
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
        # there is 87 inputs
        randomNote = 24 + random.randrange(84)
        note = music21.note.Note(randomNote)
        d = music21.duration.Duration(random.randrange(2,4))
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

def duplicate_model():
    hparams = {
        "n_cnn_layers": 6,
        "n_class": 86,
        "n_feats": 128,
        "stride": 2,
        "n_rnn_layers": 2,
        "rnn_dim": 512,
        "momentum": 0.9,
        "dropout": 0.1,
    }

    use_cuda = torch.cuda.is_available()
    torch.manual_seed(7)
    device = torch.device("cuda" if use_cuda else "cpu")

    model = MusicRecognitionModel(
        n_cnn_layers=hparams['n_cnn_layers'], rnn_dim=hparams['rnn_dim'], n_rnn_layers=hparams['n_rnn_layers'],
        n_class=hparams['n_class'], n_feats=hparams['n_feats'], stride=hparams['stride'],
        dropout=hparams['dropout']).to(device)

    print(model)
    model = model.to(device)
    print('Num Model Parameters', sum([param.nelement() for param in model.parameters()]))
    model.load_state_dict(torch.load("model3.pth"))

    model.eval()

    _data = load_audio_item(r"C:\music-transcription\transcipt\data\test\20\5\20-5-0001.wav",
                            'C3 F4 G1 C#6 C#4 G#1 E7 E-3')
    _data = dataTransform.data_processing([_data], "valid")

    spectrograms, labels, input_lengths, label_lengths = _data
    spectrograms, labels = spectrograms.to(device), labels.to(device)

    traced_script_module = torch.jit.trace(model, spectrograms)

    traced_script_module.save("model.pt")

def main():
    for i in range (1,10):
        for j in range(1, 6):
            gerate_data_dir("data/test", str(i), str(j), 95)

def load_audio_item(path, transcript):

    # Load audio
    waveform, sample_rate = torchaudio.load(path)



    return (
        waveform,
        sample_rate,
        transcript,
        0,
        0,
        0,
    )

if __name__ == "__main__":
    main()