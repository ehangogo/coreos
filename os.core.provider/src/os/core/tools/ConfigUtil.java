package os.core.tools;


import java.io.FileInputStream;
import java.util.Properties;

/**
 * ϵͳ������
 * @author ������
 *
 */
public class ConfigUtil {
	
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
	
	
	private static Properties config = new Properties();
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
			// ��ȡ�����ļ�
			if(!StringUtil.isEmpty(home)){
				config.load(new FileInputStream(home+"/conf/config.properties"));
			// �ӵ�ǰ��·���¶�ȡ
			}else{
				config.load(ConfigUtil.class.getResourceAsStream(defualt));
			}
			
		}catch(Exception e){}
	}
	
	public static String get(String key){
		return get(key,null);
	}
	public static String get(String key,String def){
		
		// �������ȼ�  ��������->��������->�����ļ�
		String val=System.getenv().get(key);
		if(val!=null){
			return val;
		}
		val=System.getProperty(key);
		if(val!=null){
			return val;
		}
		return config.getProperty(key, def);
	}
	
	
}
