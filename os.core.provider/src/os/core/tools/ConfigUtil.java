package os.core.tools;


import java.io.FileInputStream;
import java.util.Properties;

/**
 * 系统配置类
 * @author 尹行欣
 *
 */
public class ConfigUtil {
	
	// 主机配置信息
	public static String HOST_IP="os.host.ip";
	public static String HOST_PORT="os.host.port";
	public static String HOST_NAME="os.host.name";
	
	// 路由组件信息
	public static String ROUTE_URL="os.route.url";
	
	// 数据库连接信息
	public static String DB_URL="os.db.url";
	public static String DB_DATABASE="os.db.database";
	public static String DB_USERNAME="os.db.username";
	public static String DB_PASSWORD="os.db.password";
	
	// 组件仓库地址
	public static String REPERTORY_PATH="os.repertory.path";
	
	
	private static Properties config = new Properties();
	// 配置文件所在路径
	private static String defualt="config.properties";
	static{
		try{
			
			// 从启动参数中读取项目家目录
			String home=System.getProperty("os.home");
			
			// 从环境变量中读取项目家目录
			if(StringUtil.isEmpty(home)){
				home=System.getenv().get("OS_HOME");
			}
			// 读取配置文件
			if(!StringUtil.isEmpty(home)){
				config.load(new FileInputStream(home+"/conf/config.properties"));
			// 从当前类路径下读取
			}else{
				config.load(ConfigUtil.class.getResourceAsStream(defualt));
			}
			
		}catch(Exception e){}
	}
	
	public static String get(String key){
		return get(key,null);
	}
	public static String get(String key,String def){
		
		// 配置优先级  环境变量->启动参数->配置文件
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
