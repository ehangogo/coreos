package os.core.provider;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.core.api.CoreOS;
import os.core.conf.Config;
import os.core.model.BundleInfo;
import os.core.model.ConfigInfo;
import os.core.model.ServiceInfo;
import os.core.tools.HostUtil;
import osgi.enroute.debug.api.Debug;
//@Component ���ע��
//name ����ı���
//property �������������
//immediate ��ʾ�����������Ķ�������ʱ���Ƿ����ϳ�ʼ��new���������󣬶����ǵȵ���ĵط�ʹ��ʱ���Ž��г�ʼ��
//service ָ������ʵ�ֵĽӿ��࣬�еĲ�����д��û�е�ֱ��д�Լ������
//���þ����ܹ�����getService��ȡ������� 
@Component(name="os.shell",property = { 
		Debug.COMMAND_SCOPE + "=core",
		Debug.COMMAND_FUNCTION + "=help",
		Debug.COMMAND_FUNCTION + "=call",
		Debug.COMMAND_FUNCTION + "=services",
		Debug.COMMAND_FUNCTION + "=inspect",
		Debug.COMMAND_FUNCTION + "=bundles",
		Debug.COMMAND_FUNCTION + "=install",
		Debug.COMMAND_FUNCTION + "=start",
		Debug.COMMAND_FUNCTION + "=stop",
		Debug.COMMAND_FUNCTION + "=restart",
		Debug.COMMAND_FUNCTION + "=uninstall",
		Debug.COMMAND_FUNCTION + "=repertories",
		Debug.COMMAND_FUNCTION + "=store",
		Debug.COMMAND_FUNCTION + "=update",
		Debug.COMMAND_FUNCTION + "=config"
	},service=CoreShell.class,immediate=true)
public class CoreShell {
	
	// ϵͳ�ں�
	CoreOS coreos=null;
	@Reference
	public void setCoreOS(CoreOS coreos){
		this.coreos=coreos;
	}
	
	// �����
	PrintStream out=System.out;
	
	
	// �����
	String[] cmds;
	@Activate void start(ComponentContext context) {
		this.cmds=(String[])context.getProperties().get(Debug.COMMAND_FUNCTION);
	}
	// �����нӿ�
	public void help(){
		Stream.of(cmds).forEach(out::println);
	}
	
	// ����ӿ�
	public void install(String location) throws BundleException {
		Bundle bundle=coreos.install(location);
		print("install",bundle);
	}
	public void uninstall(String nameVersion) throws BundleException {
		Bundle bundle=coreos.uninstall(nameVersion);
		print("uninstall",bundle);
	}
	public void start(String nameVersion) throws BundleException {
		Bundle bundle=coreos.start(nameVersion);
		print("start",bundle);
	}
	public void stop(String nameVersion) throws BundleException {
		Bundle bundle=coreos.stop(nameVersion);
		print("stop", bundle);
	}
	public void update(String nameVersion) throws BundleException {
		Bundle bundle=coreos.update(nameVersion);
		print("update", bundle);
	}
	public void restart(String nameVersion) throws BundleException {
		Bundle bundle=coreos.stop(nameVersion);
		bundle=coreos.start(nameVersion);
		print("restart", bundle);
	} 
	public void config() throws BundleException {
		List<ConfigInfo> res=coreos.listConf();
		if(res==null||res.size()==0){return;};
		List<String> infos=new ArrayList<>();
		infos.add("id|key|value");
		for(int i=0;i<res.size();i++){
			ConfigInfo conf=res.get(i);
			String key=conf.key;
			String val=conf.value;
			infos.add(String.format("%s|%s|%s",i+1,key,val));
		}
		print(infos);
	}
	public void config(String key){
		String val=coreos.getConf(key, null);
		this.stdout("get:%s->%s",new Object[]{key,val});
	}
	public void config(String key,String val){
		Config.set(key, val);
		this.stdout("set:%s->%s",new Object[]{key,val});
	}
	
