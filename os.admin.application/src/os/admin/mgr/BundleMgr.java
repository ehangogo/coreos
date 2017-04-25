package os.admin.mgr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;

import os.admin.job.CheckJob;
import os.core.model.BundleInfo;
import os.core.model.HostInfo;
import os.core.model.ServiceInfo;

/**
 * ����������
 * @author ������
 *
 */
public class BundleMgr {

	private NetworkWrapper network;
	private String NAMESPACE="os.core.provider.CoreShell";
	
	
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
		if(!location.contains(".jar")){
			location+=".jar";
		}
		if(!location.startsWith("file:/")&&!location.startsWith("/")){
			 String url=System.getProperty("os.repertory");
			 if(url==null){
				 url=System.getenv().get("OS_REPERTORY");
			 }
			 if(url==null){
				 url="D:/tmp";
			 }
			 try {
				location=Paths.get(url+"/"+location).toUri().toURL().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
   	    // �����ʶ
	  	String bdlName=parseJar(location);
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
			long len=(num-size)>unsize?unsize:(num-size);
			for(int i=0;i<len;i++){
				install(unstallNodes.get(i),location);
			}
		// ����
		}else if(num<size){
			long len=size-num;
			for(int i=0;i<len;i++){
				uninstall(installNodes.get(i),bdlName);
			}
		}
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
		}else{
			String location=null;
			if(nameVersion.indexOf("moudel")>-1){
				location=nameVersion.split(":")[0]+".provider.jar";
			}else{
				location=nameVersion.split(":")[0]+".api.jar";
			}
			this.install(location, num);
			this.start(nameVersion);
			this.add(nameVersion, -1L);
		}
	}
	
	// ��̬Ǩ��
	public void move(String nameVersion,String from,String to){
		String location=nameVersion.split(":")[0]+".jar";
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
		this.update(nameVersion,10L);
	}
	public void update(String nameVersion,Long time){
		List<NetworkWrapper> routes=network.getRoutes();
		for(NetworkWrapper net:routes){
			try{
				List<BundleInfo> bdls=net.getBundles();
				int size=search(nameVersion,bdls).size();
				if(size>0){
					this.execute("update",net,nameVersion);
				}
				try {
					Thread.sleep(time*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}catch(Exception e){
				break;
			}
		}
	}
	// ָ����������
	public void update(String addr,String nameVersion){
		this.execute("update",addr,nameVersion);
	}
	
	// �쳣�ָ�
	public void check() {
		checktab.forEach((key,val)->{
			System.out.println(key+"--"+val);
		});
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
				this.change(name,num);
			}
		});
	}
	
	// ��ѯ�ӿ�
	public List<ServiceInfo> getServices(){
		List<ServiceInfo> res=new ArrayList<>();
		network.getRoutes().stream().forEach(net->{
			res.addAll(net.getServices());
		});
		return res;
	}
	public List<BundleInfo> getBundles(){
		List<BundleInfo> res=new ArrayList<>();
		network.getRoutes().stream().forEach(net->{
			res.addAll(net.getBundles());
		});
		return res;
	}
	public List<HostInfo> getNodes(){
		List<HostInfo> res=new ArrayList<>();
		network.getRoutes().stream().forEach(net->{
			res.add(net.getHostInfo());
		});
		return res;
	}
	
	// ���ߺ���
	private void install(NetworkWrapper net,String location){
		if(!location.contains(".jar")){
			location+=".jar";
		}
		if(!location.startsWith("file:/")&&!location.startsWith("/")){
			 String url=System.getProperty("os.repertory");
			 if(url==null){
				 url=System.getenv().get("OS_REPERTORY");
			 }
			 if(url==null){
				 url="D:/tmp";
			 }
			 try {
				location=Paths.get(url+"/"+location).toUri().toURL().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String nameVersion=parseJar(location);
	  	if(nameVersion==null)return;
		if(net!=null){
			List<BundleInfo> bdls=net.getBundles();
			int size=search(nameVersion,bdls).size();
			if(size<=0){
				net.call(NAMESPACE, "install", location);
				add(nameVersion,1L);
			}
		}
	}
	private void uninstall(NetworkWrapper net,String nameVersion){
		if(net!=null){
			List<BundleInfo> bdls=net.getBundles();
			int size=search(nameVersion,bdls).size();
			if(size>0){
				net.call(NAMESPACE, "uninstall",nameVersion);
				add(nameVersion,-1L);
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
			return null;
		}
		return null;
	}
	// ����ʵ����Ŀ
	private void add(String key,Long offset){
		
		String name=key;
		String version=null;
		if(name.indexOf(":")>-1){
			name=key.split(":")[0];
			version=key.split(":")[1];
			version=version.substring(0,5);
		}
		name=name.replace(".provider","").replace(".api","").replace(".application","");
		key=name+":"+version;
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
		String name=nameVersion;
		String version=null;
		if(name.indexOf(":")>-1){
			name=nameVersion.split(":")[0];
			version=nameVersion.split(":")[1];
			version=version.substring(0,5);
		}
		List<BundleInfo> bundles=new ArrayList<>();
		for(BundleInfo bde:arry){
			// �Ų�ж�ص����
			if(bde.status.equals("1")){
				continue;
			}
			String bdeName=bde.name;
			if(bdeName.equals(name)||bdeName.equals(name+".provider")||bdeName.equals(name+".api")||bdeName.equals(name+".application")){
				if(version!=null){
					if(bde.version.startsWith(version)){
						bundles.add(bde);
					}
				}else{
					bundles.add(bde);
				}
			}
		}
		return bundles;
	}
	private String parseJar(String location){
		final StringBuilder name=new StringBuilder();
		final StringBuilder version=new StringBuilder();
		JarFile jar=null;
		try{
			jar=new JarFile(new File(location.replace("file:/",""))); 
		    Manifest manifest = jar.getManifest();
		    manifest.getMainAttributes().forEach((key,value)->{
		    	if(key.toString().equals("Bundle-SymbolicName")){
		    		name.append(value.toString());
		    	}
		    	 if(key.toString().equals("Bundle-Version")){
		    		 version.append(value.toString());
		    	 }
		    });
		    if(jar!=null) jar.close();
		}catch(Exception e){
			if(jar!=null)
				try {
					jar.close();
				}catch(IOException e1){}
			return null;
		}
		String namestr=name.toString().replace(".api","").replace(".provider","").replace(".application","");
		String versionstr=version.toString().substring(0,5);
		String bdlName=namestr+":"+versionstr;
		return bdlName;
	}
	// �ṩ�ӿ�
	public void refresh(String...args){
		List<NetworkWrapper> routes=network.getRoutes();
		for(NetworkWrapper route:routes){
			route.call(NAMESPACE,"refresh",args);
		}
	}
	public void resolve(String...args){
		List<NetworkWrapper> routes=network.getRoutes();
		for(NetworkWrapper route:routes){
			route.call(NAMESPACE,"resolve",args);
		}
	}
	public void startLevel(String... args){
		List<NetworkWrapper> routes=network.getRoutes();
		for(NetworkWrapper route:routes){
			route.call(NAMESPACE,"startLevel",args);
		}
	}
	public void bundleLevel(String... args){
		List<NetworkWrapper> routes=network.getRoutes();
		for(NetworkWrapper route:routes){
			route.call(NAMESPACE,"bundleLevel",args);
		}
	}
	
}

