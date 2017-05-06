package os.network.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


// Http����ͻ���
public class HttpClient {

	@SuppressWarnings("unchecked")
	public static <T> T post(String url,String method,Object[] params) throws Exception{
		
		// ������� username=xxx&password=xxx
		String sendMsg = serializable(method,params);
		
		// HTTP����
        HttpURLConnection connect= (HttpURLConnection)new URL(url).openConnection();
        
        // ��������ͷ
        connect.setDoOutput(true);
        connect.setRequestMethod("POST");
        connect.setRequestProperty("Accept-Charset", "utf-8");
        connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connect.setRequestProperty("Content-Length", String.valueOf(sendMsg.length()));
        
        // ���������
        OutputStreamWriter output = null;
        BufferedReader input = null;
        
        // ������
        StringBuffer res = new StringBuffer();
        try{
        	
        	// �����������
        	output = new OutputStreamWriter(connect.getOutputStream());
        	output.write(sendMsg.toString());
        	output.flush();
            
            // ��ȡ������
            input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String temp = null;
            while ((temp = input.readLine()) != null) {
                res.append(temp);
            }
            
        }finally {
            if(output != null){
            	output.close();
            }
            if(input != null){
            	input.close();
            }
        }
        return (T)deserializable(res.toString());
	}
	// ���л�
	public static String serializable(String method,Object[] args){
		if(args==null){
			return "method="+method;
		}
		String params="";
		for(Object arg:args){
			if(arg instanceof Object[]){
				String tmp="";
				for(Object a:(Object[])arg){
					tmp+=a.toString()+",";
				}
				tmp=tmp.replaceAll(",$","");
				params+=tmp+",";
			}else{
				params+=arg.toString()+",";	
			}
			
		}
		params=params.replaceAll(",$","");
		return "method="+method+"&params="+params;
	}
	// �����л�
	public static Object deserializable(String res){
		return res;
	}
	public static void main(String args[]) throws Exception{
		String url="http://localhost:8080/table";
		String method="getHostInfo";
		String res=HttpClient.post(url, method,null);
		System.out.println(res);
		
		method="call";
		res=HttpClient.post(url, method,new Object[]{"os.network","test","ddd"});
		System.out.println(res);
		
	}
}
