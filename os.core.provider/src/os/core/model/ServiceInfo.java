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
	// ��������
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
        return (name+id).equals(o.name+o.id);  
	}
	@Override  
    public int hashCode() {  
        return (name+id).hashCode();  
    } 
	
}
