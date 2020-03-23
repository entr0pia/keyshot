package cn.edu.scu.jiangpeyton.keyshot.requests;

import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;

public class BaiduBOS extends APIRequest {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private BosClient client;

    public BaiduBOS() {
        this.endpoint = "http://bj.bcebos.com";
    }


    public BaiduBOS(String accessKeyId, String accessKeySecret) throws Exception {
        this.endpoint = "http://bj.bcebos.com";
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.client = clientBuilder();
    }

    public BaiduBOS(String endpoint, String accessKeyId, String accessKeySecret) throws Exception {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.client = clientBuilder();
    }

    @Override
    public BosClient clientBuilder() throws Exception {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(this.accessKeyId, this.accessKeySecret));
        config.setEndpoint(this.endpoint);
        BosClient bosClient = new BosClient(config);
        this.client = bosClient;
        return bosClient;
    }

    @Override
    public void setAccessKeyId(String s) {
        this.accessKeyId = s;
    }

    @Override
    public void setAccessKeySecret(String s) {
        this.accessKeySecret = s;
    }

    @Override
    public void setKey(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    @Override
    public Boolean shot() {
        try {
            client.listBuckets();
            // 当密钥权限过高时, 返回true
            return true;
        } catch (BceServiceException e) {
            if (e.getErrorCode().equals("AccessDenied")) {
                return false;
            }
            return null;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }


}