	// ��ѯ�ӿ�
	public void bundles(){
		bundles("^os[.].*");	
	}
	public void bundles(String filter){
		List<BundleInfo> res=coreos.getBundles();
		if(res==null||res.size()==0){return;};
		List<String> infos=new ArrayList<>();
		infos.add("id|name|version|status");
		for(int i=0;i<res.size();i++){
			BundleInfo bundle=res.get(i);
			int type=Integer.parseInt(bundle.status);
			 
			String status="STARTING";
			if(type==Bundle.INSTALLED){
				status="INSTALLED";
			}
			if(type==Bundle.UNINSTALLED){
				status="UNINSTALLED";
			}
			if(type==Bundle.RESOLVED){
				status="RESOLVED";
			}
			if(type==Bundle.STARTING){
				status="START...";
			}
			if(type==Bundle.STOPPING){
				status="STOP...";
			}
			if(type==Bundle.ACTIVE){
				status="STARTING";
			}
			String name=bundle.name;
			String version=bundle.version;
			if(filter.equals("all")){
				infos.add(String.format("%s|%s|%s|%s",bundle.id,name,version,status));
			}else{
				if(bundle.name.matches(filter)){
					if(!status.equals("UNINSTALLED")){
						infos.add(String.format("%s|%s|%s|%s",bundle.id,name,version,status));
					}
				}
			}
		}
		print(infos);
	}
	// ����ֿ�
	public void store(){
		repertories();
	}
	public void repertories(){
		List<BundleInfo> res=coreos.getRepertories();
		if(res==null||res.size()==0){return;};
		List<String> infos=new ArrayList<>();
		infos.add("id|name|version");
		for(int i=0;i<res.size();i++){
			BundleInfo bundle=res.get(i);
			String location=bundle.location;
			String version=bundle.version;
			infos.add(String.format("%s|%s|%s",i+1,location,version));
		}
		print(infos);
	}
	
	public void services(){
		services("^os[.].*");
	}
	public void services(String filter){
		List<ServiceInfo> res=coreos.getServices();
		if(res==null||res.size()==0){return;};
		List<String> infos=new ArrayList<>();
		infos.add("id|name|status|methods");
		for(int i=0;i<res.size();i++){
			ServiceInfo srvInfo=res.get(i);
			String methods="[";
			if(srvInfo.methods!=null){
				int count=0;
				for(String m:srvInfo.methods){
					methods+=m+",";
					count++;
					if(count>3){
						break;
					}
				}
			}
			methods=methods.replaceAll(",$","");
			methods+="]";
			if(srvInfo.name!=null){
				if(filter.equals("all")){
					infos.add(String.format("%s|%s|%s|%s",srvInfo.id,srvInfo.name,srvInfo.status,methods));
				}else{
					if(srvInfo.name.matches(filter)){
						infos.add(String.format("%s|%s|%s|%s",srvInfo.id,srvInfo.name,srvInfo.status,methods));
					}
				}
			}
		}
		print(infos);
	}
	public void inspect(String service){
		if(service==null) return;
		List<ServiceInfo> all=coreos.getServices();
		List<ServiceInfo> res=new ArrayList<>();
		for(Object obj:all){
			ServiceInfo srv=(ServiceInfo)obj;
			if(service.matches("\\d+")){
				if(srv.id!=null&&srv.id.equals(service)){
					res.add(srv);
				}
			}else{
				if(srv.name!=null&&srv.name.equals(service)){
					res.add(srv);
				}
			}
		}
		if(res==null||res.size()==0){return;};
		List<String> infos=new ArrayList<>();
		infos.add("id|name|status|methods");
		for(int i=0;i<res.size();i++){
			ServiceInfo srvInfo=res.get(i);
			if(srvInfo.methods!=null){
				int index=1;
				for(String m:srvInfo.methods){
					infos.add(String.format("%d|%s|%s|%s",index++,srvInfo.name,"RUNNING",m));
				}
				break;
			}
		}
		print(infos);
	}
	
