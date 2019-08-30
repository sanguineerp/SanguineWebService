/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webservice.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Git testing
 */




/**
 *
 * @author Administrator
 */
public class clsClientDetails {

	public static HashMap<String, clsClientDetails> hmClientDtl;
	private static final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	public String id;
	public String Client_Name;
	public Date installDate;
	public Date expiryDate;
	public int intPropertyMachine;
	public int intUserNo;

	private clsClientDetails(String id, String Client_Name, Date installDate, Date expiryDate) {
		this.id = id;
		this.Client_Name = Client_Name;
		this.installDate = installDate;
		this.expiryDate = expiryDate;

	}

	private clsClientDetails(String id, String Client_Name, Date installDate, Date expiryDate, int intPropertyMachine, int intUserNo) {
		this.id = id;
		this.Client_Name = Client_Name;
		this.installDate = installDate;
		this.expiryDate = expiryDate;
		this.intPropertyMachine = intPropertyMachine;
		this.intUserNo = intUserNo;
	}

	public static clsClientDetails createClientDetails(String id, String Client_Name, Date installDate, Date expiryDate) {
		return new clsClientDetails(id, Client_Name, installDate, expiryDate);
	}

	public static clsClientDetails createClientDetails(String id, String Client_Name, Date installDate, Date expiryDate, int intPropertyMachine, int intUserNo) {
		return new clsClientDetails(id, Client_Name, installDate, expiryDate, intPropertyMachine, intUserNo);
	}

