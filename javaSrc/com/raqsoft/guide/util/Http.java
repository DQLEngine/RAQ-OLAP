package com.raqsoft.guide.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {    
        try {    
            URL url = new URL(requestUrl);    
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();    
              
            conn.setDoOutput(true);    
            conn.setDoInput(true);    
            conn.setUseCaches(false);    
            // ��������ʽ��GET/POST��    
            conn.setRequestMethod(requestMethod);    
            conn.setRequestProperty("content-type", "text/xml; charset=UTF-8");    
            // ��outputStr��Ϊnullʱ�������д����    
            if (null != outputStr) {    
                OutputStream outputStream = conn.getOutputStream();    
                // ע������ʽ    
                outputStream.write(outputStr.getBytes("UTF-8"));    
                outputStream.close();    
            }    
            // ����������ȡ��������    
            InputStream inputStream = conn.getInputStream();    
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");    
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);    
            String str = null;  
            StringBuffer buffer = new StringBuffer();    
            while ((str = bufferedReader.readLine()) != null) {    
                buffer.append(str);    
            }    
            // �ͷ���Դ    
            bufferedReader.close();    
            inputStreamReader.close();    
            inputStream.close();    
            inputStream = null;    
            conn.disconnect();    
            return buffer.toString();    
        } catch (ConnectException ce) {    
            System.out.println("connect time out : "+ ce);    
        } catch (Exception e) {    
            System.out.println("http request failed : "+ e);    
        }    
        return null;    
      }    


//    public synchronized static String accessService(String wsdl,String content,String contentType)throws Exception{    
//        //ƴ�Ӳ���    
//        String soapResponseData = "";    
//        //ƴ��SOAP    
//        PostMethod postMethod = new PostMethod(wsdl);    
//        // Ȼ���Soap����������ӵ�PostMethod��    
//        byte[] b=null;    
//        InputStream is=null;    
//        try {    
//            b = content.getBytes("utf-8");     
//            is = new ByteArrayInputStream(b, 0, b.length);    
//            RequestEntity re = new InputStreamRequestEntity(is, b.length,contentType);    
//            postMethod.setRequestEntity(re);    
//            HttpClient httpClient = new HttpClient();    
//            //methods
//            int status = httpClient.executeMethod(postMethod);    
//            System.out.println("status:"+status);    
//            if(status==200){ 
//            	return postMethod.getResponseBodyAsString();
//            }    
//        } catch (Exception e) {    
//            e.printStackTrace();    
//        } finally{    
//            if(is!=null){    
//                is.close();    
//            }    
//        }    
//        return soapResponseData;    
//    }    

}
