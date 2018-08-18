package com.apos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tblordermaster")
@IdClass(clsOrderMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllOrderMaster", 
query = "select m.strOrderCode,m.strOrderDesc "
		+ "from clsOrderMasterModel m where m.strClientCode=:clientCode"),

		@NamedQuery(name = "getOrderMaster", query = "from clsOrderMasterModel where strOrderCode=:orderCode and strClientCode=:clientCode") })
public class clsOrderMasterModel extends clsBaseModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsOrderMasterModel(){}

	public clsOrderMasterModel(clsOrderMasterModel_ID objModelID){
		strOrderCode = objModelID.getStrOrderCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strOrderCode",column=@Column(name="strOrderCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strOrderCode")
	private String strOrderCode;

	@Column(name="strOrderDesc")
	private String strOrderDesc;
	
	@Column(name="tmeUpToTime")
	private String tmeUpToTime;
	
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

//Setter-Getter Methods
	public String getStrOrderCode(){
		return strOrderCode;
	}
	public void setStrOrderCode(String strOrderCode){
		this. strOrderCode = (String) setDefaultValue( strOrderCode, "NA");
	}

	public String getStrOrderDesc(){
		return strOrderDesc;
	}
	public void setStrOrderDesc(String strOrderDesc){
		this. strOrderDesc = (String) setDefaultValue( strOrderDesc, "NA");
	}

	public String getTmeUpToTime() {
		return tmeUpToTime;
	}

	public void setTmeUpToTime(String tmeUpToTime) {
		this.tmeUpToTime = tmeUpToTime;
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
