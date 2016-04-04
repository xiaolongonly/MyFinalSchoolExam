/*
 * 系统: U1CityModule
 * 文件名: TrustAllHttpsConnection.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-12-21 下午4:51:46
 */
package com.u1city.module.util;

import android.util.Log;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 设置https链接信任所有证书
 * 
 * @author zhengjb
 */
public class TrustHttpsHostHelper
{
    /**
     * Trust every server - dont check for any certificate
     */
    public static void trustAllHosts()
    {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
                Log.i(TAG, "checkServerTrusted");
            }
        }};

        // Install the all-trusting trust manager
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public  static void setHostVerifier(HttpsURLConnection httpsURLConnection){
        httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
    }

   private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier()
    {

        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    };
}
