package com.apos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name="tblzonemaster")
@NamedQueries({ 
	@NamedQuery(name = "getALLZone", query = "select m.strZoneCode,m.strZoneName"
		+ " from clsZoneMasterModel m where m.strClientCode=:clientCode"), 
	
	@NamedQuery(name = "getZone", query = "from clsZoneMasterModel where strZoneCode=:zoneCode and strClientCode=:clientCode")
})


@IdClass(clsZoneMasterModel_ID.class)

public class clsZoneMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsZoneMasterModel(){}

	public clsZoneMasterModel(clsZoneMasterModel_ID objModelID){
		strZoneCode = objModelID.getStrZoneCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strZoneCode",column=@Column(name="strZoneCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))
	})

//Variable Declaration
	@Column(name="strZoneCode")
	private String strZoneCode;

	@Column(name="strZoneName")
	private String strZoneName;

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

//Setter-Getter Methods
	public String getStrZoneCode(){
		return strZoneCode;
	}
	public void setStrZoneCode(String strZoneCode){
		this. strZoneCode = (String) setDefaultValue( strZoneCode, "");
	}

	public String getStrZoneName(){
		return strZoneName;
	}
	public void setStrZoneName(String strZoneName){
		this. strZoneName = (String) setDefaultValue( strZoneName, "");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "");
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
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
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
