package os.core.model;

import org.osgi.dto.DTO;

/**
 * ������ϢModel
 * @author ������
 *
 */
public class HostInfo extends DTO{

	// ������
	public String hostname;
	// ����IP
	public String ip;
	// ����ͨѶ�˿�
	public String port;
	// ����״̬
	public String status;
	
	public HostInfo(){};
	public HostInfo(String ip,String port,String hostname){
		this.ip=ip;
		this.port=port;
		this.hostname=hostname;
	}
	
	@Override  
	public boolean equals(Object other) {
		HostInfo o=null;
		if(other instanceof HostInfo){
			o=(HostInfo)(other);
		}
		String key=ip+port;
		return key.equals(o.ip+o.port);
	}
	@Override  
    public int hashCode() {
		String key=ip+port;
		return key.hashCode();
    }  
	
}
