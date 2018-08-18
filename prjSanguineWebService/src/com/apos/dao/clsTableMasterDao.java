package com.apos.dao;

import java.lang.reflect.Array;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsTableMasterModel;
import com.apos.model.clsTableMasterModel_ID;

@Repository("clsTableMasterDao")

@Transactional(value = "webPOSTransactionManager")
public class clsTableMasterDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	 public String funGenerateTableCode()
	    {
		String tableCode = "";
		try
		{
		    
		    String sql = "select count(*) from tbltablemaster";
		    
		    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    List list = query.list();
		    
		    if (!list.get(0).toString().equals("0"))
		    {
			
			
			long code = Long.parseLong(list.get(0).toString());
			code = code + 1;
			tableCode = "TB" + String.format("%07d", code);
			
		    }
		    else
		    {
		    	tableCode = "TB0000001";
		    }
		    
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return tableCode;
	    }


	public void funAddUpdateTableMaster(clsTableMasterModel objModel){
		try
		{
		    webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funGetTableDtl(String clientCode)
	{
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String hql="select a.strTableNo,a.strTableName,b.strAreaName,ifnull(c.strPosName,'All') "
                    + " from tbltablemaster a left outer join tblareamaster b on a.strAreaCode=b.strAreaCode "
                    + " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
                    + "ORDER by a.intSequence";
			
		
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(hql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
	          
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("TableNo",Array.get(obj, 0));
						objSettle.put("TableName",Array.get(obj, 1));
						objSettle.put("AreaName",Array.get(obj, 2));
						objSettle.put("PosName",Array.get(obj, 3));
						
						jArrData.put(objSettle);
					}
		           	jObjTableData.put("TableDtl", jArrData);
			      }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	
	public void funSaveTableSequenceDetails( JSONArray tableList,String clientCode)
	 {
		 
		 
		 for(int i=0; i<tableList.length(); i++)
		    {
		    try
		    {
			 JSONObject jObj = new JSONObject();
		    	jObj=tableList.getJSONObject(i);
		    	long sequence=jObj.getLong("Sequence");
		    	String tableNo=jObj.getString("TableNo");
		    	
		    	clsTableMasterModel objTableModel= new clsTableMasterModel(new clsTableMasterModel_ID(tableNo,clientCode));
		    	
		    	objTableModel.setIntSequence(sequence);
		    
		    	
		    	
		    	funAddUpdateTableMaster(objTableModel);
		    }
		    catch (Exception e)
			{
			    e.printStackTrace();
			}
		    }
		    
	 }
	
	
	   @SuppressWarnings("finally")
	   public JSONObject funGetTableList(String posCode)
		{
			List list =null;
			JSONArray jArrAreaData = new JSONArray();
			JSONObject jObjData=new JSONObject();
			try{
			
	    Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsTableMasterModel where strPOSCode= :posCode");
		query.setParameter("posCode", posCode);
		 list = query.list();
		 clsTableMasterModel objAreaModel = null;
		 if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jobj = new JSONObject();
					
					objAreaModel = (clsTableMasterModel) list.get(i);
					jobj.put("strTableNo",objAreaModel.getStrTableNo());
					jobj.put("strTableName",objAreaModel.getStrTableName());					
					
					jArrAreaData.put(jobj);
				}
				jObjData.put("TableList", jArrAreaData);
			}
			
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjData;
			}
	}
}
