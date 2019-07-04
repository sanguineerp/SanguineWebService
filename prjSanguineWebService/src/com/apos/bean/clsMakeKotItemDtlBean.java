package com.apos.bean;

public class clsMakeKotItemDtlBean 
{
	private String sequenceNo;
    private String KOTNo;//1
    private String tableNo;//2
    private String waiterNo;//3
    private String itemName;//4
    private String itemCode;//5
    private double qty;//6
    private double amt;//7
    private int paxNo;//8
    private String printYN;//9
    private String tdhComboItemYN;//10
    private boolean isModifier;//11
    private String modifierCode;//12
    private String tdh_ComboItemCode;//13
    private String modifierGroupCode;
    private String strNCKotYN;
    private double itemRate;
    private String strDefaultModifierDeselectedYN;
    private double dblFiredQty;//
    private double dblPendingQty;//
    private double dblFireQty;//
    private double dblPrintQty;//

    public clsMakeKotItemDtlBean()
    {
    }

    public clsMakeKotItemDtlBean(String sequenceNo, String KOTNo, String tableNo, String waiterNo, String itemName, String itemCode,
	    double qty, double amt, int paxNo, String printYN, String tdhComboItemYN, boolean isModifier, String modifierCode,
	    String tdh_ComboItemCode, String modifierGroupCode, String strNCKotYN, double itemRate)
    {
		this.sequenceNo = sequenceNo;
		this.KOTNo = KOTNo;
		this.tableNo = tableNo;
		this.waiterNo = waiterNo;
		this.itemName = itemName;
		this.itemCode = itemCode;
		this.qty = qty;
		this.amt = amt;
		this.paxNo = paxNo;
		this.printYN = printYN;
		this.tdhComboItemYN = tdhComboItemYN;
		this.isModifier = isModifier;
		this.modifierCode = modifierCode;
		this.tdh_ComboItemCode = tdh_ComboItemCode;
		this.modifierGroupCode = modifierGroupCode;
		this.strNCKotYN = strNCKotYN;
		this.itemRate = itemRate;
		this.strDefaultModifierDeselectedYN = "N";
    }

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getKOTNo() {
		return KOTNo;
	}

	public void setKOTNo(String kOTNo) {
		KOTNo = kOTNo;
	}

	public String getTableNo() {
		return tableNo;
	}

	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}

	public String getWaiterNo() {
		return waiterNo;
	}

	public void setWaiterNo(String waiterNo) {
		this.waiterNo = waiterNo;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public double getAmt() {
		return amt;
	}

	public void setAmt(double amt) {
		this.amt = amt;
	}

	public int getPaxNo() {
		return paxNo;
	}

	public void setPaxNo(int paxNo) {
		this.paxNo = paxNo;
	}

	public String getPrintYN() {
		return printYN;
	}

	public void setPrintYN(String printYN) {
		this.printYN = printYN;
	}

	public String getTdhComboItemYN() {
		return tdhComboItemYN;
	}

	public void setTdhComboItemYN(String tdhComboItemYN) {
		this.tdhComboItemYN = tdhComboItemYN;
	}

	public boolean isModifier() {
		return isModifier;
	}

	public void setModifier(boolean isModifier) {
		this.isModifier = isModifier;
	}

	public String getModifierCode() {
		return modifierCode;
	}

	public void setModifierCode(String modifierCode) {
		this.modifierCode = modifierCode;
	}

	public String getTdh_ComboItemCode() {
		return tdh_ComboItemCode;
	}

	public void setTdh_ComboItemCode(String tdh_ComboItemCode) {
		this.tdh_ComboItemCode = tdh_ComboItemCode;
	}

	public String getModifierGroupCode() {
		return modifierGroupCode;
	}

	public void setModifierGroupCode(String modifierGroupCode) {
		this.modifierGroupCode = modifierGroupCode;
	}

	public String getStrNCKotYN() {
		return strNCKotYN;
	}

	public void setStrNCKotYN(String strNCKotYN) {
		this.strNCKotYN = strNCKotYN;
	}

	public double getItemRate() {
		return itemRate;
	}

	public void setItemRate(double itemRate) {
		this.itemRate = itemRate;
	}

	public String getStrDefaultModifierDeselectedYN() {
		return strDefaultModifierDeselectedYN;
	}

	public void setStrDefaultModifierDeselectedYN(
			String strDefaultModifierDeselectedYN) {
		this.strDefaultModifierDeselectedYN = strDefaultModifierDeselectedYN;
	}

	public double getDblFiredQty() {
		return dblFiredQty;
	}

	public void setDblFiredQty(double dblFiredQty) {
		this.dblFiredQty = dblFiredQty;
	}

	public double getDblPendingQty() {
		return dblPendingQty;
	}

	public void setDblPendingQty(double dblPendingQty) {
		this.dblPendingQty = dblPendingQty;
	}

	public double getDblFireQty() {
		return dblFireQty;
	}

	public void setDblFireQty(double dblFireQty) {
		this.dblFireQty = dblFireQty;
	}

	public double getDblPrintQty() {
		return dblPrintQty;
	}

	public void setDblPrintQty(double dblPrintQty) {
		this.dblPrintQty = dblPrintQty;
	}
    
    
}
