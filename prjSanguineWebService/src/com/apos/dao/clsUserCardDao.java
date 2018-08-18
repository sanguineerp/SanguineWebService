package com.apos.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsUserDetailModel;


@Repository("clsUserCardDao")

@Transactional(value = "webPOSTransactionManager")
public class clsUserCardDao
{
	 @Autowired
	    private SessionFactory webPOSSessionFactory;

	 
	 public String funSaveUserCard(clsUserDetailModel objModel) throws Exception
	    {
	    	webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
	    	return objModel.getStrUserCode();
	    }
	    
	
	 public String funUpdateUserCardString(String userCode, String swipeCard)
		{
		 
            
		// String userCode = "";
			String sql = "Update tblUserhd set strDebitCardString = '"+swipeCard+"' where strUserCode = '"+userCode+"' ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		//	List list = query.list();
		 query.executeUpdate();
			
		
			return userCode;
			
			
		}
		 
	 
	 
	 public String funGenerateUserCode() throws Exception
	    {
			String userCode = "";
			String sql = "select ifnull(max(struserCode),0) from tbluserhd";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			List list = query.list();
			
			if (!list.get(0).toString().equals("0"))
			{
			    String strCode = "0";
			    String code = list.get(0).toString();
			    StringBuilder sb = new StringBuilder(code);
			    String ss = sb.delete(0, 1).toString();
			    for (int i = 0; i < ss.length(); i++)
			    {
					if (ss.charAt(i) != '0')
					{
					    strCode = ss.substring(i, ss.length());
					    break;
					}
			    }
			    
			    int intCode = Integer.parseInt(strCode);
			    intCode++;
			    if (intCode < 10)
			    {
			    	userCode = "G000000" + intCode;
			    }
			    else if (intCode < 100)
			    {
			    	userCode = "G00000" + intCode;
			    }
			    else if (intCode < 1000)
			    {
			    	userCode = "G0000" + intCode;
			    }
			    else if (intCode < 10000)
			    {
			    	userCode = "G000" + intCode;
			    }
			    else if (intCode < 100000)
			    {
			    	userCode = "G00" + intCode;
			    }
			    else if (intCode < 1000000)
			    {
			    	userCode = "G0" + intCode;
			    }
			}
			else
			{
			    userCode = "G0000001";
			}
			return userCode;
	    }
	 
	 
	 public List<clsUserDetailModel> funGetAllGroup(String strClientCode)
		{
			List<clsUserDetailModel> list=null;
			try
			{
			Query query=webPOSSessionFactory.getCurrentSession().createQuery("from clsUserDetailHdModel where strClientCode=:strClientCode");
			query.setParameter("strClientCode", strClientCode);
			list=query.list();
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return list;
		}





	 
	 
	 
	
}

