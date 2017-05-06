package os.core.conf;


import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import os.core.tools.StringUtil;

/**
 * ϵͳ������
 * @author ������
 *
 */
public class Config {
	
	// ����������Ϣ
	public static String HOST_IP="os.host.ip";
	public static String HOST_PORT="os.host.port";
	public static String HOST_NAME="os.host.name";
	
	// ·�������Ϣ
	public static String ROUTE_URL="os.route.url";
	
	// ���ݿ�������Ϣ
	public static String DB_URL="os.db.url";
	public static String DB_DATABASE="os.db.database";
	public static String DB_USERNAME="os.db.username";
	public static String DB_PASSWORD="os.db.password";
	
	// ����ֿ��ַ
	public static String REPERTORY_PATH="os.repertory.path";
	
	// ϵͳ��ʱĿ¼
	public static String COREOS_TMP="os.coreos.tmp";
	
	public static Properties config = new Properties();
	// �����ļ�����·��
	private static String defualt="config.properties";
	static{
		try{
			
			// �����������ж�ȡ��Ŀ��Ŀ¼
			String home=System.getProperty("os.home");
			
			// �ӻ��������ж�ȡ��Ŀ��Ŀ¼
			if(StringUtil.isEmpty(home)){
				home=System.getenv().get("OS_HOME");
			}
			
			// ���������ж�ȡ�����ļ�
			String conf=System.getProperty("os.conf");
			if(conf!=null){
				config.load(new FileInputStream(conf));
			
			// ���ԴӼ�Ŀ¼�¶�ȡ�����ļ�
			}else{
				// ��ȡ�����ļ�
				if(!StringUtil.isEmpty(home)){
					config.load(new FileInputStream(home+"/conf/config.properties"));
				// �ӵ�ǰ��·���¶�ȡ
				}else{
					config.load(Config.class.getResourceAsStream(defualt));
				}
			}
			
			
			config.putAll(System.getProperties());
			
			String port=get("org.osgi.service.http.port");
			if(port!=null){
				config.put(Config.HOST_PORT,port);
			}
			String path=config.getProperty(Config.REPERTORY_PATH);
			if(!Paths.get(path).isAbsolute()){
				config.setProperty(Config.REPERTORY_PATH, Paths.get(home,path).toString());
			}
			
		}catch(Exception e){}
	}
	
	public static String get(String key){
		return config.getProperty(key);
	}
	public static String get(String key,String def){
		return config.getProperty(key, def);
	}
	public static void set(String key,String val){
		config.setProperty(key, val);
	}
	
	public static void update(ConfigurationAdmin cm) {
		// ����Jetty������ͨѶ�˿�
		try{
			Configuration conf=cm.getConfiguration("org.apache.felix.http",null);
			if(conf!=null){
				Dictionary<String,Object> param=conf.getProperties();
				if(param==null){
					param=new Hashtable<String,Object>();
				}
				param.put("org.osgi.service.http.port",get(Config.HOST_PORT,"8080"));
				conf.update(param);
			}
		}catch(Exception e){}
	}
	
}
