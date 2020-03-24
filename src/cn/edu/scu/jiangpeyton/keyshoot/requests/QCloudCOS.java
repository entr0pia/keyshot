package cn.edu.scu.jiangpeyton.keyshoot.requests;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.region.Region;

public class QCloudCOS extends APIRequest {
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private COSClient client;

    public QCloudCOS() {
        this.endPoint = "http://cos.ap-beijing.myqcloud.com";
    }

    @Override
    public COSClient clientBuilder() throws Exception {
        COSCredentials cred = new BasicCOSCredentials(this.accessKeyId, this.accessKeySecret);
        Region region = new Region(this.endPoint);
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
        this.client = cosClient;
        return cosClient;
    }

    @Override
    public Boolean shoot() {
        try {
            this.client.listBuckets();
            return true;
        } catch (CosServiceException e) {
            if (e.getErrorCode().equals("AccessDenied")) {
                return false;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
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
}
