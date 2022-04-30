import socket
import sys
import os
import pyrebase
import torch
import music21
import torchaudio
import time

ADD_DATA_CODE = "100"
firebaseConfig = {
  "apiKey": "AIzaSyDWb8bINnFe7fYGXU63XpXFBm60j2hcOj4",
  "authDomain": "betaversion-90c06.firebaseapp.com",
  "databaseURL": "https://betaversion-90c06-default-rtdb.firebaseio.com",
  "projectId": "betaversion-90c06",
  "storageBucket": "betaversion-90c06.appspot.com",
  "messagingSenderId": "999205442490",
  "appId": "1:999205442490:web:12c12cf6d535e1fd0ec383",
  "measurementId": "G-4HJM7WEJKD"
}

firebase = pyrebase.initialize_app(firebaseConfig)

db=firebase.database()
storge = firebase.storage()


def string_to_notes(string, file_name):
    music_stream = music21.stream.Stream()
    print(string.split(" "))

    for note_str in string.split(" "):
        if note_str == "<SPACE>":
            continue
        if note_str == '':
            continue
        n = music21.note.Note(note_str, quarterLength=1)
        music_stream.append(n)
    conv = music21.converter.subConverters.ConverterLilypond()
    conv.write(music_stream, fmt='lilypond', fp='file', subformats=['pdf'])
    storge.child("files/" + file_name).put("file.pdf")


def get_data_back():
    model2 = torch.jit.load('waveformToString.ptl')
    waveform, _ = torchaudio.load(r"file.wav")
    return model2(waveform)


def start_server():
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to the port
    server_address = ('0.0.0.0', 9002)

    f = os.system("ipconfig")
    print(f)
    print('starting up on %s port %s' % server_address)
    sock.bind(server_address)

    # Listen for incoming connections
    sock.listen(1)

    while True:
        # Wait for a connection
        print('waiting for a connection')
        connection, client_address = sock.accept()

        try:
            print('connection from', client_address)

            # Receive the data in small chunks and retransmit it
            while True:
                codeOfMsg = connection.recv(4).decode()

                if codeOfMsg == ADD_DATA_CODE:
                    add_section(connection)
                    break

        finally:
            # Clean up the connection
            connection.close()


def add_section(connection):


    con = connection.recv(4)
    data = b''
    while con.decode() == 'true':
        data += connection.recv(1024)
        print('received "%s"' % data)
        connection.sendall("more".encode())
        con = connection.recv(4)
        print('con =  "%s"' % con)

    name_of_file = connection.recv(1024).decode()
    name_of_file = name_of_file[:name_of_file.find(".")] + ".pdf"
    print(name_of_file)
    if data:
        file = open("file.3gp", "wb")
        file.write(data)
        file.close()
        # create wav file
        os.remove("file.wav")
        os.system('ffmpeg -i file.3gp file.wav')
        databack = get_data_back()
        print(databack)

        string_to_notes(databack, name_of_file)

        print('sending data back to the client')
        data = ""
        connection.sendall(databack.encode())
    else:
        print('no more data from', client_address)


def file_to_wav():
    os.system('ffmpeg -i file.3gp file.wav')


def main():
    string_to_notes("c1 d4" , "file2.pdf")
    sdf
    start_server()

if __name__ == "__main__":
    main()
