package com.apos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name="tbltdhhd")
@NamedQueries({ 
	@NamedQuery(name = "getALLTDH", query = "select m.strTDHCode,m.strDescription,m.strMenuCode,m.strItemCode,m.intMaxQuantity"
		+ " from clsPOSTDHModel m where m.strClientCode=:clientCode"),
		
		
		
		@NamedQuery(name = "getTDH", query = "from clsPOSTDHModel where strTDHCode=:TDHCode and strClientCode=:clientCode"),
	
		
})
@IdClass(clsPOSTDHModel_ID.class)

public class clsPOSTDHModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSTDHModel(){}

	public clsPOSTDHModel(clsPOSTDHModel_ID objModelID){
		strTDHCode = objModelID.getStrTDHCode();
		strClientCode = objModelID.getStrClientCode();
	}


	//@CollectionOfElements(fetch=FetchType.EAGER)
	@CollectionOfElements(fetch=FetchType.LAZY)
	 @JoinTable(name="tbltdhcomboitemdtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strTDHCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strTDHCode",column=@Column(name="strTDHCode")),
		 @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))})
	
	//List<clsPOSTDHDtlModel> listTDHDtl = new ArrayList<clsPOSTDHDtlModel>();
	Set<clsPOSTDHDtlModel> listTDHDtl = new HashSet<clsPOSTDHDtlModel>();
	
	
	


	public Set<clsPOSTDHDtlModel> getListTDHDtl() {
		return listTDHDtl;
	}

	public void setListTDHDtl(Set<clsPOSTDHDtlModel> listTDHDtl) {
		this.listTDHDtl = listTDHDtl;
	}


	//Variable Declaration
	@Column(name="strTDHCode")
	private String strTDHCode;

	@Column(name="strDescription")
	private String strDescription;

	@Column(name="strMenuCode")
	private String strMenuCode;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="intMaxQuantity")
	private long intMaxQuantity;

	@Column(name="strApplicable")
	private String strApplicable;

	@Column(name="strComboItemYN")
	private String strComboItemYN;

	@Column(name="strClientCode")
	private String strClientCode;

//Setter-Getter Methods
	public String getStrTDHCode(){
		return strTDHCode;
	}
	public void setStrTDHCode(String strTDHCode){
		this. strTDHCode = (String) setDefaultValue( strTDHCode, "NA");
	}

	public String getStrDescription(){
		return strDescription;
	}
	public void setStrDescription(String strDescription){
		this. strDescription = (String) setDefaultValue( strDescription, "NA");
	}

	public String getStrMenuCode(){
		return strMenuCode;
	}
	public void setStrMenuCode(String strMenuCode){
		this. strMenuCode = (String) setDefaultValue( strMenuCode, "NA");
	}

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "NA");
	}

	public long getIntMaxQuantity(){
		return intMaxQuantity;
	}
	public void setIntMaxQuantity(long intMaxQuantity){
		this. intMaxQuantity = (Long) setDefaultValue( intMaxQuantity, "0");
	}

	public String getStrApplicable(){
		return strApplicable;
	}
	public void setStrApplicable(String strApplicable){
		this. strApplicable = (String) setDefaultValue( strApplicable, "NA");
	}

	public String getStrComboItemYN(){
		return strComboItemYN;
	}
	public void setStrComboItemYN(String strComboItemYN){
		this. strComboItemYN = (String) setDefaultValue( strComboItemYN, "NA");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
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
