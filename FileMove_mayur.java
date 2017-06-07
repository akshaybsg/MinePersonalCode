package com.tcs.EformsTesting;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMove_mayur 
{
	 public void FileMoveMayur() 
	{
		 //System.out.println(System.getProperty("user.home"));
		 File root=new File("D://IIT/Input_Source");
		 File[] rootlist = root.listFiles();
		 ArrayList<File> rootArr=new ArrayList<File>();
		 for (int i = 0;i < rootlist.length;i++)
		{
				if (rootlist[i].isDirectory())
				{
					 //System.out.println(rootlist[i]);
					 rootArr.add(rootlist[i]);
				 }
		 }
		 for (File obj:rootArr)
		{
				  getFiles(obj);
		 }
 }
	 public void getFiles(File dir)
 {
		 File[] fileList = dir.listFiles();
		 ArrayList<File> fileArr=new ArrayList<File>();
		 for (int i = 0;i < fileList.length;i++)
		 {
			 if (fileList[i].isFile()){
				 //System.out.println(rootlist[i]);
				 fileArr.add(fileList[i]);
			 }
		 }
		 for (File obj:fileArr)
		{
			 //System.out.println(itr.next());
			 System.out.println(obj.getName().substring(0,obj.getName().length()-4));
			 File canFol = new File("D://IIT/Output_Mayur/"+obj.getName().substring(0,obj.getName().length()-4));
			 if (!canFol.exists())
			{
				 canFol.mkdirs();
			 }
			 if (obj.exists())
			{
				 //System.out.println(canFol);
				 String x = "D://IIT/Output_Mayur/"+obj.getName().substring(0,obj.getName().length()-4);
				 x=x+"/"+obj.getParentFile().getName()+"_";
				 x=x+obj.getName().substring(0,obj.getName().length()-4)+obj.getName().substring(obj.getName().length()-4);
				 obj.renameTo(new File(x));
				 
			 }
		 }
				
	 }
}


