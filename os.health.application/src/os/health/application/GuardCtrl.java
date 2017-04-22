package os.health.application;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.core.api.CoreOS;
import os.core.tools.StringUtil;
import os.health.base.BaseCtrl;
import osgi.enroute.jsonrpc.api.JSONRPC;

/**
 * �໤��ģ��
 * @author ������
 *
 */
@SuppressWarnings("rawtypes")
@Component(name="os.guard",property=JSONRPC.ENDPOINT + "=guard")
public class GuardCtrl extends BaseCtrl implements JSONRPC  {
	// �໤����
	String GUARD_CLASS="os.moudel.guard.provider.GuardService";
	
	// ϵͳ�ں�
	CoreOS coreos;
	@Reference
	void setCoreOS(CoreOS coreos){
		this.coreos=coreos;
	}
	@Override
	public CoreOS getCoreOS() {
		return this.coreos;
	}
	// �б��ѯ
	public List query(Map param){
		// ��ѯ
		String table=(String)param.get("table");
		try{
			List res=this.coreos.call(GUARD_CLASS,"query",table,param);
			this.log("info", getDesc(table)+"�б��ѯ�ɹ�");
			return res;
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+"�б��ѯʧ��");
		}
		return null;
		
	}
	public Object userInfo(Map param){
		String table=(String)param.get("table");
		return this.coreos.call(GUARD_CLASS,"userInfo",table,param);
	}
	
	// �༭��ѯ
	public Object queryById(String table,String id){
		Object res=null;
		try{
			res=this.coreos.call(GUARD_CLASS,"queryById",table,id);
			this.log("info", getDesc(table)+"�����ѯ�ɹ�");
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+"�����ѯʧ��");
		}
		return res;
	}
	// �༭�����
	public Object update(Map params){
		String table=params.get("table").toString();
		String id=params.get("id").toString();
		String info=!StringUtil.isEmpty(id)?"���":"�༭";
		Object res=null;
		try{
			res=this.coreos.call(GUARD_CLASS,"update",table,params);
			
			// ��Ӳ��� ����������ϵ
			if(StringUtil.isEmpty(id)){
				Object userid=params.get("userid");
				if(userid!=null){
					id=res.toString();
					add_user_fk(table,userid.toString(),id);	
				}
			}
			this.log("info", getDesc(table)+info+"�ɹ�");
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+info+"ʧ��");
		}
		return res;
	}
	// ɾ��
	public Object remove(Map params){
		String table=params.get("table").toString();
		String ids=params.get("ids").toString();
		Object res=null;
		try{
			for(String id:ids.split(",")){
				Object cascade=params.get("cascade");
				// ����ɾ��
				if(cascade!=null&&cascade.equals("yes")){
					res=this.coreos.call(GUARD_CLASS,"remove",table,id);
					remove_user_fk(table,null,id);
				}else{
					String userid=params.get("userid").toString();
					remove_user_fk(table,userid,id);
				}
			}
			this.log("info", getDesc(table)+"ɾ������ɹ�");
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+"ɾ������ʧ��");
		}
		return res;
	}
	// ��ѯδ����б�
	public List query_noadd(String table,String userid){
		List res=null;
		try{
			res=this.coreos.call(GUARD_CLASS,"query_noadd",table,userid);
			this.log("info", getDesc(table)+"��ѯδ����б�ɹ�");
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+"��ѯδ����б�ʧ��");
		}
		return res;
	}
	// ��ӹ�����ϵ
	public boolean add_user_fk(String table,String userid,String ids){
		boolean res=false;
		try{
			res=this.coreos.call(GUARD_CLASS,"add_user_fk",table,userid,ids);
			this.log("info", getDesc(table)+"��ӹ�����ϵ�ɹ�");
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+"��ӹ�����ϵʧ��");
		}
		return res;
	}
	// ɾ��������ϵ
	public boolean remove_user_fk(String table,String userid,String ids){
		boolean res=false;
		try{
			res=this.coreos.call(GUARD_CLASS,"remove_user_fk",table,userid,ids);
		this.log("info", getDesc(table)+"ɾ��������ϵ�ɹ�");
		}catch(Exception e){
			e.printStackTrace();
			this.log("error", getDesc(table)+"ɾ��������ϵʧ��");
		}
		return res;
	}

	// ������Ϣ
	private String getDesc(String table){
		String info="�໤��";
		if(table.equals("mn_info")){
			info="����";
		}else if(table.equals("mn_doctor")){
			info="ҽ��";
		}else if(table.equals("mn_relatives")){
			info="����";
		}else if(table.equals("mn_volunteer")){
			info="־Ը��";
		}
		return info;
	}
	@Override
	public Object getDescriptor() throws Exception {
		return "guard";
	}
	

}
