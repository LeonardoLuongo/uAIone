# La classe Host rappresenta un host di rete con un indirizzo e una porta.

import socket

class Host:
    def __init__(self, addr: str, port: int):
        self.addr = addr
        self.port = port

    def get_address(self) -> str:
        return self.addr

    def set_address(self, addr: str):
        self.addr = addr

    def get_port(self) -> int:
        return self.port

    def set_port(self, port: int):
        self.port = port