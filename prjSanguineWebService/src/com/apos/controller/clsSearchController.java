package com.apos.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.model.clsGroupMasterModel;
import com.excise.controller.clsExciseIntegration;
import com.sanguine.service.intfBaseService;
import com.webbooks.controller.clsWebbooksIntegration;

@Path("/APOSSearchIntegration")
@Controller
public class clsSearchController
{
    @Autowired
    intfBaseService objBaseService;
    
    @GET
    @Path("/funGetPOSSearchAll")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetSearchData(@QueryParam("masterName") String masterName, @QueryParam("clientCode") String clientCode)
    {
		JSONObject jObjSearchData = new JSONObject();
		JSONArray jArrData=new JSONArray();
		StringBuilder hqlQuery = new StringBuilder();
		try
		{
		    switch(masterName)
		    {
		    	case "POSTaxMaster":
		    		List list=objBaseService.funGetSerachList("getAllTaxMaster",clientCode);
		    		
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						   
						    jArrData.put(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
		    		break;	
		    		
		    	case "POSPromotionMaster":
			    	
		     		  
						 list = objBaseService.funGetSerachList("getAllPromotionMaster",clientCode);
							
						
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						    jArrDataRow.put(obj[2]);
						    jArrData.put(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
						break;
						
		    	case "POSOrderMaster":
					
		    		list = objBaseService.funGetSerachList("getAllOrderMaster",clientCode);
					
					 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						  
						    jArrData.put(jArrDataRow);
						}
					jObjSearchData.put(masterName, jArrData);
					break;	
		    	case "POSDeliveryBoyMaster":
					
					
		    		list = objBaseService.funGetSerachList("getAllDeliveryBoyMaster",clientCode);
		    		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						  
						    jArrData.put(jArrDataRow);
						}
		    		 
					jObjSearchData.put(masterName, jArrData);
					break;
					
			    	
		    	case "POSAdvOrderMaster":
					
					
		    		list = objBaseService.funGetSerachList("getAllAdvOrderMaster",clientCode);
						
		    		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						  
						    jArrData.put(jArrDataRow);
						}
					jObjSearchData.put(masterName, jArrData);
					break;	
				    	
		     	case "POSSettlementMaster":
					
		     		list = objBaseService.funGetSerachList("getAllSettlementMaster",clientCode);
						
		     		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						  
						    jArrData.put(jArrDataRow);
						}
					jObjSearchData.put(masterName, jArrData);
					break;
					
		     	 case "POSMaster":
						
		     		list = objBaseService.funGetSerachList("getAllPOSMaster",clientCode);
					   
						
		     		for(int cnt=0;cnt<list.size();cnt++)
					{
						Object[] obj = (Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.put(obj[0]);
					    jArrDataRow.put(obj[1]);
					   
					    jArrData.put(jArrDataRow);
					}
					jObjSearchData.put(masterName, jArrData);
					break;
				
		     	case "POSAreaMaster":
					
		     		list = objBaseService.funGetSerachList("getAllAreaMaster",clientCode);
						
		     		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						    jArrDataRow.put(obj[2]);
						    jArrData.put(jArrDataRow);
						}
		     		jObjSearchData.put(masterName, jArrData);
					break;
				    

				    
			    case "POSWaiterMaster":
					
			    	list = objBaseService.funGetSerachList("getAllWaiterMaster",clientCode);
			    	 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(obj[0]);
						    jArrDataRow.put(obj[1]);
						    jArrDataRow.put(obj[2]);
						    jArrData.put(jArrDataRow);
						}
		     		jObjSearchData.put(masterName, jArrData);
					
					
					break;
					
		    	case "WebBooksAcountMaster":
		    		clsWebbooksIntegration objWB = new clsWebbooksIntegration();
		    		jObjSearchData=objWB.funGetAccountDtl(masterName, clientCode);
		    		break;
		    		
		    	case "ExciseLicenseMaster":
		    		clsExciseIntegration objEx = new clsExciseIntegration();
		    		jObjSearchData=objEx.funGetLicenceDtlforPOS(masterName, clientCode);
		    		break;
		    	
			    case "POSCustomerAreaMaster":
			    	
