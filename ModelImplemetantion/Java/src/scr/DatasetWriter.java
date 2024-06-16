package scr;

import scr.Sampler;
import scr.Utility;
import scr.Host;
import scr.Peer;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatasetWriter
{
    public static void main(String[] args) throws Exception
    {
        Map<String, String> propertiesNames = new HashMap<>();
        propertiesNames.put("dsw_ip", "dsw_to_client.ip");
        propertiesNames.put("dsw_port", "dsw_to_client.port");
        propertiesNames.put("dsw_filename", "dsw.filename");
        
        Collection<String> values = propertiesNames.values();
				String[] stringArray = new String[values.size()];
				int index = 0;
				for (Object obj : values) {
					if (obj instanceof String) {
						stringArray[index++] = (String) obj;
					} else {
						System.err.println("a propertyName isn't a sting.");
					}
				}
		HashMap<String, String> props = Utility.readProperties("./src/config.txt", stringArray);

        Host host = new Host(
            InetAddress.getByName(props.get(propertiesNames.get("dsw_ip"))), 
            Integer.parseInt(props.get(propertiesNames.get("dsw_port")))
        );

        Peer peer = null;
        try {
            peer = new Peer(host.getAddress(), host.getPort());
        } catch (Exception e) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(-1);
        }

        Sampler sampler;
        sampler = new Sampler(props.get(propertiesNames.get("dsw_filename")));

        String msg;
        while (true)
        {

            msg = peer.receiveString(2_000);
            msg = msg.split("#!")[0];

            if(msg.equals("EXIT"))
            {
                break;
            }

            String sample = msg;

            try {
                sampler.writeIntoDataset(sample, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
