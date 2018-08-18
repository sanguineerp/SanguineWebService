package com.apos.model;

import java.io.Serializable;

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
@Table(name="tblfactorymaster")
@IdClass(clsFactoryMasterModel_ID.class)

//@NamedQueries({ @NamedQuery(name = "POSFactory", 
//query = "from clsFactoryMasterHdModel") })

@NamedQueries(
		{@NamedQuery (name="POSFactory" 
				, query="select m.strFactoryCode,m.strFactoryName,m.strUserCreated,m.strUserEdited,m.dteDateCreated from clsFactoryMasterHdModel m where m.strClientCode=:clientCode" ),


		@NamedQuery(name = "getFactoryMaster", query = "from clsFactoryMasterHdModel where strFactoryCode=:factoryCode and strClientCode=:clientCode")}
	)

public class clsFactoryMasterHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsFactoryMasterHdModel(){}

	public clsFactoryMasterHdModel(clsFactoryMasterModel_ID objModelID){
		strFactoryCode = objModelID.getStrFactoryCode();
		strClientCode = objModelID.getStrClientCode();
	}
	
	@CollectionOfElements(fetch=FetchType.EAGER)	
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strFactoryCode",column=@Column(name="strFactoryCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))
		
	})

//Variable Declaration
	@Column(name="strFactoryCode")
	private String strFactoryCode;

	@Column(name="strFactoryName")
	private String strFactoryName;

	@Column(name="strUserCreated",updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated",updatable=false)
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

//Setter-Getter Methods
	public String getStrFactoryCode(){
		return strFactoryCode;
	}
	public void setStrFactoryCode(String strFactoryCode){
		this. strFactoryCode = (String) setDefaultValue( strFactoryCode, "NA");
	}

	public String getStrFactoryName(){
		return strFactoryName;
	}
	public void setStrFactoryName(String strFactoryName){
		this. strFactoryName = (String) setDefaultValue( strFactoryName, "NA");
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
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "N");
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
