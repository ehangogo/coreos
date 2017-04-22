package os.core.provider;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.tracker.ServiceTracker;

import os.core.api.CoreOS;
import os.core.model.BundleInfo;
import os.core.model.ServiceInfo;
import os.core.tools.JarUtil;
import os.core.tools.ReflectUtil;
import os.core.tools.StringUtil;

/**
 * 软件内核
 * 提供基础组件安装卸载等操作
 */
@Component(name="os.core")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CoreImpl implements CoreOS{
	
	BundleContext context=null;
	StartLevel startLevel=null;
	PackageAdmin pageAdmin=null;
	
	// 本地服务
	Set<ServiceInfo> services=new HashSet<>();
	// 本地组件
	Set<BundleInfo>  bundles=new HashSet<>();
		
	// 启动
	@Activate void start(ComponentContext componet,BundleContext context) {
		
		this.context=context;
		// 监听本地节点的安装和卸载
		this.context.addBundleListener(new BundleListener(){
			@Override
			public void bundleChanged(BundleEvent event) {
				BundleInfo bleInfo=BleInfo(event.getBundle());
				if(bleInfo!=null){
					// update
					if(bundles.contains(bleInfo)){
						bundles.remove(bleInfo);
					}
					bundles.add(bleInfo);
				}
			}
		});
		for(Bundle bundle:context.getBundles()){
			BundleInfo bleInfo=BleInfo(bundle);
			if(bleInfo!=null){
				// update
				if(bundles.contains(bleInfo)){
					bundles.remove(bleInfo);
				}
				bundles.add(bleInfo);
			}
		};
		
		// 监听本地服务的注册和注销  
		this.context.addServiceListener(new ServiceListener(){
			@Override
			public void serviceChanged(ServiceEvent event) {
				ServiceInfo srvInfo=SrvInfo(event.getServiceReference());
				if(srvInfo!=null){
					if(event.getType()==ServiceEvent.UNREGISTERING){ 
						services.remove(srvInfo);
					}else{
						if(services.contains(srvInfo)){
							services.remove(srvInfo);
						}
						services.add(srvInfo);
					}
				}
			}
		});
		try{
			ServiceReference[] refs = context.getAllServiceReferences(null,null);
	        if(refs != null){
	        	for(ServiceReference ref : refs){
	        		ServiceInfo srvInfo=SrvInfo(ref);
	        		if(srvInfo!=null){
		        		if(services.contains(srvInfo)){
							services.remove(srvInfo);
						}
		            	services.add(srvInfo);
	        		}
	            }
	        }
	        String id="0";
	        try{
	        	id=componet.getServiceReference().getProperty("service.id").toString();
	        }catch(Exception e){};
	        services.add(SrvInfo(CoreOS.class,id));
	        
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	// 注入
	@Reference void setStartLevel(StartLevel startLevel){
		this.startLevel=startLevel;
	}
	// 注入
	@Reference void setPackageAdmin(PackageAdmin pageAdmin){
		this.pageAdmin=pageAdmin;
	}
	
	// 管理接口
	@Override
	public Bundle install(String location) throws BundleException{
		location=JarUtil.getRepertPath(location);
		return context.installBundle(location);
	}
	@Override
	public Bundle uninstall(String nameVersion) throws BundleException{
		Bundle bundle=null;
		List<Bundle> bundles=this.search(nameVersion);
		if(bundles!=null&&bundles.size()>0){
			for(Bundle bde:bundles){
				bundle=bde;
				bde.uninstall();
				break;
			}
		}
		return bundle;
	}
	@Override
	public Bundle start(String nameVersion)throws BundleException{
		Bundle bundle=null;
		List<Bundle> bundles=this.search(nameVersion);
		if(bundles!=null&&bundles.size()>0){
			for(Bundle bde:bundles){
				bundle=bde;
				bde.start();
			}
		}
		return bundle;
	}
	@Override
	public Bundle stop(String nameVersion)throws BundleException{
		Bundle bundle=null;
		List<Bundle> bundles=this.search(nameVersion);
		if(bundles!=null&&bundles.size()>0){
			for(Bundle bde:bundles){
				bundle=bde;
				bde.stop();
				break;
			}
		}
		return bundle;
	}
	@Override
	public Bundle update(String nameVersion)throws BundleException{
		Bundle bundle=null;
		List<Bundle> bundles=this.search(nameVersion);
		if(bundles!=null&&bundles.size()>0){
			for(Bundle bde:bundles){
				bundle=bde;
				bde.update();
				break;
			}
		}
		return bundle;
	}
	
	// 查询接口
	@Override
	public List<BundleInfo> getBundles(){
		List list=Arrays.asList(bundles.toArray());
		return list;
	}
	@Override
	public List<BundleInfo> getRepertories() {
		// 组件仓库地址
		 String url=null;
		
		 // 从启动参数和环境变量中获取仓库地址
		 url=System.getProperty("os.repertory");
		 if(StringUtil.isEmpty(url)){
			 url=System.getenv().get("OS_REPERTORY");
		 }
		 if(StringUtil.isEmpty(url)){
			 url="D:/tmp";
		 }
		 
		 List<String> list=JarUtil.bunldeList(url);
		 List<BundleInfo> res=new ArrayList<>();
		 list.stream().forEach(row->{
			 BundleInfo info=new BundleInfo();
			 info.name=row.split(":")[0];
			 info.version=row.split(":")[1];
			 info.location=row.split(":")[2];
			 res.add(info);
		 });
		return res;
	}
	@Override
	public List<ServiceInfo> getServices() {
		List list=Arrays.asList(services.toArray());
		return list;
	}
	@Override
	public Object getService(String namespace) {
		// 本地查找
		if(context!=null){
			// 根据类名查找对应的服务
			ServiceTracker tracker = new ServiceTracker(context,namespace,null);tracker.open();
			Object service =tracker.getService();
			return service;
		}
		return null;
	}
	
	// 组件调用接口
	@Override
	public <T> T call(String namespace,String method,Object... args){
		// 本地查找
		if(context!=null){
			// 根据类名查找对应的服务
			ServiceTracker tracker = new ServiceTracker(context,namespace,null);tracker.open();
			Object service =tracker.getService();
				// 反射调用服务
				if(service!=null){
					// 目标类
					Class clazz=service.getClass();
					// 目标参数
			    	List params=new ArrayList();
			    	for(Object obj:args){
			    		params.add(obj);
			    	}
			    	// 查询目标方法
			    	Method func=ReflectUtil.search(clazz,method, params);
					
					// 本地调用
					if(func!=null){
						try{
							return (T)ReflectUtil.invoke(service, method, params);
						}catch (Exception e) {
							tracker.close();
							throw new RuntimeException("本地调用错误");
						}
					// 远程调用
					}else{
						return (T)rmtCall(namespace,method,args);
					}
				}else{
					return (T)rmtCall(namespace,method,args);
				}
		}
	
		return null;
	}
	Object rmtCall(String namespace,String service,Object... args) {
		// 获取网卡
		ServiceTracker tracker = new ServiceTracker(context,"os.network.api.Network",null);tracker.open();
		Object network =tracker.getService();
		// 通过网卡调用服务
		if(network!=null){
			try{
				Method func=network.getClass().getMethod("call",new Class[]{String.class,String.class,Object[].class});
				return func.invoke(network, namespace,service,args);
			}catch(Exception e){
				throw new RuntimeException("远程调用错误");
			}
		}
		return null;
	}
	// 工具函数
	List<Bundle> search(String nameVersion){
		List<Bundle> bundles=new ArrayList<>();
		if(nameVersion.matches("\\d+")){
			Bundle bundle=context.getBundle(Long.parseLong(nameVersion));
			bundles.add(bundle);
		}else{
			String name=nameVersion;
			String version=null;
			if(name.indexOf(":")>-1){
				name=nameVersion.split(":")[0];
				version=nameVersion.split(":")[1];
			}
			for(Bundle bde:context.getBundles()){
				String bdeName=bde.getSymbolicName();
				if(bdeName.equals(name)||bdeName.equals(name+".provider")||bdeName.equals(name+".api")||bdeName.equals(name+".application")){
					if(version!=null){
						if(bde.getVersion().toString().startsWith(version)){
							bundles.add(bde);
						}
					}else{
						bundles.add(bde);
					}
				}
			}
		}
		return bundles;
	}
	ServiceInfo SrvInfo(Class clazz,String id){
		ServiceInfo srvInfo=new ServiceInfo();
		try{
			srvInfo.id=id;
	    	srvInfo.name=clazz.getName();
	    	List<String> methods=new ArrayList<>();
	    	for(Method m:clazz.getDeclaredMethods()){
	    		methods.add(m.getName());
			}
	    	srvInfo.status="RUNNING";
	    	srvInfo.methods=methods;
	    	
	    	if(srvInfo.name==null){return null;};
		}catch(Exception e){ e.printStackTrace();return null;}
    	return srvInfo;
	}
	ServiceInfo SrvInfo(ServiceReference ref){
		
	    Pattern pattern = Pattern.compile("\\{(.*)\\}=\\{(.*)\\}");
	    Matcher matcher = pattern.matcher(ref.toString());
	    
	    String clz=null;
	    Map<String,String> props=new HashMap<>();
    	while(matcher.find()){
    		clz=matcher.group(1);
    		for(String item:matcher.group(2).split(",\\s")){
    			try{
    				props.put(item.split("=")[0],item.split("=")[1]);
    			}catch(Exception e){};
    		}
    	}
    	
		ServiceInfo srvInfo=new ServiceInfo();
		try{
			Object obj=context.getService(ref);
			if(obj!=null){
		    	Class clazz=obj.getClass();
		    	for(Class<?> inter:clazz.getInterfaces()){
		    		if(inter.getName().equals(clz)){
		    			clazz=inter;
		    		}
		    	}
		    	List<String> methods=new ArrayList<>();
		    	Method ms[]=null;
		    	try{
		    		// hock
		    		if(clazz.getName().equals("org.apache.felix.gogo.command.OBR")){
		    			return null;
		    		}
		    		if(clazz.getName().equals("org.apache.felix.gogo.runtime.threadio.ThreadIOImpl")){
		    			return null;
		    		}
		    		ms=clazz.getDeclaredMethods();
		    	}catch(Exception e){
		    		ms=clazz.getMethods();
		    	}finally{}
		    	for(Method m:ms){
		    		methods.add(m.getName());
				}
		    	
		    	// save
		    	srvInfo.id=props.get("service.id");
		    	srvInfo.name=clz;
		    	srvInfo.status="RUNNING";
		    	srvInfo.methods=methods;
		    	// 所属组件ID
		    	srvInfo.bundle=ref.getBundle().getBundleId()+"";
			}else{
				return null;
			}
		}catch(Exception e){ e.printStackTrace();return null;}
    	return srvInfo;
	 }
	BundleInfo BleInfo(Bundle bundle){
		BundleInfo bleInfo=new BundleInfo();
		try{
			bleInfo.id=bundle.getBundleId()+"";
			bleInfo.name=bundle.getSymbolicName();
			bleInfo.version=bundle.getVersion().toString();
			bleInfo.status=bundle.getState()+"";
			bleInfo.location=bundle.getLocation();
			
			ServiceReference[] list=null;
			try{
				list=bundle.getRegisteredServices();
			}catch(Exception e){};
			if(list!=null){
				// 添加组件对应的服务信息
				for(ServiceReference srv :list){
					ServiceInfo service=SrvInfo(srv);
					if(service!=null){
						bleInfo.services.add(service);
					}
				}
			}
			
		}catch(Exception e){e.printStackTrace();}
		return bleInfo;
	 }
	Object translate(Object obj, Class<?> clz) {
		String name=clz.getName();
		boolean flag1=obj instanceof Integer;
		boolean flag2=obj instanceof Long;
		boolean flag3=obj instanceof Double;
		boolean flag4=obj instanceof Float;
		
		if(flag1||flag2||flag3||flag4||obj instanceof String){
			if(name.equals("int")||name.equals(Integer.class.getName())){
				return Integer.parseInt(obj.toString());
			}	
			if(name.equals("long")||name.equals(Long.class.getName())){
				return Long.parseLong(obj.toString());
			}
			if(name.equals("float")||name.equals(Float.class.getName())){
				return Float.parseFloat(obj.toString());
			}
			if(name.equals("double")||name.equals(Double.class.getName())){
				return Double.parseDouble(obj.toString());
			}
		}
		
		return obj;
	}
	
	
	// 其他接口
	@Override
	public BundleContext getContext(){
		return context;
	}
	// 其他接口
	@Override
	public List<Bundle> refresh(String...args){
		if(args.length==0){
			pageAdmin.refreshPackages(null);
			return null;
		}else{
			List<Bundle> bundles = new ArrayList<Bundle>();
			for(String arg:args){
				bundles.addAll(search(arg));
			}
			pageAdmin.refreshPackages(bundles.toArray(new Bundle[bundles.size()]));
			return bundles;
		}
	}
	@Override
	public List<Bundle> resolve(String...args){
		if(args.length==0){
			pageAdmin.resolveBundles(null);
			return null;
		}else{
			List<Bundle> bundles = new ArrayList<Bundle>();
			for(String arg:args){
				bundles.addAll(search(arg));
			}
			pageAdmin.resolveBundles(bundles.toArray(new Bundle[bundles.size()]));
			return bundles;
		}
	}
	@Override
	public String startLevel(String... args){
		if(args==null||args.length==0){
			int level=startLevel.getStartLevel();
			return level+"";
		}else{
			startLevel.setStartLevel(Integer.parseInt(args[0]));
			return null;
		}
		
	}
	@Override
	public String bundleLevel(String... args){
		
		if(args==null||args.length==0) return null;
		if(args.length==1){
			String args1=args[0].toString();
			Bundle bundle=search(args1).get(0);
			int level=startLevel.getBundleStartLevel(bundle);
			return level+"";
		}
		if(args.length==2){
			String args1=args[0].toString();
			String args2=args[1].toString();
			if(args1.equals("-i")){
				startLevel.setInitialBundleStartLevel(Integer.parseInt(args2));
				return null;
			}else{
				Bundle bundle=search(args1).get(0);
				startLevel.setBundleStartLevel(bundle,Integer.parseInt(args2));
				return null;
			}
		}
		return null;
	}
	@Override
	public Bundle install(String location,InputStream input) throws BundleException{
		return context.installBundle(location, input);
	}
	
}
