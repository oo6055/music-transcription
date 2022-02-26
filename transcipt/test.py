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


def GreedyDecoder(output, labels, label_lengths, blank_label=28, collapse_repeated=True):
    arg_maxes = torch.argmax(output, dim=2)
    decodes = []
    targets = []
    for i, args in enumerate(arg_maxes):
        decode = []
        targets.append(tfClass().int_to_text(labels[i][:label_lengths[i]].tolist()))
        for j, index in enumerate(args):
            if index != blank_label:
                if collapse_repeated and j != 0 and index == args[j -1]:
                    continue
                decode.append(index.item())
        decodes.append(tfClass().int_to_text(decode))
    return decodes, targets


class IterMeter(object):
    """keeps track of total iterations"""
    def __init__(self):
        self.val = 0

    def step(self):
        self.val += 1

    def get(self):
        return self.val

def collate_fn_valid(batch):
    return TextTransform.data_processing(batch,'valid')


def test(model, device, test_loader, criterion, iter_meter):
    print('\nevaluating…')
    model.eval()
    test_loss = 0
    test_cer, test_wer = [], []

    with torch.no_grad():
        for I, _data in enumerate(test_loader):
            spectrograms, labels, input_lengths, label_lengths = _data
            spectrograms, labels = spectrograms.to(device), labels.to(device)

            output = model(spectrograms)  # (batch, time, n_class)
            output = F.log_softmax(output, dim=2)
            output = output.transpose(0, 1) # (time, batch, n_class)

            loss = criterion(output, labels, input_lengths, label_lengths)
            test_loss += loss.item() / len(test_loader)

            decoded_preds, decoded_targets = GreedyDecoder(output.transpose(0, 1), labels, label_lengths)
            for j in range(len(decoded_preds)):
                test_cer.append(TextTransform.word_error_rate(decoded_targets[j], decoded_preds[j]))
                test_wer.append(TextTransform.wer(decoded_targets[j], decoded_preds[j]))


    avg_cer = sum(test_cer)/len(test_cer)
    avg_wer = sum(test_wer)/len(test_wer)
    print("our accuracy:",avg_cer,"the other = ", avg_wer)

def main(learning_rate=5e-4, batch_size=20, epochs=10):
    hparams = {
        "n_cnn_layers": 6,
        "n_class": 86,
        "n_feats": 128,
        "stride": 2,
        "n_rnn_layers": 2,
        "rnn_dim": 512,
        "momentum": 0.9,
        "dropout": 0.1,
        "learning_rate": learning_rate,
        "batch_size": batch_size,
        "epochs": epochs
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

    model = torch.jit.load('model2.pt')

    model.eval()
    #test(model, device, test_loader,criterion, i)

    _data = load_audio_item(r"C:\music-transcription\transcipt\1-3-0001.wav", 'C3 F4 G1 C#6 C#4 G#1 E7 E-3')
    _data = dataTransform.data_processing([_data],"valid")


    spectrograms, labels, input_lengths, label_lengths = _data
    spectrograms, labels = spectrograms.to(device), labels.to(device)

    print(spectrograms.shape)
    output = model(spectrograms)  # (batch, time, n_class)
    output = F.log_softmax(output, dim=2)
    output = output.transpose(0, 1)  # (time, batch, n_class)


    decoded_preds, decoded_targets = GreedyDecoder(output.transpose(0, 1), labels, label_lengths)
    print(decoded_preds, decoded_targets)



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