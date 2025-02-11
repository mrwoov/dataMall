package com.dataMall.userCenter.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient工具类
 */
public class HttpClientUtils {

    private static final CloseableHttpClient httpClient;

    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    /**
     * 发送 HTTP GET请求，不带请求参数和请求头
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static String doGet(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return doHttp(httpGet);
    }

    /**
     * 发送 HTTP GET，请求带参数，不带请求头
     * @param url 请求地址
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParamsToList(params);
        // 装载请求地址和参数
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ub.setParameters(pairs);
        HttpGet httpGet = new HttpGet(ub.build());
        return doHttp(httpGet);
    }

    /**
     * 发送 HTTP GET请求，带请求参数和请求头
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> headers, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParamsToList(params);
        // 装载请求地址和参数
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        // 设置请求头
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return doHttp(httpGet);
    }

    /**
     * 发送 HTTP POST请求，不带请求参数和请求头
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static String doPost(String url) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        return doHttp(httpPost);
    }

    /**
     * 发送 HTTP POST请求，带请求参数，不带请求头
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParamsToList(params);
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8.name()));

        return doHttp(httpPost);
    }

    /**
     * 发送 HTTP POST请求，带请求参数和请求头
     *
     * @param url     地址
     * @param headers 请求头
     * @param params  参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> headers, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParamsToList(params);
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8.name()));
        // 设置请求头
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return doHttp(httpPost);
    }

    /**
     * 发送 HTTP POST请求，请求参数是JSON格式，数据编码是UTF-8
     *
     * @param url 请求地址
     * @param param 请求参数
     * @return
     * @throws Exception
     */
    public static String doPostJson(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));
        return doHttp(httpPost);
    }

    /**
     * 发送 HTTP POST请求，请求参数是XML格式，数据编码是UTF-8
     *
     * @param url 请求地址
     * @param param 请求参数
     * @return
     * @throws Exception
     */
    public static String doPostXml(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/xml; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));

        return doHttp(httpPost);
    }

    /**
     * 发送 HTTPS POST请求，使用指定的证书文件及密码，不带请求头信息<
     *
     * @param url 请求地址
     * @param param 请求参数
     * @param path 证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String doHttpsPost(String url, String param, String path, String password) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));

        return doHttps(httpPost, path, password);
    }

    /**
     * 发送 HTTPS POST请求,使用指定的证书文件及密码，请求头为“application/xml;charset=UTF-8”
     *
     * @param url 请求地址
     * @param param 请求参数
     * @param path 证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String doHttpsPostXml(String url, String param, String path, String password) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/xml; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));
        return doHttps(httpPost, path, password);
    }

    /**
     * 发送 HTTPS 请求,使用指定的证书文件及密码
     *
     * @param request
     * @param path 证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    private static String doHttps(HttpRequestBase request, String path, String password) throws Exception {
        // 获取HTTPS SSL证书
        SSLConnectionSocketFactory csf = getHttpsFactory(path, password);
        // 通过连接池获取连接对象
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        return doRequest(httpClient, request);
    }

    /**
     * 获取HTTPS SSL连接工厂,使用指定的证书文件及密码
     *
     * @param path     证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    private static SSLConnectionSocketFactory getHttpsFactory(String path, String password) throws Exception {

        // 初始化证书，指定证书类型为“PKCS12”
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取指定路径的证书
        FileInputStream input = new FileInputStream(new File(path));
        try {
            // 装载读取到的证书，并指定证书密码
            keyStore.load(input, password.toCharArray());
        } finally {
            input.close();
        }
        // 获取HTTPS SSL证书连接上下文
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).build();
        // 获取HTTPS连接工厂，指定TSL版本
        SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(sslContext, new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        return sslCsf;
    }
    /**
     * 发送 HTTP 请求
     *
     * @param request
     * @return
     * @throws Exception
     */
    private static String doHttp(HttpRequestBase request) throws Exception {
        // 通过连接池获取连接对象
        return doRequest(httpClient, request);
    }

    /**
     * 处理Http/Https请求，并返回请求结果，默认请求编码方式：UTF-8
     * @param httpClient
     * @param request
     * @return
     */
    private static String doRequest(CloseableHttpClient httpClient, HttpRequestBase request) throws Exception {
        String result = null;
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            // 获取请求结果
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                request.abort();
                throw new RuntimeException("HttpClient error status code: " + statusCode);
            }
            // 解析请求结果
            HttpEntity entity = response.getEntity();
            // 转换结果
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
            // 关闭IO流
            EntityUtils.consume(entity);
        }
        return result;
    }

    /**
     * 转换请求参数，将Map键值对拼接成QueryString字符串
     *
     * @param params
     * @return
     */
    public static String covertMapToQueryStr(Map<String, Object> params) {
        List<NameValuePair> pairs = covertParamsToList(params);
        return URLEncodedUtils.format(pairs, StandardCharsets.UTF_8.name());
    }

    /**
     * 转换请求参数
     *
     * @param params
     * @return
     */
    public static List<NameValuePair> covertParamsToList(Map<String, Object> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }
}
