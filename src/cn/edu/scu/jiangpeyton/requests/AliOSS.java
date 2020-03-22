package cn.edu.scu.jiangpeyton.requests;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.Bucket;

import java.util.List;

public class AliOSS extends APIRequest {
    /**
     * 阿里对象存储
     */
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private OSS client;

    public AliOSS() {
        this.endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    }


    public AliOSS(String accessKeyId, String accessKeySecret) throws Exception {
        this.endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.client = clientBuilder();
    }

    public AliOSS(String endpoint, String accessKeyId, String accessKeySecret) throws Exception {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.client = clientBuilder();
    }

    public OSS clientBuilder() throws Exception {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        this.client = ossClient;
        return ossClient;
    }

    public void setAccessKeyId(String s) {
        this.accessKeyId = s;
    }

    public void setAccessKeySecret(String s) {
        this.accessKeySecret = s;
    }

    public boolean shot() {
        try {
            client.listBuckets();
            // 当密钥权限过高时, 返回true
            return true;
        } catch (OSSException e) {
            //e.printStackTrace();
            return false;
        }
    }

}
