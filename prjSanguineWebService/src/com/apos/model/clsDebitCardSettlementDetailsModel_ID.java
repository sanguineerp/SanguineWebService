
package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsDebitCardSettlementDetailsModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCardTypeCode")
	private String strCardTypeCode;
	
	@Column(name="strSettlementCode")
	private String strSettlementCode;
	

	public clsDebitCardSettlementDetailsModel_ID(){}
	public clsDebitCardSettlementDetailsModel_ID(String strCardTypeCode, String strSettlementCode){
		this.strCardTypeCode=strCardTypeCode;
		this.strSettlementCode=strSettlementCode;
		
	}

//Setter-Getter Methods
	
	public String getStrCardTypeCode() {
		return strCardTypeCode;
	}
	public void setStrCardTypeCode(String strCardTypeCode) {
		this.strCardTypeCode = strCardTypeCode;
	}


public String getStrSettlementCode() {
		return strSettlementCode;
	}
		public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}
	//HashCode and Equals Funtions
	
	@Override
	public int hashCode() {
		return this.strCardTypeCode.hashCode();
		
	}

}
