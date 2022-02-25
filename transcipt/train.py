from dataset import MusicDataset
import torch
import torch.optim as optim
import torchaudio
from dataTransform import TextTransform as tfClass
import dataTransform
import torch.nn.functional as F
from comet_ml import Experiment
import torch.utils.data as data
import os
import torch.nn as nn
from model import MusicRecognitionModel
from torch.utils.tensorboard import SummaryWriter
import matplotlib.pyplot as plt

# default `log_dir` is "runs" - we'll be more specific here
writer = SummaryWriter('runs/music_expr2')



t = tfClass()

def GreedyDecoder(output, labels, label_lengths, blank_label=85, collapse_repeated=True):
    arg_maxes = torch.argmax(output, dim=2)
    decodes = []
    targets = []
    t = tfClass()
    for i, args in enumerate(arg_maxes):
        decode = []
        targets.append(t.int_to_text(labels[i][:label_lengths[i]].tolist()))
        for j, index in enumerate(args):
            if index != blank_label:
                if collapse_repeated and j != 0 and index == args[j -1]:
                    continue
                decode.append(index.item())
        decodes.append(t.int_to_text(decode))
    return decodes, targets


class IterMeter(object):
    """keeps track of total iterations"""
    def __init__(self):
        self.val = 0

    def step(self):
        self.val += 1

    def get(self):
        return self.val

def train(model, device, train_loader, criterion, optimizer, scheduler, epoch, iter_meter):
    model.train()
    running_loss = 0.0
    data_len = len(train_loader.dataset)


    for batch_idx, _data in enumerate(train_loader):
        spectrograms, labels, input_lengths, label_lengths = _data
        spectrograms, labels = spectrograms.to(device), labels.to(device)

        optimizer.zero_grad()


        output = model(spectrograms)  # (batch, time, n_class)
        output = F.log_softmax(output, dim=2)
        output = output.transpose(0, 1)  # (time, batch, n_class)

        loss = criterion(output, labels, input_lengths, label_lengths).float()


        loss.backward()

        # take the loss

        optimizer.step()
        scheduler.step()
        iter_meter.step()

        running_loss += loss.item()

        if batch_idx % 100 == 0 or batch_idx == data_len:
            # ...log the running loss
            writer.add_scalar('training loss',
                              running_loss / 1000,
                              epoch * len(train_loader.dataset) + batch_idx)


            print('Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}'.format(
                epoch, batch_idx * len(spectrograms), data_len,
                100. * batch_idx / len(train_loader), loss.item()))



def test(model, device, test_loader, criterion, epoch, iter_meter):
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
                writer.add_scalar('testing loss',
                                  test_loss / 1000,
                                  epoch * len(test_loader.dataset) + I)
                test_cer.append(dataTransform.cer(decoded_targets[j], decoded_preds[j]))
                test_wer.append(dataTransform.wer(decoded_targets[j], decoded_preds[j]))


    avg_cer = sum(test_cer)/len(test_cer)
    avg_wer = sum(test_wer)/len(test_wer)

    print('Test set: Average loss: {:.4f}, Average CER: {:4f} Average WER: {:.4f}\n'.format(test_loss, avg_cer, avg_wer))

def main(learning_rate=5e-4, batch_size=20, epochs=53,
        train_url="train-clean-100", test_url="test-clean"):

    hparams = {
        "n_cnn_layers": 6,
        "n_class": 86,
        "n_feats": 128,
        "stride": 2,
        "n_rnn_layers" : 2,
        "rnn_dim" : 512,
        "momentum": 0.9,
        "dropout": 0.3,
        "learning_rate": learning_rate,
        "batch_size": batch_size,
        "epochs": epochs
    }


    use_cuda = torch.cuda.is_available()
    torch.manual_seed(7)
    device = torch.device("cuda" if use_cuda else "cpu")

    if not os.path.isdir("./data"):
        os.makedirs("./data")

    train_dataset = MusicDataset("","train")
    test_dataset = MusicDataset("","test")


    kwargs = {'num_workers': 1, 'pin_memory': True} if use_cuda else {}
    train_loader = data.DataLoader(dataset=train_dataset,
                                collate_fn=collate_fn_train,
                                batch_size=hparams['batch_size'],
                                shuffle=True,
                                **kwargs)
    test_loader = data.DataLoader(dataset=test_dataset,
                                batch_size=hparams['batch_size'],
                                shuffle=False,
                                collate_fn=collate_fn_valid,
                                **kwargs)

    model = MusicRecognitionModel(
        n_cnn_layers=hparams['n_cnn_layers'], rnn_dim=hparams['rnn_dim'],n_rnn_layers=hparams['n_rnn_layers'],
        n_class=hparams['n_class'], n_feats=hparams['n_feats'], stride=hparams['stride'], dropout=hparams['dropout']).to(device)

    #model.load_state_dict(torch.load("model.pth"))
    print(model)
    model = model.to(device)
    print('Num Model Parameters', sum([param.nelement() for param in model.parameters()]))

    optimizer = optim.AdamW(model.parameters(), hparams['learning_rate'])
    criterion = nn.CTCLoss(blank=85).to(device)
    scheduler = optim.lr_scheduler.OneCycleLR(optimizer, max_lr=hparams['learning_rate'],
                                            steps_per_epoch=int(len(train_loader)),
                                            epochs=hparams['epochs'],
                                            anneal_strategy='linear')

    iter_meter = IterMeter()
    for epoch in range(1, epochs + 1):
        train(model, device, train_loader, criterion, optimizer, scheduler, epoch, iter_meter)
        test(model, device, test_loader, criterion, epoch, iter_meter)
        torch.save(model.state_dict(), "model3.pth")

def collate_fn_train(batch):
    return dataTransform.data_processing(batch,'train')

def collate_fn_valid(batch):
    return dataTransform.data_processing(batch,'valid')

if __name__ == "__main__":
    main()