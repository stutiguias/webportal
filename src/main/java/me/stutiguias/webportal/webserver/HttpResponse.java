/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.information.Info;

/**
 *
 * @author Daniel
 */
public class HttpResponse extends Info {
    
    private WebPortal plugin;
    private HttpExchange httpExchange;
    
    public HttpResponse(WebPortal plugin)
    {
        super(plugin);
        this.plugin = plugin;
    }

    public void Print(String data, String MimeType)
    {
        try
        {
            OutputStream out = getHttpExchange().getResponseBody();
                            
            getHttpExchange().getResponseHeaders().set("Content-Type", MimeType);
            getHttpExchange().getResponseHeaders().set("Server","WebPortal Server");
            getHttpExchange().getResponseHeaders().set("Connection","Close");
            getHttpExchange().getResponseHeaders().set("Cache-Control","no-cache, must-revalidate");
            getHttpExchange().sendResponseHeaders(200, data.length());
 
            out.write(data.getBytes());
            out.close();
        }
        catch(Exception e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in print(): ").append(e.getMessage()).toString());
            e.printStackTrace();
        }
    }

    public void Error(String error)
    {
        try
        {
            OutputStream out = getHttpExchange().getResponseBody();
            getHttpExchange().getResponseHeaders().set("Server","WebPortal Server");
            getHttpExchange().getResponseHeaders().set("Connection","Close");
            getHttpExchange().sendResponseHeaders(200, error.length());
            out.write(error.getBytes());
            out.close();
        }
        catch(Exception e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in httperror(): ").append(e.getMessage()).toString());
        }
    }
    
    public void ReadFile(String path,String Mime) throws IOException
    {
        try
        {
            File archivo = new File(path);
            if(archivo.exists())
            {
                FileInputStream file = new FileInputStream(archivo);
                byte[] buffer = new byte[0x10000];
                long length = archivo.length();
                int leng;
                
                getHttpExchange().getResponseHeaders().set("Content-Type", Mime);
                getHttpExchange().sendResponseHeaders(200, length);
                
                OutputStream out = getHttpExchange().getResponseBody();
                while ((leng = file.read(buffer)) >= 0) {
                    out.write(buffer, 0, leng);
                }
                out.close();
                file.close();
                
            } else
            {
                Error("404 Not Found");
            }
        }
        catch(Exception e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in readFileAsBinary(): ").append(e.getMessage()).toString());
        }
    }
    
    public String GetParam(String param, String URL)
    {
        Pattern regex = Pattern.compile("[\\?&]"+param+"=([^&#]*)");
        Matcher result = regex.matcher(URL);
        if(result.find()){
            try{
                String resdec = URLDecoder.decode(result.group(1),"UTF-8");
                return resdec;
            }catch (UnsupportedEncodingException e){
                WebPortal.logger.log(Level.INFO, "{0} ERROR in getParam(): {1}", new Object[]{plugin.logPrefix, e.getMessage()});
                return "";
            }
        }else
        return "";
    }

    /**
     * @return the httpExchange
     */
    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    /**
     * @param httpExchange the httpExchange to set
     */
    public void setHttpExchange(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }
}
