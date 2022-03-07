from midi2audio import FluidSynth

def midi_to_wav(path, soundfont =r"C:\music-transcription\transcipt\Essential Keys-sforzando-v9.6.sf2"):
    fs = FluidSynth(sound_font=soundfont, sample_rate=22050)
    #
    fs.midi_to_audio(path, path[:path.find(".")] + ".wav")
midi_to_wav(r"C:\Users\User\Downloads\house.mid")