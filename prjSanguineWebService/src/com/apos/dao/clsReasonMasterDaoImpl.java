package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsReasonMasterModel;
import com.apos.model.clsZoneMasterModel;

@Repository("clsReasonMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsReasonMasterDaoImpl implements inftReasonMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    
    
    
    public clsReasonMasterModel funLoadReasoneMasterData(String sql,Map<String,String> hmParameters) throws Exception
    {
    	Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
    	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
    	{
    		query.setParameter(entrySet.getKey(), entrySet.getValue());
    	}
    	List list=query.list();
    	
    	clsReasonMasterModel model=new clsReasonMasterModel();
    	if(list.size()>0)
    	{
    		model=(clsReasonMasterModel)list.get(0);
    					
    	}
    	return model;
    }
    
    
    @Override
    public JSONObject funLoadAllReasonMasterData(String clientCode)
    {
    	JSONObject JObj=new JSONObject();
    	JSONObject JObjModifyBill;
    	JSONObject JObjComplementry;
    	JSONObject JObjDiscount;
    	JSONArray jArr=new JSONArray();
    	try{
    		
    		String sqlModifyBill = "select strReasonCode,strReasonName from tblreasonmaster "
                    + "where strModifyBill='Y' and strClientCode='"+clientCode+"'";
        	List list = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifyBill).list();
        	if(list.size()>0){
    		   
    		   for(int i=0;i<list.size();i++){
    			   Object[] ob=(Object[])list.get(i);
    			   JObjModifyBill=new JSONObject();
    			   JObjModifyBill.put("strReasonCode",ob[0].toString());
    			   JObjModifyBill.put("strReasonName",ob[1].toString());
    			   jArr.put(JObjModifyBill);
    		   }
    		   JObj.put("ModifyBill",jArr);
    	   }
        	jArr=new JSONArray();
        	
        	String sqlCmplementReason = "select strReasonCode,strReasonName from tblreasonmaster "
                    + "where strComplementary='Y' and strClientCode='"+clientCode+"'";
        	 list = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlCmplementReason).list();
        	if(list.size()>0){
    		   
    		   for(int i=0;i<list.size();i++){
    			   Object[] ob=(Object[])list.get(i);
    			   JObjComplementry=new JSONObject();
    			   JObjComplementry.put("strReasonCode",ob[0].toString());
    			   JObjComplementry.put("strReasonName",ob[1].toString());
    			   jArr.put(JObjComplementry);
    		   }
    		   JObj.put("Complementry",jArr);
    	   }
        	jArr=new JSONArray();
        	String sqlDiscount = "select strReasonCode,strReasonName from tblreasonmaster where strDiscount='Y'"
        			+ " and strClientCode='"+clientCode+"'";
        	 list = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDiscount).list();
        	if(list.size()>0){
    		   
    		   for(int i=0;i<list.size();i++){
    			   Object[] ob=(Object[])list.get(i);
    			   JObjDiscount=new JSONObject();
    			   JObjDiscount.put("strReasonCode",ob[0].toString());
    			   JObjDiscount.put("strReasonName",ob[1].toString());
    			   jArr.put(JObjDiscount);
    		   }
    		   JObj.put("Discount",jArr);
    	   }
        	jArr=new JSONArray();
        	String sqlReason = "select strReasonCode,strReasonName from tblreasonmaster where strComplementary='Y' "
        			+ " and strClientCode='"+clientCode+"'";
        	 list = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlReason).list();
        	if(list.size()>0){
    		   
    		   for(int i=0;i<list.size();i++){
    			   Object[] ob=(Object[])list.get(i);
    			   JObjDiscount=new JSONObject();
    			   JObjDiscount.put("strReasonCode",ob[0].toString());
    			   JObjDiscount.put("strReasonName",ob[1].toString());
    			   jArr.put(JObjDiscount);
    		   }
    		   JObj.put("AllReason",jArr);
    	   }
        	
    	}
    	catch(Exception e){
    		
    	}
    	
    	
    	return JObj;
    }

    
    
    
    
}
