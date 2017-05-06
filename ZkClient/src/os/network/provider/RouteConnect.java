package os.network.provider;

import java.util.List;
import java.util.UUID;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class RouteConnect {
	private ZooKeeper zk=null;
	
	// ����·��,��ע������¼���Zookeeper����˻�ȡ���µ�����
	public RouteConnect connect(String route_addr) throws Exception{
		zk = new ZooKeeper(route_addr,10000,new Watcher(){
			
			// ���ӵ�ʱ��,֪ͨע������¼�,���ڻ�ȡָ���洢Ŀ¼��,�ͻ������Ӵ�ŵ�ͨѶ��ַ��Ϣ
			@Override
			public void process(WatchedEvent event) {
				 if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged)
					try{
						List<String> lists=zk.getChildren("/route", true);
						System.out.println("\n--------���½ڵ��б�----------");
						for(String path:lists){
							byte[] data = zk.getData("/route/"+path, false, null);
							String info=new String(data, "UTF-8");
							System.out.println(info);
						}
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
	public void report(String ip,String port,String url) throws Exception{
		// ����route�µ���Ŀ¼,һ����Ŀ¼��¼һ������������һ��UUID��Ϊkey,��Ӧ������ͨѶIP�Ͷ˿�,��Ϊ����value
		UUID uuid = UUID.randomUUID();
		zk.create("/route/"+uuid.toString().substring(0,8), ("http://"+ip+":"+port+"/"+url+"/").getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
		
		// ������ȡһ��
		List<String> lists=zk.getChildren("/route", true);
		System.out.println("\n--------���½ڵ��б�----------");
		for(String path:lists){
			byte[] data = zk.getData("/route/"+path, false, null);
			String info=new String(data, "UTF-8");
			System.out.println(info);
		}
	}
	
		
	
}
