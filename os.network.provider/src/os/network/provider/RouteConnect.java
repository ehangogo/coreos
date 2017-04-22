package os.network.provider;

import java.net.Socket;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class RouteConnect {
	
	// ·�ɵ�ַ
	private String addr=null;
	
	// ����IP��ַ�Ͷ˿�
	private String ip=null;
	private String port=null;
	
	public RouteConnect(String ip,String port){
		this.ip=ip;
		this.port=port;
		
		 String addr=System.getenv().get("OS_ROUTE");
		 if(addr==null||addr.equals("")){
			 addr=System.getProperty("os.route");
		 }
		 if(addr==null||addr.equals("")){
			 addr="localhost:6789";
		 }
		 this.addr=addr;
	}
	// ��������
	public void connect(ConfigurationAdmin cm){
		
		String route_ip=addr.split(":")[0];
		String route_port=addr.split(":")[1];
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				boolean ping=false;
				boolean init=false;
				while(true){
					// ���·���Ƿ����
					try{
						new Socket(route_ip,Integer.parseInt(route_port));
						ping=true;
					} catch (Exception e) {
						// ��ʼ��ʧ��
						init=false;
						ping=false;
						System.out.println("connect route error");
					}	
					// ������÷�������
					if(ping&&init==false){
						try{
							Configuration configuration = cm.getConfiguration("org.amdatu.remote.discovery.zookeeper", "?");
							if(configuration!=null){
								Hashtable<String, Object> map = new Hashtable<String, Object>();
								map.put("org.amdatu.remote.discovery.zookeeper.connectstring",addr);
								map.put("org.amdatu.remote.admin.http.host", ip);
								map.put("org.amdatu.remote.discovery.zookeeper.host", ip);
								map.put("org.amdatu.remote.discovery.zookeeper.rootpath", "/route");
								map.put("org.amdatu.remote.discovery.zookeeper.path","table");
								map.put("org.amdatu.remote.discovery.zookeeper.schedule", 3);//3��һ���������
								configuration.update(map);
								// ��ʼ���ɹ�
								init=true;
							}
						} catch (Exception e) {}
					}
					try{
						Thread.sleep(10000);
					} catch (Exception e) {}
				}
			}
			
		}).start();
		
	}
	
}
