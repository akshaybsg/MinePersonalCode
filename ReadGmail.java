package com.tcs.EformsTesting;
import javax.mail.*;
import java.util.*;

public class ReadGmail {
	
	public void readGmail() 
	{
		System.out.println("Begin");        
	 //   Thread.currentThread().setContextClassLoader(ReadGmail.class.getClassLoader());
	    //read emails
	 
	/*	try
		{
			Properties props = System.getProperties();
			Session session = Session.getInstance(props, null);
			Store store = session.getStore("imaps");
			
	        props.setProperty("mail.store.protocol" , "imaps");
	        store.connect("imap.gmail.com", -1, "akshay.bsg@gmail.com", "Google@1");
		}
		catch(Exception e)
		{
			System.out.println("Exception"+e);
			e.printStackTrace();
		}*/
		Properties prop=new Properties();
        prop.setProperty("mail.store.protocol", "imap");
        prop.setProperty("mail.imap.port", "993");
        try
        {
        Session session=Session.getInstance(prop,null);
        Store store=session.getStore();
        store.connect("imap.gmail.com", "akshay.bsg@gmail.com", "Google@1");
        Folder folder=store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
            Message msg=folder.getMessage(folder.getMessageCount());
            Address[] add=msg.getFrom();
            for(Address address:add)
            {
                System.out.println("FROM:"+address.toString());
            }
            Multipart mp=(Multipart)msg.getContent();
            BodyPart bp=mp.getBodyPart(0);
            System.out.println("SENT DATE:"+msg.getSentDate());
            System.out.println("SUBJECT:"+msg.getSubject());
            System.out.println("CONTENT:"+msg.getContent());

        }
        catch(Exception e)
        {
        	System.out.println("Exception Occured"); 
            e.printStackTrace();
        }

}
}
			       
	

