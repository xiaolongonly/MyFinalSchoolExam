package com.u1city.module.util;

import android.app.ProgressDialog;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author linjy
 * @time 2015-5-5 14:45:32
 * @类说明：下载工具类
 * 
 *            <pre>
 * 1.从服务器下载apk
 * 2.获取文件的大小
 * </pre>
 * 
 */
public class DownLoadManager
{
    private static final String TAG = DownLoadManager.class.getName();

    /**
     * 1.从服务器下载apk
     * 
     * @param path
     * @param pd
     */
    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception
    {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            HttpURLConnection conn;
            
            if(path.indexOf("https") != -1){//独立打包的地址为https，因此需要用Https方式下载
                int lastIndex = path.lastIndexOf("/");
                if(lastIndex == -1){
                     File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
                     return file;
                }
                
                String preScheme = path.substring(0, lastIndex);
                String name = path.substring(lastIndex, path.length());
                URL url = new URL(preScheme + URLEncoder.encode(name,"UTF-8"));//独立打包的包名时utf-8的
                
                TrustHttpsHostHelper.trustAllHosts();
                conn = (HttpsURLConnection)url.openConnection();
                TrustHttpsHostHelper.setHostVerifier((HttpsURLConnection) conn);
                conn.connect();
            }else{
                URL url = new URL(path);
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setConnectTimeout(5000);

            // 获取到文件的大小
            int fileSize = conn.getContentLength();
            int newFileSize = FormetFileSize(fileSize);
            pd.setMax(newFileSize);

            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1)
            {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前已下载量
                int newTotal = FormetFileSize(total);
                pd.setProgress(newTotal);
                pd.setProgressNumberFormat(newTotal + " kb/" + newFileSize + " kb");
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else
        {
            return null;
        }
    }

    /**
     * 2.获取文件的大小
     * 
     * @param fileSize
     *            文件的大小
     * @return
     */
    public static int FormetFileSize(int fileSize)
    {// 转换文件大小
        double fileSizeString = (double) fileSize / 1024;
        return (int) fileSizeString;
    }

}
