import socket
from . import Host

class Peer:
    DEFAULT_DATAGRAM_BYTES = 100

    def __init__(self, addr: str, port: int):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.bind((addr, port))

    def receive(self, receive_buffer: bytes = None) -> (bytes, tuple):
        if receive_buffer is None:
            receive_buffer = bytearray(self.DEFAULT_DATAGRAM_BYTES)

        data, addr = self.socket.recvfrom(len(receive_buffer))
        return data, addr

    def send(self, send_buffer: bytes, remote_host: Host) -> int:
        if send_buffer is None:
            send_buffer = bytearray(self.DEFAULT_DATAGRAM_BYTES)

        return self.socket.sendto(send_buffer, (remote_host.get_address(), remote_host.get_port()))

    def receive_packet(self, remote_host: Host) -> (bytes, tuple):
        data, addr = self.socket.recvfrom(self.DEFAULT_DATAGRAM_BYTES)
        remote_host.set_address(addr[0])
        remote_host.set_port(addr[1])
        return data, addr

    def send_packet(self, datagram_packet: bytes, remote_host: Host) -> int:
        return self.socket.sendto(datagram_packet, (remote_host.get_address(), remote_host.get_port()))

    def receive_string(self, max_size: int = DEFAULT_DATAGRAM_BYTES) -> str:
        receive_buffer = bytearray(max_size)
        data, addr = self.socket.recvfrom(max_size)
        return data.decode('utf-8')

    def send_string(self, string: str, remote_host: Host):
        send_buffer = string.encode('utf-8')
        self.socket.sendto(send_buffer, (remote_host.get_address(), remote_host.get_port()))