import socket
import os
import pyrebase
import torch
import music21
import torchaudio

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
    """
    this function convertes string to pdf file and put him in the firebase
    :param string: the string of notes
    :param file_name: the file name
    :return: none
    """
    music_stream = music21.stream.Stream()
    print(string.split(" "))

    for note_str in string.split(" "):
        if note_str == "<SPACE>":
            continue
        if note_str == '':
            continue
        n = music21.note.Note(note_str, quarterLength=1)
        music_stream.append(n)
        
    # convert to musuicNotes ods
    conv = music21.converter.subConverters.ConverterLilypond()
    conv.write(music_stream, fmt='lilypond', fp='file', subformats=['pdf'])
    storge.child("files/" + file_name).put("file.pdf")


def get_data_back(file_name = "file.wav"):
    """
    this function load the model
    :param file_name: the audio file name
    :return: a string of model transcript
    """
    model2 = torch.jit.load('waveformToString.ptl')
    waveform, _ = torchaudio.load(file_name)
    return model2(waveform)


def start_server():
    """
    this func starts the server
    :return: none
    """
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to the port
    server_address = ('0.0.0.0', 9002)

    os.system("ipconfig")
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

        except Exception as e:
            print(e)
        finally:
            # Clean up the connection
            connection.close()



def add_section(connection):
    """
    this func get a section from the client
    :param connection: the socket transcript it and send to the firebase.
    :return: none
    """
    con = connection.recv(4)
    data = b''
    while con.decode() == 'true':
        data += connection.recv(1024)
        # print('received "%s"' % data)
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
        if (os.path.isfile("file.wav")):
            os.remove("file.wav")
        # convert file.3gp to file.wav
        os.system('ffmpeg -i file.3gp file.wav')
        databack = get_data_back("file.wav")
        print(databack)

        string_to_notes(databack, name_of_file)

        print('sending data back to the client')
        data = ""
        connection.sendall(databack.encode())
    else:
        print('no more data from celint')

def main():
    """
    this main function
    :return: none
    """
    start_server()

if __name__ == "__main__":
    main()