package scr;

import java.net.InetAddress;

public class Host 
{
    /*
     * Questa classe rappresenta un host identificato da un indirizzo IP e una porta.
     * Fornisce metodi per ottenere e impostare l'indirizzo e la porta dell'host.
     */

    // Indirizzo IP dell'host
    private InetAddress addr;
    // Porta dell'host
    private int port;

    public Host() throws Exception
    {
        this.addr = InetAddress.getByName("0.0.0.0");
        this.port = 0;
    }

    public Host(InetAddress addr, int port)
    {
        this.addr = addr;
        this.port = port;
    }

    public InetAddress getAddress()
    {
        return this.addr;
    }

    public void setAddress(InetAddress addr)
    {
        this.addr = addr;
    }

    public int getPort()
    {
        return this.port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

}
