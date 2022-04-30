import tensorflow_hub as hub
import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
from scipy.io import wavfile
def output2hz(pitch_output):
  # Calibration constants
  PT_OFFSET = 25.58
  PT_SLOPE = 63.07
  FMIN = 10.0;
  BINS_PER_OCTAVE = 12.0;
  cqt_bin = pitch_output * PT_SLOPE + PT_OFFSET;
  return FMIN * 2.0 ** (1.0 * cqt_bin / BINS_PER_OCTAVE)


def audiofile_to_wave(filename):
  sr, wave = wavfile.read(filename)
  return wave.astype(np.float32)


def pitch_detection(wave:np.array) -> list:
  # Load the model
  model = hub.load("https://tfhub.dev/google/magenta/coconet/1")
  # Run the model
  pitch_output = model(tf.convert_to_tensor(wave))
  # Convert the output to Hz
  return output2hz(pitch_output)


wave = audiofile_to_wave(input("Enter the file name: "))
notes = pitch_detection(wave)
print(notes)

