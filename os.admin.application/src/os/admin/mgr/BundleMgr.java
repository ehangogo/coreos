package os.admin.mgr;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.osgi.framework.Bundle;

import os.admin.job.CheckJob;
import os.core.model.BundleInfo;
import os.core.model.HostInfo;
import os.core.model.ServiceInfo;
import os.core.tools.BundleUtil;

/**
 * ����������
 * @author ������
 *
 */
public class BundleMgr {

	private NetworkWrapper network;
	private String NAMESPACE="os.core.provider.CoreShell";
	
	// ������
	PrintStream out=System.out;
	
	// �쳣�ָ�
	public Map<String,Long> checktab=CheckJob.checktab;
	// ���캯��
	public BundleMgr(NetworkWrapper network){
		this.network=network;;
	}

	// ȫ����װ
	public void install(String location){
		install(location,-1L);
	}
	// ָ����Ŀ��װ
	public void install(String location,Long num){
		
   	    // �����ʶ
	  	BundleInfo bundle=BundleUtil.bundleInfo(location);
	    String bdlName=bundle.name;
	  	if(bdlName==null)return;
		// ��װbundle�Ľڵ�
		List<NetworkWrapper> installNodes=new ArrayList<>();
		// δ��װbundle�Ľڵ�
		List<NetworkWrapper> unstallNodes=new ArrayList<>();
		
		List<NetworkWrapper> routes=network.getRoutes();
		// ��ѯ��װ���
		for(NetworkWrapper net:routes){
			List<BundleInfo> bdls=net.getBundles();
			int size=search(bdlName,bdls).size();
			if(size>0){
				installNodes.add(net);
			}else{
				unstallNodes.add(net);
			}
		}
		// ȫ����װ
		if(num<0){
			for(NetworkWrapper net:unstallNodes){
				 install(net,location);
			}
			return;
		}
		long size=installNodes.size();
		int unsize=unstallNodes.size();
		// ����
		if(num>size){
			Long len=(num-size)>unsize?unsize:(num-size);
			int old=-1;
			for(int i=0;i<len;i++){
				int index=random(unsize);
				if(index==old){
					index=random(unsize);
					i--;
					continue;
				}
				install(unstallNodes.get(index),location);
				old=index;
			}
		// ����
		}else if(num<size){
			long len=size-num;
			for(int i=0;i<len;i++){
				uninstall(installNodes.get(i),bdlName);
			}
		}
	}
	private int random(int range){
		return new Random().nextInt(range);
	}
	// ָ��������װ
	public void install(String addr,String location){
		// ����Ŀ������
		NetworkWrapper net=search(addr);
		this.install(net,location);
	}
	// ȫ��ж��
	public void uninstall(String nameVersion){
		List<NetworkWrapper> routes=network.getRoutes();
		// ��ѯ��װ���
		for(NetworkWrapper net:routes){
			List<BundleInfo> bdls=net.getBundles();
			int size=search(nameVersion,bdls).size();
			if(size>0){
				this.uninstall(net, nameVersion);
			}
		}
	}
	// ָ������ж��
	public void uninstall(String addr,String nameVersion){
		NetworkWrapper net=search(addr);
		this.uninstall(net,nameVersion);
	}
	
	// ��̬����
	public void change(String nameVersion,Long num){
		List<NetworkWrapper> routes=network.getRoutes();
		BundleInfo bundle=null;
		// ��ѯ��װ���
		for(NetworkWrapper net:routes){
			List<BundleInfo> bdls=net.getBundles();
			List<BundleInfo> targets=search(nameVersion,bdls);
			int size=targets.size();
			if(size>0){
				bundle=targets.get(0);
				break;
			}
		}
		if(bundle!=null){
			String location=bundle.location;
			this.install(location, num);
			this.start(nameVersion);
			
			this.checktab.put(BundleUtil.nameVersion(bundle), num);
		// hock
		}else{
			// ͨ��nameVersion��ȡjar����װ·��
			String location=BundleUtil.fullName(nameVersion)+".jar";
			this.install(location, num);
			this.start(nameVersion);
			
			bundle=BundleUtil.bundleInfo(location);
			this.checktab.put(BundleUtil.nameVersion(bundle), num);
		}
	}
	
	// ��̬Ǩ��
	public void move(String nameVersion,String from,String to){
		String location=BundleUtil.bundlePath(nameVersion);
		this.install(to,location);
		this.start(to,nameVersion);
		this.uninstall(from,nameVersion);
	}
	
