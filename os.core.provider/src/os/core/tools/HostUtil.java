package os.core.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ������ϢModel
 * @author ������
 *
 */
public class HostUtil{
	
	public static String hostname(){
		// �ӻ��������ж�ȡ������
		String host=System.getenv().get("OS_HOST");
		
		// �����������ж�ȡ������
		if(isEmpty(host)){
			host=System.getProperty("os.host");
		}
		if(!isEmpty(host)){
			return host;
		}
		// ͨ��ϵͳ�ӿڶ�ȡ������
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostName=inetAddress.getHostName();
	        return hostName;
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}
	public static String address(){
		
		// �ӻ��������ж�ȡIP��ַ
		String addr=System.getenv().get("OS_ADDR");
		
		// �����������ж�ȡIP��ַ
		if(isEmpty(addr)){
			addr=System.getProperty("os.addr");
		}
		if(!isEmpty(addr)){
			return addr;
		}
		
		// ͨ��ϵͳ�ӿڶ�ȡIP��ַ
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostAddress=inetAddress.getHostAddress();
	        return hostAddress;
		} catch (UnknownHostException e) {
			return "localhost";
		}
		
	}
	private static boolean isEmpty(String str){
		return str==null||str.equals("");
	}
}
