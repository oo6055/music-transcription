import music21
import torchaudio
import torch.nn as nn
import torch
char_map_str = """
 C1 0
 C#1 1
 D1 2
 E-1 3
 E1 4
 F1 5
 F#1 6
 G1 7
 G#1 8
 A1 9
 B-1 10
 B1 11
 C2 12
 C#2 13
 D2 14
 E-2 15
 E2 16
 F2 17
 F#2 18
 G2 19
 G#2 20
 A2 21
 B-2 22
 B2 23
 C3 24
 C#3 25
 D3 26
 E-3 27
 E3 28
 F3 29
 F#3 30
 G3 31
 G#3 32
 A3 33
 B-3 34
 B3 35
 C4 36
 C#4 37
 D4 38
 E-4 39
 E4 40
 F4 41
 F#4 42
 G4 43
 G#4 44
 A4 45
 B-4 46
 B4 47
 C5 48
 C#5 49
 D5 50
 E-5 51
 E5 52
 F5 53
 F#5 54
 G5 55
 G#5 56
 A5 57
 B-5 58
 B5 59
 C6 60
 C#6 61
 D6 62
 E-6 63
 E6 64
 F6 65
 F#6 66
 G6 67
 G#6 68
 A6 69
 B-6 70
 B6 71
 C7 72
 C#7 73
 D7 74
 E-7 75
 E7 76
 F7 77
 F#7 78
 G7 79
 G#7 80
 A7 81
 B-7 82
 B7 83
 <SPACE> 84
 """


class TextTransform:
    def __init__(self):
        global char_map_str
        char_map_str = char_map_str
        self.char_map = {}
        self.index_map = {}
        # init the dictionary
        for line in char_map_str.strip().split('\n'):
            ch, index = line.split()
            ch = ch.lower()
            self.char_map[ch] = int(index)
            self.index_map[int(index)] = ch



    def text_to_int(self, text):
        """ Use a character map and convert text to an integer sequence """
        int_sequence = []
        for c in text.split():

            ch = self.char_map[c]
            int_sequence.append(ch)
            int_sequence.append(self.char_map['<space>'])
        int_sequence = int_sequence[:-1]
        return int_sequence

    def int_to_text(self, labels):
        """ Use a character map and convert integer labels to an text sequence """
        string = []
        for i in labels:
            if self.index_map[i] == '<SPACE>':
                string.append(' ')
            else:
                string.append(self.index_map[i])
        return ''.join(string)


train_audio_transforms = nn.Sequential(
    torchaudio.transforms.MelSpectrogram(sample_rate=16000, n_mels=128),
    torchaudio.transforms.FrequencyMasking(freq_mask_param=15),
    torchaudio.transforms.TimeMasking(time_mask_param=35)
)

valid_audio_transforms = torchaudio.transforms.MelSpectrogram()

text_transform = TextTransform()

def data_processing(data, data_type="train"):
    spectrograms = []
    labels = []
    input_lengths = []
    label_lengths = []
    for (waveform, _, utterance, _, _, _) in data:
        waveform = waveform.sum(0)
        waveform = waveform[None, :]
        if data_type == 'train':
            spec = train_audio_transforms(waveform).squeeze(0).transpose(0, 1)
        else:
            spec = valid_audio_transforms(waveform).squeeze(0).transpose(0, 1)
        spectrograms.append(spec)
        label = torch.Tensor(text_transform.text_to_int(utterance.lower()))
        labels.append(label)
        input_lengths.append(spec.shape[0] // 2)
        label_lengths.append(len(label))

    spectrograms = nn.utils.rnn.pad_sequence(spectrograms, batch_first=True).unsqueeze(1).transpose(2, 3)
    labels = nn.utils.rnn.pad_sequence(labels, batch_first=True)

    return spectrograms, labels, input_lengths, label_lengths

