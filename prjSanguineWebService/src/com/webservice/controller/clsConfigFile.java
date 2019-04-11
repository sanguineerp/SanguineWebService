package com.webservice.controller;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class clsConfigFile
{
    private File file;
    private BufferedReader br;
    public static String posDBBackupPath,
	    posExcelReportExportPath;
    public static String mmsDBUserId,
	    mmsDBPassword, mmsDBIPAddress,
	    mmsDBPortNo, mmsDatabaseName;
    public static String exciseDBUserId,
	    exciseDBPassword, exciseDBIPAddress,
	    exciseDBPortNo, exciseDatabaseName;
    public static String posDBUserId,
	    posDBPassword, posDBIPAddress,
	    posDBPortNo, posDBName,
	    posDBServerName;
    public static String[] configData, tempData;
    public static String webbooksDBUserId,
	    webbooksDBPassword,
	    webbooksDBIPAddress,
	    webbooksDBPortNo,
	    webbooksDatabaseName;
    public static String webpmsDBUserId,
	    webpmsDBPassword, webpmsDBIPAddress,
	    webpmsDBPortNo, webpmsDatabaseName;
    public static String aposDBUserId,
	    aposDBPassword, aposDBIPAddress,
	    aposDBPortNo, aposDatabaseName;
    public static String onlineorderDBUserId,
	    onlineorderDBPassword, onlineorderDBIPAddress,
	    onlineorderDBPortNo, onlineorderDatabaseName;
    private String fileData;
    private int i;    
    private static String unicode= "?useUnicode=yes&characterEncoding=UTF-8";
    public static String cmsDBUserId,
	    cmsDBPassword, cmsDBIpAddress,
	    cmsDBName, cmsDBPortNo,
	    cmsDBServerName;
    
    public clsConfigFile()
    {
		try
		{
		    i = 0;
		    configData = new String[45];
		    tempData = new String[45];
		    file = new File(System.getProperty("user.dir") + "/DBConfigFile.txt");
		    System.out.println("File Path= "+file.getAbsolutePath());
		    System.out.println("File Path= "+System.getProperty("user.dir"));
		    
		   
		    br = new BufferedReader(new FileReader(file));
		    while ((fileData = br.readLine()) != null)
		    {
			String[] split = fileData.split("=");
			if (split.length > 1)
			{
			    tempData[i] = split[0];
			    configData[i] = split[1];
			    i++;
			}
		    }
		  
		    posDBServerName = configData[0].trim();
		    posDBName = configData[1].trim();
		    posDBUserId = configData[2].trim();
		    posDBPassword = configData[3].trim();
		    posDBIPAddress = configData[4].trim();
		    posDBPortNo = configData[5].trim();
		    
		    mmsDatabaseName = configData[6].trim();
		    mmsDBUserId = configData[7].trim();
		    mmsDBPassword = configData[8].trim();
		    mmsDBIPAddress = configData[9].trim();
		    mmsDBPortNo = configData[10].trim();
		    
		    cmsDBServerName = configData[11].trim();
		    cmsDBName = configData[12].trim();
		    cmsDBUserId = configData[13].trim();
		    cmsDBPassword = configData[14].trim();
		    cmsDBIpAddress = configData[15].trim();
		    cmsDBPortNo = configData[16].trim();
		    
		    exciseDatabaseName = configData[17].trim();
		    exciseDBUserId = configData[18].trim();
		    exciseDBPassword = configData[19].trim();
		    exciseDBIPAddress = configData[20].trim();
		    exciseDBPortNo = configData[21].trim();
		    
		    webbooksDatabaseName = configData[22].trim();
		    webbooksDBUserId = configData[23].trim();
		    webbooksDBPassword = configData[24].trim();
		    webbooksDBIPAddress = configData[25].trim();
		    webbooksDBPortNo = configData[26].trim();
		    
		    aposDatabaseName = configData[27].trim();
		    aposDBUserId = configData[28].trim();
		    aposDBPassword = configData[29].trim();
		    aposDBIPAddress = configData[30].trim();
		    aposDBPortNo = configData[31].trim();
		    
		    webpmsDatabaseName = configData[32].trim();
		    webpmsDBUserId = configData[33].trim();
		    webpmsDBPassword = configData[34].trim();
		    webpmsDBIPAddress = configData[35].trim();
		    webpmsDBPortNo = configData[36].trim();
		    
		    onlineorderDatabaseName = configData[37].trim();
		    onlineorderDBUserId = configData[38].trim();
		    onlineorderDBPassword = configData[39].trim();
		    onlineorderDBIPAddress = configData[40].trim();
		    onlineorderDBPortNo = configData[41].trim();
		    
		    posDBBackupPath = configData[42].trim();
		    posExcelReportExportPath = configData[43].trim();  	   
		   
		    /*
		    posDBServerName = "mysql";
		    posDBName = "sk";
		    posDBUserId = "swapnil";
		    posDBPassword = "sanguine";
		    posDBIPAddress = "localhost";
		    posDBPortNo = "3306";
		    
		    mmsDatabaseName = "sanguine_webmms";
		    mmsDBUserId = "swapnil";
		    mmsDBPassword = "sanguine";
		    mmsDBIPAddress = "localhost";
		    mmsDBPortNo = "3306";
		    
		    cmsDBServerName = "";
		    cmsDBName = "";
		    cmsDBUserId = "";
		    cmsDBPassword = "";
		    cmsDBIpAddress = "";
		    cmsDBPortNo = "";
		    
		    exciseDatabaseName = "";
		    exciseDBUserId = "";
		    exciseDBPassword = "";
		    exciseDBIPAddress = "";
		    exciseDBPortNo = "";
		    
		    webbooksDatabaseName = "sanguine_webbooks";
		    webbooksDBUserId = "sanguine_root";
		    webbooksDBPassword = "sanguine";
		    webbooksDBIPAddress = "localhost";
		    webbooksDBPortNo = "3306";
		    
		    aposDatabaseName = "sk";
		    aposDBUserId = "swapnil";
		    aposDBPassword = "sanguine";
		    aposDBIPAddress = "localhost";
		    aposDBPortNo = "3306";
		    
		    webpmsDatabaseName = "";
		    webpmsDBUserId = "";
		    webpmsDBPassword = "";
		    webpmsDBIPAddress = "";
		    webpmsDBPortNo = "";
		    
		    onlineorderDatabaseName = "";
		    onlineorderDBUserId = "";
		    onlineorderDBPassword = "";
		    onlineorderDBIPAddress = "";
		    onlineorderDBPortNo = "";
		    
		    posDBBackupPath = "";
		    posExcelReportExportPath = "";
*/

		    
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
    }
              
    
    public static void main(String args[])
    {
	clsConfigFile pc = new clsConfigFile();
    }
}
