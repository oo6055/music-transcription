from music21 import note,stream,environment,converter
import pyrebase
import torch
import torch.optim as optim
import torchaudio
import torch.nn.functional as F
import dataTransform
from comet_ml import Experiment
import torch.utils.data as data
from dataTransform import TextTransform as tfClass
import os
import torch.nn as nn
from model import MusicRecognitionModel


firebaseConfig = {
  "apiKey": "AIzaSyA05G0tSIcWUvDePHl-ER1PROXyYN19FsI",
  "authDomain": "alpha-version-de63c.firebaseapp.com",
  "databaseURL": "https://alpha-version-de63c-default-rtdb.firebaseio.com",
  "projectId": "alpha-version-de63c",
  "storageBucket": "alpha-version-de63c.appspot.com",
  "messagingSenderId": "74190074805",
  "appId": "1:74190074805:web:85300a9cb40cc40ae35ffc",
  "measurementId": "G-T8L31BXF0R"
};

def string_to_notes(string):
    music_stream = stream.Stream()

    for i in range(0,len(string),2):
        n = note.Note(string[i] + string[i+1], quarterLength=1)
        music_stream.append(n)
    conv = converter.subConverters.ConverterLilypond()
    conv.write(music_stream, fmt='lilypond', fp='file', subformats=['pdf'])


firebase = pyrebase.initialize_app(firebaseConfig)

db=firebase.database()
storge = firebase.storage()


def stream_handler(message):
    print(message)

    notes = message['data']
    print(notes)
    if type(notes) is str:
        if str != "":
            string_to_notes(notes)
            storge.child("files/" + notes+ ".pdf").put("file.pdf")
    else:
        for seq in notes.values():
            string_to_notes(seq)
            storge.child("files/" + seq + ".pdf").put("file.pdf")

my_stream = db.child().stream(stream_handler)

# string_to_notes("C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4")