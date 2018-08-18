package com.webbooks.controller;

public class clsJVDtlModel
{
	
		private String strAccountCode;
		private String strAccountName;
		private String strCrDr;
		private double dblDrAmt;
		private double dblCrAmt;
		public clsJVDtlModel(String strAccountCode, String strAccountName,
				String strCrDr, double dblDrAmt, double dblCrAmt) {
			super();
			this.strAccountCode = strAccountCode;
			this.strAccountName = strAccountName;
			this.strCrDr = strCrDr;
			this.dblDrAmt = dblDrAmt;
			this.dblCrAmt = dblCrAmt;
			
		}


		private String strNarration;
		private String strOneLine;
		private String strPropertyCode;

	//Setter-Getter Methods
		public String getStrAccountCode(){
			return strAccountCode;
		}
		public void setStrAccountCode(String strAccountCode){
			this. strAccountCode = (String) setDefaultValue( strAccountCode, "NA");
		}

		public String getStrAccountName(){
			return strAccountName;
		}
		public void setStrAccountName(String strAccountName){
			this. strAccountName = (String) setDefaultValue( strAccountName, "NA");
		}

		public String getStrCrDr(){
			return strCrDr;
		}
		public void setStrCrDr(String strCrDr){
			this. strCrDr = (String) setDefaultValue( strCrDr, "NA");
		}

		public double getDblDrAmt(){
			return dblDrAmt;
		}
		public void setDblDrAmt(double dblDrAmt){
			this. dblDrAmt = (Double) setDefaultValue( dblDrAmt, "0.0000");
		}

		public double getDblCrAmt(){
			return dblCrAmt;
		}
		public void setDblCrAmt(double dblCrAmt){
			this. dblCrAmt = (Double) setDefaultValue( dblCrAmt, "0.0000");
		}

		public String getStrNarration(){
			return strNarration;
		}
		public void setStrNarration(String strNarration){
			this. strNarration = (String) setDefaultValue( strNarration, "NA");
		}

		public String getStrOneLine(){
			return strOneLine;
		}
		public void setStrOneLine(String strOneLine){
			this. strOneLine = (String) setDefaultValue( strOneLine, "NA");
		}

		public String getStrPropertyCode(){
			return strPropertyCode;
		}
		public void setStrPropertyCode(String strPropertyCode){
			this. strPropertyCode = (String) setDefaultValue( strPropertyCode, "NA");
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
