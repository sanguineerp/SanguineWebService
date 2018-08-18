
package com.apos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsAdvanceOrderMasterModel_ID implements Serializable
{
    
    // Variable Declaration
    @Column(name = "strAdvOrderTypeCode")
    private String strAdvOrderTypeCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
   
    public clsAdvanceOrderMasterModel_ID()
    {
    }
    
    public clsAdvanceOrderMasterModel_ID(String strAdvOrderTypeCode, String strClientCode)
    {
	this.strAdvOrderTypeCode = strAdvOrderTypeCode;
	this.strClientCode = strClientCode;
	
    }
    
    // Setter-Getter Methods

    public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = strClientCode;
    }

	public String getStrAdvOrderTypeCode() {
		return strAdvOrderTypeCode;
	}

	public void setStrAdvOrderTypeCode(String strAdvOrderTypeCode) {
		this.strAdvOrderTypeCode = strAdvOrderTypeCode;
	}
    
    
   
}
