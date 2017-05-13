package os.network.provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import os.network.api.Network;
import os.network.http.HttpClient;
import os.network.http.HttpServerExport;

public class RouteConnect {
	private ZooKeeper zk=null;
	private Network network=null;
	public RouteConnect(Network network){
		this.network=network;
	}
	// ����·��,��ע������¼���Zookeeper����˻�ȡ���µ�����
	public RouteConnect connect(String route_addr) throws Exception{
		zk = new ZooKeeper(route_addr,10000,new Watcher(){
			// ���ӵ�ʱ��,֪ͨע������¼�,���ڻ�ȡָ���洢Ŀ¼��,�ͻ������Ӵ�ŵ�ͨѶ��ַ��Ϣ
			@Override
			public void process(WatchedEvent event) {
				 if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged)
					try{
						updateUrls();
					}catch(Exception e){
						e.printStackTrace();
					}
			}
		});
		
		// ���routeĿ¼������,�򴴽�routeĿ¼,
		if(zk.exists("/route",true)==null){
			zk.create("/route", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		return this;
	}
	// �ϱ��Լ�IP�Ͷ˿�
	public RouteConnect report(String ip,String port,String url) throws Exception{
		// ����route�µ���Ŀ¼,һ����Ŀ¼��¼һ������������һ��UUID��Ϊkey,��Ӧ������ͨѶIP�Ͷ˿�,��Ϊ����value
		UUID uuid = UUID.randomUUID();
		
		zk.create("/route/"+uuid.toString().substring(0,8), ("http://"+ip+":"+port+"/"+url+"/").getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
		// ����ͨѶ��ַ
		updateUrls();
		return this;
	}
	public void updateUrls()throws Exception{
		// ������ȡһ��
		List<String> lists=zk.getChildren("/route", true);
		System.out.println("\n--------���½ڵ��б�----------");
		List<String> urls=new ArrayList<>();
		for(String path:lists){
			byte[] data = zk.getData("/route/"+path, false, null);
			String info=new String(data, "UTF-8");
			System.out.println(info);
			urls.add(info);
		}
		System.out.println("����Զ�̴������");
		create_proxy(urls);
	}
	public RouteConnect listen(String port){
		try{
			new HttpServerExport(network).start(Integer.parseInt(port));
		}catch(Exception e){
			e.printStackTrace();
		}
		return this;
	}
	public void create_proxy(List<String> urls){
		
		// ���ԭ������
		NetworkImpl net=(NetworkImpl)this.network;
		net.clear();
		
		for(String url:urls){
			// ����:�������,Network�ӿ�,Hanlder����
			Object network =Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Network.class}, new ProxyHandler(url));
			// ��network���Զ�̴������
			net.add((Network)network);
		}
	}
	public class ProxyHandler implements InvocationHandler{
		private String url=null;
		public ProxyHandler(String url){
			this.url=url;
		}
		// ���������󷽷�����
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			
			// ͨ��HttpClient�ͻ��˷���Զ������
			return HttpClient.post(url, method.getName(),args);
		}
		
	}
		
	
}
