import socket
import sys
import os


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
            data = connection.recv(100000)
            print('received "%s"' % data)
            if data:
                file = open("file.3gp", "wb")
                file.write(data)
                file.close()
                # os.system('ffmpeg -i file.3gp file.wav')
                print('sending data back to the client')
                connection.sendall("f3f4f5".encode())
            else:
                print('no more data from', client_address)
                break

    finally:
        # Clean up the connection
        connection.close()