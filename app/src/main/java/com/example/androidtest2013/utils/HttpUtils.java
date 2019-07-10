package com.example.androidtest2013.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    /**
     * 通过URL_PATH的地址访问图片并保存到本地
     */
    public static void saveImageToDisk(String url_path, String out_path)
    {
        InputStream inputStream= getInputStream(url_path);
        byte[] data=new byte[1024];
        int len=0;
        FileOutputStream fileOutputStream=null;
        try {
            //把图片文件保存在本地F盘下
            fileOutputStream=new FileOutputStream(out_path);
            while((len=inputStream.read(data))!=-1)
            {
                //向本地文件中写入图片流
                fileOutputStream.write(data,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            //最后关闭流
            if(inputStream!=null)
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null)
            {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 通过URL获取图片
     * @return URL地址图片的输入流。
     */
    public static InputStream getInputStream(String url_path) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            //根据URL地址实例化一个URL对象，用于创建HttpURLConnection对象。
            URL url = new URL(url_path);

            if (url != null) {
                //openConnection获得当前URL的连接
                httpURLConnection = (HttpURLConnection) url.openConnection();
                //设置3秒的响应超时
                httpURLConnection.setConnectTimeout(3000);
                //设置允许输入
                httpURLConnection.setDoInput(true);
                //设置为GET方式请求数据
                httpURLConnection.setRequestMethod("GET");
                //获取连接响应码，200为成功，如果为其他，均表示有问题
                int responseCode=httpURLConnection.getResponseCode();
                if(responseCode==200)
                {
                    //getInputStream获取服务端返回的数据流。
                    inputStream=httpURLConnection.getInputStream();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 通过给定的请求参数和编码格式，获取服务器返回的数据
     * @param params 请求参数
     * @param encode 编码格式
     * @return 获得的字符串
     */
    public static String sendPostMessage(Map<String, String> params,
                                         String encode, URL url) {
        StringBuffer buffer = new StringBuffer();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");//请求的参数之间使用&分割。
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            buffer.deleteCharAt(buffer.length() - 1);
            System.out.println(buffer.toString());
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setConnectTimeout(3000);
                //设置允许输入输出
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                byte[] mydata = buffer.toString().getBytes();
                //设置请求报文头，设定请求数据类型
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                //设置请求数据长度
                urlConnection.setRequestProperty("Content-Length",
                        String.valueOf(mydata.length));
                //设置POST方式请求数据
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(mydata);
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    return changeInputStream(urlConnection.getInputStream(),
                            encode);
                }else{
                    System.out.println("test:" + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 把服务端返回的输入流转换成字符串格式
     * @param inputStream 服务器返回的输入流
     * @param encode 编码格式
     * @return 解析后的字符串
     */
    private static String changeInputStream(InputStream inputStream,
                                            String encode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result="";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data,0,len);
                }
                result=new String(outputStream.toByteArray(),encode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws MalformedURLException {
        //get test
        //saveImageToDisk("http://www.baidu.com", "d:\\abc.html");
        //post test
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("name", "zdf");
        System.out.println(sendPostMessage(params, "utf-8", new URL("http://www.visenergy.cc") ));
    }
}
