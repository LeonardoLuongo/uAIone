package scr;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Peer 
{
    /*
     * Questa classe permette la comunicazione UDP tramite la classe DatagramSocket
     */
    private final int DEFAULT_DATAGRAM_BYTES = 100;
    private DatagramSocket socket;

    public Peer(InetAddress addr, int port) throws Exception
    {
        socket = new DatagramSocket(port, addr);
    }

    public DatagramPacket receive(byte[] receiveBuffer) throws Exception
    {
        if (receiveBuffer == null)
        {
            receiveBuffer = new byte[DEFAULT_DATAGRAM_BYTES];
        }

        DatagramPacket receivePacket = new DatagramPacket(
            receiveBuffer, receiveBuffer.length
        );

        socket.receive(receivePacket);

        return receivePacket;
    }

    public DatagramPacket send(
        byte[] sendBuffer,
        Host remoteHost
    ) throws Exception
    {
        if (sendBuffer == null)
        {
            sendBuffer = new byte[DEFAULT_DATAGRAM_BYTES];
        }

        DatagramPacket sendPacket = new DatagramPacket(
            sendBuffer, sendBuffer.length,
            remoteHost.getAddress(), remoteHost.getPort()
        );

        socket.send(sendPacket);

        return sendPacket;
    }

    public DatagramPacket receivePacket(DatagramPacket receivePacket, Host remoteHost) throws Exception
    {
        socket.receive(receivePacket);

        remoteHost = new Host(receivePacket.getAddress(), receivePacket.getPort());

        return receivePacket;
    }

    public DatagramPacket sendPacket(
        DatagramPacket datagramPacket
    ) throws Exception
    {
        socket.send(datagramPacket);

        return datagramPacket;
    }

    public String receiveString() throws Exception
    {
        return receiveString(DEFAULT_DATAGRAM_BYTES);
    }

    public String receiveString(int maxSize) throws Exception
    {
        byte[] receiveBuffer = new byte[maxSize];
        DatagramPacket receivePacket = new DatagramPacket(
            receiveBuffer, maxSize
        );

        socket.receive(receivePacket);

        return new String(receivePacket.getData());
    }

    public void sendString(String str, Host remoteHost) throws Exception
    {
        DatagramPacket sendPacket = new DatagramPacket(
            str.getBytes(), str.length(),
            remoteHost.getAddress(), remoteHost.getPort()
        );

        socket.send(sendPacket);

    }

}
