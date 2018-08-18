package com.apos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tbldebitcardtype")
@IdClass(clsDebitCardMasterModel_ID.class)


//@NamedQueries({ @NamedQuery(name = "POSDebitCard", 
//query = "from clsDebitCardMasterHdModel") })


@NamedQueries(
		{ @NamedQuery (name="POSDebitCard" 
				, query="select m.strCardTypeCode,m.strCardName from clsDebitCardMasterHdModel m where m.strClientCode=:clientCode" ),

		@NamedQuery(name = "getDebitCardMaster", query = "from clsDebitCardMasterHdModel where strCardTypeCode=:cardTypeCode and strClientCode=:clientCode")
		
		
		}
	)


public class clsDebitCardMasterHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsDebitCardMasterHdModel(){}

	public clsDebitCardMasterHdModel(clsDebitCardMasterModel_ID objModelID){
		strCardTypeCode = objModelID.getStrCardTypeCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tbldebitcardsettlementdtl" , joinColumns={@JoinColumn(name="strCardTypeCode"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strCardTypeCode",column=@Column(name="strCardTypeCode"))
	})
	
	List<clsDebitCardSettlementDtlModel> listDebitCardSettleDtl = new ArrayList<clsDebitCardSettlementDtlModel>();

	
	
public List<clsDebitCardSettlementDtlModel> getListDebitCardSettleDtl() {
		return listDebitCardSettleDtl;
	}

	public void setListDebitCardSettleDtl(
			List<clsDebitCardSettlementDtlModel> listDebitCardSettleDtl) {
		this.listDebitCardSettleDtl = listDebitCardSettleDtl;
	}

	//Variable Declaration
	@Column(name="strCardTypeCode")
	private String strCardTypeCode;

	@Column(name="strCardName")
	private String strCardName;

	@Column(name="strDebitOnCredit")
	private String strDebitOnCredit;

	@Column(name="strRoomCard")
	private String strRoomCard;

	@Column(name="strComplementary")
	private String strComplementary;

	@Column(name="strAutoTopUp")
	private String strAutoTopUp;

	@Column(name="strRedeemableCard")
	private String strRedeemableCard;

	@Column(name="strCardInUse")
	private String strCardInUse;

	@Column(name="strEntryCharge")
	private String strEntryCharge;

	@Column(name="strCoverCharge")
	private String strCoverCharge;

	@Column(name="strDiplomate")
	private String strDiplomate;

	@Column(name="strAllowTopUp")
	private String strAllowTopUp;

	@Column(name="strExValOnTopUp")
	private String strExValOnTopUp;

	@Column(name="strSetExpiryDt")
	private String strSetExpiryDt;

	@Column(name="dteExpiryDt")
	private String dteExpiryDt;

	@Column(name="strCurrentFinacialYr")
	private String strCurrentFinacialYr;

	@Column(name="intValidityDays")
	private long intValidityDays;

	@Column(name="dblCardValueFixed")
	private double dblCardValueFixed;

	@Column(name="dblMinVal")
	private double dblMinVal;

	@Column(name="dblMaxVal")
	private double dblMaxVal;

	@Column(name="dblDepositAmt")
	private double dblDepositAmt;

	@Column(name="dblMinCharge")
	private double dblMinCharge;

	@Column(name="strPayModCash")
	private String strPayModCash;

	@Column(name="strPayModParty")
	private String strPayModParty;

	@Column(name="strPayModMember")
	private String strPayModMember;

	@Column(name="strPayModCreditCard")
	private String strPayModCreditCard;

	@Column(name="strPayModStaff")
	private String strPayModStaff;

	@Column(name="strPayModCheque")
	private String strPayModCheque;

	@Column(name="dblMaxRefundAmt")
	private double dblMaxRefundAmt;

	@Column(name="strUserCreated",updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated",updatable=false)
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strRedemptionLimitType")
	private String strRedemptionLimitType;

	@Column(name="dblRedemptionLimitValue")
	private double dblRedemptionLimitValue;

	@Column(name="strCustomerCompulsory")
	private String strCustomerCompulsory;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strCashCard")
	private String strCashCard;

	@Column(name="strAuthorizeMemberCard")
	private String strAuthorizeMemberCard;

	

//Setter-Getter Methods
	public String getStrCardTypeCode(){
		return strCardTypeCode;
	}
	public void setStrCardTypeCode(String strCardTypeCode){
		this. strCardTypeCode = (String) setDefaultValue( strCardTypeCode, "NA");
	}

	public String getStrCardName(){
		return strCardName;
	}
	public void setStrCardName(String strCardName){
		this. strCardName = (String) setDefaultValue( strCardName, "NA");
	}

	public String getStrDebitOnCredit(){
		return strDebitOnCredit;
	}
	public void setStrDebitOnCredit(String strDebitOnCredit){
		this. strDebitOnCredit = (String) setDefaultValue( strDebitOnCredit, "NA");
	}

	public String getStrRoomCard(){
		return strRoomCard;
	}
	public void setStrRoomCard(String strRoomCard){
		this. strRoomCard = (String) setDefaultValue( strRoomCard, "NA");
	}

	public String getStrComplementary(){
		return strComplementary;
	}
	public void setStrComplementary(String strComplementary){
		this. strComplementary = (String) setDefaultValue( strComplementary, "NA");
	}

	public String getStrAutoTopUp(){
		return strAutoTopUp;
	}
	public void setStrAutoTopUp(String strAutoTopUp){
		this. strAutoTopUp = (String) setDefaultValue( strAutoTopUp, "NA");
	}

	public String getStrRedeemableCard(){
		return strRedeemableCard;
	}
	public void setStrRedeemableCard(String strRedeemableCard){
		this. strRedeemableCard = (String) setDefaultValue( strRedeemableCard, "NA");
	}

	public String getStrCardInUse(){
		return strCardInUse;
	}
	public void setStrCardInUse(String strCardInUse){
		this. strCardInUse = (String) setDefaultValue( strCardInUse, "NA");
	}

	public String getStrEntryCharge(){
		return strEntryCharge;
	}
	public void setStrEntryCharge(String strEntryCharge){
		this. strEntryCharge = (String) setDefaultValue( strEntryCharge, "NA");
	}

	public String getStrCoverCharge(){
		return strCoverCharge;
	}
	public void setStrCoverCharge(String strCoverCharge){
		this. strCoverCharge = (String) setDefaultValue( strCoverCharge, "NA");
	}

	public String getStrDiplomate(){
		return strDiplomate;
	}
	public void setStrDiplomate(String strDiplomate){
		this. strDiplomate = (String) setDefaultValue( strDiplomate, "NA");
	}

	public String getStrAllowTopUp(){
		return strAllowTopUp;
	}
	public void setStrAllowTopUp(String strAllowTopUp){
		this. strAllowTopUp = (String) setDefaultValue( strAllowTopUp, "NA");
	}

	public String getStrExValOnTopUp(){
		return strExValOnTopUp;
	}
	public void setStrExValOnTopUp(String strExValOnTopUp){
		this. strExValOnTopUp = (String) setDefaultValue( strExValOnTopUp, "NA");
	}

	public String getStrSetExpiryDt(){
		return strSetExpiryDt;
	}
	public void setStrSetExpiryDt(String strSetExpiryDt){
		this. strSetExpiryDt = (String) setDefaultValue( strSetExpiryDt, "NA");
	}

	public String getDteExpiryDt(){
		return dteExpiryDt;
	}
	public void setDteExpiryDt(String dteExpiryDt){
		this.dteExpiryDt=dteExpiryDt;
	}

	public String getStrCurrentFinacialYr(){
		return strCurrentFinacialYr;
	}
	public void setStrCurrentFinacialYr(String strCurrentFinacialYr){
		this. strCurrentFinacialYr = (String) setDefaultValue( strCurrentFinacialYr, "NA");
	}

	public long getIntValidityDays(){
		return intValidityDays;
	}
	public void setIntValidityDays(long intValidityDays){
		this. intValidityDays = (Long) setDefaultValue( intValidityDays, "0");
	}

	public double getDblCardValueFixed(){
		return dblCardValueFixed;
	}
	public void setDblCardValueFixed(double dblCardValueFixed){
		this. dblCardValueFixed = (Double) setDefaultValue( dblCardValueFixed, "0.0000");
	}

	public double getDblMinVal(){
		return dblMinVal;
	}
	public void setDblMinVal(double dblMinVal){
		this. dblMinVal = (Double) setDefaultValue( dblMinVal, "0.0000");
	}

	public double getDblMaxVal(){
		return dblMaxVal;
	}
	public void setDblMaxVal(double dblMaxVal){
		this. dblMaxVal = (Double) setDefaultValue( dblMaxVal, "0.0000");
	}

	public double getDblDepositAmt(){
		return dblDepositAmt;
	}
	public void setDblDepositAmt(double dblDepositAmt){
		this. dblDepositAmt = (Double) setDefaultValue( dblDepositAmt, "0.0000");
	}

	public double getDblMinCharge(){
		return dblMinCharge;
	}
	public void setDblMinCharge(double dblMinCharge){
		this. dblMinCharge = (Double) setDefaultValue( dblMinCharge, "0.0000");
	}

	public String getStrPayModCash(){
		return strPayModCash;
	}
	public void setStrPayModCash(String strPayModCash){
		this. strPayModCash = (String) setDefaultValue( strPayModCash, "NA");
	}

	public String getStrPayModParty(){
		return strPayModParty;
	}
	public void setStrPayModParty(String strPayModParty){
		this. strPayModParty = (String) setDefaultValue( strPayModParty, "NA");
	}

	public String getStrPayModMember(){
		return strPayModMember;
	}
	public void setStrPayModMember(String strPayModMember){
		this. strPayModMember = (String) setDefaultValue( strPayModMember, "NA");
	}

	public String getStrPayModCreditCard(){
		return strPayModCreditCard;
	}
	public void setStrPayModCreditCard(String strPayModCreditCard){
		this. strPayModCreditCard = (String) setDefaultValue( strPayModCreditCard, "NA");
	}

	public String getStrPayModStaff(){
		return strPayModStaff;
	}
	public void setStrPayModStaff(String strPayModStaff){
		this. strPayModStaff = (String) setDefaultValue( strPayModStaff, "NA");
	}

	public String getStrPayModCheque(){
		return strPayModCheque;
	}
	public void setStrPayModCheque(String strPayModCheque){
		this. strPayModCheque = (String) setDefaultValue( strPayModCheque, "NA");
	}

	public double getDblMaxRefundAmt(){
		return dblMaxRefundAmt;
	}
	public void setDblMaxRefundAmt(double dblMaxRefundAmt){
		this. dblMaxRefundAmt = (Double) setDefaultValue( dblMaxRefundAmt, "0.0000");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "NA");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDateEdited(){
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited){
		this.dteDateEdited=dteDateEdited;
	}

	public String getStrRedemptionLimitType(){
		return strRedemptionLimitType;
	}
	public void setStrRedemptionLimitType(String strRedemptionLimitType){
		this. strRedemptionLimitType = (String) setDefaultValue( strRedemptionLimitType, "NA");
	}

	public double getDblRedemptionLimitValue(){
		return dblRedemptionLimitValue;
	}
	public void setDblRedemptionLimitValue(double dblRedemptionLimitValue){
		this. dblRedemptionLimitValue = (Double) setDefaultValue( dblRedemptionLimitValue, "0.0000");
	}

	public String getStrCustomerCompulsory(){
		return strCustomerCompulsory;
	}
	public void setStrCustomerCompulsory(String strCustomerCompulsory){
		this. strCustomerCompulsory = (String) setDefaultValue( strCustomerCompulsory, "NA");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrCashCard(){
		return strCashCard;
	}
	public void setStrCashCard(String strCashCard){
		this. strCashCard = (String) setDefaultValue( strCashCard, "NA");
	}

	public String getStrAuthorizeMemberCard(){
		return strAuthorizeMemberCard;
	}
	public void setStrAuthorizeMemberCard(String strAuthorizeMemberCard){
		this. strAuthorizeMemberCard = (String) setDefaultValue( strAuthorizeMemberCard, "NA");
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
