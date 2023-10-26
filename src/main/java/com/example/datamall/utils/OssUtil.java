package com.example.datamall.utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;

/**
 * @deprecated : 操作oss数据存取等的工具类
 */
@Component
public class OssUtil {

    /**
     * 默认的oss桶位置
     */
    public static final String fixedBucket = "woov-data-mall";
    /**
     * 为了能在每个方法中使用配置号参数的oss对象，需要将其静态化，如果oss被static修饰，则每次调用时oss都是空，所以这里需要做一下转换
     * 通过postConstruct注解将oss转为静态的ossP,这样在每次调用时都是配置好的参数的一个oss对象
     * PostConstruct是Java自带的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法。
     * postConstruct可以修饰一个非静态的void方法，使用场景为：在生成对象时想完成某些初始化操作，初始化又依赖于自动注入，构造方法中无法实现时
     * 使用此注解修饰一个方法来实现初始化操作，注解的方法会在自动注入完成后被调用-----执行顺序--》construct构造函数-》@Autowired自动注入-》@PostConstruct
     */
    private static OSS ossP;
    /**
     * 此处的oss是将配置文件中的参数配置后生成bean，通过自动注入来获取配置参数后的对象oss
     */
    @Resource
    private OSS oss;
    

    /**
     * 上传文件
     *
     * @param objectName 文件在oss中的全路径
     * @param filePath   上传文件的本地路径
     */
    public static void uploadFile(String objectName, String filePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(fixedBucket, objectName, new File(filePath));
        ossP.putObject(putObjectRequest);
    }

    /**
     * 上传文件
     *
     * @param bucketName 文件所在oss桶名称
     * @param objectName 文件在oss中的全路径
     * @param filePath   上传文件的本地路径
     */
    public static void uploadFile(String bucketName, String objectName, String filePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(filePath));
        ossP.putObject(putObjectRequest);
    }


    /**
     * 上传流数据
     *
     * @param objectName  数据在oss中的全路径
     * @param inputStream 流数据内容
     */
    public static void uploadStream(String objectName, InputStream inputStream) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(fixedBucket, objectName, inputStream);
        ossP.putObject(putObjectRequest);
    }

    /**
     * 上传流数据
     *
     * @param bucketName  oss桶名称
     * @param objectName  数据所在oss中的全路径
     * @param inputStream 流数据
     */
    public static void uploadStream(String bucketName, String objectName, InputStream inputStream) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
        ossP.putObject(putObjectRequest);
    }
    
    /**
     * 下载文件
     *
     * @param objectName 数据所在oss全路径
     * @param fileName   文件名
     */
    public static void downloadFile(String objectName, String fileName) {
        ossP.getObject(new GetObjectRequest(fixedBucket, objectName), new File(fileName));
    }

    /**
     * 下载文件
     *
     * @param bucketName 文件所在oss桶名称
     * @param objectName 文件所在oss中的全路径
     * @param fileName   文件名
     */
    public static void downloadFile(String bucketName, String objectName, String fileName) {
        ossP.getObject(new GetObjectRequest(bucketName, objectName), new File(fileName));
    }

    /**
     * 删除数据
     *
     * @param objectName 删除文件的全路径
     */
    public static void delete(String objectName) {
        ossP.deleteObject(fixedBucket, objectName);
    }

    /**
     * 删除数据
     *
     * @param bucketName 桶名称
     * @param objectName 删除文件的全路径
     */
    public static void delete(String bucketName, String objectName) {
        ossP.deleteObject(bucketName, objectName);
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 文件所在oss中的全路径
     * @return 返回是否
     */
    public static boolean checkExist(String objectName) {
        return ossP.doesObjectExist(fixedBucket, objectName);
    }

    /**
     * 检查文件是否存在
     *
     * @param bucketName 桶名称
     * @param objectName 文件所在oss中的全路径
     * @return 返回是否
     */
    public static boolean checkExist(String bucketName, String objectName) {
        return ossP.doesObjectExist(bucketName, objectName);
    }

    @PostConstruct
    public void ossTransfer() {
        //该方法会在上面的依赖注入后自动被调用
        ossP = oss;
    }


}


