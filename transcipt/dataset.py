import os
from typing import Tuple, Union
from pathlib import Path
import torchaudio
from torch import Tensor
from torch.utils.data import Dataset


def load_item(set_id: str,
                          path: str,
                          ext_audio: str,
                          ext_txt: str) -> Tuple[Tensor, int, str, int, int, int]:
    instrument_id, set_id, section_id = set_id.split("-")

    file_text = instrument_id + "-" + set_id + ext_txt
    file_text = os.path.join(path, instrument_id, set_id, file_text) # get into the dir

    fileid_audio = instrument_id + "-" + set_id + "-" + section_id
    file_audio = fileid_audio + ext_audio
    file_audio = os.path.join(path, instrument_id, set_id, file_audio)# get into it

    # Load audio
    waveform, sample_rate = torchaudio.load(file_audio)

    # Load text


    with open(file_text) as ft:
        # get the transcipt
        fileid_text, transcript = (ft.read().split("\n")[int(section_id)]).strip().split(" ", 1)
        if fileid_audio != fileid_text:
            raise FileNotFoundError("Translation not found for " + fileid_audio)

    return (
        waveform,
        sample_rate,
        transcript,
        int(instrument_id),
        int(set_id),
        int(section_id),
    )


class MusicDataset(Dataset):

    _ext_txt = ".trans.txt"
    _ext_audio = ".wav"

    def __init__(self,
                 root: Union[str, Path],
                 url: str = "train",
                 folder_in_archive: str = "data") -> None:

        root = os.fspath(root)
        basename = os.path.basename(url)


        basename = basename.split(".")[0]
        folder_in_archive = os.path.join(folder_in_archive, basename)

        self._path = os.path.join(root, folder_in_archive)
        self._walker = sorted(str(p.stem) for p in Path(self._path).glob('*/*/*' + self._ext_audio))

    def __getitem__(self, n: int) -> Tuple[Tensor, int, str, int, int, int]:
        fileid = self._walker[n]
        return load_item(fileid, self._path, self._ext_audio, self._ext_txt)

    def __len__(self) -> int:
        return len(self._walker)