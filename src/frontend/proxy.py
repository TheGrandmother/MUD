import socket, select, sys

if __name__ == "__main__":
    TCP_IP = 'localhost'
    TCP_PORT = 1337
    BUFFER_SIZE = 1024
    MESSAGE = "Hello, World!"

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((TCP_IP, TCP_PORT))
    s.setblocking(0)
    s.settimeout(10)

    socket_list = [sys.stdin, s]

    read_sockets, write_sockets, error_sockets = select.select(socket_list , [], [])

    while(True):
    
        socket_list = [sys.stdin, s]

        read_sockets, write_sockets, error_sockets = select.select(socket_list , [], [])
        
        for sock in read_sockets:
            if sock == s:
                data = sock.recv(4096)
                if not data:
                    print "something something darkside"
                    quit()
                else:
                    sys.stdout.write("incomming: " + data)
            else:
                balls = sys.stdin.readline()
                sent = s.send(balls.encode('utf-8'))
                print "sent " + str(sent) + " bytes"
                if(sent == 0):
                    raise RuntimeExceprion("Connection failure")
