import socket
import sys
import os
import torch
import torchaudio
import time


def get_data_back():
    model2 = torch.jit.load('waveformToString.pt')
    waveform, _ = torchaudio.load(r"file.wav")
    return model2(waveform)


def start_server():
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to the port
    server_address = ('0.0.0.0', 9002)
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
                con = connection.recv(4)
                data = b''
                while con.decode() == 'true':

                    data += connection.recv(1024)
                    print('received "%s"' % data)
                    connection.sendall("more".encode())
                    con = connection.recv(4)
                    print('con =  "%s"' % con)
                if data:
                    file = open("file.3gp", "wb")
                    file.write(data)
                    file.close()
                    # create wav file
                    os.system('ffmpeg -i file.3gp file.wav')
                    databack = get_data_back()




                    print('sending data back to the client')
                    data = ""
                    connection.sendall(databack.encode())
                else:
                    print('no more data from', client_address)
                    break

        finally:
            # Clean up the connection
            connection.close()


def file_to_wav():


    os.system('ffmpeg -i file.3gp file.wav')


def main():
    #file_to_wav()
    start_server()

if __name__ == "__main__":
    main()
