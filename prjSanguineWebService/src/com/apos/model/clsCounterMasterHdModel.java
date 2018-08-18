package com.apos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tblcounterhd")
@IdClass(clsCounterMasterModel_ID.class)


//@NamedQueries({ @NamedQuery(name = "POSCounter", 
//query = "from clsCounterMasterHdModel") })
//@NamedNativeQueries({ @NamedNativeQuery(name = "@SQL_GET_ALL_ADDRESS", 
//query = "select emp_id, address_line1, city, zipcode from Address") })


@NamedQueries(
		{ @NamedQuery (name="POSCounter",
				query="select m.strCounterCode,m.strCounterName,m.strOperational,m.strUserCode,m.strPOSCode from clsCounterMasterHdModel m where m.strClientCode=:clientCode" ),


		@NamedQuery(name = "getCounterMaster", query = "from clsCounterMasterHdModel where strCounterCode=:counterCode and strClientCode=:clientCode")}
	)




public class clsCounterMasterHdModel  extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsCounterMasterHdModel(){}

	public clsCounterMasterHdModel(clsCounterMasterModel_ID objModelID){
		strCounterCode = objModelID.getStrCounterCode();
		strClientCode = objModelID.getStrClientCode();
		
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblcounterdtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strCounterCode")})
	@Id
    @AttributeOverrides({ @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")), @AttributeOverride(name = "strCounterCode", column = @Column(name = "strCounterCode")) })
	
	List<clsPosCounterDtlModel> listMenuDtl = new ArrayList<clsPosCounterDtlModel>();
	
	

	public List<clsPosCounterDtlModel> getListMenuDtl() {
		return listMenuDtl;
	}

	public void setListMenuDtl(List<clsPosCounterDtlModel> listMenuDtl) {
		this.listMenuDtl = listMenuDtl;
	}

	//Variable Declaration
	@Column(name="strCounterCode")
	private String strCounterCode;

	@Column(name="strCounterName")
	private String strCounterName;

	@Column(name="strPOSCode")
	private String strPOSCode;
	
	

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

	@Column(name="strOperational")
	private String strOperational;

	@Column(name="strUserCode")
	private String strUserCode;
	

//Setter-Getter Methods
	public String getStrCounterCode(){
		return strCounterCode;
	}
	public void setStrCounterCode(String strCounterCode){
		this. strCounterCode = (String) setDefaultValue( strCounterCode, "NA");
	}

	public String getStrCounterName(){
		return strCounterName;
	}
	public void setStrCounterName(String strCounterName){
		this. strCounterName = (String) setDefaultValue( strCounterName, "NA");
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

	public String getStrOperational(){
		return strOperational;
	}
	public void setStrOperational(String strOperational){
		this. strOperational = (String) setDefaultValue( strOperational, "N");
	}
	public String getStrPOSCode() {
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
	


public String getStrUserCode() {
		return strUserCode;
	}

	public void setStrUserCode(String strUserCode) {
		this.strUserCode = strUserCode;
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
