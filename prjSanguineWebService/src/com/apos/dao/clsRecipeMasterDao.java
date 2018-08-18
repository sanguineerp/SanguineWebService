


package com.apos.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsRecipeMasterModel;

@Repository("clsRecipeMasterDao")

@Transactional(value = "webPOSTransactionManager")
public class clsRecipeMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	 public String funGenerateRecipeCode()
	    {
			String strSettelmentCode = "";
			try
			{
			    
			    String sql = "select ifnull(max(strRecipeCode),0) from tblrecipehd";
			    
			    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			    List list = query.list();
			    
			    
			    if (!list.get(0).toString().equals("0"))
				{
					String strCode = "0";
					String code = list.get(0).toString();
					StringBuilder sb = new StringBuilder(code);
					String ss = sb.delete(0, 2).toString();
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
						strSettelmentCode = "R000000" + intCode;
					}
					else if (intCode < 100)
					{
						strSettelmentCode = "R00000" + intCode;
					}
					else if (intCode < 1000)
					{
						strSettelmentCode = "R0000" + intCode;
					}
					else if (intCode < 10000)
					{
						strSettelmentCode = "R000" + intCode;
					}
					else if (intCode < 100000)
					{
						strSettelmentCode = "R00" + intCode;
					}
					else if (intCode < 1000000)
					{
						strSettelmentCode = "R0" + intCode;
					}
					else if (intCode < 10000000)
					{
						strSettelmentCode = "R" + intCode;
					}
				}
				else
				{
					strSettelmentCode = "R0000001";
				}

			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
			
			return strSettelmentCode;
		    }


	public void funAddUpdateRecipeMaster(clsRecipeMasterModel objModel){
		try
		{
		    webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
	}




}
