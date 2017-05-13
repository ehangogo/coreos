package os.moudel.person.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.core.api.CoreOS;

/**
 * 个人体征模块
 */
@Component(name = "os.moudel.person",service=PersonService.class)
@SuppressWarnings({"rawtypes","unchecked"})
public class PersonService {
	
	// 数据库访问类
	String DB_CLASS="os.moudel.db.api.DBase";
	
	CoreOS coreos=null;
	@Reference void setCoreOS(CoreOS coreos){
		this.coreos=coreos;
	}
	public List query(String table,Map param) {
		return this.coreos.call(DB_CLASS,"query",table,param);
	}
	public Object update(String table,Map param){
		return this.coreos.call(DB_CLASS,"excute",table,param);
	}
	// 实验测试使用
	public void list(String cmd){
		if(cmd.equals("cmd:tz")){
			debug("bld_fat");
		}
	}
	public void debug(String table){
		Map where=new HashMap<>();
		List<Map<String,Object>> res=this.coreos.call(DB_CLASS,"query",table,where,"time desc","10");
		// 获取表头信息
		StringBuilder header=new StringBuilder();
		if(res!=null&&res.size()>0){
			Map row=res.get(0);
			row.forEach((key,val)->{
				header.append(key+"|");
			});
		}
		// 添加表头信息
		List<String> lines=new ArrayList<>();
		lines.add("用户名|脂肪含量|BMI|基础代谢|体脂判断|体型判断|测量时间|报警|医生建议");
		
		String fields[]={"username","zfhl","bmi","jcdx","tzpd","txpd","time","alert","ysjy"};
		// 数据列
		res.forEach(row->{
			StringBuilder line=new StringBuilder();
			for(String f:fields){
				line.append(row.get(f).toString()+"|");
			}
			// 添加数据列
			lines.add(line.toString().replaceAll("[|]$",""));
		});
		print(lines);
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
		
		// 打印头
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
		
		// 打印体
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
		System.out.println(String.format(format, args));
	}
	
}

