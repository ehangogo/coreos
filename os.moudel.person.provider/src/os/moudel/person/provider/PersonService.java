package os.moudel.person.provider;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.core.api.CoreOS;

/**
 * 个人体征模块
 */
@Component(name = "os.moudel.person",service=PersonService.class)
@SuppressWarnings("rawtypes")
public class PersonService {
	
	// 数据库访问类
	String DB_CLASS="os.moudel.db.api.DBase";
	
	CoreOS coreos=null;
	@Reference void setCoreOS(CoreOS coreos){
		this.coreos=coreos;
	}
	public List query(String table,Map param) {
		return this.coreos.call(DB_CLASS,"query",table,param);
	}
	public Object update(String table,Map param){
		return this.coreos.call(DB_CLASS,"excute",table,param);
	}
}