	public static void funAddClientCodeAndName() {
		try {

			hmClientDtl = new HashMap<String, clsClientDetails>();

			hmClientDtl.put("001.001", clsClientDetails.createClientDetails("001.001", "Quick Bite", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.002", clsClientDetails.createClientDetails("001.002", "Irish House", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.003", clsClientDetails.createClientDetails("001.003", "CONTEMPORARY KITCHENS PVT LTD", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.004", clsClientDetails.createClientDetails("001.004", "M/S PALI PRESIDENCY", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.005", clsClientDetails.createClientDetails("001.005", "M/S CORUM HOSPITALITY", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.006", clsClientDetails.createClientDetails("001.006", "M/S. BOMBAY BRONX (THE SLICE OF WINE HOSP. LTD)", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.007", clsClientDetails.createClientDetails("001.007", "AQAISTION FOOD THE SHOCK FINE", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.008", clsClientDetails.createClientDetails("001.008", "M/S BEDI A.M. PVT. LTD", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.009", clsClientDetails.createClientDetails("001.009", "M/S HOTEL SAHIL PVT. LTD", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.010", clsClientDetails.createClientDetails("001.010", "M/S HOTEL SKY LARK (GANGSTAR)", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.011", clsClientDetails.createClientDetails("001.011", "M/S LITTLE DARLING", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.012", clsClientDetails.createClientDetails("001.012", "M/S MANGI CAFES PVT LIMITED", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.013", clsClientDetails.createClientDetails("001.013", "M/S FOOD CULTURE", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.014", clsClientDetails.createClientDetails("001.014", "M/S BARBEQUE NATION", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.015", clsClientDetails.createClientDetails("001.015", "M/S TIAN RESTAURANT", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.016", clsClientDetails.createClientDetails("001.016", "M/S RICE BOAT RESTAURANT", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.017", clsClientDetails.createClientDetails("001.017", "M/S VEDANGI BEER SHOP", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.018", clsClientDetails.createClientDetails("001.018", "M/S SAMRAT RESTAURANT AND BAR", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.019", clsClientDetails.createClientDetails("001.019", "M/S KPT HOSPITALITY P. LTD.", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.020", clsClientDetails.createClientDetails("001.020", "M/S IVY WINE", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.021", clsClientDetails.createClientDetails("001.021", "M/S GOURMET INVESTMENT PVT. LTD.(PIZZA EXPS.)", dFormat.parse("2015-03-01"), dFormat.parse("2016-03-30")));
			hmClientDtl.put("001.022", clsClientDetails.createClientDetails("001.022", "M/S WODE HOUSE GYMKHANA", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.023", clsClientDetails.createClientDetails("001.023", "M/S CAFE PARIS", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.024", clsClientDetails.createClientDetails("001.024", "M/S CREATIVE HOTELS PVT LTD", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.025", clsClientDetails.createClientDetails("001.025", "M/S VESHNAVI HOSPITALITY PVT LTD", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.026", clsClientDetails.createClientDetails("001.026", "M/S. BARBEQUE NATION HOSPITALITY LTD.", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.027", clsClientDetails.createClientDetails("001.027", "M/S. OM SHIVAM CONSTRUCTIONS (BOMBAY BARBEQUE REST.)", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.028", clsClientDetails.createClientDetails("001.028", "M/S. ROSE GARDEN HOTEL P. LTD. (SHUBHANGEN HOTEL).", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.029", clsClientDetails.createClientDetails("001.029", "M/S. GOURMET INVESTMENT PVT LTD (PIZZA EXPRESS)", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.030", clsClientDetails.createClientDetails("001.030", "M/S.NIDO RESTAURANT & BAR", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.031", clsClientDetails.createClientDetails("001.031", "M/S GOA PORTUGUESA REST. PVT. LTD.", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("001.032", clsClientDetails.createClientDetails("001.032", "M/S. HOTEL METRO PALACE PVT. LTD.", dFormat.parse("2015-08-01"), dFormat.parse("2016-08-30")));
			hmClientDtl.put("074.001", clsClientDetails.createClientDetails("074.001", "THE POONA CLUB LTD", dFormat.parse("2016-01-21"), dFormat.parse("2099-09-11")));

			// WebStocks Clients
			hmClientDtl.put("000.000", clsClientDetails.createClientDetails("000.000", "Demo Company", dFormat.parse("2014-06-19"), dFormat.parse("2017-10-22")));
			hmClientDtl.put("024.001", clsClientDetails.createClientDetails("024.001", "Eden Cake Shop", dFormat.parse("2014-09-23"), dFormat.parse("2019-12-31")));//"024.001" renewed on 27-10-2018 for 1 year till 31-12-2019 "Mr. Salim Bhai", "9820141743", "salimsheliya@gmail.com", 
			hmClientDtl.put("044.001", clsClientDetails.createClientDetails("044.001", "HOTEL KAMAL PVT. LTD.", dFormat.parse("2015-01-20"), dFormat.parse("2095-02-21")));
			hmClientDtl.put("048.001", clsClientDetails.createClientDetails("048.001", "SHREE SIDDHIVINAYAK FOODS", dFormat.parse("2015-01-20"), dFormat.parse("2095-02-21")));
			hmClientDtl.put("060.001", clsClientDetails.createClientDetails("060.001", "FLYING SAUCER SKY BAR", dFormat.parse("2015-07-01"), dFormat.parse("2019-06-01")));//Renewd for1 year till 01-06-2019
			hmClientDtl.put("060.002", clsClientDetails.createClientDetails("060.002", "Flying Demo", dFormat.parse("2015-07-01"), dFormat.parse("2016-07-30")));
			
			// hmClientDtl.put("080.001",
			// clsClientDetails.createClientDetails("080.001",
			// "PRIMEBIZ HOSPILTALITY LLP",dFormat.parse("2016-01-16"),dFormat.parse("2017-01-16")));

			hmClientDtl.put("105.001", clsClientDetails.createClientDetails("105.001", "PRIMEBIZ HOSPILTALITY LLP", dFormat.parse("2016-01-16"), dFormat.parse("2017-01-16")));
			hmClientDtl.put("080.001", clsClientDetails.createClientDetails("080.001", "KAREEMS", dFormat.parse("2016-03-01"), dFormat.parse("2017-03-31")));
			hmClientDtl.put("092.001", clsClientDetails.createClientDetails("092.001", "Shree Sound Pvt Ltd", dFormat.parse("2016-02-15"), dFormat.parse("2018-12-28"))); // waters
			hmClientDtl.put("096.001", clsClientDetails.createClientDetails("096.001", "Red Consulting Pvt Ltd", dFormat.parse("2016-01-16"), dFormat.parse("2016-04-01")));
			hmClientDtl.put("097.001", clsClientDetails.createClientDetails("097.001", "GADGIL HOTELS PVT LTD", dFormat.parse("2016-01-30"), dFormat.parse("2017-01-30")));
			hmClientDtl.put("097.002", clsClientDetails.createClientDetails("097.002", "GADGIL HOTELS PVT LTD", dFormat.parse("2016-04-11"), dFormat.parse("2016-07-15")));
			hmClientDtl.put("103.001", clsClientDetails.createClientDetails("103.001", "BOMBAY HIGH", dFormat.parse("2016-03-05"), dFormat.parse("2018-08-01"))); // extend
			hmClientDtl.put("104.001", clsClientDetails.createClientDetails("104.001", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07")));// HO																																									// Unit
			hmClientDtl.put("106.001", clsClientDetails.createClientDetails("106.001", "Independence Brewing Co. Pvt Ltd", dFormat.parse("2016-03-21"), dFormat.parse("2017-03-21")));
			hmClientDtl.put("107.001", clsClientDetails.createClientDetails("107.001", "UNITED CRANE COMPONENTS PVT. LTD.", dFormat.parse("2016-03-30"), dFormat.parse("2019-09-30"))); // testing  for checking
			hmClientDtl.put("109.001", clsClientDetails.createClientDetails("109.001", "Chemistry 101", dFormat.parse("2016-04-11"), dFormat.parse("2017-04-11")));
			hmClientDtl.put("110.001", clsClientDetails.createClientDetails("110.001", "CAKE SHOP", dFormat.parse("2016-01-23"), dFormat.parse("2016-05-30")));
			hmClientDtl.put("111.001", clsClientDetails.createClientDetails("111.001", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2050-12-30")));// HO
			hmClientDtl.put("112.001", clsClientDetails.createClientDetails("112.001", "GBC MEGA MOTELS", dFormat.parse("2016-05-11"), dFormat.parse("2017-05-11")));// HO																																										// Carnival
			hmClientDtl.put("114.001", clsClientDetails.createClientDetails("114.001", "Dr. Asif Khan Wellness Clinic LLP", dFormat.parse("2016-05-14"), dFormat.parse("2017-05-14")));// HO
			hmClientDtl.put("117.001", clsClientDetails.createClientDetails("117.001", "THE PREM'S HOTEL", dFormat.parse("2016-08-01"), dFormat.parse("2019-06-30")));// HO
			hmClientDtl.put("124.001", clsClientDetails.createClientDetails("124.001", "ATITHYA DINNING LLP", dFormat.parse("2016-11-04"), dFormat.parse("2019-10-18")));//renewed for 8 days on 22-01-2019 till 28-01-2019 //renewd for 45 days on 29-11-2018 till 15-01-2019// renew for one month on 3-11-2018 //one month extends//one month licence extend  on 10-10-2018 to 31-10-2018 of teddy boy --Nikhil
			hmClientDtl.put("127.001", clsClientDetails.createClientDetails("127.001", "Cumin Food & Beverage Pvt Ltd", dFormat.parse("2016-10-26"), dFormat.parse("2017-10-26")));
			hmClientDtl.put("131.001", clsClientDetails.createClientDetails("131.001", "KETTLE AND KEG CAFE", dFormat.parse("2016-11-23"), dFormat.parse("2019-11-23")));// renwed on 24-11-2018 for one year(23-11-2019) //Contact Mr.Kiran 9892232593   kiran.pt79@hotmail.com
			hmClientDtl.put("132.001", clsClientDetails.createClientDetails("132.001", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2017-08-30")));
			hmClientDtl.put("136.001", clsClientDetails.createClientDetails("136.001", "KINKI", dFormat.parse("2017-01-05"), dFormat.parse("2020-01-05")));// released on
			hmClientDtl.put("137.001", clsClientDetails.createClientDetails("137.001", "IMAK HOSPITALITY LLP", dFormat.parse("2018-11-05"), dFormat.parse("2019-11-05")));// JPOS Client  renewed on 05-11-2018 for 1 year	// 05-01-2018, 1year
			hmClientDtl.put("141.001", clsClientDetails.createClientDetails("141.001", "SANGUINE SOFTWARE SOLUTIONS PVT LTD", dFormat.parse("2017-01-30"), dFormat.parse("2099-01-30")));
			hmClientDtl.put("148.001", clsClientDetails.createClientDetails("148.001", "MURPHIES", dFormat.parse("2017-02-16"), dFormat.parse("2019-02-25"), 4, 3));// Renewed on 18-02-2019 for 1 week till 25-02-2019 //release on 16-02-2017 for 1 year
			hmClientDtl.put("151.001", clsClientDetails.createClientDetails("151.001", "Bottle Street Restaurant & Lounge", dFormat.parse("2017-02-23"), dFormat.parse("2018-03-27")));// renewed on 27-02-2017 for 1 month
			hmClientDtl.put("155.001", clsClientDetails.createClientDetails("155.001", "CAVALLI THE LOUNGE", dFormat.parse("2017-03-24"), dFormat.parse("2018-03-24"))); // extend as pos
			hmClientDtl.put("159.001", clsClientDetails.createClientDetails("159.001", "BIG PLATE CUISINES LLP", dFormat.parse("2017-06-02"), dFormat.parse("2019-04-14")));
			hmClientDtl.put("161.001", clsClientDetails.createClientDetails("161.001", "HOTEL GRAND CENTRAL", dFormat.parse("2017-04-18"), dFormat.parse("2018-01-18")));
			hmClientDtl.put("163.001", clsClientDetails.createClientDetails("163.001", "KADAR KHAN'S SHEESHA", dFormat.parse("2017-05-15"), dFormat.parse("2020-05-15")));// renewed on 19-05-2018 for 1 year 
			hmClientDtl.put("166.001", clsClientDetails.createClientDetails("166.001", "SUNNYS WORLD", dFormat.parse("2017-05-01"), dFormat.parse("2025-09-01")));
			hmClientDtl.put("169.001", clsClientDetails.createClientDetails("169.001", "A R HOSPITALITY", dFormat.parse("2017-05-10"), dFormat.parse("2019-10-09")));
			hmClientDtl.put("172.001", clsClientDetails.createClientDetails("172.001", "DIOS HOTEL LLP", dFormat.parse("2017-05-25"), dFormat.parse("2018-08-25"))); // one
 			hmClientDtl.put("173.002", clsClientDetails.createClientDetails("173.002", "Le Flamington", dFormat.parse("2018-12-15"), dFormat.parse("2019-12-24")));
			hmClientDtl.put("174.001", clsClientDetails.createClientDetails("174.001", "KRD Eateries Pvt Ltd", dFormat.parse("2017-06-06"), dFormat.parse("2017-10-31"))); // krimsons
			hmClientDtl.put("175.001", clsClientDetails.createClientDetails("175.001", "COPPER STORY PRIVATE LIMITED", dFormat.parse("2017-06-06"), dFormat.parse("2019-06-12")));//Renewed on 06-02-2019 for 15 days till 20-02-2019 //renewed licence and rename TJS to Copper story privated limited till 01-02-2019 //renewed on 6-10-18 till 15-10-18 //renewed on 22-9-2018 for 8 days till 30-9-2018 //renewed on 8-9-2018 for 8 days till 15-09-2018//renewed on 03-07-2018 for 7 days till 10-07-2018
			hmClientDtl.put("178.001", clsClientDetails.createClientDetails("178.001", "UNWIND", dFormat.parse("2017-07-27"), dFormat.parse("2019-06-24")));
			hmClientDtl.put("181.001", clsClientDetails.createClientDetails("181.001", "RMV COMMUNICATION PVT LTD", dFormat.parse("2017-06-20"), dFormat.parse("2017-07-20")));
			hmClientDtl.put("184.001", clsClientDetails.createClientDetails("184.001", "BALAJI TRADERS PVT LTD", dFormat.parse("2017-07-03"), dFormat.parse("2018-01-07")));
			hmClientDtl.put("189.001", clsClientDetails.createClientDetails("189.001", "CLASSIC BANGLES", dFormat.parse("2017-08-31"), dFormat.parse("2018-08-10"))); // for demo
			hmClientDtl.put("193.001", clsClientDetails.createClientDetails("193.001", "PRECISION FOOD WORKS", dFormat.parse("2017-08-25"), dFormat.parse("2019-08-25")));//renewed on 24-09-2018 for 1 year 
			hmClientDtl.put("194.001", clsClientDetails.createClientDetails("194.001", "SWIG", dFormat.parse("2017-09-11"), dFormat.parse("2019-09-06"))); // Name
			hmClientDtl.put("197.001", clsClientDetails.createClientDetails("197.001", "Juhu Hotel Pvt Ltd", dFormat.parse("2017-09-20"), dFormat.parse("2019-10-20")));// renewed on 31-10-2017 for 1 year till 20-09-2018b renamed  REZBERRY RHINOCERES to Juhu Hotel Pvt Ltd in JPOS
			hmClientDtl.put("211.001", clsClientDetails.createClientDetails("211.001", "CHEFS CORNER", dFormat.parse("2017-11-29"), dFormat.parse("2019-08-09")));// renewed on 10-07-2019 for 1 month till 09-08-2019//renewed on 25-06-2019 for 10 days till 09-07-2019//Renewed On 17-05-2019 for 15 days till 31-05-2019// Renewed on 16-04-2019 for 1 month till 16-05-2019//Renewed on 14-03-2019 for 1 month till 16-04-2019//renewed on 15-02-2019 for 1 month till 15-03-2019 //renwed for on emonth on 01-12-2018 till  //renewed  on 30-11-2018 for one month till 30-12-2018  // renewd
			hmClientDtl.put("217.001", clsClientDetails.createClientDetails("217.001", "BURNT CRUST HOSPITALITY PVT LTD", dFormat.parse("2017-12-13"), dFormat.parse("2020-01-03")));//renewed on 26-11-2108 (EndDate 03-12-2018)for one month till 03-01-2018//renewed on 25-10-2018 for one month 03-10-018(from date is 03-11-2018)//renewed on 03-07-2018 for 1 month till 03-08-2018// eenewed for one month till 05-10-2018
			hmClientDtl.put("218.001", clsClientDetails.createClientDetails("218.001", "THE LIQUID WISDOM CO.PVT LTD", dFormat.parse("2018-01-15"), dFormat.parse("2019-12-13")));// temporory //renewed on 16-01-2019 for 1 year same as pos date
			hmClientDtl.put("222.001", clsClientDetails.createClientDetails("222.001", "PURANCHAND & SONS", dFormat.parse("2018-01-06"), dFormat.parse("2018-01-10")));// temporory
			hmClientDtl.put("223.001", clsClientDetails.createClientDetails("223.001", "BANYAN TREE HOSPITALITY LLP", dFormat.parse("2018-01-17"), dFormat.parse("2020-01-17")));// temporory
			hmClientDtl.put("224.001", clsClientDetails.createClientDetails("224.001", "Friendship hospitality Associates", dFormat.parse("2018-01-19"), dFormat.parse("2020-01-19")));//renewed for one year on 21-01-2019 from 19-01-2019 to 19-01-2020  //XO Zero pos
			hmClientDtl.put("225.001", clsClientDetails.createClientDetails("225.001", "SHEESHA SKY LOUNGE HOSPITALITY AND SERVICES PVT LTD", dFormat.parse("2018-08-21"), dFormat.parse("2019-08-21")));//From POS 
			hmClientDtl.put("226.001", clsClientDetails.createClientDetails("226.001", "MAYA EAST AFRICA LTD.", dFormat.parse("2018-02-12"), dFormat.parse("2019-09-06")));// Renewed for 2019-09-06 1 year as per sachin sir discussion
			hmClientDtl.put("233.001", clsClientDetails.createClientDetails("233.001", "PLAYBOY INDIA", dFormat.parse("2018-06-13"), dFormat.parse("2020-06-12")));//Renewed on 03-05-2019 for 1 year till 12-06-2020 //Renewed on 27-04-2019 for 10 days till 07-05-2019 //First Outlet 
			hmClientDtl.put("233.002", clsClientDetails.createClientDetails("233.002", "PLAYBOY INDIA", dFormat.parse("2018-06-13"), dFormat.parse("2020-06-12")));//playBoy 2nd outlet//renewed on 11-06-2019 for 1 year till 12-06-2020 //renewed on 13-06-2018 for 1 month till 13-07-2018
			hmClientDtl.put("233.003", clsClientDetails.createClientDetails("233.003", "SPJ HOSPITALITY AND HOTELS", dFormat.parse("2018-12-21"), dFormat.parse("2019-08-21")));//on 20-05 for 3 months //Renewed on 12-02-2019 till 20-05-2019
			hmClientDtl.put("236.001", clsClientDetails.createClientDetails("236.001", "JAMUN HOSPITALITY CONSULTANCY LLP", dFormat.parse("2018-03-16"), dFormat.parse("2018-06-18")));
			hmClientDtl.put("242.001", clsClientDetails.createClientDetails("242.001", "SRINATHJIS CUISINES PVT LTD", dFormat.parse("2018-04-03"), dFormat.parse("2018-08-18")));//renewed on 01-06-2018 for 1 month till 30-06-2018// Contact No-022-23755444 / 666  Email-Id-srinathjiscuisine@gmail.com
			hmClientDtl.put("240.001", clsClientDetails.createClientDetails("240.001", "THOUSAND OAKS", dFormat.parse("2018-04-12"), dFormat.parse("2020-04-12")));//renewed on 22-05-2019 for 1 year//renewed on 01-12-2018 for one month till 01-01-2019//renewed on 20-08-2018 for 1 week till 27-08-2018 //renewed on 13-06-2018 for 1 month// Mr. Sanjeet Lamba", 2417 East Street (G T Road),Camp, Pune 411001. Tel: (020) 2634 3194 / 2634 5598
			hmClientDtl.put("241.001", clsClientDetails.createClientDetails("241.001", "LALITHA HOSPITALITY PVT LTD", dFormat.parse("2018-04-06"), dFormat.parse("2019-04-06")));//(MUMBAI)//release on 06-05-2018 for 1 month till 06-06-2018 for 4 SPOS ,6 APOS,1 WebStocks,No SMS Pack.", "Mr.Santosh Shetty", "+919769214553", "Santoshshetty_0173@gmail.com"
			hmClientDtl.put("244.001", clsClientDetails.createClientDetails("244.001", "VIVIDH HOSPITALITY", dFormat.parse("2018-04-17"), dFormat.parse("2019-04-17")));//release on 17-04-2018 for 1 year till 17-04-22019
			hmClientDtl.put("245.001", clsClientDetails.createClientDetails("245.001", "LSD NO LIMITS LLP (C/O)", dFormat.parse("2018-04-19"), dFormat.parse("2019-04-19")));//renewed on 30-05-2018 for 1 year till 19-04-2019//release on 19-04-2018 for 1 month till 19-05-2018
			hmClientDtl.put("257.001", clsClientDetails.createClientDetails("257.001", "BLACK SHEEP HOSPITALITY PVT LTD", dFormat.parse("2018-08-16"), dFormat.parse("2019-08-16")));//  renwed on 06-12-2018 for one year accordin to from date till 06-08-2019  //Sunny Shriram contact no - 9769545955 email id - arrowhospitalityconsulting@gmail.com
			hmClientDtl.put("258.001", clsClientDetails.createClientDetails("258.001", "SAI MADHUBAN HOSPITALITY PVT LTD", dFormat.parse("2018-05-27"), dFormat.parse("2019-05-27")));//Sunny Shriram contact no - 9769545955 email id - arrowhospitalityconsulting@gmail.com
			hmClientDtl.put("259.001", clsClientDetails.createClientDetails("259.001", "PENTAGON INDIA RETAIL PVT LTD", dFormat.parse("2018-08-24"), dFormat.parse("2019-08-24")));//POS Licence //As Per JPOs iYear Licence Renewed
		   //260.001", "PARAG HOTELS AND RESORT PVT LTD" for POS
			//Africa International
			hmClientDtl.put("261.001", clsClientDetails.createClientDetails("261.001", "KIN DELICIEUX", dFormat.parse("2018-09-08"), dFormat.parse("2019-09-08")));//(AFRICA)//released on 08-09-2018 for 1 year till 08-09-2019 
			hmClientDtl.put("262.001", clsClientDetails.createClientDetails("262.001", "COCO JAMBO", dFormat.parse("2018-09-08"), dFormat.parse("2019-09-08")));//(AFRICA)//released on 08-09-2018 for 1 year till 08-09-2019 
			hmClientDtl.put("263.001", clsClientDetails.createClientDetails("263.001", "FIESTA CLUB", dFormat.parse("2018-09-08"), dFormat.parse("2019-09-08")));//(AFRICA)//released on 08-09-2018 for 1 year till 08-09-2019hmClientDtl.put("263.001", clsClientDetails.createClientDetails("263.001", "FIESTA CLUB", dFormat.parse("2018-09-08"), dFormat.parse("2019-09-08")));//(AFRICA)//released on 08-09-2018 for 1 year till 08-09-2019
			// 264-266 used for POS
			hmClientDtl.put("267.001", clsClientDetails.createClientDetails("267.001", "AGA HOSPITALITY PVT LTD", dFormat.parse("2018-10-04"), dFormat.parse("2019-12-25")));//Renewed on 12-03-2019 for 1 year till 25-12-2019 //renewed on 06-12-2018 for one month till 06-01-2019// One month stsrt date 2018-10-04  end date Office	AddressB-304,	Dynasty	Business	Park,J.B.Nagar,Andheri	Kurla	Road,	Andheri	East	Mumbai-400059. Contact	Person(s)-Mr.Sandesh,Contact	Number(s)9987994188  Email	ID Sandesh.aga@gmail.com //MUBASHIR//Renew  on 04-05-2018
			
			hmClientDtl.put("268.001", clsClientDetails.createClientDetails("268.001", "ROVE HOSPITALITY", dFormat.parse("2018-10-04"), dFormat.parse("2019-11-23")));//(Pune)//renewed on 23-11-2018 for 1 year till 15-10-2019 "Amol Gargote", "9545160160", "amololive6@gmail.com", 
			
			// 268-269 used for POS
			hmClientDtl.put("270.001", clsClientDetails.createClientDetails("270.001", "Hotel Troy Ltd", dFormat.parse("2018-10-17"), dFormat.parse("2019-12-18")));//Kenya Client renewed on 17-10-2018 for one month 17-11-2018 "Mr Chris Karugu", "0705244273", "info@hotel-troy.com",
			
			
			hmClientDtl.put("273.001", clsClientDetails.createClientDetails("273.001", "SHEESHA SKY LOUNGE", dFormat.parse("2018-10-25"), dFormat.parse("2019-10-24")));//released on 25-10-2018 for 1 Year till 24-10-2019 for 1 SPOS ,0 APOS,0 WebStocks,No SMS Pack,No Bill Deletion.", "Mr.Alam", "9920939306", "alam.sky@gmail.com");	
		
			hmClientDtl.put("277.001", clsClientDetails.createClientDetails("277.001", "Kapila Garden Restaurant", dFormat.parse("2018-11-21"), dFormat.parse("2020-04-01")));//Renewed on 01-04-2019 for 1 year till 2020-04-01//Renewed on 16-03-19 for 16 days till 01-04-2019//Renewed on 15-02-2019 for 1 months till 15-03-2019//Renewed on 01-02-2019 for 15 days till 15-02-2019//renewed on 10-01-2019 till 31-01-2019//relased on 21-11-2018 for 1 month till 2018-12-21
			
			hmClientDtl.put("284.001", clsClientDetails.createClientDetails("284.001", "CNP HOSPITALITY PVT LTD", dFormat.parse("2018-11-29"), dFormat.parse("2018-12-29")));//relased on 29-11-2018 for 1 month till 2018-12-29
			
			hmClientDtl.put("287.001", clsClientDetails.createClientDetails("287.001", "OVER THE TOP HOSPITALITY", dFormat.parse("2018-12-05"), dFormat.parse("2019-08-31")));//released on 5-12-2018 till 31-08-2019 for 4 SPOS ,5 APOS,WebStocks,No SMS Pack,No Bill Deletion.", "Mr.Ravi", "9136025911", "Ravitejold@oldtimessake.in"
			hmClientDtl.put("290.001", clsClientDetails.createClientDetails("290.001", "CREME HOSPITALITY AND MANAGEMENT", dFormat.parse("2018-12-15"), dFormat.parse("2019-12-15"))); //Renewed on 16-04-2019 till 15-12-2019//Renewed on 12-3-2019 for 1 month till 15-4-2019//Renewed on 12-2-2019 for 1 year till 15-12-2019//renewed  for one month on 17-01-2019 form 15-01-2019 till 15-02-2019
			
			hmClientDtl.put("293.001", clsClientDetails.createClientDetails("293.001", "ZELEB", dFormat.parse("2019-01-30"), dFormat.parse("2019-12-27")));//renewed on 2-02-2019 for 1 year till 2019-12-27//relased zaleb license for 8 days POS Client
			hmClientDtl.put("294.001", clsClientDetails.createClientDetails("294.001", "SEERAN HOSPITALITY", dFormat.parse("2019-01-11"), dFormat.parse("2020-01-11")));//Renewed on 06-02-2019 for 1 year till 11-01-2020 //released for one month on 11-01-2019 to 11-02-2019 "Mr.Santosh ", "9820339701", "santosh.seeran@gmail.com"
			hmClientDtl.put("296.001", clsClientDetails.createClientDetails("296.001", "ISKCON", dFormat.parse("2019-02-08"), dFormat.parse("2020-02-08")));//(Gujrat)//released on 08-02-2019 for 1 year till 08-02-2020 for 3 SPOS ,10 APOS,No WebStocks,No SMS Pack,No Bill Deletion.", "Mr. Rajesh ", "9619378249", "rajesh.sha19@gmail.com", "No Bill Deletion","santesting"));	    
			hmClientDtl.put("302.001", clsClientDetails.createClientDetails("302.001", "MOSHING CUPPA", dFormat.parse("2019-03-07"), dFormat.parse("2019-04-15"))); // released on 07-03-2019 for 1 month till 07-04-2019
			hmClientDtl.put("312.001", clsClientDetails.createClientDetails("312.001", "Reddy's Fine Dine Restaurant", dFormat.parse("2019-05-08"), dFormat.parse("2019-06-10"))); // released on 2019-05-08 for 1 month till 2019-06-10
			hmClientDtl.put("315.001", clsClientDetails.createClientDetails("315.001", "KUTTING FUSION HOSPITALITY LLP", dFormat.parse("2019-05-25"), dFormat.parse("2019-06-25"))); // renewed on 25-05-2019 for 1 month till 25-06-2019
			hmClientDtl.put("316.001", clsClientDetails.createClientDetails("316.001", "MRP HOSPITALITY", dFormat.parse("2019-05-25"), dFormat.parse("2019-06-25"))); // renewed on 25-05-2019 for 1 month till 25-06-2019
			hmClientDtl.put("318.001", clsClientDetails.createClientDetails("318.001", "EURISKA", dFormat.parse("2019-06-11"), dFormat.parse("2019-07-11"))); // renewed on 04-06-2019 for 1 month till 04-07-2019
			hmClientDtl.put("319.001", clsClientDetails.createClientDetails("319.001", "LAXMI FOODS", dFormat.parse("2019-06-04"), dFormat.parse("2019-07-04"))); // Released on 04-06-2019 for 1 month till 04-07-2019
			hmClientDtl.put("320.001", clsClientDetails.createClientDetails("320.001", "HOTEL SURYA", dFormat.parse("2019-06-13"), dFormat.parse("2019-09-23"))); //Released on 24-07-2019 for 1 month till 23-08-2019//Released on 16-06-2019 for 1 week till 23-07-2019// Released on 13-06-2019 for 1 month till 15-07-2019

			//
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
