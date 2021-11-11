from music21 import note,stream,environment,converter


def string_to_notes(string):
    music_stream = stream.Stream()

    for i in range(0,len(string),2):
        n = note.Note(string[i] + string[i+1], quarterLength=1)
        music_stream.append(n)
    conv = converter.subConverters.ConverterLilypond()
    conv.write(music_stream, fmt='lilypond', fp='file', subformats=['pdf'])




string_to_notes("C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4C4A4")