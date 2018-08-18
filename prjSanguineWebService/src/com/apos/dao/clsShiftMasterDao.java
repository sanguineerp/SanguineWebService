package com.apos.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsShiftMasterModel;

@Repository("clsShiftMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsShiftMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

		public String funSaveShiftMaster(clsShiftMasterModel objModel) throws Exception
		{
			webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
			return objModel.getIntShiftCode();
		}
		
		 public String funGenerateShiftCode() throws Exception
		    {
				String shiftCode = "";
				String sql = "select ifnull(max(intShiftCode),0) from tblshiftmaster";
				Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List list = query.list();
				
				if (list.size()>(0))
				{
				   
				    int code =  Integer.parseInt(list.get(0).toString());
				    code++;
				    shiftCode=String.valueOf(code);
				}
				else
				{
					shiftCode = "1";
				}
				return shiftCode;
		    }

}
