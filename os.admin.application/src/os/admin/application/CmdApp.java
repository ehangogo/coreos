package os.admin.application;

import java.io.PrintStream;
import java.util.stream.Stream;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.admin.mgr.ClusterMgr;
import os.admin.mgr.NetworkWrapper;
import os.core.api.CoreOS;
import os.core.tools.JarUtil;
import osgi.enroute.debug.api.Debug;

// 系统命令行管理组件
@Component(name="os.cmd",
property = { 
		Debug.COMMAND_SCOPE + "=root",
		Debug.COMMAND_FUNCTION + "=call",
		Debug.COMMAND_FUNCTION + "=help",
		Debug.COMMAND_FUNCTION + "=services",
		Debug.COMMAND_FUNCTION + "=inspect",
		Debug.COMMAND_FUNCTION + "=bundles",
		Debug.COMMAND_FUNCTION + "=nodes",
		Debug.COMMAND_FUNCTION + "=install",
		Debug.COMMAND_FUNCTION + "=start",
		Debug.COMMAND_FUNCTION + "=stop",
		Debug.COMMAND_FUNCTION + "=uninstall",
		Debug.COMMAND_FUNCTION + "=repertories",
		Debug.COMMAND_FUNCTION + "=update",
		Debug.COMMAND_FUNCTION + "=change",
		Debug.COMMAND_FUNCTION + "=move",
		Debug.COMMAND_FUNCTION + "=check"
},service=CmdApp.class)
public class CmdApp {
		
		
	// 依赖对象
	CoreOS coreos=null;
	@Reference void setCoreOS(CoreOS coreos){
		this.coreos=coreos;
	}
	// 输出流
	private ThreadLocal<PrintStream> outMap=new ThreadLocal<>();
	public void setOut(PrintStream out){
		outMap.set(out);
	}
	public PrintStream getOut(){
		PrintStream out=outMap.get();
		if(out==null){
			return System.out;
		}else{
			return out;
		}
	}
	// 命令行指令
	String[] cmds;
	@Activate void start(ComponentContext component) {
		this.cmds=(String[])component.getProperties().get(Debug.COMMAND_FUNCTION);
	}
	
	// 命令行接口
	public void help(){
		Stream.of(cmds).forEach(getOut()::println);
	}
	
	private String NAMESPACE="os.core.provider.CoreShell";
	
	// 集群信息
	public void nodes(){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.nodes();
		}
	}
	public void services(){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.nodes();
		}else{
			coreos.call(NAMESPACE,"services");
		}
	}
	public void services(String addr){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.services(addr);
		}
	}
	public void inspect(String service){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.inspect(service);
		}else{
			coreos.call(NAMESPACE,"inspect",service);
		}
	}
	public void bundles(){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.bundles();
		}else{
			coreos.call(NAMESPACE,"bundles");
		}
	}
	public void bundles(String addr){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.bundles(addr);
		}
	}
	public void repertories(){
		coreos.call(NAMESPACE,"repertories");
	}
	
	// 集群管理
	public void install(String location){
		location=JarUtil.getRepertPath(location);
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.install(location, -1L);
		}else{
			coreos.call(NAMESPACE,"install",location);
		}
		
	}
	public void install(String addr,String location){
		location=JarUtil.getRepertPath(location);
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.install(addr, location);
		}
	}
	public void install(String location,long num){
		location=JarUtil.getRepertPath(location);
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.install(location, num);
		}
	}
	public void start(String addr,String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.start(addr, bundle);
		}
	}
	public void start(String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.start(bundle);
		}else{
			coreos.call(NAMESPACE,"start",bundle);
		}
	}
	public void stop(String addr,String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.stop(addr,bundle);
		}
	}
	public void stop(String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.stop(bundle);
		}else{
			coreos.call(NAMESPACE,"stop",bundle);
		}
	}
	public void uninstall(String addr,String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.uninstall(addr,bundle);
		}
	}
	public void uninstall(String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.uninstall(bundle);
		}else{
			coreos.call(NAMESPACE,"uninstall",bundle);
		}
	}
	public void update(String addr,String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.update(addr,bundle);
		}
	}
	public void update(String bundle,Long time){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.update(bundle,time);
		}
	}
	public void update(String bundle){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.update(bundle);
		}else{
			coreos.call(NAMESPACE,"update",bundle);
		}
	}
	public void change(String bundle,Long num){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.change(bundle,num);
		}
	}
	public void move(String bundle,String from,String to){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.move(bundle,from,to);
		}
	}
	
	// 异常恢复
	public void check(){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.check();
		}
	}
	// 调用服务
	public void call(String namespace,String method,Object... args){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.call(namespace,method,args);
		}else{
			coreos.call(NAMESPACE,method,args);
		}
	}
	public void call(String addr,String namespace,String method,Object... args){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.call(addr,namespace, method, args);
		}
	}
	
	// 其他接口
	public void refresh(String... args){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.refresh(args);
		}else{
			coreos.refresh(args);
		}
	}
	public void resolve(String... args){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.resolve(args);
		}else{
			coreos.resolve(args);
		}
	}
	public void startLevel(String... args){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.startLevel(args);
		}else{
			coreos.startLevel(args);
		}
	}
	public void bundleLevel(String... args){
		ClusterMgr cluser=this.getManager();
		if(cluser!=null){
			cluser.bundleLevel(args);
		}else{
			coreos.bundleLevel(args);
		}
	}
	public ClusterMgr getManager(){
		Object target=this.coreos.getService("os.network.api.Network");
		if(target!=null){
			NetworkWrapper network=new NetworkWrapper(target);
			ClusterMgr cluster=new ClusterMgr(network);
			cluster.setOut(getOut());
			return cluster;
		}
		return null;
	}
	
}
