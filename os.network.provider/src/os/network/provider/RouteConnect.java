package os.network.provider;

import java.net.Socket;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class RouteConnect {
	
	// 路由地址
	private String addr=null;
	
	// 本机IP地址和端口
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
	// 链接请求
	public void connect(ConfigurationAdmin cm){
		
		String route_ip=addr.split(":")[0];
		String route_port=addr.split(":")[1];
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				boolean ping=false;
				boolean init=false;
				while(true){
					// 检测路由是否可用
					try{
						new Socket(route_ip,Integer.parseInt(route_port));
						ping=true;
					} catch (Exception e) {
						// 初始化失败
						init=false;
						ping=false;
						System.out.println("connect route error");
					}	
					// 如果可用发起连接
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
								map.put("org.amdatu.remote.discovery.zookeeper.schedule", 3);//3秒一次心跳检测
								configuration.update(map);
								// 初始化成功
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
