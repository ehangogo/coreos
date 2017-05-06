package os.core.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import os.core.conf.Config;
import os.core.model.BundleInfo;

/**
 * 
 * ���ڽ���Bundle Jar�ļ�
 * @author ������
 *
 */
public class BundleUtil {
	
	
	public static String name(String nameVersion){
		String name=nameVersion;
		if(name.indexOf(":")>-1){
			name=nameVersion.split(":")[0];
		}
		return name.replaceAll("(.application$|.provider$|.api$)","");
	}
	public static String version(String nameVersion){
		String version=nameVersion;
		if(nameVersion.indexOf(":")>-1){
			version=nameVersion.split(":")[1];
		}
		// �����ֿ�ͷ
		if(version.matches("^\\d.*")){
			if(version.length()>5){
				return version.substring(0,5);
			}else{
				return version;
			}
		}
		return null;
	}
	public static String nameVersion(String str){
		String name=name(str);
		String version=version(str);
		if(version!=null){
			return name+":"+version;
		}else{
			return name;
		}
	}
	public static String nameVersion(BundleInfo bundle){
		return bundle.name+":"+bundle.version;
	}
	public static String bundlePath(String nameVersion){
		String name=nameVersion;
		if(nameVersion.indexOf(":")>-1){
			name=nameVersion.split(":")[0];
		}
		return fullName(name)+".jar";
	}
	// �������ȫ��
	public static String fullName(String simple){
		if(simple.indexOf(":")>-1){
			simple=simple.split(":")[0];
		}
		if(simple.matches("(.application$|.provider$|.api$)")){
			return simple;
		}else{
			if(simple.contains("os.moudel")||simple.matches("(os.core|os.network|os.route)")){
				return simple+".provider";
			}
			if(simple.contains("os.api")){
				return simple+".api";
			}
			return simple+".application";
		}
	}

	// ���ݴ洢·�����������Ϣ
	public static BundleInfo bundleInfo(String location){
		String path=getRepPath(location);
		try {
			return bundleInfo(new File(new URL(path).toURI()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// ��������ļ����������Ϣ
	public static BundleInfo bundleInfo(File bundlejar){
		final StringBuilder name=new StringBuilder();
		final StringBuilder version=new StringBuilder();
		JarFile jar=null;
		try{
			jar=new JarFile(bundlejar); 
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
		
		BundleInfo bundle=new BundleInfo();
		bundle.name=name(name.toString());
		bundle.location=bundlejar.getName();
		bundle.version=version(version.toString());
		return bundle;
	}
	// ����ֿ��б�
	public static List<BundleInfo> getRepList(){
		 List<BundleInfo> list=new ArrayList<>();
		 //�ӻ��������ж�ȡREPERTOTY_PATH,�齨�ֿ�·��
		 //ConfigUtil.get�ж�ȡ˳�򣬻������������������������ļ�
		 Path path=Paths.get(Config.get(Config.REPERTORY_PATH));
		 try{
			 Files.list(path).filter(file->{
				 String name=file.getFileName().toString();
				 if(name.endsWith(".jar")){
					 return true;
				 }else{
					 return false;
				 }
			 }).forEach(file->{
				 list.add(bundleInfo(file.toFile()));
			 });
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return list;
	}
	// ��ȡ�����Ӧ�Ĳֿ��ַ
	public static String getRepPath(String location){
		
		// ���location��Ӧ��·���Ƿ�������
		boolean local=true;
		try{
			//���location�Ƿ���һ�������еĵ�ַ�����粻���ڣ��׳��쳣
			new URL(location).openStream();
			local=false;
			return location;
		}catch(Exception e){
			local=true;
		}
		
		// ���ֿ����Ƿ������Ӧ���
		String bath=Config.get(Config.REPERTORY_PATH);
		if(local){
			File file=Paths.get(bath,location).toFile();
			if(file.exists()){
				try{
					location=file.toURI().toURL().toString();
					return location;
				}catch(Exception e){
					return bath+"/"+location;
				}
			}
		}
		return bath+"/"+location;
	}
	
	public static void main(String args[]) throws Exception{
		 List<BundleInfo> list=BundleUtil.getRepList();
		 list.forEach(row->{
			 System.out.println(row);
		 });
	}
}
