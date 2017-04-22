package os.core.model;

import java.util.List;

import org.osgi.dto.DTO;
/**
 * 服务信息Model
 * @author 尹行欣
 */
public class ServiceInfo extends DTO{

	// 服务ID
	public String id;
	// 服务名称
	public String name;
	// 服务状态
	public String status;
	// 接口方法
	public List<String> methods;
	// 主机IP
	public String ip;
	// 主机端口
	public String port;
	
	// 所属bundle
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