			    	//List list1=objintfBaseService.funGetSerachList("getALLCustomerArea1");
			    	list=objBaseService.funGetSerachList("getALLCustomerArea",clientCode);
			    	for(int i=0 ;i<list.size();i++ )
		 	    	{
			    		Object obj[]=(Object[]) list.get(i);
			    		//clsCustomerAreaMasterModel obj = (clsCustomerAreaMasterModel) list.get(i);
		            	JSONArray jObj= new JSONArray();
		            		                     
		            	jObj.put(obj[0].toString());
		            	jObj.put(obj[1].toString());
		            	jObj.put(obj[2].toString());
		            	
		            	jArrData.put(jObj);
		            }
			    	jObjSearchData.put(masterName,jArrData);
			    break;	
			    
			    case "POSZoneMaster":
			    	
			    	
			    	list=objBaseService.funGetSerachList("getALLZone",clientCode);
			    	for(int i=0 ;i<list.size();i++ )
		 	    	{
			    		//clsZoneMasterModel obj = (clsZoneMasterModel) list.get(i);
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObj= new JSONArray();
		            		                     
		            	jObj.put(obj[0].toString());
		            	jObj.put(obj[1].toString());	            	
		                jArrData.put(jObj);                
		            }
			    	jObjSearchData.put(masterName,jArrData);
			    break;	
			    
			 case "POSshiMaster":
			 	
			 	
			 	list=objBaseService.funGetSerachList("getALLShift",clientCode);
			 	for(int i=0 ;i<list.size();i++ )
			  	{
			 		//clsShiftMasterModel obj = (clsShiftMasterModel) list.get(i);
			 		Object obj[]=(Object[]) list.get(i);
			     	JSONArray jObj= new JSONArray();
			     		                     
			     	jObj.put(obj[0].toString());
			     	jObj.put(obj[1].toString());
			     	jObj.put(obj[2].toString());
			     	jObj.put(obj[3].toString());
			     	jObj.put(obj[4].toString());
			     	jObj.put(obj[5].toString());
			     	
			     	
			         jArrData.put(jObj);		         
			    
			     }
			 	jObjSearchData.put(masterName,jArrData);
			 break;	
			 
			 case "POSReasonMaster":
			 	
			 	
			 	list=objBaseService.funGetSerachList("getALLReason",clientCode);
			 	for(int cnt=0;cnt<list.size();cnt++)
				{
			 		//clsReasonMasterModel objModelReason = (clsReasonMasterModel) list.get(cnt);
			 		Object obj[]=(Object[]) list.get(cnt);
				    JSONArray jArrDataRow = new JSONArray();
				    jArrDataRow.put(obj[0].toString());
				    jArrDataRow.put(obj[1].toString());
				    jArrData.put(jArrDataRow);
				}		 	
			 	jObjSearchData.put(masterName,jArrData);
			 break;	
			 
