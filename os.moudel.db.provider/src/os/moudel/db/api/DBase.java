package os.moudel.db.api;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
@SuppressWarnings({"rawtypes"})
public interface  DBase {
	
	// �������ݿ����Ӷ���
	public Connection getConnect();

	// ��������
	public <T> T excute(String sql);
	public <T> T excute(String sql,List param);
	public <T> T excute(String table,Map param);
	
	// ��ѯ����
	public List<Map<String,Object>> query(String sql);
	public List<Map<String,Object>> query(String sql,List param);
	public List<Map<String,Object>> query(String table,Map param);
	public List<Map<String,Object>> query(String table,Map param,String order,String limit);
	
	// ����ID��ѯ
	public <T> T queryById(String table,Object id);
	// ��ѯ�������
	public List<Map<String,Object>> queryByIds(String table,String ids);
	
	// ����ID�б�ɾ��
	public void deleteByIds(String table,String ids);
	
	// ���³�ʼ���ݿ�
	public Object init(boolean reset);
}
