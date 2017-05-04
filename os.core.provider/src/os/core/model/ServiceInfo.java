package os.core.model;

import java.util.List;

import org.osgi.dto.DTO;
/**
 * ������ϢModel
 * @author ������
 */
public class ServiceInfo extends DTO{

	// ����ID
	public String id;
	// �������ƣ���·�����������
	public String name;
	// ����״̬
	public String status;
	// �ӿڷ���
	public List<String> methods;
	// ����IP
	public String ip;
	// �����˿�
	public String port;
	
	// ����bundle
	public String bundle;
	@Override  
	public boolean equals(Object other) {
		ServiceInfo o=null;
		if(other instanceof ServiceInfo){
			o=(ServiceInfo)(other);
		}
		if(id!=null){
			return id.equals(o.id);
		}else{
			return (name).equals(o.name);
		}
	}
	@Override  
    public int hashCode() {  
		if(id!=null){
			return id.hashCode();
		}else{
			String key=name;
			return key.hashCode();
		}
    } 
	
}
