

package com.apos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.sanguine.model.clsBaseModel;

@Entity
@Table(name = "tbladvanceordertypemaster")
@IdClass(clsAdvanceOrderMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllAdvOrderMaster", 
query = "select m.strAdvOrderTypeCode,m.strAdvOrderTypeName " 
		+ "from clsAdvanceOrderMasterModel m where m.strClientCode=:clientCode "),

		@NamedQuery(name = "getAdvOrderMaster", 
		query = "from clsAdvanceOrderMasterModel where strAdvOrderTypeCode=:advOrderCode and strClientCode=:clientCode") })
public class clsAdvanceOrderMasterModel  extends clsBaseModel  implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsAdvanceOrderMasterModel()
    {
    }
    
    public clsAdvanceOrderMasterModel(clsAdvanceOrderMasterModel_ID objModelID)
    {
    	strAdvOrderTypeCode = objModelID.getStrAdvOrderTypeCode();
	strClientCode = objModelID.getStrClientCode();
	 }
    
    @Id
    @AttributeOverrides(
    { @AttributeOverride(name = "strAdvOrderTypeCode", column = @Column(name = "strAdvOrderTypeCode")), 
    	@AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))})
    // Variable Declaration
    @Column(name = "strAdvOrderTypeCode")
    private String strAdvOrderTypeCode;
    
    @Column(name = "strAdvOrderTypeName")
    private String strAdvOrderTypeName;
    
    @Column(name = "strOperational")
    private String strOperational;
    
    @Column(name = "strUserCreated", updatable=false)
    private String strUserCreated;
    
    @Column(name = "strUserEdited")
    private String strUserEdited;
    
    @Column(name = "dteDateCreated", updatable=false)
    private String dteDateCreated;
    
    @Column(name = "dteDateEdited")
    private String dteDateEdited;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
    @Column(name = "strDataPostFlag")
    private String strDataPostFlag;
    
    @Column(name = "strPOSCode")
    private String strPOSCode;

	public String getStrAdvOrderTypeCode() {
		return strAdvOrderTypeCode;
	}

	public void setStrAdvOrderTypeCode(String strAdvOrderTypeCode) {
		this.strAdvOrderTypeCode = strAdvOrderTypeCode;
	}

	public String getStrAdvOrderTypeName() {
		return strAdvOrderTypeName;
	}

	public void setStrAdvOrderTypeName(String strAdvOrderTypeName) {
		this.strAdvOrderTypeName = strAdvOrderTypeName;
	}

	public String getStrOperational() {
		return strOperational;
	}

	public void setStrOperational(String strOperational) {
		this.strOperational = strOperational;
	}

	public String getStrUserCreated() {
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated) {
		this.strUserCreated = strUserCreated;
	}

	public String getStrUserEdited() {
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited) {
		this.strUserEdited = strUserEdited;
	}

	public String getDteDateCreated() {
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated) {
		this.dteDateCreated = dteDateCreated;
	}

	public String getDteDateEdited() {
		return dteDateEdited;
	}

	public void setDteDateEdited(String dteDateEdited) {
		this.dteDateEdited = dteDateEdited;
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

	public String getStrPOSCode() {
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
    
    
    
    
}
