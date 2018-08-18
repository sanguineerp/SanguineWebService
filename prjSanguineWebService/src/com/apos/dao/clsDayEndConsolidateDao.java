package com.apos.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.controller.clsUtilityController;
import com.apos.service.clsPOSConfigSettingService;
import com.apos.util.clsBackupDatabase;
import com.apos.util.clsSendMail;
import com.apos.util.clsTextFileGenerationForPrinting2;

@Repository("clsDayEndConsolidateDao")
@Transactional(value = "webPOSTransactionManager")
public class clsDayEndConsolidateDao 
 {
	@Autowired 
	clsSendMail obSendMail;
	
	
	@Autowired 
	clsUtilityController objUtility;
	@Autowired 
	clsPOSConfigSettingService objPOSConfigSettingService;
	
	@Autowired
	clsSetupDao objSetupDao;
	
	@Autowired
	clsPOSMasterDao objPOSMasterDao;
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	clsBackupDatabase obBackupDatabase=new clsBackupDatabase();

	@Autowired
	clsTextFileGenerationForPrinting2 obTextFileGenerationForPrinting2; 
	
	public static JSONObject jsonConsolidateDayEndReturn=new JSONObject();
	
	JSONObject jsonConfig=new JSONObject(); 
	String strClientCode="",strUserCode="", strPOSCode="", strPOSDate="",strPOSName="" ;
	String sql="";
	double sales = 0,cashIn = 0,cashOut = 0,totalSales = 0,totalWithdrawl = 0, 
			totalTransIn = 0,totalTransOuts = 0,totalPayments = 0, totalFloat = 0,
			advCash=0, totalDiscount=0,dblApproxSaleAmount=0;
	int noOfDiscountedBills=0,shiftNo=0;
	String ShiftEnd,DayEnd,EmailReport="";
	Map<String, String> hmPOS=new HashMap<String, String>();
	
	public static String loginPOS=""; 
	
	public JSONObject funDayEndConsolidateGetUIData(JSONObject jObj) 
	{
			 JSONObject jsonDayEndProcess=new JSONObject();
			 JSONObject jsonDayEnd=new JSONObject();
			 JSONObject jsonSettlement=new JSONObject();
			 JSONObject jsonSaleInProgress =new JSONObject();
			 JSONObject jsonUnSettleBill =new JSONObject();
		 dblApproxSaleAmount=0;
		try{
			 strClientCode=jObj.getString("strClientCode");
			 strUserCode=jObj.getString("userCode");
			 strPOSCode=jObj.getString("strPOSCode");
			 strPOSDate=jObj.getString("strPOSDate");
			 shiftNo=Integer.parseInt(jObj.getString("strShiftNo"));
			 jsonDayEnd= funFillCurrencyGrid();
			 jsonSettlement=  funFillSettlementWiseSalesGrid();
			 jsonSaleInProgress=funFillTableSaleInProgress();
			 jsonUnSettleBill=funFillTableUnsettleBills();
			 funFillPOS();
				jsonDayEndProcess.put("jsonDayEnd",jsonDayEnd);
				jsonDayEndProcess.put("jsonSettlement",jsonSettlement);
				jsonDayEndProcess.put("salesInProg", jsonSaleInProgress);
				jsonDayEndProcess.put("UnSettleBill", jsonUnSettleBill);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return jsonDayEndProcess;
	}
	

	public JSONObject funFillCurrencyGrid()throws Exception
	{
		totalSales=0;
		JSONArray jArrDayEnd=new JSONArray();
		 JSONArray jArrDayEndTot=new JSONArray();
		JSONObject jsonDayEnd =new JSONObject();
		JSONObject jsonDayEndTot =new JSONObject();
		//List listDayEnd=new ArrayList<>();
        sql = "select strSettelmentDesc from tblsettelmenthd where strSettelmentType='Cash'";
    	Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	List listSql = querySql.list();
    	if (listSql.size() > 0) 
		{
			for (int i = 0; i < listSql.size(); i++) 
			{
				JSONObject jsonOb =new JSONObject();
				List dataList=new ArrayList();
				String str= (String) listSql.get(i);
			
				jsonOb.put("0",str.toString());
				jsonOb.put("1","0.00");
				jsonOb.put("2","0.00");
				jsonOb.put("3","0.00");
				jsonOb.put("4","0.00");
				jsonOb.put("5","0.00");
				jsonOb.put("6","0.00");
				jsonOb.put("7","0.00");
				jsonOb.put("8","0.00");
				jsonOb.put("9","0.00");
				jsonOb.put("10","0");
		
				jArrDayEnd.put(jsonOb);
				
				
				}
			
			jsonDayEnd.put("tblDayEnd", jArrDayEnd);
		}
		
		 sql = "SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt),c.strSettelmentType "
                    + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + "Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
                    + " and date(a.dteBillDate ) ='" + strPOSDate + "' "
                    + " and c.strSettelmentType='Cash' and a.intShiftCode=" + shiftNo + " GROUP BY c.strSettelmentDesc";
		
		 querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	      listSql = querySql.list();
	    	if (listSql.size() > 0) 
			{
				for (int i = 0; i < listSql.size(); i++) 
				{
					List dataList=new ArrayList();
					Object[] obj = (Object[]) listSql.get(i);

					if (obj[0].toString().equals("Cash"))
			            {
			                sales = sales + (Double.parseDouble(obj[1].toString().toString()));
			            }
			            totalDiscount = totalDiscount + (Double.parseDouble(obj[2].toString().toString()));
		
			            totalSales = totalSales + (Double.parseDouble(obj[1].toString().toString()));
		
			            for (int cntDayEndTable = 0; cntDayEndTable < jArrDayEnd.length(); cntDayEndTable++)
			            {
			            	JSONObject jr=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			            	
			            	if(jr.get("0").toString().equals(obj[0].toString()))
			            	{
			            		jr.put("1", obj[1].toString());

			            	}
			            	
			            }
			    }
			}

	    	  noOfDiscountedBills = 0;
	          sql = "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
	                  + "Where date(dteBillDate ) ='" + strPOSDate + "' "
	                  + "and dblDiscountAmt > 0.00 and intShiftCode=" + shiftNo;
	          Query totDiscBill = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		      List listTotDiscBill = totDiscBill.list();
		    	if (listTotDiscBill.size() > 0) 
				{
					for (int i = 0; i < listTotDiscBill.size(); i++) 
					{
						
						Object[] obj = (Object[]) listTotDiscBill.get(i);
						 noOfDiscountedBills = Integer.parseInt(obj[0].toString());
					}
				}
		    	
	          int totalBillNo = 0;
	          sql = "select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + strPOSDate + "' and "
	                  + " intShiftCode='" + shiftNo+"'";
	                  
	         
	          Query qtotBill = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		      List listTotBill = qtotBill.list();
		    	if (listTotBill.size() > 0) 
				{
		    		totalBillNo = Integer.parseInt(listTotBill.get(0).toString());
					/*for (int i = 0; i < listTotBill.size(); i++) 
					{
						Object[] obj = (Object[]) listTotBill.get(i);
						totalBillNo = Integer.parseInt(obj[0].toString());
					}*/
				}
		    	
		    	
	 
		        // jsonDayEndTot
		        
		         JSONObject jsonOb =new JSONObject();
		         jsonOb.put("0", "Total Sales");
		         jsonOb.put("1", totalSales);
		         jsonOb.put("8", totalBillNo);
		     //    jsonOb.put("1", discountRecords);
		         
		         //jArrDayEndTot.put(jsonOb);
		         
		         sql = "select count(dblAdvDeposite) from tbladvancereceipthd "
		                 + "where dtReceiptDate='" + strPOSDate + "' and intShiftCode=" + shiftNo;
		         Query qTotalAdvance= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			      List listTotalAdvance = qTotalAdvance.list();
			    	if (listTotalAdvance.size() > 0) 
					{ 
			    		int count =0;
						for (int i = 0; i < listTotalAdvance.size(); i++) 
						{
							String str= String.valueOf(listTotalAdvance.get(i));
							count = Integer.parseInt(str);
						}
				
		        
		        
				         if (count > 0)
				         {
				             //sql="select sum(dblAdvDeposite) from tbladvancereceipthd where dtReceiptDate='"+posDate+"'";
				             sql = "select sum(b.dblAdvDepositesettleAmt) from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
				                     + "where date(a.dtReceiptDate)='" + strPOSDate + "'"
				                     + "' and intShiftCode=" + shiftNo + " and c.strSettelmentCode=b.strSettlementCode "
				                     + "and a.strReceiptNo=b.strReceiptNo and c.strSettelmentType='Cash'";
				             System.out.println(sql);

				             qTotalAdvance =webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				             listTotalAdvance = qTotalAdvance.list();
				             if (listTotalAdvance.size() > 0) 
								{
				            	 Object[] obj = (Object[]) listSql.get(0);
				            	 advCash = Double.parseDouble(obj[0].toString());
								}
				             JSONObject jr=(JSONObject) jArrDayEnd.get(0);
				             jr.put("4",advCash );
				           }
					}

		         sql = "select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
		                 + "where dteTransDate='" + strPOSDate + "' "
		                 + "and intShiftCode=" + shiftNo
		                 + " group by strTransType,strCurrencyType";
		         //System.out.println(sql);
		         Query qTransaction= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			      List listTransaction = qTransaction.list();
			    	if (listTransaction.size() > 0) 
					{
			    		for(int i=0;i<listTransaction.size();i++)
			    		{
			    			 Object[] obj = (Object[]) listTransaction.get(i);
					        
			    			 for (int cntDayEndTable = 0; cntDayEndTable < jArrDayEnd.length(); cntDayEndTable++)
				             {
				                 if (obj[0].toString().equals("Float"))
				                 {
				                	  JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
				                         if (job.get("0").toString().equals(obj[2].toString()))
					                     {
					                    	 totalFloat += Double.parseDouble(obj[1].toString());
					                    	 job.put("2", obj[1].toString());
					                    	 cashIn = cashIn + (Double.parseDouble(obj[1].toString().toString()));
					                     }
				                 }
				                 else if (obj[0].toString().equals("Transfer In"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalTransIn +=Double.parseDouble(obj[1].toString());
				                         job.put("3", obj[1].toString());
				                         cashIn = cashIn + (Double.parseDouble(obj[1].toString().toString()));
				                     }
				                 }
				                 else if (obj[0].toString().equals("Payments"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalPayments += Double.parseDouble(obj[1].toString());
				                         job.put("6", obj[1].toString());
				                         cashOut = cashOut + (Double.parseDouble(obj[1].toString().toString()));
				                     }
				                 }
				                 else if (obj[0].toString().equals("Transfer Out"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalTransOuts += Double.parseDouble(obj[1].toString());
				                         job.put("7", obj[1].toString());
				                         cashOut = cashOut + (Double.parseDouble(obj[1].toString().toString()));
				                     }
				                 }
				                 else if (obj[0].toString().equals("Withdrawl"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalWithdrawl += Double.parseDouble(obj[1].toString());
				                         job.put("8", obj[1].toString());
				                         cashOut = cashOut + (Double.parseDouble(obj[1].toString()));
				                     }
				                 }
				             }
						}
					}
		
			    	 sql = "select sum(intPaxNo) from tblbillhd where intShiftCode=" + shiftNo + " "
			                 + "and date(dteBillDate ) ='" + strPOSDate + "'";// + "and strPOSCode='" + strPOSCode + "'";
			         //System.out.println(sql);
			    	 String totalPax="";
			    	 Query qTotalPax= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				      List listTotalPax = qTotalPax.list();
				      if(listTotalPax.get(0) !=null)
				      {
					    	if (listTotalPax.size() > 0) 
							{
				    	
				              totalPax = listTotalPax.get(0).toString();
				          
							}
				      }
				    	jsonDayEnd.put("totalPax", totalPax);
				    		
				    	 cashIn = cashIn + advCash + sales;
	
				         jsonOb.put("2", totalFloat);
				         jsonOb.put("3", totalTransIn);
				         jsonOb.put("4", advCash);
				         jsonOb.put("5", cashIn);
				         jsonOb.put("6", totalPayments);
				         jsonOb.put("7", totalTransOuts);
				         jsonOb.put("8", totalWithdrawl);
				         jsonOb.put("9", cashOut);
				         jsonOb.put("10","");
				         
				         jArrDayEndTot.put(jsonOb);
				         
				         double inHandCash = (cashIn) - cashOut;
				         
				         jsonDayEnd.put("TotalDayEnd", jArrDayEndTot);
				    	
				         double totalReceipts = 0.00, totalPayments = 0.00, balance = 0.00;
				         for (int cntDayEndTable = 0; cntDayEndTable < jArrDayEnd.length(); cntDayEndTable++)
				         {
				        	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
	                         totalReceipts = Double.parseDouble(job.get("1").toString())
				                     + Double.parseDouble(job.get("2").toString())
				                     + Double.parseDouble(job.get("3").toString())
				                     + Double.parseDouble(job.get("4").toString());

				             totalPayments = Double.parseDouble(job.get("6").toString())
				                     + Double.parseDouble(job.get("7").toString())
				                     + Double.parseDouble(job.get("8").toString());
				             balance = totalReceipts - totalPayments;
				             job.put("10", balance);
				            
				         }
	return jsonDayEnd;
	}
	
	public JSONObject funFillSettlementWiseSalesGrid() throws Exception
    {
		
		JSONObject jsonSettlement =new JSONObject();
		JSONObject jsonSettlementTot =new JSONObject();
		JSONArray jArrSettt=new JSONArray();
		JSONArray jArrSetttTot=new JSONArray();
		 totalDiscount = 0;
	        totalSales = 0;
	        sql = "SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt) "
	                + "FROM tblbillhd a, tblbillsettlementdtl b"
	                + ", tblsettelmenthd c Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
	                + " and date(a.dteBillDate ) ='" + strPOSDate + "' "
	                + " and intShiftCode=" + shiftNo
	                + " GROUP BY c.strSettelmentDesc,a.strPosCode";
	        //System.out.println(sql);
	        Query qSettlementSale= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		      List listSettlementSale = qSettlementSale.list();
		    	if (listSettlementSale.size() > 0) 
				{
			    		for(int i=0;i<listSettlementSale.size();i++)
			    		{
			    			 Object[] obj = (Object[]) listSettlementSale.get(i);
			    			 JSONObject js=new JSONObject();
			    			 js.put("0",obj[0].toString());
			    			 js.put("1",obj[1].toString());
				        
				            totalDiscount = totalDiscount + (Double.parseDouble(obj[2].toString()));
				            totalSales = totalSales + (Double.parseDouble(obj[1].toString()));
				            jArrSettt.put(js);
			    		}
				}
		    	
		    	  noOfDiscountedBills = 0;
		          sql = "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
		                  + "Where date(dteBillDate ) ='" + strPOSDate + "'  "
		                  + "and dblDiscountAmt > 0.00 ";

		          Query qTotalDiscountBills= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			      List listTotalDiscountBills = qTotalDiscountBills.list();
			    	if (listTotalDiscountBills.size() > 0) 
					{
			    		for(int i=0;i<listTotalDiscountBills.size();i++)
			    		{
			    			 Object[] obj = (Object[]) listTotalDiscountBills.get(i);
			    			 noOfDiscountedBills =Integer.parseInt(obj[0].toString());
			    		}
					}
		          //System.out.println("Discounts="+totalDiscount+"\tTotal Bills="+noOfDiscountedBills);
			          int totalBillNo = 0;
			          sql = "select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + strPOSDate + "'";
		
			          Query qTotalBills= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				      List listTotalBills = qTotalBills.list();
				    	if (listTotalBills.size() > 0) 
						{
				    		
				    			totalBillNo = Integer.parseInt(String.valueOf(listTotalBills.get(0)));
			            }
			    	
				    	JSONObject job=new JSONObject();
				    	job.put("0","Total Sales");
				    	job.put("1",totalSales);
				    	job.put("2",totalBillNo);
				    	jArrSetttTot.put(job);
				    	
				    	job=new JSONObject();
				    	job.put("0","Total Discount");
				    	job.put("1",totalDiscount);
				    	job.put("2",noOfDiscountedBills);
				    	jArrSetttTot.put(job);
			         
			    	
			         //tblSettlementWiseSalesTotal
			    	if (jArrSettt.length() > 0)
			        {
			    		JSONObject jo=(JSONObject) jArrSettt.get(0);
			    		jo.put("2", totalBillNo);
			            
			        }
			        dblApproxSaleAmount += totalSales;
			        
			        jsonSettlement.put("settlement", jArrSettt);
			        jsonSettlement.put("settlementTot", jArrSetttTot);
		return jsonSettlement;
    }
	      
	public JSONObject funFillTableSaleInProgress() throws Exception
	{
		JSONObject jsonSaleInProgress =new JSONObject();
		JSONArray jArrSalesInProgress=new JSONArray();
		 double dblSaleInProgressAmount = 0.00;
		 

	        String sql_FillTable = "select b.strTableName,sum(a.dblAmount) "
	                + " from tblitemrtemp a,tbltablemaster b "
	                + " where a.strTableNo=b.strTableNo and a.strNCKotYN='N' "
	                + " group by a.strTableNo";
	        JSONObject jsonOb=new JSONObject();
	        Query qSaleprog= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_FillTable);
		      List listSaleprog = qSaleprog.list();
		    	if (listSaleprog.size() > 0) 
				{
			    		for(int i=0;i<listSaleprog.size();i++)
			    		{
			    			 Object[] obj = (Object[]) listSaleprog.get(i);
			    			 dblSaleInProgressAmount += Double.parseDouble(obj[1].toString());
			    			 JSONObject jOb=new JSONObject();
			    			 jOb.put("0", obj[0].toString());
			    			 jOb.put("1", obj[1].toString());
			    			
			    			 jArrSalesInProgress.put(jOb);
			    			
					       }
				}
		    	jsonOb.put("0","");
		    	jsonOb.put("1","");
		    	jArrSalesInProgress.put(jsonOb);
		    	jsonOb=new JSONObject();
		    	jsonOb.put("0","Total");
		    	jsonOb.put("1",dblSaleInProgressAmount);
		    	jArrSalesInProgress.put(jsonOb);
		    	dblApproxSaleAmount += dblSaleInProgressAmount;
		    	jsonSaleInProgress.put("salesInProg", jArrSalesInProgress);
		return jsonSaleInProgress;
	}
		
	public JSONObject funFillTableUnsettleBills() throws Exception
	{
		JSONObject jsonUnSettleBill =new JSONObject();
		JSONArray jArrUnSettleBill=new JSONArray();
		 double unSetteledBillAmount = 0.00;
		 
		 String sqlUnsettledBillsDina = "select a.strBillNo,c.strTableName,a.dblGrandTotal "
	                + " from tblbillhd a,tbltablemaster c "
	                + " where  date(a.dteBillDate)='" + strPOSDate+ "' "
	                + " and a.strTableNo=c.strTableNo and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) ";
		 
		  Query qUnsettledBills= webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlUnsettledBillsDina);
	      List listUnsettledBills = qUnsettledBills.list();
	    	if (listUnsettledBills.size() > 0) 
			{
		    		for(int i=0;i<listUnsettledBills.size();i++)
		    		{
		    			 Object[] obj = (Object[]) listUnsettledBills.get(i);
		                 unSetteledBillAmount += Double.parseDouble(obj[2].toString());
			             JSONObject jb=new JSONObject();
			             jb.put("0",obj[0].toString());
			             jb.put("1",obj[1].toString());
			             jb.put("2",obj[2].toString());
		               
			             jArrUnSettleBill.put(jb);
			        }
			}
	       
	        String sqlUnsettledBillDirectBiller = "select a.strBillNo,a.dblGrandTotal "
	                + " from tblbillhd a "
	                + " where a.strTableNo='' and  date(a.dteBillDate)='" + strPOSDate + "' "
	                + " and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) ";
	        
	        Query qUnsettledBillsDirectBiller= webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlUnsettledBillDirectBiller);
		      List listUnBillsDirectBiller = qUnsettledBillsDirectBiller.list();
		    	if (listUnBillsDirectBiller.size() > 0) 
				{
			    		for(int i=0;i<listUnBillsDirectBiller.size();i++)
			    		{
			    			 Object[] obj = (Object[]) listUnBillsDirectBiller.get(i);
			    			 unSetteledBillAmount +=  Double.parseDouble(obj[1].toString());
			    			    JSONObject jb=new JSONObject();
					             jb.put("0",obj[0].toString());
					             jb.put("1","Direct Biller");
					             jb.put("2",obj[1].toString());
				               
					             jArrUnSettleBill.put(jb);
			    			
					     }
				}
		    	
		     JSONObject jsonOb=new JSONObject();
		    	jsonOb.put("0","");
		    	jsonOb.put("1","");
		    	jsonOb.put("2","");
		    	jArrUnSettleBill.put(jsonOb);
		    	jsonOb=new JSONObject();
		    	jsonOb.put("0","Total");
		    	jsonOb.put("1","");
		    	jsonOb.put("2",unSetteledBillAmount);
		    	jArrUnSettleBill.put(jsonOb);
		    	
		    	jsonUnSettleBill.put("jArrUnSettle", jArrUnSettleBill);
		    	
		    	 dblApproxSaleAmount += unSetteledBillAmount;
		    	 jsonUnSettleBill.put("ApproxSaleAmount", dblApproxSaleAmount);
		    	 
		return jsonUnSettleBill;
	}
	
	private void funFillPOS() throws Exception
    {
        hmPOS.clear();
        sql = "select strPOSCode,strPOSName from tblposmaster ";
        Query qpos=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		List listPOS=qpos.list();
		if(listPOS.size()>0)
		{
			for(int i=0;i<listPOS.size();i++)
			{
				Object obPOS[]=(Object[])listPOS.get(i);
				hmPOS.put(obPOS[0].toString(),obPOS[1].toString());
			}
		}
        
        
    }

	public JSONObject funShiftStartProcess(String shift) 
    {
		JSONObject jObj=new JSONObject();
		try{
				
				
				int shiftNo=Integer.parseInt(shift);
				String strPOS="";
				String sql = "select strPOSCode from tblposmaster where strOperationalYN='Y' ";
				Query qpos=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listPOS=qpos.list();
				if(listPOS.size()>0)
				{
					
					for(int i=0;i<listPOS.size();i++)
					{
						strPOS=listPOS.get(i).toString();
						sql = "update tbldayendprocess set strShiftEnd='N' "
							    + "where strPOSCode='" +strPOS + "' and strDayEnd='N' and strShiftEnd=''";
							Query qUpdate=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
							qUpdate.executeUpdate();
						
					}
				}
	            if (shiftNo == 0)
					{
					    shiftNo++;
					}
	            
	            sql = "select strPOSCode from tblposmaster where strOperationalYN='Y' ";
	            qpos=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            listPOS=qpos.list();
				if(listPOS.size()>0)
				{
					for(int i=0;i<listPOS.size();i++)
					{
						strPOS=listPOS.get(i).toString();
					    sql = "update tbldayendprocess set intShiftCode= " + shiftNo + " "
		                        + "where strPOSCode='" + strPOS + "' and strShiftEnd='N' and strDayEnd='N'";
		            
					}
				}
	            	
					jObj.put("shiftEnd", "N");
					jObj.put("DayEnd", "N");
					jObj.put("shiftNo",shiftNo);


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return jObj;
    }  
	  
	//btnShiftEndMouseClicked()..jpos
	public JSONObject funConsolidateDayEndProcess(String strPOSCode,String shiftNo,String strUserCode,String POSDate,String strClientCode,String EmailReport)
	{
		try{
			loginPOS=strPOSCode; //need login poscode in text file gen... 
			this.strUserCode=strUserCode;
			this.strPOSCode=strPOSCode;
			strPOSDate=POSDate;
			this.shiftNo=Integer.parseInt(shiftNo);
			JSONObject JSONEnableShiftYN = objSetupDao.funGetParameterValuePOSWise(strUserCode,strPOSCode, "gEnableShiftYN");
			String gEnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
			
			//All table config data loaded.. DBConnfig File
			JSONObject jsonData=objPOSConfigSettingService.funLoadPOSConfigSetting(strClientCode);
		     JSONArray jArr=(JSONArray)jsonData.get("configSetting");
		     jsonConfig=jArr.getJSONObject(0);
		     
			if(gEnableShiftYN.equals("Y"))
			{
				obBackupDatabase.funTakeBackUpDB(strClientCode);
				//clsGlobalVarClass.funBackupDatabase();
                funShiftEndButtonClicked();
			}
			else
			{
				 
			     //strOS
				 if (jsonConfig.get("strOS").toString().equalsIgnoreCase("Windows"))
                 {
					 obBackupDatabase.funTakeBackUpDB(strClientCode);// clsGlobalVarClass.funBackupDatabase();
                 }
				funShiftEndButtonClicked();
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return jsonConsolidateDayEndReturn;
	}
	
	private void funShiftEndButtonClicked()
    {
        try
        {
        	JSONObject JSONEnableShiftYN = objSetupDao.funGetParameterValuePOSWise(strUserCode,strPOSCode, "gEnableShiftYN");
			String gEnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
			
            if (gEnableShiftYN.equals("Y"))//for shift wise
            {
                boolean flgDayEnd = false;
                sql = "select strPosCode from tbldayendprocess  "
                        + " where intShiftCode>0 and strShiftEnd='N' "
                        + " and date(dtePOSDate) = '" + strPOSDate + "' "
                        + " ORDER by strPosCode ";
                Query qpos=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listPOS=qpos.list();
				if(listPOS.size()>0)
				{
					for(int i=0;i<listPOS.size();i++)
					{
						funShiftEnd(listPOS.get(i).toString());
					}
				}
               
				sql = "select  sum(dblTotalSale),sum(dblTotalDiscount),sum(dblPayments) "
                        + " from tbldayendprocess where date(dtePOSDate)='" + strPOSDate + "' "
                        + " and strDayEnd='Y'";
				
				Query qTot=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listTot=qTot.list();
				if(listTot.size()>0)
				{
					for(int i=0;i<listTot.size();i++)
					{
						Object obTotData[]=(Object[]) listTot.get(i);
						
						String filePath = System.getProperty("user.dir");
	                    filePath = filePath + "/Temp/Temp_DayEndReport.txt";

	                    JSONObject jsPrintType = objSetupDao.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gPrintType");
	        			String gPrintType=jsPrintType.get("gPrintType").toString();
	                    if (gPrintType.equalsIgnoreCase("Text File"))
	                    {
	                    	
	                       // clsTextFileGenerationForPrinting2 obj = new clsTextFileGenerationForPrinting2();
	                    	obTextFileGenerationForPrinting2.funGenerateTextDayEndReport("All", strPOSDate, "", shiftNo,strClientCode,strUserCode);
	                        //String posCode, String billDate, String reprint, int shiftNo,String clientCode,String userCode
	                    }
	                    new clsSendMail().funSendMail(Double.parseDouble(obTotData[0].toString()),Double.parseDouble(obTotData[1].toString()), Double.parseDouble(obTotData[2].toString()), filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
	                    //obSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
					}
				}
             
            }
            else
            {
                boolean flgDayEnd = false;
                sql = "select strPosCode from tbldayendprocess  "
                        + " where intShiftCode>0 and strShiftEnd='N' "
                        + " and date(dtePOSDate) = '" + strPOSDate + "' "
                        + " ORDER by strPosCode ";
                
                Query qpos=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listPOS=qpos.list();
				if(listPOS.size()>0)
				{
					for(int i=0;i<listPOS.size();i++)
					{
						funShiftEnd(listPOS.get(i).toString());
					}
				}
              
                sql = "select  sum(dblTotalSale),sum(dblTotalDiscount),sum(dblPayments) "
                        + " from tbldayendprocess where date(dtePOSDate)='" + strPOSDate + "' "
                        + " and strDayEnd='Y'";
                
                Query qTot=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listTot=qTot.list();
				if(listTot.size()>0)
				{
					for(int i=0;i<listTot.size();i++)
					{
						Object obTotData[]=(Object[]) listTot.get(i);
  					    String filePath = System.getProperty("user.dir");
	                    filePath = filePath + "/Temp/Temp_DayEndReport.txt";
	                    JSONObject jsPrintType = objSetupDao.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gPrintType");
	        			String gPrintType=jsPrintType.get("gPrintType").toString();
	                    
	                    if (gPrintType.equalsIgnoreCase("Text File"))
	                    {
	                      //  clsTextFileGenerationForPrinting2 obj = new clsTextFileGenerationForPrinting2();
	                    	obTextFileGenerationForPrinting2.funGenerateTextDayEndReport("All", strPOSDate, "", shiftNo,strClientCode,strUserCode);
	                    	 
	                    }
	                    //new clsSendMail().funSendMail(rsTotData.getDouble(1), rsTotData.getDouble(2), rsTotData.getDouble(3), filePath);
	                    obSendMail.funSendMail(Double.parseDouble(obTotData[0].toString()),Double.parseDouble(obTotData[1].toString()), Double.parseDouble(obTotData[2].toString()), filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
					}
				}
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	 private void funShiftEnd(String posCode)
	    {
	        try
	        {
	        	JSONObject JSONEnableShiftYN = objSetupDao.funGetParameterValuePOSWise(strUserCode,strPOSCode, "gEnableShiftYN");
				String gEnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
				
	            if (gEnableShiftYN.equals("Y"))//for shift wise
	            {
	                sql = "delete from tblitemrtemp where strTableNo='null'";
	                webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
	                
	                //clsGlobalVarClass.gDayEndReportForm = "DayEndReport";
	                clsDayEndProcessDao.gDayEndReportForm = "DayEndReport";
	                jsonConsolidateDayEndReturn.put("gDayEndReportForm","DayEndReport");
	                
	                String sqlShift = "select date(max(dtePOSDate)),intShiftCode"
	                        + " from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'"
	                        + " and (strShiftEnd='' or strShiftEnd='N')";
	                
	                Query qShift=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlShift);
	                List listShift=qShift.list();
	                if(listShift.size()>0)
	                {
	                	for(int i=0;i<listShift.size();i++)
	                	{
	                		Object ob[]=(Object[])listShift.get(i);
	        	            shiftNo = Integer.parseInt(ob[1].toString());	
	                	}
	                	
	                }

	                sql = "update tbltablemaster set strStatus='Normal' "
	                        + " where strPOSCode='" + posCode + "' ";

	                //                sql = "update tbldayendprocess set strShiftEnd='Y'"
	                //                        + " where strPOSCode='" + posCode + "' and strDayEnd='N'";
	                //                clsGlobalVarClass.dbMysql.execute(sql);
	                objUtility.funGetNextShiftNoForShiftEnd(posCode, shiftNo,strClientCode, strUserCode);
	                //String posCode, int shiftNo,String strClientCode,String strUserCode
	            }
	            else
	            {

	                sql = "delete from tblitemrtemp where strTableNo='null'";
	                webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
	                //clsGlobalVarClass.dbMysql.execute(sql);

	                //clsGlobalVarClass.gDayEndReportForm = "DayEndReport";
	                clsDayEndProcessDao.gDayEndReportForm = "DayEndReport";
	                jsonConsolidateDayEndReturn.put("gDayEndReportForm","DayEndReport");

	                String sqlShift = "select date(max(dtePOSDate)),intShiftCode"
	                        + " from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'"
	                        + " and (strShiftEnd='' or strShiftEnd='N')";
	              
	                Query qShift=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlShift);
	                List listShift=qShift.list();
	                if(listShift.size()>0)
	                {
	                	for(int i=0;i<listShift.size();i++)
	                	{
	                		Object ob[]=(Object[])listShift.get(i);
	        	            shiftNo = Integer.parseInt(ob[1].toString());	
	                	}
	                	
	                }
	                else
	                {
	                    shiftNo++;
	                }

	                sql = "update tbltablemaster set strStatus='Normal' "
	                        + " where strPOSCode='" + posCode + "'";

	                sql = "update tbldayendprocess set strShiftEnd='Y'"
	                        + " where strPOSCode='" + posCode + "' and strDayEnd='N'";
	                webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                
	                objUtility.funGetNextShiftNo(posCode, shiftNo,strClientCode,strUserCode);
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	
}
