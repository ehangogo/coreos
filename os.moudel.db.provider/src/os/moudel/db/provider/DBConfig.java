package os.moudel.db.provider;

import java.io.IOException;
import java.util.Properties;

import os.core.conf.Config;
/**
 * ���ݿ�������
 * @author ������
 */
public class DBConfig {
	private static Properties config = new Properties();
	static{
		try {
			config.load(DBConfig.class.getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// �û���
	public static String username(){
		String username=Config.get(Config.DB_USERNAME);
		if(username==null){
			username=config.getProperty("jdbc.username");
		}
		return username;
	}
	// ����
	public static String password(){
		String password=Config.get(Config.DB_PASSWORD);
		if(password==null){
			password=config.getProperty("jdbc.password");
		}
		return password;
	}
	// ������
	public static String driver(){
		String driver=config.getProperty("jdbc.driver");
		return driver;
	}
	// ���Ӵ�
	public static String url(){
		String url=Config.get(Config.DB_URL);
		if(url!=null){
			url="jdbc:mysql://"+url+"/";
		}
		if(url==null){
			url=config.getProperty("jdbc.url");
		}
		String db=Config.get(Config.DB_DATABASE);
		if(db==null){
			db=config.getProperty("jdbc.db");
		}
		// ���ݱ���
		String code=config.getProperty("jdbc.code");
		return url+db+code;
	}
	// ������Ӵ�
	public static String maxconnects(){
		return config.getProperty("jdbc.maxconnects");
	}
	// rootȨ�����Ӵ�
	public static String rooturl(){
		String url=Config.get(Config.DB_URL);
		if(url!=null){
			url="jdbc:mysql://"+url+"/";
		}
		if(url==null){
			url=config.getProperty("jdbc.url");
		}
		// ���ݱ���
		String code=config.getProperty("jdbc.code");
		return url+code;
	}
}
