/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.stutiguias.webportal.init.WebAuction;

/**
 *
 * @author Daniel
 */
public class Response {
    
    WebAuction plugin;
    Socket WebServerSocket;
    
    public Response(WebAuction plugin,Socket s)
    {
        this.plugin = plugin;
        WebServerSocket = s;
    }
    
    public void print(String data, String MimeType)
    {
        try
        {
            DataOutputStream out = new DataOutputStream(WebServerSocket.getOutputStream());
            out.writeBytes("HTTP/1.1 200 OK\r\n");
            out.writeBytes((new StringBuilder()).append("Content-Type: ").append(MimeType).append("; charset=utf-8\r\n").toString());
            out.writeBytes("Cache-Control: no-cache, must-revalidate\r\n");
            out.writeBytes((new StringBuilder()).append("Content-Length: ").append(data.length()).append("\r\n").toString());
            out.writeBytes("Server: webauction lite Server\r\n");
            out.writeBytes("Connection: Close\r\n\r\n");
            out.writeBytes(data);
            out.flush();
            out.close();
        }
        catch(Exception e)
        {
            WebAuction.log.info((new StringBuilder()).append("ERROR in print(): ").append(e.getMessage()).toString());
        }
    }

    public void httperror(String error)
    {
        try
        {
            DataOutputStream out = new DataOutputStream(WebServerSocket.getOutputStream());
            out.writeBytes((new StringBuilder()).append("HTTP/1.1 ").append(error).append("\r\n").toString());
            out.writeBytes("Server: webauction lite Server\r\n");
            out.writeBytes("Connection: Close\r\n\r\n");
            out.flush();
            out.close();
        }
        catch(Exception e)
        {
            WebAuction.log.info((new StringBuilder()).append("ERROR in httperror(): ").append(e.getMessage()).toString());
        }
    }
    
    public void readFileAsBinary(String path,String Mime) throws IOException
    {
        try
        {
            File archivo = new File(path);
            if(archivo.exists())
            {
                OutputStream out = WebServerSocket.getOutputStream();
                FileInputStream file = new FileInputStream(archivo);
                byte fileData[] = new byte[0x10000];
                long length = archivo.length();
                int leng;
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write((new StringBuilder()).append("Content-Type: ").append(Mime).append("; charset=utf-8\r\n").toString().getBytes());
                out.write("Cache-Control: no-cache, must-revalidate\r\n".getBytes());
                out.write((new StringBuilder()).append("Content-Length: ").append(length).append("\r\n").toString().getBytes());
                out.write("Server: webauctionlite server\r\n".getBytes());
                out.write("Connection: Close\r\n\r\n".getBytes());
                while ((leng = file.read(fileData)) > 0)
                        out.write(fileData, 0, leng);
                out.flush();
                out.close();
                file.close();
                
            } else
            {
                httperror("404 Not Found");
            }
        }
        catch(Exception e)
        {
            WebAuction.log.info((new StringBuilder()).append("ERROR in readFileAsBinary(): ").append(e.getMessage()).toString());
        }
    }
    
    public String getParam(String param, String URL)
    {
            Pattern regex = Pattern.compile("[\\?&]"+param+"=([^&#]*)");
            Matcher result = regex.matcher(URL);
            if(result.find()){
                    try{
                            String resdec = URLDecoder.decode(result.group(1),"UTF-8");
                            return resdec;
                    }catch (UnsupportedEncodingException e){
                            WebAuction.log.info(plugin.logPrefix+"ERROR in getParam(): " + e.getMessage());
                            return "";
                    }
            }else
                    return "";
    }
}
