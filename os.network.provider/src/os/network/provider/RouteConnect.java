package os.network.provider;

import java.net.Socket;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
/**
 * 
 * ·�����Ӷ���
 * @author ������
 *
 */
public class RouteConnect {
	
	// OSGI���ù������
	ConfigurationAdmin cm=null;
	public RouteConnect(ConfigurationAdmin cm){
		this.cm=cm;
	}
	
	// ����IP�˿���Ϣ
	String ip=null;
	String port=null;
	
	// ��������
	public void connect(String route_url,String ip,String port){
		
		this.ip=ip;
		this.port=port;
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				// ���route_url�Ƿ����
				ping(route_url);
				// �����������÷�������
				try{
					Configuration configuration = cm.getConfiguration("org.amdatu.remote.discovery.zookeeper","?");
					if(configuration!=null){
						Dictionary<String, Object> map = configuration.getProperties();
						if(map==null){map=new Hashtable<String, Object>();}
						
						// һ��zookeeper����˵�ַ
						map.put("org.amdatu.remote.discovery.zookeeper.connectstring",route_url);
						
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
	// ��������Ƿ���õ���
	public void ping(String url){
		while(true){
			String route_ip=url.split(":")[0];
			String route_port=url.split(":")[1];
			try{
				new Socket(route_ip,Integer.parseInt(route_port));
				break;
			}catch (Exception e){
				System.out.println(String.format("network[%s:%s]->route[%s:%s] connect error",ip,port,route_ip,route_port));
			}
			try{
				Thread.sleep(3000);
			}catch(Exception e){};
		}
	}
	
}
