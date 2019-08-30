package com.test;

import java.util.List;

import javax.ws.rs.Path;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Path("/Test")
public class clsTestPOSApplicationContext
{    
    @Autowired    
    private SessionFactory webPOSSessionFactory;
    
    public clsTestPOSApplicationContext()
    {
	//funTest();
    }
    
    public static void main(String[] args)
    {	
	new clsTestPOSApplicationContext();	
    }
    
    private void funTest()
    {
	try
	{
	    
	    //for manually configure
	    
	    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/POSApplicationContext.xml");
	    
	    DriverManagerDataSource dataSource = (DriverManagerDataSource) applicationContext.getBean("posDataSource");
	    
	    System.out.println("data source success");
	    
	    
	    SessionFactory posSessionFactory=(SessionFactory)applicationContext.getBean("sessionFactory");
	    System.out.println("session factory success");
	    	    	    
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
    }


    @Path("/print")
    @Transactional
    public String printSession()
    {		
	Query query=this.webPOSSessionFactory.getCurrentSession().createSQLQuery("select strPOSName from tblposmaster");
	List<String>listOfPOS=query.list();
	for(int i=0;i<listOfPOS.size();i++)
	{
	    System.out.println(listOfPOS.get(i));
	}
	
	return "true";
    }
}
