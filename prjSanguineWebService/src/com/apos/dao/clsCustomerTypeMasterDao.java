package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsCustomerTypeMasterModel;



@Repository("clsCustomerTypeMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsCustomerTypeMasterDao{

	@Autowired
	private SessionFactory WebPOSSessionFactory;

		public String funSaveCustomerTypeMaster(clsCustomerTypeMasterModel objModel) throws Exception
		{
			WebPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
			return objModel.getStrCustTypeCode();
			
		}  
		    
		    public String funGenerateCustomerTypeCode() throws Exception

		    {
				String customerTypeCode = "";
				String sql = "select ifnull(max(strCustTypeCode),0) from tblcustomertypemaster";
				Query query = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List list = query.list();
				
				if (!list.get(0).toString().equals("0"))
				{
				    String strCode = "00";
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
				    	customerTypeCode = "CT00" + intCode;
				    }
				    else if (intCode < 100)
				    {
				    	customerTypeCode = "CT0" + intCode;
				    }
				    else if (intCode < 1000)
				    {
				    	customerTypeCode = "CT" + intCode;
				    }
				   
				}
				else
				{
					customerTypeCode = "CT001";
				}
				return customerTypeCode;
		    }
		    
		    public String funGetAllCustomerType(String clientCode)
			{
				List list = null;
				
				JSONObject jObjData = new JSONObject();
				JSONArray jArrAreaData = new JSONArray();
				try
				{

					Query query = WebPOSSessionFactory.getCurrentSession().createQuery("from clsCustomerTypeMasterModel where strClientCode= :clientCode");
					query.setParameter("clientCode", clientCode);
					list = query.list();
					clsCustomerTypeMasterModel objAreaModel = null;
					if (list.size() > 0)
					{
						for (int i = 0; i < list.size(); i++)
						{
							JSONObject jobj = new JSONObject();
							
							objAreaModel = (clsCustomerTypeMasterModel) list.get(i);
							jobj.put("strCustomeTypeCode",objAreaModel.getStrCustTypeCode());
							jobj.put("strCustomeTypeName",objAreaModel.getStrCustType());					
							
							jArrAreaData.put(jobj);
						}
						jObjData.put("CustomerTypeList", jArrAreaData);
					}

				}
				catch (Exception ex)
				{
					ex.printStackTrace();

				}
				finally
				{
					return jObjData.toString();
				}
			}
		}




