import torch
from torch import Tensor
from torch.utils.mobile_optimizer import optimize_for_mobile
import torchaudio
import torch.nn as nn
import numpy as np
from model import MusicRecognitionModel
import dataTransform

# Wav2vec2 model emits sequences of probability (logits) distributions over the characters
# The following class adds steps to decode the transcript (best path)
class Music_Recognize_new(torch.nn.Module):
    def __init__(self, model):
        super().__init__()
        self.model = model
        self.valid_audio_transforms = torchaudio.transforms.MelSpectrogram()
        self.labels = ["C1","C#1","D1","E-1","E1","F1","F#1","G1","G#1","A1","B-1","B1","C2","C#2","D2","E-2","E2","F2","F#2","G2","G#2","A2","B-2","B2","C3","C#3","D3","E-3","E3","F3","F#3","G3","G#3","A3","B-3","B3","C4","C#4","D4","E-4","E4","F4","F#4","G4","G#4","A4","B-4","B4","C5","C#5","D5","E-5","E5","F5","F#5","G5","G#5","A5","B-5","B5","C6","C#6","D6","E-6","E6","F6","F#6","G6","G#6","A6","B-6","B6","C7","C#7","D7","E-7","E7","F7","F#7","G7","G#7","A7","B-7","B7","<SPACE>"]

    def forward(self, waveforms: Tensor) -> str:
        """Given a single channel speech data, return transcription.
        Args:
            waveforms (Tensor): Speech tensor. Shape `[1, num_frames]`.
        Returns:
            str: The resulting transcript
        """
        waveforms = waveforms.sum(0)
        waveforms = waveforms[None, :]
        spec = [self.valid_audio_transforms(waveforms).squeeze(0).transpose(0, 1)]

        spectrograms = nn.utils.rnn.pad_sequence(spec, batch_first=True).unsqueeze(1).transpose(2, 3)
        logits = self.model(spectrograms)  # [batch, num_seq, num_label]
        best_path = torch.argmax(logits[0], dim=-1)  # [num_seq,]
        prev = ''
        hypothesis = ''
        for i in best_path:
            if i == 85:
                continue
            char = self.labels[i]

            if char == prev:
                continue

            hypothesis += char + ' '
            prev = char
        return hypothesis[0:-1]


# Load Wav2Vec2 pretrained model from Hugging Face Hub
model = torch.jit.load('model.pt')

# Remove weight normalization which is not supported by quantization.
model = model.eval()
# Attach decoder
model = Music_Recognize_new(model)
model2 = torch.jit.load('waveformToString.ptl')

# Apply quantization / script / optimize for motbile
quantized_model = torch.quantization.quantize_dynamic(
    model, qconfig_spec={torch.nn.Linear}, dtype=torch.qint8)
scripted_model = torch.jit.script(quantized_model)
optimized_model = optimize_for_mobile(scripted_model)


# Sanity check
waveform , _ = torchaudio.load(r"C:\music-transcription\transcipt\1-3-0001.wav")
print('Result:', model2(waveform))
print('Result:', optimized_model(waveform))


optimized_model._save_for_lite_interpreter("waveformToString.ptl")

# model2 = torch.jit.load("model21.pt")
# model = torch.jit.load('model.pt')
#
# model.eval()
# # Attach decoder
# new_model = Music_Recognize_new(model)
#
# # Sanity check
# waveform, _ = torchaudio.load(r"C:\music-transcription\transcipt\1-3-0001.wav")
#
#
# print('Result:', new_model(waveform))
#
# traced_script_module = torch.jit.trace(new_model, waveform)
#
# traced_script_module.save("model21.pt")