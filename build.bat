echo This builds the Server Code for MM.NET
javac -d ./classes -classpath ./TinyXML.jar;./MegaMek.jar;./client-dist/chatclient_full.jar;./server-dist/chatserver.jar;./server-dist/jms.jar;./server-dist/servlet.jar;.;./src ./src/server/MMServ.java
pause