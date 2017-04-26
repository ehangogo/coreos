package os.core.model;

import java.util.ArrayList;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * �����ϢModel
 * @author ������
 */
public class BundleInfo extends DTO{
	
	// ���ID
	public String id;
	// �������
	public String name;
	// ������м���
	public String level;
	// ���״̬
	public String status;
	// ����汾
	public String version;
	// ��װ·��
	public String location;
	// ����IP
	public String ip;
	// �����˿�
	public String port;
	
	// ����������ķ�����Ϣ
	public List<ServiceInfo> services=new ArrayList<>();
	
	@Override  
	public boolean equals(Object other) {
		BundleInfo o=null;
		if(other instanceof BundleInfo){
			o=(BundleInfo)(other);
		}
		if(id!=null){
			return id.equals(o.id);
		}else{
			String key=name+version;
			return key.equals(o.name+o.version);
		}
		
	}
	@Override  
    public int hashCode() {
		if(id!=null){
			return id.hashCode();
		}else{
			String key=name+version;
			return key.hashCode();
		}
		
    }  
	
}
