package com.apos.bean;

import java.util.Comparator;

public class clsItemWiseConsumptionBean {

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
	    
	    private double promoQty;

	    /**
	     * 
	     */
	    public clsItemWiseConsumptionBean()
	    {
	        this.promoQty=0;
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

		public String getPOSName() {
			return POSName;
		}

		public void setPOSName(String pOSName) {
			POSName = pOSName;
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

		public int getSeqNo() {
			return seqNo;
		}

		public void setSeqNo(int seqNo) {
			this.seqNo = seqNo;
		}

		public double getPromoQty() {
			return promoQty;
		}

		public void setPromoQty(double promoQty) {
			this.promoQty = promoQty;
		}
		
		
		 public static Comparator<clsItemWiseConsumptionBean> comparatorItemConsumptionColumnDtl = new Comparator<clsItemWiseConsumptionBean>()
				    {
				        public int compare(clsItemWiseConsumptionBean s1, clsItemWiseConsumptionBean s2)
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
				        }
				    };
}
