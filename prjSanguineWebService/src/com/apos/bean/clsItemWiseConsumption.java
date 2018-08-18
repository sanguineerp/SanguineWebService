package com.apos.bean;

import java.util.Comparator;

public class clsItemWiseConsumption {
	
    private String itemCode;
    
    private String itemName;
    
    private String subGroupName;
    
    private String groupName;
    
    private String POSName;
    
    private double saleQty;
    
    private double complimentaryQty;
    
    private double ncQty;
    
    private double totalQty;
    
    private double saleAmt;
    
    private double subTotal;
    
    private double discAmt;
    
    private int seqNo;

    public String getExternalCode()
    {
        return externalCode;
    }

    public void setExternalCode(String externalCode)
    {
        this.externalCode = externalCode;
    }

    public static Comparator<clsItemWiseConsumption> getComparatorItemConsumptionColumnDtl()
    {
        return comparatorItemConsumptionColumnDtl;
    }

    public static void setComparatorItemConsumptionColumnDtl(Comparator<clsItemWiseConsumption> comparatorItemConsumptionColumnDtl)
    {
        clsItemWiseConsumption.comparatorItemConsumptionColumnDtl = comparatorItemConsumptionColumnDtl;
    }
    
    private double promoQty;
    
    private String externalCode;

    public clsItemWiseConsumption()
    {
        this.promoQty=0;
    }
    
    
    

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPOSName() {
        return POSName;
    }

    public void setPOSName(String POSName) {
        this.POSName = POSName;
    }

    public double getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(double saleQty) {
        this.saleQty = saleQty;
    }

    public double getComplimentaryQty() {
        return complimentaryQty;
    }

    public void setComplimentaryQty(double complimentaryQty) {
        this.complimentaryQty = complimentaryQty;
    }

    public double getNcQty() {
        return ncQty;
    }

    public void setNcQty(double ncQty) {
        this.ncQty = ncQty;
    }

    public double getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(double totalQty) {
        this.totalQty = totalQty;
    }

    public double getSaleAmt() {
        return saleAmt;
    }

    public void setSaleAmt(double saleAmt) {
        this.saleAmt = saleAmt;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscAmt() {
        return discAmt;
    }

    public void setDiscAmt(double discAmt) {
        this.discAmt = discAmt;
    }

    public String getSubGroupName() {
        return subGroupName;
    }

    public void setSubGroupName(String subGroupName) {
        this.subGroupName = subGroupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public double getPromoQty()
    {
        return promoQty;
    }

    public void setPromoQty(double promoQty)
    {
        this.promoQty = promoQty;
    }
    
    
        
 
    public static Comparator<clsItemWiseConsumption> comparatorItemConsumptionColumnDtl = new Comparator<clsItemWiseConsumption>()
    {
        public int compare(clsItemWiseConsumption s1, clsItemWiseConsumption s2)
        {
            int seqNo1 = s1.getSeqNo();
            int seqNo2 = s2.getSeqNo();

            if (seqNo1 == seqNo2)
            {
                return 0;
            }
            else if (seqNo1 > seqNo2)
            {
                return 1;
            }
            else
            {
                return -1;
            }
            
            
//            if(s1.itemCode.substring(0, 7).equalsIgnoreCase(s2.itemCode.substring(0, 7)))
//            {
//                return 0;
//            }
//            else
//            {
//                return 1;
//            }
        }
    };
    
    


	

}
