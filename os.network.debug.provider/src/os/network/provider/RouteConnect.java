package os.network.provider;

import java.net.Socket;
import java.util.Dictionary;
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
		new Thread(new Runnable(){
			@Override
			public void run() {
				
				// ���·������ֱ������
				String route_ip=addr.split(":")[0];
				String route_port=addr.split(":")[1];
				while(true){
					try{
						new Socket(route_ip,Integer.parseInt(route_port));
						break;
					} catch (Exception e){
						System.out.println("connect route error");
						try {
							Thread.sleep(3000);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				
				// �����������÷�������
				try{
					Configuration configuration = cm.getConfiguration("org.amdatu.remote.discovery.zookeeper","?");
					if(configuration!=null){
						Dictionary<String, Object> map = configuration.getProperties();
						if(map==null){map=new Hashtable<String, Object>();}
						
						// һ��zookeeper����˵�ַ
						map.put("org.amdatu.remote.discovery.zookeeper.connectstring",addr);
						
						// ������Ҫ�洢����Ϣ
						// ��Ҫ��zookeeper����˴洢����Ϣ,��Щ��Ϣ�����˱�������ͨѶ����Ļ�����Ϣ
						// �����������������IP��PROT
						// ��������ΪͨѶЭ���ǻ���HTTP��,����ִ����һ��table·��
						// �⼸����Ϣ����zookeeper�����,��http://ip:port/path����ʽ���д洢
						map.put("org.amdatu.remote.discovery.zookeeper.host", ip);
						map.put("org.amdatu.remote.discovery.zookeeper.port", port);
						map.put("org.amdatu.remote.discovery.zookeeper.path","table");
						// �����洢��·��
						// ����zookeeper�洢��Щ��Ϣ��·��,�������Ϊkey
						// key:Ϊroute value��http://ip:port/path ��ɵ�url
						map.put("org.amdatu.remote.discovery.zookeeper.rootpath", "/route");
						
						// �ġ���������Ƶ��
						map.put("org.amdatu.remote.discovery.zookeeper.schedule", 3);
						configuration.update(map);
					}
				} catch (Exception e) {}
			
				try{
					// ����Զ�̹�����Ϣ
					Configuration configuration = cm.getConfiguration("org.amdatu.remote.admin.http","?");
					if(configuration!=null){
						Dictionary<String, Object> map=configuration.getProperties();
						if(map==null){map=new Hashtable<String, Object>();}
						
						map.put("org.amdatu.remote.admin.http.host",ip);
						map.put("org.amdatu.remote.admin.http.port",port);
						map.put("org.amdatu.remote.admin.http.path","services");
						
						configuration.update(map);
					}
				} catch (Exception e) {}
			}
		}).start();
	}
		
	
}
