
/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection. 
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring 
* the fact that the entirety of the webserver execution might be handling
* other clients, too. 
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format). 
*
**/

//put this link in firefox to load webpage (may need to modify "test.html")
//http://localhost:8080/res/acc/test.html

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;

//create array that is referencing image
//os.write
//how to display an image via binary
//convert file to binary, do os.write on binary.
//for image specifications requirement:
//53 width
//53 height
//or 100x100

//image is binary, dont read as line, work with fileinputstream
//go inside loop and do i.read, everytime you read it, write it immediately.
//collect them all and send as getByte

//bufferedreader input filestream
//while loop
//read by fileinputstream
//go in loop with bufferedreader r. Read, send each time.
//test.jpg
//byte array.
//inputstream.length to assign the size of the byte array.

public class WebWorker implements Runnable
{
   String filePath;
   String fileType;
   BufferedImage img = null;

private Socket socket;

/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
}

/**
* Worker thread starting point. Each worker handles just one HTTP 
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
   System.err.println("Handling connection...");
   try {
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      readHTTPRequest(is);
      writeHTTPHeader(os,fileType);
      writeContent(os);
      os.flush();
      socket.close();
   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   System.err.println("Done handling connection.");
   return;
}

/**
* Read the HTTP request header.
**/
private void readHTTPRequest(InputStream is)
{
   

   String line = " ";
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
   try {
   line = r.readLine();
   } catch (Exception e) {
         System.err.println("Request error: "+e);
   } // end catch
   
   //extract filepath to read
   String a[] = new String [3];
   a = line.split(" ");
   filePath = a[1];
   System.out.println("filePath =========== " + filePath);

   //extract the fileType from the filePath, store in fileType.
   fileType = filePath.substring(filePath.lastIndexOf(".") + 1);
   System.out.println("fileType =========== " + fileType);
  
   filePath = "." + filePath;
   
   
   return;
}
/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param contentType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
{
   //convert test1.jpg to binary data. os.write the binary data.
   byte[] byteArray = new byte [100];
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
  try {
   BufferedReader k = new BufferedReader( new FileReader(filePath));
   os.write("HTTP/1.1 200 OK\n".getBytes());

   } catch (Exception e) {
      os.write("HTTP/1.1 404 Not Found \n".getBytes());
      os.write("file not found\n".getBytes());
      
   } // end try exception

   os.write("Date: ".getBytes());
   os.write((df.format(d)).getBytes());
   os.write("\n".getBytes());
   os.write("Server: Jon's very own server\n".getBytes());
   os.write("Connection: close\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(contentType.getBytes());
   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os) throws Exception
{


//if fileType == html, do everything from P1 assignment.
//else, perform image file functionality.   

if (fileType.equals("html")) {
System.out.println("fileType===" + fileType);

    
   String content = "";
   BufferedReader r = new BufferedReader(new FileReader(filePath));
   String newLine;
   while (true) {
      try {

         newLine = r.readLine();
         if (newLine.equals("<cs371date>")) {
            newLine = "9/13/17 \n";
            content += newLine;
         } 
         
         else if (newLine.equals("<cs371server>")) {
            newLine = "Kevin's Web Server for CS371 \n";
            content += newLine;
         } 
         
         else
            content += newLine;         
            System.err.println("Request line: ("+newLine+")");
            if (newLine.length()==0) break;
      } catch (Exception e) {
         System.err.println("Request error: "+e);
         break;
      }
   } // end while
   os.write(content.getBytes());
   

}//end if statement

else 
{
//if this statement is reached, fileType is not html (must be an image file)

//create a byteArray, fill byteArray with filePath contents.
//perform a read and write on byteArray to write image bytes to WebServer.

   byte byteArray[] = null;
   FileInputStream stream = new FileInputStream(filePath);
   File nFile = new File(filePath);
   byteArray = new byte [(int)nFile.length()];
   stream.read(byteArray);
   stream.close();
   os.write(byteArray);




}//end else statement


} // end writeContent

} // end class
