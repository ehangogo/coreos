package os.network;

import os.network.provider.RouteConnect;

public class Host8081 {
	public static void main(String args[]) throws Exception{
		
		// ·�ɵ�ַ
		String route_addr="localhost:6789";
		
		// Ҫ�ϱ�������ͨѶ��Ϣ
		String ip="localhost";
		String port="8081";
		String path="table";
		
		// �����ϱ�
		RouteConnect client=new RouteConnect();
		client.connect(route_addr).report(ip, port, path);
		Thread.sleep(Long.MAX_VALUE);
	}
}
