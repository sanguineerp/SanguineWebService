
package com.apos.dao;

import java.lang.reflect.Array;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsDebitCardSettlementDetailsModel;
import com.apos.model.clsPosSettlementDetailsModel;



@Repository("clsDebitCardSettlementDetailsDao")
@Transactional(value = "webPOSTransactionManager")
public class clsDebitCardSettlementDetailsDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public void funSaveDebitCardSettlementDetails(clsDebitCardSettlementDetailsModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
		

	
	@SuppressWarnings("finally")
	public JSONObject funGetDebitCardSettlementDtl(String cardTypeCode,String clientCode)
	{
		List list =null;
		JSONObject jObjSettlementData=new JSONObject();
		try{
		
//			String hql="select strSettelmentCode,strSettelmentDesc "
//                + "from tblsettelmenthd" + " where strClientCode='"+clientCode+"' ";
			
//			 String hql = "select b.strSettelmentDesc,a.strApplicable  from tbldebitcardsettlementdtl a,tblsettelmenthd b "
//		                + " where a.strSettlementCode=b.strSettelmentCode and  a.strCardTypeCode='"+cardTypeCode+"' and a.strClientCode='"+clientCode+"' ";
//			
			
			String hql="select a.strSettelmentCode,a.strSettelmentDesc,ifnull(b.strCardTypeCode,'"+cardTypeCode+"'),ifnull(b.strApplicable,'false')"
						+" from tblsettelmenthd a left outer join tbldebitcardsettlementdtl b on a.strSettelmentCode=b.strSettlementCode"
						+" where (b.strCardTypeCode='"+cardTypeCode+"' or b.strCardTypeCode is null)";
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(hql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
	          
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						
						JSONObject objSettle=new JSONObject();
						objSettle.put("SettlementCode",Array.get(obj, 0));
						objSettle.put("SettlementDesc",Array.get(obj, 1));
						objSettle.put("ApplicableYN",Array.get(obj,3));
//						if((boolean) Array.get(obj,3))
//						{
//							objSettle.put("ApplicableYN",true);
//						}
//						
						jArrData.put(objSettle);
			    	
					}
					jObjSettlementData.put("SettlementDtl", jArrData);
			      }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjSettlementData;
			}
	}


	
}
