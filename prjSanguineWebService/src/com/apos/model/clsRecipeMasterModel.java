package com.apos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tblrecipehd")
@IdClass(clsRecipeMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getRecipeMaster", 
query = "from clsRecipeMasterModel where strRecipeCode=:recipeCode and strClientCode=:clientCode") })
public class clsRecipeMasterModel extends clsBaseModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsRecipeMasterModel(){}

	public clsRecipeMasterModel(clsRecipeMasterModel_ID objModelID){
		strRecipeCode = objModelID.getStrRecipeCode();
		strClientCode = objModelID.getStrClientCode();
	}
	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tblrecipedtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strRecipeCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strRecipeCode",column=@Column(name="strRecipeCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

	List<clsRecipeDtlModel> listRecipeDtl = new ArrayList<clsRecipeDtlModel>();
	
	
public List<clsRecipeDtlModel> getListRecipeDtl() {
		return listRecipeDtl;
	}

	public void setListRecipeDtl(List<clsRecipeDtlModel> listRecipeDtl) {
		this.listRecipeDtl = listRecipeDtl;
	}
	//Variable Declaration
	@Column(name="strRecipeCode")
	private String strRecipeCode;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="dteFromDate")
	private String dteFromDate;

	@Column(name="dteToDate")
	private String dteToDate;

	@Column(name="strPOSCode")
	private String strPOSCode;

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
	public String getStrRecipeCode(){
		return strRecipeCode;
	}
	public void setStrRecipeCode(String strRecipeCode){
		this. strRecipeCode = (String) setDefaultValue( strRecipeCode, "");
	}

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "");
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

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "");
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
		this. strClientCode = (String) setDefaultValue( strClientCode, "");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
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
