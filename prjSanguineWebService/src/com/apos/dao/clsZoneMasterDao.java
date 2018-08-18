package com.apos.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsZoneMasterModel;

@Repository("clsZoneMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsZoneMasterDao{

	@Autowired
	private SessionFactory WebPOSSessionFactory;

		public String funSaveZoneMaster(clsZoneMasterModel objModel) throws Exception
		{
			WebPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
			return objModel.getStrZoneCode();
		}
		public String funGenerateZoneCode() throws Exception
	    {
			String zoneCode = "";
			String sql = "select ifnull(max(strZoneCode),0) from tblzonemaster";
			Query query = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			List list = query.list();
			
			if (!list.get(0).toString().equals("0"))
			{
			    String strCode = "00";
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
			    	zoneCode = "Z000" + intCode;
			    }
			    else if (intCode < 100)
			    {
			    	zoneCode = "Z00" + intCode;
			    }
			    else if (intCode < 1000)
			    {
			    	zoneCode = "Z0" + intCode;
			    }
			   
			}
			else
			{
				zoneCode = "Z0001";
			}
			return zoneCode;
	    }

}
