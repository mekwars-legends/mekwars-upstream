/*
 * MekWars - Copyright (C) 2005 
 * 
 * Original author - Torren (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */


/**
 * 
 * @author Torren (Jason Tighe) 11.9.05 
 * Main Class used to communicate with a Cyclops RPC Server
 * 
 * With Lots of help from Guibod
 * http://muposerver.dyndns.org/devel/cyclops
 * 
 */

/*
 * Thanks to www.koders.com
 * for all the ideas.
 * 
 */

 package server.mwcyclopscomm;

 /**
  * This is the mail utility class for XML calls to cyclops.
  * @author Torren Nov 10, 2005
  *
  */
 public class MWCyclopsUtils{

     public static String structStart(){
         return "<struct>";
     }
     
     public static String structEnd(){
         return "</struct>";
     }
     
     public static String methodName(String name){
         return "<methodName>"+name+"</methodName>";
     }
     
     public static String methodCallStart(){
         return "<methodCall>";
     }
     
     public static String methodCallEnd(){
         return "</methodCall>";
     }
     
     public static String paramsStart(){
         return "<params>";
     }
     
     public static String paramsEnd(){
         return "</params>";
     }

     public static String paramStart(){
         return "<param>";
     }
     
     public static String paramEnd(){
         return "</param>";
     }

     public static String valueStart(){
         return "<value>";
     }
     
     public static String valueEnd(){
         return "</value>";
     }

     public static String arrayStart(){
         return "<array>";
     }
     
     public static String arrayEnd(){
         return "</array>";
     }
     
     public static String dataStart(){
         return "<data>";
     }
     
     public static String dataEnd(){
         return "</data>";
     }
     
     public static String structMember(String name,String value){
         String message = "";
         
         message += "<member>";
         message += "<name>"+name+"</name>";
         message += "<value>"+value+"</value>";
         message += "</member>";
         
         return message;
     }

     public static String structMember(String name,int value){
         String message = "";
         
         message += "<member>";
         message += "<name>"+name+"</name>";
         message += "<value><int>"+Integer.toString(value)+"</int></value>";
         message += "</member>";
         
         return message;
     }

     public static String structMember(String name,boolean value){
         String message = "";
         
         message += "<member>";
         message += "<name>"+name+"</name>";
         int boolvalue = value?1:0;
         message += "<value><boolean>"+Integer.toString(boolvalue)+"</boolean></value>";
         message += "</member>";
         
         return message;
     }
     
     public static String structMember(String name,double value){
         String message = "";
         
         message += "<member>";
         message += "<name>"+name+"</name>";
         message += "<value><double>"+value+"</double></value>";
         message += "</member>";
         
         return message;
     }
     
     public static String structMember(String name,long value){
         String message = "";
         
         message += "<member>";
         message += "<name>"+name+"</name>";
         message += "<value><double>"+value+"</double></value>";
         message += "</member>";
         
         return message;
     }

     public static String value(String value){
         String message = "";
         
         message = "<value>"+value+"</value>";
         
         return message;
     }

     public static String value(int value){
         String message = "";
         
         message = "<value><int>"+value+"</int></value>";
         
         return message;
     }

     public static String value(double value){
         String message = "";
         
         message = "<value><double>"+value+"</double></value>";
         
         return message;
     }

     public static String value(boolean value){
         String message = "";
         String boolmessage = value?"1":"0";
         
         message = "<value><boolean>"+boolmessage+"</boolean></value>";
         
         return message;
     }

     /**
      * Converts the normal xml stream MW sends to Cyclops into a more
      * human readable format.
      * @param message
      * @return
      */
     public static String formatMessage(String message){
         String tempMessage = message.replaceAll("><",">@<");
         String tabs = "";
         int tabCount = 1;
         int index = 1;
         int end = tempMessage.length();
         
         for (int pos = 0; pos < end; pos++ ){
             index = tempMessage.indexOf("<",index);
             //CampaignData.mwlog.errLog("pos: "+pos+" end: "+end+" index: "+index);
             if ( index < 0 )
                 break;
             if ( tempMessage.substring(index).startsWith("</")){

                 if ( !tempMessage.substring(0,index+2).endsWith(">@</")){
                     index++;
                     tabCount--;
                     continue;
                 }
                 tabs = getTabs(--tabCount);
             }
             else
                 tabs = getTabs(tabCount++);
             //CampaignData.mwlog.errLog("tabs: "+tabs.length());
             if ( tabs.length() > 0 ){
                 tempMessage = tempMessage.substring(0,index)+tabs+tempMessage.substring(index);
             }
             index += tabs.length()+1;
         }
         
         return tempMessage.replaceAll("@","\n");
     }
     /**
      * @author Torren (Jason Tighe)
      * @param count
      * @return a string with count number of \t characters
      */
     public static String getTabs(int count){
         String tab = "\t";
         String tabs = "";
         
         if ( count <= 0 )
             return "";
         
         for ( int x = count; x > 0; x--)
             tabs += tab;
         
         return tabs;
     }
     
}//end Class
