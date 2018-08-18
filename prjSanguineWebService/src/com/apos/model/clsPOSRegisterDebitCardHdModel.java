package com.apos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tbldebitcardmaster")
@IdClass(clsPOSRegisterDebitCardModel_ID.class)

@NamedQueries({ 
//	@NamedQuery(name = "getALLZone", query = "select m.strZoneCode,m.strZoneName"
//		+ " from clsZoneMasterModel m where m.strClientCode=:clientCode"), 
//	
	@NamedQuery(name = "getCardDtl", query = "from clsDebitCardMasterHdModel m where m.strCardTypeCode=:cardTypeCode and m.strClientCode=:clientCode"),

	@NamedQuery(name = "cardNoToDelist", query = "select count(*) from clsPOSRegisterDebitCardHdModel m where m.strCardString=:cardString and m.strStatus='Active' and m.strClientCode=:clientCode"),
	
	@NamedQuery(name = "getCardDtlForUpdate", query = "from clsPOSRegisterDebitCardHdModel m where m.strStatus=:cardStatus and m.strClientCode=:clientCode"),

})




public class clsPOSRegisterDebitCardHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSRegisterDebitCardHdModel(){}

	public clsPOSRegisterDebitCardHdModel(clsPOSRegisterDebitCardModel_ID objModelID){
		strCardNo = objModelID.getStrCardNo();
		
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strCardNo",column=@Column(name="strCardNo")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))
	})

//Variable Declaration
	@Column(name="strCardTypeCode")
	private String strCardTypeCode;

	@Column(name="strCardNo")
	private String strCardNo;

	@Column(name="dblRedeemAmt")
	private double dblRedeemAmt;

	@Column(name="strStatus")
	private String strStatus;

	@Column(name="strUserCreated",updatable=false)
	private String strUserCreated;

	@Column(name="dteDateCreated",updatable=false)
	private String dteDateCreated;

	@Column(name="strCustomerCode")
	private String strCustomerCode;

	@Column(name="intPassword")
	private long intPassword;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strCardString")
	private String strCardString;

	@Column(name="strReachrgeRemark")
	private String strReachrgeRemark;

	@Column(name="strRefMemberCode")
	private String strRefMemberCode;

//Setter-Getter Methods
	public String getStrCardTypeCode(){
		return strCardTypeCode;
	}
	public void setStrCardTypeCode(String strCardTypeCode){
		this. strCardTypeCode = (String) setDefaultValue( strCardTypeCode, "NA");
	}

	public String getStrCardNo(){
		return strCardNo;
	}
	public void setStrCardNo(String strCardNo){
		this. strCardNo = (String) setDefaultValue( strCardNo, "NA");
	}

	public double getDblRedeemAmt(){
		return dblRedeemAmt;
	}
	public void setDblRedeemAmt(double dblRedeemAmt){
		this. dblRedeemAmt = (Double) setDefaultValue( dblRedeemAmt, "0.0000");
	}

	public String getStrStatus(){
		return strStatus;
	}
	public void setStrStatus(String strStatus){
		this. strStatus = (String) setDefaultValue( strStatus, "NA");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getStrCustomerCode(){
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode){
		this. strCustomerCode = (String) setDefaultValue( strCustomerCode, "NA");
	}

	public long getIntPassword(){
		return intPassword;
	}
	public void setIntPassword(long intPassword){
		this. intPassword = (Long) setDefaultValue( intPassword, "0");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrCardString(){
		return strCardString;
	}
	public void setStrCardString(String strCardString){
		this. strCardString = (String) setDefaultValue( strCardString, "NA");
	}

	public String getStrReachrgeRemark(){
		return strReachrgeRemark;
	}
	public void setStrReachrgeRemark(String strReachrgeRemark){
		this. strReachrgeRemark = (String) setDefaultValue( strReachrgeRemark, "");
	}

	public String getStrRefMemberCode(){
		return strRefMemberCode;
	}
	public void setStrRefMemberCode(String strRefMemberCode){
		this. strRefMemberCode = (String) setDefaultValue( strRefMemberCode, "");
	}


//Function to Set Default Values
	private Object setDefaultValue(Object value, Object defaultValue){
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Double && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Integer && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Long && value.toString().length()>0)){
			return value;
		}
		else{
			return defaultValue;
		}
	}

}