	// ������ýӿ�
	public void call(String namespace, String method, Object... args){
		Object res=null;
		try{
			res=coreos.call(namespace,method,args);
		}catch(Exception e){}
		String params="";
		for(Object arg:args){
			if(arg!=null){
				params+=arg.toString()+",";
			}
		}
		// print
		params=params.replaceAll(",$", "");
		String address=HostUtil.address();
		String port=HostUtil.port();
		DateFormat format=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String time=format.format(new Date());
		if(port!=null){
			out.println(String.format("[%s]->[%s:%s-%s]->[%s.%s(%s)]->[%s]",time,address,port,"call",namespace,method,params,res));
		}else{
			out.println(String.format("[%s]->[%s-%s]->[%s(%s)]->[%s]",time,address,"call",namespace,method,params,res));
		}
	}
	
	
	// ��ӡ��Ϣ������
	void print(String action,Bundle bundle){
		String name=bundle.getSymbolicName().replace(".provider","").replace(".api","").replace(".application","");
		String address=HostUtil.address();
		String port=HostUtil.port();
		DateFormat format=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String time=format.format(new Date());
		String version=bundle.getVersion().toString();
		if(version.split("[.]").length>3){
			int index=version.lastIndexOf(".");
			version=version.substring(0, index);
		}
		if(port!=null){
			out.println(String.format("[%s]->[%s:%s-%s]->[%s:%s]->[success]",time,address,port,action,name,version));
		}else{
			out.println(String.format("[%s]->[%s-%s]->[%s:%s]->[success]",time,address,action,name,version));
		}
	}
	void print(List<String> lines){
		if(lines==null||lines.size()==1){
			System.out.println("empty data to print");
			return;
		}
		List<Integer> maxlen=new ArrayList<>();
		for(String row:lines){
			String args[]=row.split("[|]");
			for(int i=0;i<args.length;i++){
				int len=args[i].getBytes().length;
				if(maxlen.size()<args.length){
					maxlen.add(len);
				}else{
					Integer max=maxlen.get(i);
					if(max<len){
						maxlen.set(i,len+2);
					}
				}
			}
		}
		String header=lines.remove(0);
		
		String line_fmt="";
		List<String> chs=new ArrayList<>();
		for(int i=0;i<header.split("[|]").length;i++){
			line_fmt+="+"+"%-"+maxlen.get(i)+"s";
			String res="";
			for(int j=0;j<maxlen.get(i);j++){
				res+="-";
			}
			chs.add(res);
		}
		
		// ��ӡͷ
		stdout(line_fmt+"+",chs.toArray());
		String fields[]=header.split("[|]");
		String res="|";
		for(int i=0;i<fields.length;i++){
			
			int num=maxlen.get(i)-fields[i].getBytes().length;
			String blank="";
			for(int b=0;b<num;b++){
				blank+=" ";
			}
			res+=fields[i]+blank+"|";
		}
		stdout("%s",new Object[]{res});
		stdout(line_fmt+"+",chs.toArray());
		
		// ��ӡ��
		for(String line:lines){
			fields=line.split("[|]");
			res="|";
			for(int i=0;i<fields.length;i++){
				
				int num=maxlen.get(i)-fields[i].getBytes().length;
				String blank="";
				for(int b=0;b<num;b++){
					blank+=" ";
				}
				res+=fields[i]+blank+"|";
			}
			stdout("%s",new Object[]{res});
		}
		stdout(line_fmt+"+",chs.toArray());
		
	}
	void stdout(String format,Object[] args){
		out.println(String.format(format, args));
	}
	String version(String version){
		String v=version;
		String infos[]=version.split("[.]");
		if(infos.length>3){
			v+=infos[0]+"."+infos[1]+"."+infos[2];
		}
		return v;
	}
	
	// �����ӿ�
	public void install(String location, InputStream input) throws BundleException{
		Bundle bundle=coreos.install(location, input);
		print("install",bundle);
	}
	public void refresh(String... args){
		coreos.refresh(args);
		if(args.length==0){
			print("refresh","");
		}else{
			for(String arg:args){
				print("refresh", arg);
			}
		}
	}
	public void resolve(String... args){
		coreos.resolve(args);
		if(args.length==0){
			print("resolve","");
		}else{
			for(String arg:args){
				print("resolve", arg);
			}
		}
	}
	public String startLevel(String... args){
		if(args==null||args.length==0){
			String level=coreos.startLevel();
			return level;
		}else{
			coreos.startLevel(args[0]);
			out.println("������������:"+args[0]);
			return null;
		}
	}
	public String bundleLevel(String... args){
		if(args==null||args.length==0){
			String level=coreos.bundleLevel();
			return level;
		}else{
			coreos.bundleLevel(args[0]);
			out.println("����Bundle��������:"+args[0]);
			return null;
		}
	}
	void print(String action,String versionName){
		String name=versionName;
		String version="1.0.0";
		if(versionName.equals("")||versionName==null){
			name="all";
			version="all";
		}
		if(versionName.contains(":")){
			name=versionName.split(":")[0];
			version=versionName.split(":")[1];
		}
		DateFormat format=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String time=format.format(new Date());
		String hostname=HostUtil.address();
		out.println(String.format("[%s]-%s->%s [%s:%s] success",time,hostname,action,name,version));
	}
}
