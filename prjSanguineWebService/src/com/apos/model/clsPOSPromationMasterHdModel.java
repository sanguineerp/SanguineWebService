 package com.apos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tblpromotionmaster")
@IdClass(clsPOSPromationMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllPromotionMaster", 
query = "select m.strPromoCode,m.strPromoName,m.strPromotionOn "
		+ "from clsPOSPromationMasterHdModel m where m.strClientCode=:clientCode"),

		@NamedQuery(name = "getPromotionMaster", query = "from clsPOSPromationMasterHdModel where strPromoCode=:promoCode and strClientCode=:clientCode")})
public class clsPOSPromationMasterHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSPromationMasterHdModel(){}

	public clsPOSPromationMasterHdModel(clsPOSPromationMasterModel_ID objModelID){
		strPromoCode = objModelID.getStrPromoCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tblbuypromotiondtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strPromoCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strPromoCode",column=@Column(name="strPromoCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

	Set<clsBuyPromotionDtlHdModel> listBuyPromotionDtl = new HashSet<clsBuyPromotionDtlHdModel>();
	
	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tblpromotiondtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strPromoCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strPromoCode",column=@Column(name="strPromoCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

	Set<clsGetPromotionDtlHdModel> listGetPromotionDtl = new HashSet<clsGetPromotionDtlHdModel>();
	
	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tblpromotiondaytimedtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strPromoCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strPromoCode",column=@Column(name="strPromoCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

	Set<clsPromotionDayTimeDtlHdModel> listDayTimeDtl = new HashSet<clsPromotionDayTimeDtlHdModel>();
	
	
public Set<clsBuyPromotionDtlHdModel> getListBuyPromotionDtl() {
		return listBuyPromotionDtl;
	}

	public void setListBuyPromotionDtl(
			Set<clsBuyPromotionDtlHdModel> listBuyPromotionDtl) {
		this.listBuyPromotionDtl = listBuyPromotionDtl;
	}

	public Set<clsGetPromotionDtlHdModel> getListGetPromotionDtl() {
		return listGetPromotionDtl;
	}

	public void setListGetPromotionDtl(
			Set<clsGetPromotionDtlHdModel> listGetPromotionDtl) {
		this.listGetPromotionDtl = listGetPromotionDtl;
	}

	public Set<clsPromotionDayTimeDtlHdModel> getListDayTimeDtl() {
		return listDayTimeDtl;
	}

	public void setListDayTimeDtl(Set<clsPromotionDayTimeDtlHdModel> listDayTimeDtl) {
		this.listDayTimeDtl = listDayTimeDtl;
	}

	//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="strPromoName")
	private String strPromoName;

	@Column(name="strPromotionOn")
	private String strPromotionOn;

	@Column(name="strPromoItemCode")
	private String strPromoItemCode;

	@Column(name="strOperator")
	private String strOperator;

	@Column(name="dblBuyQty")
	private double dblBuyQty;

	@Column(name="dteFromDate")
	private String dteFromDate;

	@Column(name="dteToDate")
	private String dteToDate;

	@Column(name="tmeFromTime")
	private String tmeFromTime;

	@Column(name="tmeToTime")
	private String tmeToTime;
	
	@Column(name="strDays")
	private String strDays;

	@Column(name="strType")
	private String strType;

	@Column(name="strPromoNote")
	private String strPromoNote;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strGetItemCode")
	private String strGetItemCode;

	@Column(name="strGetPromoOn")
	private String strGetPromoOn;
	
	@Column(name="strAreaCode")
	private String strAreaCode;

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = (String) setDefaultValue( strPromoCode, "NA");
	}

	public String getStrPromoName(){
		return strPromoName;
	}
	public void setStrPromoName(String strPromoName){
		this. strPromoName = (String) setDefaultValue( strPromoName, "NA");
	}

	public String getStrPromotionOn(){
		return strPromotionOn;
	}
	public void setStrPromotionOn(String strPromotionOn){
		this. strPromotionOn = (String) setDefaultValue( strPromotionOn, "NA");
	}

	public String getStrPromoItemCode(){
		return strPromoItemCode;
	}
	public void setStrPromoItemCode(String strPromoItemCode){
		this. strPromoItemCode = (String) setDefaultValue( strPromoItemCode, "NA");
	}

	public String getStrOperator(){
		return strOperator;
	}
	public void setStrOperator(String strOperator){
		this. strOperator = (String) setDefaultValue( strOperator, "NA");
	}

	public double getDblBuyQty(){
		return dblBuyQty;
	}
	public void setDblBuyQty(double dblBuyQty){
		this. dblBuyQty = (Double) setDefaultValue( dblBuyQty, "0.0000");
	}

	public String getDteFromDate(){
		return dteFromDate;
	}
	public void setDteFromDate(String dteFromDate){
		this.dteFromDate=dteFromDate;
	}

	public String getDteToDate(){
		return dteToDate;
	}
	public void setDteToDate(String dteToDate){
		this.dteToDate=dteToDate;
	}

	public String getTmeFromTime() {
		return tmeFromTime;
	}

	public void setTmeFromTime(String tmeFromTime) {
		this.tmeFromTime = tmeFromTime;
	}

	public String getTmeToTime() {
		return tmeToTime;
	}

	public void setTmeToTime(String tmeToTime) {
		this.tmeToTime = tmeToTime;
	}

	public String getStrDays(){
		return strDays;
	}
	public void setStrDays(String strDays){
		this. strDays = (String) setDefaultValue( strDays, "NA");
	}

	public String getStrType(){
		return strType;
	}
	public void setStrType(String strType){
		this. strType = (String) setDefaultValue( strType, "NA");
	}

	public String getStrPromoNote(){
		return strPromoNote;
	}
	public void setStrPromoNote(String strPromoNote){
		this. strPromoNote = (String) setDefaultValue( strPromoNote, "NA");
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
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public String getStrGetItemCode(){
		return strGetItemCode;
	}
	public void setStrGetItemCode(String strGetItemCode){
		this. strGetItemCode = (String) setDefaultValue( strGetItemCode, "NA");
	}

	public String getStrGetPromoOn(){
		return strGetPromoOn;
	}
	public void setStrGetPromoOn(String strGetPromoOn){
		this. strGetPromoOn = (String) setDefaultValue( strGetPromoOn, "NA");
	}


	public String getStrAreaCode() {
		return strAreaCode;
	}

	public void setStrAreaCode(String strAreaCode) {
		this.strAreaCode = strAreaCode;
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
