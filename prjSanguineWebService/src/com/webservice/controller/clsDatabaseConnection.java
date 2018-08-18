package com.webservice.controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class clsDatabaseConnection {
    
    private String dbURL;
    static String unicode= "?useUnicode=yes&characterEncoding=UTF-8";
    public static Connection DBPOSCONNECTION=null;
    public static Connection DBMMSCONNECTION=null;
    
    public clsDatabaseConnection()
    {
        clsConfigFile pc=new clsConfigFile();
    }
    
    
    
    public Connection funOpenCMSCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mssql"))
        {
            String dbName=clsConfigFile.cmsDBName;
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String[] spDbName=clsConfigFile.cmsDBName.split(",");
            if(database.equalsIgnoreCase("master"))
            {
                dbName=spDbName[0];
            }
            else
            {
                dbName=spDbName[1];
            }
            dbURL="jdbc:sqlserver://"+clsConfigFile.cmsDBServerName+":"+clsConfigFile.cmsDBPortNo+";user="+clsConfigFile.cmsDBUserId+";password="+clsConfigFile.cmsDBPassword+";database="+dbName;
            dbCon = DriverManager.getConnection(dbURL);
        }
        
        return dbCon;
    }
    

    public Connection funOpenMMSCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
        	Class.forName("com.mysql.jdbc.Driver");
        	
            dbURL="jdbc:mysql://"+clsConfigFile.mmsDBIPAddress+":"+clsConfigFile.mmsDBPortNo+"/"
            		+clsConfigFile.mmsDatabaseName+unicode;
            //System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.mmsDBUserId,clsConfigFile.mmsDBPassword);
        }        
        return dbCon;
    }
    
    
    public Connection funOpenPOSCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
            Class.forName("com.mysql.jdbc.Driver");
            dbURL="jdbc:mysql://"+clsConfigFile.posDBIPAddress+":"+clsConfigFile.posDBPortNo+"/"
            		+clsConfigFile.posDBName+unicode;
            //System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.posDBUserId,clsConfigFile.posDBPassword);
        }        
        return dbCon;
    }
    
    
    public Connection funOpenExciseCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
        	Class.forName("com.mysql.jdbc.Driver");
        	
            dbURL="jdbc:mysql://"+clsConfigFile.exciseDBIPAddress+":"+clsConfigFile.exciseDBPortNo+"/"
            		+clsConfigFile.exciseDatabaseName+unicode;
//            System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.exciseDBUserId,clsConfigFile.exciseDBPassword);
        }        
        return dbCon;
    }
    
    
    public Connection funOpenWebbooksCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
        	Class.forName("com.mysql.jdbc.Driver");
        	
            dbURL="jdbc:mysql://"+clsConfigFile.webbooksDBIPAddress+":"+clsConfigFile.webbooksDBPortNo+"/"
            		+clsConfigFile.webbooksDatabaseName+unicode;
//            System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.webbooksDBUserId,clsConfigFile.webbooksDBPassword);
        }        
        return dbCon;
    }
    
    
   
    
    public Connection funOpenAPOSCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
        	Class.forName("com.mysql.jdbc.Driver");
        	
            dbURL="jdbc:mysql://"+clsConfigFile.aposDBIPAddress+":"+clsConfigFile.aposDBPortNo+"/"
            		+clsConfigFile.aposDatabaseName+unicode;
//            System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.aposDBUserId,clsConfigFile.aposDBPassword);
        }        
        return dbCon;
    }
    
    
    
    public Connection funOpenWebPMSCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
        	Class.forName("com.mysql.jdbc.Driver");
        	
            dbURL="jdbc:mysql://"+clsConfigFile.webpmsDBIPAddress+":"+clsConfigFile.webpmsDBPortNo+"/"
            		+clsConfigFile.webpmsDatabaseName+unicode;
//            System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.webpmsDBUserId,clsConfigFile.webpmsDBPassword);
        }        
        return dbCon;
    }
    
    public Connection funOpenOnlineOrderCon(String dbType,String database) throws Exception
    {
        Connection dbCon=null;
        if(dbType.equalsIgnoreCase("mysql"))
        {
        	Class.forName("com.mysql.jdbc.Driver");
        	
            dbURL="jdbc:mysql://"+clsConfigFile.onlineorderDBIPAddress+":"+clsConfigFile.onlineorderDBPortNo+"/"
            		+clsConfigFile.onlineorderDatabaseName+unicode;
//            System.out.println(dbURL);
            dbCon = DriverManager.getConnection(dbURL,clsConfigFile.onlineorderDBUserId,clsConfigFile.onlineorderDBPassword);
        }        
        return dbCon;
    }
    
   
}
