
package com.apos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;


@Entity
@Table(name="tbldebitcardsettlementdtl")

@IdClass(clsDebitCardSettlementDetailsModel_ID.class)
public class clsDebitCardSettlementDetailsModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsDebitCardSettlementDetailsModel(){}
	public clsDebitCardSettlementDetailsModel(clsDebitCardSettlementDetailsModel_ID objModelID){
		strCardTypeCode = objModelID.getStrCardTypeCode();
		strSettlementCode=objModelID.getStrSettlementCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strCardTypeCode",column=@Column(name="strCardTypeCode")), @AttributeOverride(name = "strSettlementCode", column = @Column(name = "strSettlementCode"))
	})
	
//Variable Declaration
	@Column(name="strCardTypeCode")
	private String strCardTypeCode;

	@Column(name="strSettlementCode")
	private String strSettlementCode;

	@Column(name="strApplicable")
	private String strApplicable;

	@Column(name="strClientCode")
	private String strClientCode;
	
	@Column(name="strDataPostFlag")
	private String strDataPostFlag;
	
	
	

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

	

	public String getStrApplicable() {
		return strApplicable;
	}
	public void setStrApplicable(String strApplicable) {
		this.strApplicable = strApplicable;
	}
	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}
	
}

	