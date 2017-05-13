package os.network.provider;

import java.util.ArrayList;
import java.util.List;

import os.network.api.Network;

public class NetworkImpl implements Network{
	// ��ǰ����IP�Ͷ˿�
	private String addr=null;
	// ·�ɻ���
	public List<Network> routes = new ArrayList<>();
		
	public NetworkImpl(String addr){
		this.addr=addr;
	}
	
	public void connect() {

		// ·�ɵ�ַ
		String route_addr="localhost:6789";
		
		// Ҫ�ϱ�������ͨѶ��Ϣ
		String ip=addr.split(":")[0];
		String port=addr.split(":")[1];
		String path="table";
		
		// ����·��
		try{
			RouteConnect client=new RouteConnect(this);
			// ����·�ɡ�>������������>�ϱ�ͨѶ��ַ
			client.connect(route_addr).listen(port).report(ip, port, path);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String getHostInfo() {
		return addr;
	}

	@Override
	public String call(String namespace, String method, Object[] args) {
		
		System.out.println("\n---------Զ�̵���------------");
		System.out.println("[  host] -> "+addr);
		System.out.println("[ class] -> "+namespace);
		System.out.println("[method] -> "+method);
		String params="";
		for(Object arg:args){
			params+=arg.toString()+",";
		}
		params=params.replaceAll(",$","");
		System.out.println("[  args] -> "+params);
		
		// �������Ŀ����,����ִ�н��
		String res="I'm "+this.getHostInfo();
		System.out.println("[result] -> "+res);
		return res;
	}
	public void add(Network network){
		if(routes.contains(network.getHostInfo())){
			routes.remove(network.getHostInfo());
		}
		routes.add(network);
	}
	public void clear(){
		routes.clear();
	}
	public void remove(Network network){
		routes.remove(network);
	}
	
	@Override
	public String toString(){
		return addr;
	}

}