			 case "POSCustomerTypeMaster":
				 	
				 	
				 	list=objBaseService.funGetSerachList("getALLCustomerType",clientCode);
				 	for(int cnt=0;cnt<list.size();cnt++)
					{
				 		//clsCustomerTypeMasterModel objModel1 = (clsCustomerTypeMasterModel) list.get(cnt);
				 		Object obj[]=(Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.put(obj[0].toString());
					    jArrDataRow.put(obj[1].toString());
					    jArrDataRow.put(obj[2].toString());
					    
					    jArrData.put(jArrDataRow);
					}	 	
				 	jObjSearchData.put(masterName,jArrData);
				 break;	
				 
			 case "POSCustomerMaster":
				 	
				 	
				 	list=objBaseService.funGetSerachList("getALLCustomer",clientCode);
				 	for(int cnt=0;cnt<list.size();cnt++)
					{
				 		//clsCustomerMasterModel objModelCustomer = (clsCustomerMasterModel) list.get(cnt);
				 		Object obj[]=(Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.put(obj[0].toString());
					    jArrDataRow.put(obj[1].toString());
					    jArrDataRow.put(obj[2].toString());
					    jArrDataRow.put(obj[3].toString());
					    
					    		
				
					    
					    jArrData.put(jArrDataRow);
					}	
				 	jObjSearchData.put(masterName,jArrData);
				 break;	
				 
			
			    case "POSItemModifierMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(ob[0].toString());
						    jArrDataRow.put(ob[1].toString());
						    jArrDataRow.put(ob[2].toString());
						    
						    jArrData.put(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			        break;	
			    
			    case "POSMenuHeadMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(ob[0].toString());
						    jArrDataRow.put(ob[1].toString());
						    jArrDataRow.put(ob[2].toString());
						    
						    jArrData.put(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			        break;	
			    	
			        
			    case "POSSubMenuHeadMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(ob[0].toString());
						    jArrDataRow.put(ob[1].toString());
						    jArrDataRow.put(ob[2].toString());
						    jArrDataRow.put(ob[3].toString());
						    jArrData.put(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			        break;	
			        
			    case "POSMenuItemMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(ob[0].toString());
						    jArrDataRow.put(ob[1].toString());
						    jArrDataRow.put(ob[2].toString());
						    jArrDataRow.put(ob[3].toString());
						    jArrDataRow.put(ob[4].toString());
						    jArrDataRow.put(ob[5].toString());
						    jArrDataRow.put(ob[6].toString());
						    jArrData.put(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			    	break;
			    	
			    case "POSModifierGroupMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.put(ob[0].toString());
						    jArrDataRow.put(ob[1].toString());
						    jArrDataRow.put(ob[2].toString());
						    jArrDataRow.put(ob[3].toString());
						    jArrData.put(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
				    break;
				    
				    
			    case "POSCostCenterMaster":
			    	list = 	objBaseService.funGetSerachList("POSCostCenter",clientCode);

			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObjArr= new JSONArray();

					    
		            	jObjArr.put(obj[0].toString());
		            	jObjArr.put(obj[1].toString());
		            	jArrData.put(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;
			    
			    case "POSCounterMaster":
			    
			    	list = 	objBaseService.funGetSerachList("POSCounter",clientCode);
			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObjArr= new JSONArray();

					    
		            	jObjArr.put(obj[0].toString());
		            	jObjArr.put(obj[1].toString());
		            	jObjArr.put(obj[2].toString());
		            	jObjArr.put(obj[3].toString());
		            	jObjArr.put(obj[4].toString());
		            	jArrData.put(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;
			    
			    
			    case "POSDebitCardMaster":
			    	
			    	list = 	objBaseService.funGetSerachList("POSDebitCard",clientCode);
			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObjArr= new JSONArray();

					    
		            	jObjArr.put(obj[0].toString());
		            	jObjArr.put(obj[1].toString());
		            	
		            	jArrData.put(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;
			    
			    case "POSFactoryMaster":
			    	
			    	list = 	objBaseService.funGetSerachList("POSFactory",clientCode);
			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
			        	JSONArray jObjArr= new JSONArray();

					    
			        	jObjArr.put(obj[0].toString());
			        	jObjArr.put(obj[1].toString());
			        	jObjArr.put(obj[2].toString());
			        	jObjArr.put(obj[3].toString());
			        	jObjArr.put(obj[4].toString());
			        	jArrData.put(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;    
			    
			    case "POSRecipeMaster":
				 	
				 	
				 	hqlQuery.append("select a.strRecipeCode ,a.strItemCode, b.strItemName "
		                           + " from tblrecipehd a left outer join tblitemmaster b on a.strItemCode=b.strItemCode "); 
				 	   
						
				 		list=objBaseService.funGetList(hqlQuery, "sql");
			    								
				 		for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] objArr = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    
						    jArrDataRow.put(objArr[0].toString());//longPricingId
						    jArrDataRow.put(objArr[1].toString());//strItemCode
						    jArrDataRow.put(objArr[2].toString());//strItemName
						    
						    jArrData.put(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
							
				    	break; 
			    
				 	case "POSTableMaster":
						

				 		hqlQuery.setLength(0);
				 		hqlQuery.append("select a.strTableNo ,a.strTableName,"
		                        + "IFNULL(b.strAreaName,'') ,IFNULL(c.strWShortName,'') "
		                        + ",ifnull(d.strPosName,'All') ,a.strStatus  "
		                        + "from tbltablemaster a left outer join tblareamaster b "
		                        + "on a.strAreaCode=b.strAreaCode left outer join tblwaitermaster c "
		                        + "on a.strWaiterNo=c.strWaiterNo "
		                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
		                        + "order by a.strTableName");
			    		
		                		
				 		list=objBaseService.funGetList(hqlQuery, "sql");
				 		
		    		for(int cnt=0;cnt<list.size();cnt++)
					{
						Object[] objArr = (Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    
					    jArrDataRow.put(objArr[0].toString());
					    jArrDataRow.put(objArr[1].toString());
					    jArrDataRow.put(objArr[2].toString());
					    jArrDataRow.put(objArr[3].toString());
					    jArrDataRow.put(objArr[4].toString());
					    jArrDataRow.put(objArr[5].toString());
					    
					    
					    jArrData.put(jArrDataRow);
					}
					jObjSearchData.put(masterName, jArrData);
					    
						break;        
			    
				 	case "POSMenuItemPricingMaster":
				 		hqlQuery.setLength(0);
				 		hqlQuery.append("select a.longPricingId,a.strItemCode,a.strItemName,IF(a.strPosCode='All','All',b.strPosName)strPosName,c.strAreaName,e.strMenuName "
								+",a.strPriceMonday,a.strPriceTuesday,a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,a.strPriceSaturday,a.strPriceSunday "
								+",a.strPopular "
								+",a.dteFromDate,a.dteToDate,d.strCostCenterName,ifnull(f.strSubMenuHeadName,'')strSubMenuHeadName "
								+",a.strHourlyPricing "
								+"from tblmenuitempricingdtl a " 
								+"left outer join  tblsubmenuhead f on a.strSubMenuHeadCode=f.strSubMenuHeadCode "
								+",tblposmaster b,tblareamaster c,tblcostcentermaster d "
								+",tblmenuhd e  "
								+"where (a.strPosCode=b.strPosCode or a.strPosCode='All') "
								+"and a.strAreaCode=c.strAreaCode "
								+"and a.strCostCenterCode=d.strCostCenterCode "
								+"and a.strMenuCode=e.strMenuCode "
								+"GROUP BY a.longPricingId "
								+"ORDER BY a.longPricingId "); 
						
				 		list=objBaseService.funGetList(hqlQuery, "sql");
			    								
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] objArr = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    
						    jArrDataRow.put(objArr[0].toString());//longPricingId
						    jArrDataRow.put(objArr[1].toString());//strItemCode
						    jArrDataRow.put(objArr[2].toString());//strItemName
						    jArrDataRow.put(objArr[3].toString());//strPosName
						    jArrDataRow.put(objArr[4].toString());//strAreaName
						    jArrDataRow.put(objArr[5].toString());//strMenuName
						    jArrDataRow.put(objArr[6].toString());//strPriceMonday
						    jArrDataRow.put(objArr[7].toString());//strPriceTuesday
						    jArrDataRow.put(objArr[8].toString());//strPriceWednesday
						    jArrDataRow.put(objArr[9].toString());//strPriceThursday
						    jArrDataRow.put(objArr[10].toString());//strPriceFriday
						    jArrDataRow.put(objArr[11].toString());//strPriceSaturday
						    jArrDataRow.put(objArr[12].toString());//strPriceSunday
						    jArrDataRow.put(objArr[13].toString());//strPopular
						    jArrDataRow.put(objArr[14].toString());//dteFromDate
						    jArrDataRow.put(objArr[15].toString());//dteToDate
						    jArrDataRow.put(objArr[16].toString());//strCostCenterName
						    jArrDataRow.put(objArr[17].toString());//strSubMenuHeadName
						    jArrDataRow.put(objArr[18].toString());//strHourlyPricing
						   
						    
						    jArrData.put(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
							
				    	break; 
			    
					 case "POSLoadTDHData":
						 	
						 	
						 	list=objBaseService.funGetSerachList("getALLTDH",clientCode);
						 	for(int cnt=0;cnt<list.size();cnt++)
							{
						 		//clsPOSTDHModel objTDHModel = (clsPOSTDHModel) list.get(cnt);
						 		Object obj[]=(Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    jArrDataRow.put(obj[0].toString());
							    jArrDataRow.put(obj[1].toString());
							    jArrDataRow.put(obj[2].toString());
							    jArrDataRow.put(obj[3].toString());
							    jArrDataRow.put(obj[4].toString());
							    
							    
							    jArrData.put(jArrDataRow);
							}
						 	jObjSearchData.put(masterName,jArrData);
						 break;	
						 
					 
				    		
					 case "POSTDHOnItem":
						 
				    		list=objBaseService.funGetSerachList("getALLItemPricing",clientCode);
				    		
								for(int cnt=0;cnt<list.size();cnt++)
								{
									Object[] obj = (Object[]) list.get(cnt);
								    
								    JSONArray jArrDataRow = new JSONArray();
								    jArrDataRow.put(obj[0]);
								    jArrDataRow.put(obj[1]);
								   
								    jArrData.put(jArrDataRow);
								}
								jObjSearchData.put(masterName, jArrData);
				    		break;	
				    		
					 
					    	
							
					 case "POSWiseItemIncentive":
						 	
				 		 boolean flgPreviousRecordFound=false;
				 		 
				 		hqlQuery.setLength(0);
				 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPOSCode,b.strPosName,a.strIncentiveType,a.dblIncentiveValue "
			                + " FROM tblposwiseitemwiseincentives a  left outer join tblposmaster b on a.strPosCode=b.strPosCode"); 
				 		
				 		if(!clientCode.equalsIgnoreCase("All"))
			            {
				 			
				 			hqlQuery.append(" Where a.strPOSCode='").append(clientCode).append("' "); 
			            }
				 		hqlQuery.append(" order by a.strItemCode ");
			            
						
				 		list=objBaseService.funGetList(hqlQuery, "sql");
			    		if(list.size()>0)
			    		{
			    			 flgPreviousRecordFound=true;
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONObject jobjDataRow = new JSONObject();
						    
						    jobjDataRow.put("strItemCode", obj[0].toString());
						    jobjDataRow.put("strItemName", obj[1].toString());
						    jobjDataRow.put("strPOSCode", obj[2].toString());
						    jobjDataRow.put("strPosName", obj[3].toString());
						    jobjDataRow.put("strIncentiveType", obj[4].toString());
						    jobjDataRow.put("dblIncentiveValue", obj[5].toString());
							
						   
						    jArrData.put(jobjDataRow);
						}
			    		}
			    		
			    		 if(!flgPreviousRecordFound)
			             {
			    			 hqlQuery.setLength(0);
			    			 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPosCode,b.strPosName"
			    			 						+ " FROM tblmenuitempricingdtl a  "
			    			 						+ " left outer join tblposmaster b on a.strPosCode=b.strPosCode "); 
			    			 		
			    			 		if(!clientCode.equalsIgnoreCase("All"))
			    		            {
			    			 			
			    			 			hqlQuery.append(" Where a.strPOSCode='").append(clientCode).append("' "); 
			    		            }
			    			 		hqlQuery.append(" order by a.strItemCode ");
			    		            
			    					
			    			 		list=objBaseService.funGetList(hqlQuery, "sql");
			    			 		
			    			 		if(list.size()>0)
			    		    		{
			    			 			
			    					for(int cnt=0;cnt<list.size();cnt++)
			    					{
			    						Object[] obj = (Object[]) list.get(cnt);
			    					    
			    					    JSONObject jobjDataRow = new JSONObject();
			    					    
			    					    jobjDataRow.put("strItemCode", obj[0].toString());
			    					    jobjDataRow.put("strItemName", obj[1].toString());
			    					    jobjDataRow.put("strPOSCode", obj[2].toString());
			    					    jobjDataRow.put("strPosName", obj[3].toString());
			    					    jobjDataRow.put("strIncentiveType", "amt");
			    					    jobjDataRow.put("dblIncentiveValue","0.0");
			    						
			    					   
			    					    jArrData.put(jobjDataRow);
			    					}
			    		    		}
			    		    		
			             }
						jObjSearchData.put(masterName, jArrData);
							
				    	break; 
			    
			    
					  case "POSGroupMaster":
						  	list=objBaseService.funGetSerachList(masterName,clientCode);
							clsGroupMasterModel objModel = null;
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object ob[]=(Object[]) list.get(cnt);
								JSONArray jArrDataRow = new JSONArray();
							    jArrDataRow.put(ob[0].toString());
							    jArrDataRow.put(ob[1].toString());
							    jArrDataRow.put(ob[2].toString());
							    jArrData.put(jArrDataRow);
							}
							jObjSearchData.put(masterName, jArrData);
							break;
							
    	case "MenuItemForPrice":
					    	
				     		hqlQuery.setLength(0);
		    			 	hqlQuery.append(" select a.strItemCode ,a.strItemName ,a.strItemType, a.strRevenueHead ,a.strExternalCode ,b.strSubGroupName "
			                       + " from tblitemmaster a,tblsubgrouphd b "
			                       + " where a.strSubGroupCode=b.strSubGroupCode "
			                       + " and (a.strRawMaterial='N' or a.strItemForSale='Y') "
			                       + " order by a.strItemName");
		    			 	list=objBaseService.funGetList(hqlQuery, "sql");
	    			 		
	    			 		if(list.size()>0)
	    		    		{
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.put(objArr[0].toString());//longPricingId
							    jArrDataRow.put(objArr[1].toString());//strItemCode
							    jArrDataRow.put(objArr[2].toString());//strItemName
							    jArrDataRow.put(objArr[3].toString());//strPosName
							    jArrDataRow.put(objArr[4].toString());//strAreaName
							    jArrDataRow.put(objArr[5].toString());//strMenuName
							  
							    jArrData.put(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
								
					    	break; 
					    	
				    	case "MenuItemForRecipeChild":
					    	
				    		hqlQuery.setLength(0);
		    			 	hqlQuery.append("select a.strItemCode ,a.strItemName ,a.strItemType ,a.strRevenueHead,ifnull(a.strTaxIndicator,''),a.strExternalCode ,b.strSubGroupName  "
			                       + "from tblitemmaster a,tblsubgrouphd b "
			                       + "where a.strSubGroupCode=b.strSubGroupCode "
			                       + "and a.strRawMaterial='Y' "
			                       + "order by a.strItemName;");
		    			 	list=objBaseService.funGetList(hqlQuery, "sql");			
				    		if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.put(objArr[0].toString());//longPricingId
							    jArrDataRow.put(objArr[1].toString());//strItemCode
							    jArrDataRow.put(objArr[2].toString());//strItemName
							    jArrDataRow.put(objArr[3].toString());//strPosName
							    jArrDataRow.put(objArr[4].toString());//strAreaName
							    jArrDataRow.put(objArr[5].toString());//strMenuName
							    jArrDataRow.put(objArr[6].toString());//strPriceMonday
							  
							    
							    jArrData.put(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
								
					    	break;
					    	
				    	case "POSUnSettleBill":
				    		
				    		
				    		String []splitCleientCode=clientCode.split("#");
				    		hqlQuery.setLength(0);
				    		hqlQuery.append("select  a.strBillNo ,ifnull(d.strTableName,'ND')"
			                        + " ,a.dblGrandTotal ,c.strSettelmentDesc, a.strUserCreated"
			                        + " , a.strRemarks"
			                        + " from tblbillhd a inner join tblbillsettlementdtl b on a.strbillno=b.strbillno"
			                        + " inner join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode"
			                        + " left outer join tbltablemaster d on a.strTableNo=d.strTableNo"
			                        + " where date(a.dteBillDate)='" + splitCleientCode[2] + "' "
			                        + " and a.strPOSCode='" + splitCleientCode[1] + "'  ");
				    		
				    		 if (false)
			                  {
				    			 hqlQuery.append(" and a.strUserCreated='" + splitCleientCode[3] + "' ");
			                  }
				    		 hqlQuery.append(" group by a.strbillno order by a.strbillno DESC");
				    		 
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{	 
				    			 for(int cnt=0;cnt<list.size();cnt++)
				    			 {
				    				 Object[] objArr = (Object[]) list.get(cnt);
						    
				    				 JSONArray jArrDataRow = new JSONArray();
						    
				    				 jArrDataRow.put(objArr[0].toString());
				    				 jArrDataRow.put(objArr[1].toString());
				    				 jArrDataRow.put(objArr[2].toString());
				    				 jArrDataRow.put(objArr[3].toString());
				    				 jArrDataRow.put(objArr[4].toString());
				    				 jArrDataRow.put(objArr[5].toString());
						   
				    				 jArrData.put(jArrDataRow);
				    			 }
							}
						jObjSearchData.put(masterName, jArrData);
						    
							break;
			 
				    	case "POSItemList":
					    	
				    		hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strItemCode,a.strItemName, "
				    				+ " a.strItemType ,a.strExternalCode "
				    				+ " from tblitemmaster a,tblsubgrouphd b "
				    				+ " where a.strSubGroupCode=b.strSubGroupCode "
				    				+ " order by a.strItemName "); 
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.put(objArr[0].toString());//item code
							    jArrDataRow.put(objArr[1].toString());//item name
							    jArrDataRow.put(objArr[2].toString());//item type
							    jArrDataRow.put(objArr[3].toString());//external code
							    
							    jArrData.put(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
								
					    	break; 
					    	
					    	
					   	case "PhysicalStock":
						{	
							String []data=clientCode.split("#");
							String posCode=data[1];
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strPSPCode ,b.strItemCode, c.strItemName,a.dteDateCreated "
			                            + " from tblPSPhd a,tblPSPdtl b,tblItemMaster c "
			                            + "  where a.strPSPCode=b.strPSPCode  and b.strItemCode=c.strItemCode "
			                            + "  and a.strPOSCode='" + posCode + "' "
			                            + "  group by a.strPSPCode "); 
							
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.put(objArr[0].toString());//psp code
							    jArrDataRow.put(objArr[1].toString());//item code
							    jArrDataRow.put(objArr[2].toString());//item name
							    jArrDataRow.put(objArr[3].toString());//date created
							    
							    jArrData.put(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
							break;	
						}	
						case "StockIn":
						{	
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strStkInCode ,a.strReasonCode ,b.strReasonName,a.dteDateCreated  "
			                            + "from tblstkinhd a left outer join tblreasonmaster b "
			                            + "on a.strReasonCode=b.strReasonCode "
			                            + "where a.strReasonCode=b.strReasonCode order by a.strStkInCode "); 
							
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.put(objArr[0].toString());//stockIn code
							    jArrDataRow.put(objArr[1].toString());//Reason Code
							    jArrDataRow.put(objArr[2].toString());//Reason name
							    jArrDataRow.put(objArr[3].toString());//date created
							    
							    jArrData.put(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
							break;	
						}	
						case "StockOut":
						{	
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strStkOutCode ,a.strReasonCode , b.strReasonName  ,a.dteDateCreated  "
			                            + "from tblstkouthd a left outer join tblreasonmaster b "
			                            + "on a.strReasonCode=b.strReasonCode "
			                            + "where a.strReasonCode=b.strReasonCode order by a.strStkOutCode "); 
							
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{					
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.put(objArr[0].toString());//stockOut code
							    jArrDataRow.put(objArr[1].toString());//Reason Code
							    jArrDataRow.put(objArr[2].toString());//Reason name
							    jArrDataRow.put(objArr[3].toString());//date created
							    
							    jArrData.put(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
							break;	
						}
						
						case "POSTableReservation":
					    	
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strResCode,b.strCustomerName,ifnull(b.strBuldingCode,'') "
			                        + ",ifnull(b.strBuildingName,''),b.strCity "
			                        + "from tblreservation a "
			                        + "left outer join tblcustomermaster b on  a.strCustomerCode=b.strCustomerCode "
			                        + "left outer join tblbuildingmaster c on b.strBuldingCode=c.strBuildingCode "
			                        + "where a.strCustomerCode<>'' and a.strPOSCode='" +clientCode + "' ");
				    		
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)	
				    		 {
			    		for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] objArr = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    
						    jArrDataRow.put(objArr[0].toString());
						    jArrDataRow.put(objArr[1].toString());
						    jArrDataRow.put(objArr[2].toString());
						    jArrDataRow.put(objArr[3].toString());
						    jArrDataRow.put(objArr[4].toString());
						   
						    
						    
						    jArrData.put(jArrDataRow);
						}
				    		 }
						jObjSearchData.put(masterName, jArrData);
						    
							break;
				     		
				     	case "POSTableReserveMaster":
							

				     		hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strTableNo ,a.strTableName,"
			                        + "IFNULL(b.strAreaName,'') ,IFNULL(c.strWShortName,'') "
			                        + ",ifnull(d.strPosName,'All') ,a.strStatus  "
			                        + "from tbltablemaster a left outer join tblareamaster b "
			                        + "on a.strAreaCode=b.strAreaCode left outer join tblwaitermaster c "
			                        + "on a.strWaiterNo=c.strWaiterNo "
			                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode where a.strStatus!='Reserve' "
			                        + "order by a.strTableName");
				    		
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)	
				    		 {
					    	for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    jArrData.put(objArr[0].toString());
							    jArrData.put(objArr[1].toString());
							    jArrData.put(objArr[2].toString());
							    jArrData.put(objArr[3].toString());
							    jArrData.put(objArr[4].toString());
							    jArrData.put(objArr[5].toString());
							}
				    		 }
							jObjSearchData.put(masterName, jArrData);
							break;
								

				   
		    }
			
		}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return jObjSearchData;
    }
    
   
   
    
}
