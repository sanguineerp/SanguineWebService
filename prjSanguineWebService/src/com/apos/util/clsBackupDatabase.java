/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.controller.clsUtilityController;
import com.apos.dao.clsDayEndProcessDao;
import com.apos.service.clsPOSConfigSettingService;
@Controller
public class clsBackupDatabase
{

	@Autowired
	clsUtilityController objUtilityController;		//=new clsUtilityController();
	@Autowired 
	clsPOSConfigSettingService objPOSConfigSettingService;
    private static ResultSet res;
    private static Connection con;
    private Statement st;
    private int BUFFER = 99999;

    
    public String funTakeBackUpDB(String clientCode) throws Exception
    {
        funCheckBackUpFilePath(clientCode);

        Date dtCurrentDate = new Date();
        String date = dtCurrentDate.getDate() + "-" + (dtCurrentDate.getMonth() + 1) + "-" + (dtCurrentDate.getYear() + 1900);
        String time = dtCurrentDate.getHours() + "-" + dtCurrentDate.getMinutes();
        String fileName = date + "_" + time + "_JPOS";

        String batchFilePath = System.getProperty("user.dir") + "\\mysqldbbackup.bat";
        String filePath = System.getProperty("user.dir") + "/DBBackup";
        File file = new File(filePath);
        if (!file.exists())
        {
            file.mkdir();
        }

        File batchFile = new File(batchFilePath);
        if (!batchFile.exists())
        {
            batchFile.createNewFile();
        }
        BufferedWriter objWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(batchFile), "UTF8"));
        objWriter.write("@echo off");
        objWriter.newLine();
        objWriter.write("for /f \"tokens=1\" %%i in ('date /t') do set DATE_DOW=%%i");
        objWriter.newLine();
        objWriter.write("for /f \"tokens=2\" %%i in ('date /t') do set DATE_DAY=%%i");
        objWriter.newLine();
        objWriter.write("for /f %%i in ('echo %date_day:/=-%') do set DATE_DAY=%%i");
        objWriter.newLine();

        objWriter.write("for /f %%i in ('time /t') do set DATE_TIME=%%i");
        objWriter.newLine();
        objWriter.write("for /f %%i in ('echo %date_time::=-%') do set DATE_TIME=%%i");
        objWriter.newLine();
        
        
       // objWriter.write(objUtilityController.funGetDBBackUpPath(clientCode)+" -u root -proot jpos>"+"\""+filePath+"/%DATE_DAY%_%DATE_TIME%_JPOS.sql\" ");
        JSONObject jsonConfig=new JSONObject(); 
        JSONObject jsonData=objPOSConfigSettingService.funLoadPOSConfigSetting(clientCode);
         JSONArray jArr=(JSONArray)jsonData.get("configSetting");
         jsonConfig=jArr.getJSONObject(0);
        objWriter.write(objUtilityController.funGetDBBackUpPath(clientCode) + " -u "+jsonConfig.get("strUserID").toString()+" -p"+jsonConfig.get("strPassword").toString()+" -h "+jsonConfig.get("strIPAddress").toString()+" "+jsonConfig.get("strDBName").toString()+">" + "\"" + filePath + "/" + fileName + ".sql\" ");

        System.out.println(objUtilityController.funGetDBBackUpPath(clientCode) + " -u "+jsonConfig.get("strUserID").toString()+" -p"+jsonConfig.get("strPassword").toString()+" -h "+jsonConfig.get("strIPAddress").toString()+" "+jsonConfig.get("strDBName").toString()+">" + "\"" + filePath + "/" + fileName + ".sql\" ");
        System.out.println(fileName);

        objWriter.flush();
        objWriter.close();

        Process p = Runtime.getRuntime().exec("cmd /c " + "\"" + batchFilePath + "\"");

        return fileName;
    }
    
    
    
    
    public void funTakeBackUpDB(String backupPath,String clientCode)
    {
        try
        {
            boolean isValidPath=funCheckBackUpFilePath(clientCode);
            if(!isValidPath)
            {
                return;
            }
            
            Date dtCurrentDate = new Date();
            String date = dtCurrentDate.getDate() + "-" + (dtCurrentDate.getMonth() + 1) + "-" + (dtCurrentDate.getYear() + 1900);
            String time = dtCurrentDate.getHours() + "-" + dtCurrentDate.getMinutes();
            String fileName = date + "_" + time + "_JPOS";

            String batchFilePath = System.getProperty("user.dir") + "\\mysqldbbackup.bat";
            String filePath = backupPath;
            File file = new File(filePath);
            if (!file.exists())
            {
                file.mkdir();
            }

            File batchFile = new File(batchFilePath);
            if (!batchFile.exists())
            {
                batchFile.createNewFile();
            }
            BufferedWriter objWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(batchFile), "UTF8"));
            objWriter.write("@echo off");
            objWriter.newLine();
            objWriter.write("for /f \"tokens=1\" %%i in ('date /t') do set DATE_DOW=%%i");
            objWriter.newLine();
            objWriter.write("for /f \"tokens=2\" %%i in ('date /t') do set DATE_DAY=%%i");
            objWriter.newLine();
            objWriter.write("for /f %%i in ('echo %date_day:/=-%') do set DATE_DAY=%%i");
            objWriter.newLine();

            objWriter.write("for /f %%i in ('time /t') do set DATE_TIME=%%i");
            objWriter.newLine();
            objWriter.write("for /f %%i in ('echo %date_time::=-%') do set DATE_TIME=%%i");
            objWriter.newLine();
            //objWriter.write(clsPosConfigFile.gDatabaseBackupFilePath+" -u root -proot jpos>"+"\""+filePath+"/%DATE_DAY%_%DATE_TIME%_JPOS.sql\" ");
            //objWriter.write(clsPosConfigFile.gDatabaseBackupFilePath + " -u "+clsPosConfigFile.userId+" -p"+clsPosConfigFile.password+" -h "+clsPosConfigFile.ipAddress+" "+clsPosConfigFile.databaseName+">" + "\"" + filePath + "/" + fileName + ".sql\" ");

           // System.out.println(clsPosConfigFile.gDatabaseBackupFilePath + " -u "+clsPosConfigFile.userId+" -p"+clsPosConfigFile.password+" -h "+clsPosConfigFile.ipAddress+" "+clsPosConfigFile.databaseName+">" + "\"" + filePath + "/" + fileName + ".sql\" ");
            System.out.println(fileName);

            objWriter.flush();
            objWriter.close();

            Process p = Runtime.getRuntime().exec("cmd /c " + "\"" + batchFilePath + "\"");
            
            //JOptionPane.showMessageDialog(null, "Database Backup Successfully.");
            clsDayEndProcessDao.jsonDayEndReturn.put("DBBackupSuccessS","Database Backup Successfully.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
  
    

    public String getData(String host, String port, String user, String password, String db)
    {
        String Mysqlpath = getMysqlBinPath(user, password, db);
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //System.out.print("yaha dekho");
        }
        try
        {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db, user, password);
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        }
        catch (Exception e)
        {

            System.out.print("I am here yaaar");
            e.printStackTrace();
        }
        System.out.println(Mysqlpath);
        Process run = null;
        try
        {
            System.out.println(Mysqlpath + "mysqldump --host=" + host + " --port=" + port + " --user=" + user + " --password=" + password + " --compact --complete-insert --extended-insert " + "--skip-comments --skip-triggers " + db);
            run = Runtime.getRuntime().exec(Mysqlpath + "mysqldump --host=" + host + " --port=" + port + " --user=" + user + " --password=" + password + "  " + "--skip-comments --skip-triggers " + db);
        }
        catch (IOException ex)
        {
            Logger.getLogger(clsBackupDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        InputStream in = run.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuffer temp = new StringBuffer();

        int count;
        char[] cbuf = new char[BUFFER];

        try
        {

            while ((count = br.read(cbuf, 0, BUFFER)) != -1)
            {
                temp.append(cbuf, 0, count);
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(clsBackupDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {

            br.close();
            in.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(clsBackupDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp.toString();
    }

    public String getMysqlBinPath(String user, String password, String db)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db, user, password);
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String a = "";
        try
        {
            res = st.executeQuery("select @@basedir");
            while (res.next())
            {
                a = res.getString(1);
            }
        }
        catch (Exception eee)
        {
            eee.printStackTrace();
        }
        a = a + "bin\\";
        System.err.println("Mysql path is :" + a);
        return a;
    }

        

    private boolean funCheckBackUpFilePath(String clientCode)
    {
        boolean isValidPath=true;
        try
        {
        	
            String p =objUtilityController.funGetDBBackUpPath(clientCode).replaceAll("\"", "");// clsPosConfigFile.gDatabaseBackupFilePath.replaceAll("\"", "");
            File f = new File(p);
            if (!f.getParentFile().exists())
            {
                isValidPath=false;
                clsDayEndProcessDao.jsonDayEndReturn.put("DBBackupInvaliedPath","Invalid MySQL File Path!!!\nPlease Check DBConfig File.");
               // JOptionPane.showMessageDialog(null, "Invalid MySQL File Path!!!\nPlease Check DBConfig File.");
                //System.exit(0);
                
            }
            else if (!f.getPath().split("bin")[1].equals("\\mysqldump"))
            {
                isValidPath=false;                
                clsDayEndProcessDao.jsonDayEndReturn.put("DBBackupInvaliedPath","Invalid MySQL File Path!!!\nPlease Check DBConfig File.");
              //  JOptionPane.showMessageDialog(null, "Invalid MySQL File Path!!!\nPlease Check DBConfig File.");
                
                //System.exit(0);
            }
        }
        catch (Exception e)
        {
            //new clsUtility().funWriteErrorLog(e);
        	try{
        		clsDayEndProcessDao.jsonDayEndReturn.put("DBBackupInvaliedPath","Invalid MySQL File Path!!!\nPlease Check DBConfig File.");
        	}
        	catch(JSONException e1)
        	{
        		System.out.println("Invalied MySql File Path" );
                //e.printStackTrace();
        	}
           // JOptionPane.showMessageDialog(null, "Invalid MySQL File Path!!!\nPlease Check DBConfig File.");
        	System.out.println("Invalied MySql File Path" );
            //e.printStackTrace();
            isValidPath=false;
            //System.exit(0);
        }
        
        return isValidPath;
    }

}
