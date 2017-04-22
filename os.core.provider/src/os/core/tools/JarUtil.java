package os.core.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 
 * 用于解析Jar文件
 * @author 尹行欣
 *
 */
public class JarUtil {

	private static String parseJar(File file){
		final StringBuilder name=new StringBuilder();
		final StringBuilder version=new StringBuilder();
		JarFile jar=null;
		try{
			jar=new JarFile(file); 
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
		String namestr=name.toString();
		String versionstr=version.toString().substring(0,5);
		String bdlName=namestr+":"+versionstr+":"+file.getName();
		return bdlName;
	}
	public static List<String> bunldeList(String url){
		 List<String> list=new ArrayList<>();
		 Path path=Paths.get(url);
		 try{
			 Files.list(path).filter(file->{
				 String name=file.getFileName().toString();
				 if(name.endsWith(".jar")){
					 return true;
				 }else{
					 return false;
				 }
			 }).forEach(file->{
				 list.add(parseJar(file.toFile()));
			 });
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return list;
	}
	public static String getRepertPath(String location){
		if(!location.startsWith("file:/")&&!location.startsWith("/")){
			 String url=System.getProperty("os.repertory");
			 if(StringUtil.isEmpty(url)){
				 url=System.getenv().get("OS_REPERTORY");
			 }
			 if(StringUtil.isEmpty(url)){
				 url="D:/tmp";
			 }
			 try {
				location=new File(url+"/"+location).toURI().toURL().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return location;
	}
	public static void main(String args[]) throws Exception{
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
		 list.stream().forEach(row->{
			 System.out.println(row);
		 });
		 
		 
		 
		
	}
}
