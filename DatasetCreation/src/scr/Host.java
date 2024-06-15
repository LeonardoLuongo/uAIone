package scr;

import java.net.InetAddress;

public class Host 
{
    private InetAddress addr;
    private int port;

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
