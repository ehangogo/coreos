package os.network;

import os.network.coreos.CoreOS;
import os.network.provider.NetworkImpl;


public class Host8082 {
	public void start(String addr){
		new NetworkImpl(addr);
	}
	public static void main(String args[]) throws Exception{
		CoreOS coreos=new CoreOS("localhost:8082");
		System.out.println("����8082����");
		coreos.shell();
	}
}