	// ȫ������
	public void start(String nameVersion){
		this.execute("start",nameVersion);
	}
	// ָ����������
	public void start(String addr,String nameVersion){
		this.execute("start",addr,nameVersion);
	}
	
	// ȫ��ֹͣ
	public void stop(String nameVersion){
		this.execute("stop",nameVersion);
	}
	// ָ������ֹͣ
	public void stop(String addr,String nameVersion){
		this.execute("stop",addr,nameVersion);
	}

	// ȫ������
	public void update(String nameVersion){
		if(nameVersion.equals("stop")){
			status="stop";
			if(update_bundle!=null){
				System.out.println(String.format("%s->��������->��ֹ�ɹ�",update_bundle));
			}
			while(thread.isAlive()){
				thread.interrupt();
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
			}
			thread=null;
			
		}else if(nameVersion.equals("start")){
			status="start";
			if(update_bundle!=null){
				System.out.println(String.format("%s->��������->�����ɹ�",update_bundle));
			}
		}else if(nameVersion.equals("pause")){
			status="pause";
			if(update_bundle!=null){
				System.out.println(String.format("%s->��������->��ͣ�ɹ�",update_bundle));
			}
		}else if(nameVersion.equals("status")){
			if(update_bundle!=null){
				if(status.equals("start")){
					System.out.println(String.format("%s->��������->������...",update_bundle));
				}
				if(status.equals("pause")){
					System.out.println(String.format("%s->��������->����ͣ",update_bundle));
				}
			}else{
				System.out.println("������������");
			}
		}else{
			this.update(nameVersion,10L);
		}
	}
	// ����״̬
	public static String status=null;
	// �����߳�
	public static Thread thread=null;
	// �������������
	public static String update_bundle=null;
	public void update(String nameVersion,Long time){
		// ������������
		if(thread!=null&&thread.isAlive()){
			if(status.equals("start")){
				System.out.println(String.format("%s->��������->������...",update_bundle));
			}
			if(status.equals("pause")){
				System.out.println(String.format("%s->��������->����ͣ",update_bundle));
			}
			return;
		}
		// ��������
		status="start";
		update_bundle=nameVersion;
		thread=new Thread(new Runnable(){
			public void run() {
				List<NetworkWrapper> routes=network.getRoutes();
				for(int i=0;i<routes.size();i++){
					NetworkWrapper net=routes.get(i);
					// ��ͣ
					while(status.equals("pause")){
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							break;
						}
					}
					// ��ֹ
					if(status.equals("stop")){
						break;
					}
					// ����
					try{
						List<BundleInfo> bdls=net.getBundles();
						int size=search(nameVersion,bdls).size();
						if(size>0){
							execute("update",net,nameVersion);
						}
					}catch(Exception e){
						break;
					}
					// �������
					if(i==(routes.size()-1)){
						break;
					}
					// ���
					try{
						Thread.sleep(time*1000);
					} catch (Exception e) {
						break;
					}
					
				}
				update_bundle=null;
			}
			
		});
		thread.start();
		
	}
	// ָ����������
	public void update(String addr,String nameVersion){
		this.execute("update",addr,nameVersion);
	}
	
	// �쳣�ָ�
	public void check() {
		// ��ѯ��װ���
		checktab.forEach((name,num)->{
			int count=0;
			List<NetworkWrapper> routes=network.getRoutes();
			for(NetworkWrapper net:routes){
				List<BundleInfo> bdls=net.getBundles();
				int size=search(name,bdls).size();
				count+=size;
			}
			if(count<num){
				System.out.println(String.format("%s->%s->%s","�쳣�ָ�:"+name,count,num));
				this.change(name,num);
			}
		});
	}
	
	// ��ѯ�ӿ�
	public List<ServiceInfo> getServices(){
		List<ServiceInfo> res=new ArrayList<>();
		for(NetworkWrapper net:network.getRoutes()){
			List<ServiceInfo> services=net.getServices();
			if(services!=null){
				res.addAll(services);
			}
		}
		return res;
	}
	public List<BundleInfo> getBundles(){
		List<BundleInfo> res=new ArrayList<>();
		for(NetworkWrapper net:network.getRoutes()){
			List<BundleInfo> bundles=net.getBundles();
			if(bundles!=null){
				res.addAll(net.getBundles());
			}
		}
		return res;
	}
	public List<HostInfo> getNodes(){
		List<HostInfo> res=new ArrayList<>();
		for(NetworkWrapper net:network.getRoutes()){
			HostInfo host=net.getHostInfo();
			if(host!=null){
				res.add(host);
			}
		}
		return res;
	}
	
	// ���ߺ���
	private void install(NetworkWrapper net,String location){
		BundleInfo bundle=BundleUtil.bundleInfo(location);
		
		if(bundle==null)return;
		
		String name=bundle.name;
	    String version=bundle.version;
	    String nameVersion=name+":"+version;
	  	
		if(net!=null){
			List<BundleInfo> bdls=search(nameVersion,net.getBundles());
			if(bdls.size()<=0){
				net.call(NAMESPACE, "install", location);
				add(nameVersion,1L);
				
				// ��ӡ�Ǳ�����װ��Ϣ
				if(!network.equals(net)){
					DateFormat format=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
					String time=format.format(new Date());
					out.println(String.format("[%s]->[%s:%s-%s]->[%s:%s]->[success]",time,net.getHostInfo().ip,net.getHostInfo().port,"install",bundle.name,bundle.version));
				}
			}
		}
	}
	private void uninstall(NetworkWrapper net,String nameVersion){
		if(net!=null){
			List<BundleInfo> bdls=search(nameVersion,net.getBundles());
			if(bdls.size()>0){
				net.call(NAMESPACE, "uninstall",nameVersion);
				String name=bdls.get(0).name;
				String version=bdls.get(0).version;
				add(name+":"+version,-1L);
				
				// ��ӡ�Ǳ�����װ��Ϣ
				if(!network.equals(net)){
					DateFormat format=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
					String time=format.format(new Date());
					out.println(String.format("[%s]->[%s:%s-%s]->[%s]->[success]",time,net.getHostInfo().ip,net.getHostInfo().port,"uninstall",nameVersion));
				}
			}
		}
	}
	// ȫ������
	private void execute(String action,String nameVersion){
		List<NetworkWrapper> routes=network.getRoutes();
		// ��ѯ��װ���
		for(NetworkWrapper net:routes){
			List<BundleInfo> bdls=net.getBundles();
			int size=search(nameVersion,bdls).size();
			if(size>0){
				this.execute(action,net,nameVersion);
			}
		}
	}
	// ָ����������
	private void execute(String action,String addr,String nameVersion){
		NetworkWrapper net=search(addr);
		this.execute(action,net,nameVersion);
	}
	// ִ�в���
	private Bundle execute(String action,NetworkWrapper net,String nameVersion){
		if(net!=null){
			net.call(NAMESPACE,action, nameVersion);
			// ��ӡ�Ǳ�����װ��Ϣ
			if(!network.equals(net)){
				DateFormat format=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				String time=format.format(new Date());
				out.println(String.format("[%s]->[%s:%s-%s]->[%s]->[success]",time,net.getHostInfo().ip,net.getHostInfo().port,action,nameVersion));
			}
			return null;
		}
		return null;
	}
	// ����ʵ����Ŀ
	private void add(String key,Long offset){
		key=BundleUtil.nameVersion(key);
		Long num=checktab.get(key);
		if(num!=null){
			checktab.put(key,num+offset);
		}else{
			checktab.put(key,offset);
		}
	}

	// ����IP�Ͷ˿ڲ��Ҷ�ӦNetworkWrapper����
	private NetworkWrapper search(String addr){
		String ip=addr;
		String port="8080";
		if(addr.contains(":")){
			ip=addr.split(":")[0];
			port=addr.split(":")[1];
		}
		List<NetworkWrapper> routes=network.getRoutes();
		for(NetworkWrapper net:routes){
			HostInfo host=net.getHostInfo();
			if(host.ip.equals(ip)&&host.port.equals(port)){
				return net;
			}
		}
		return null;
	}
	// ����ָ���������
	private List<BundleInfo> search(String nameVersion,List<BundleInfo> arry){
		String name=BundleUtil.name(nameVersion);
		String version=BundleUtil.version(nameVersion);
		
		List<BundleInfo> bundles=new ArrayList<>();
		for(BundleInfo bundle:arry){
			// �Ų�ж�ص����
			if(bundle.status.equals("1")){
				continue;
			}
			// ��ȡ����ļ�ƺͰ汾��
			String bdlName=BundleUtil.name(bundle.name);
			String bdlVerson=BundleUtil.version(bundle.version);
			
			// �Ƚ���������Ŀ�����
			if(version!=null){
				if(bdlName.equals(name)&&bdlVerson.equals(version)){
					bundles.add(bundle);
				}
			}else{
				if(bdlName.equals(name)){
					bundles.add(bundle);
				}
			}
		}
		return bundles;
	}
	
	
}

