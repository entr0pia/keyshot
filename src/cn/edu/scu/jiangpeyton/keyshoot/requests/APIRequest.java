package cn.edu.scu.jiangpeyton.keyshoot.requests;
import org.apache.commons.lang3.RandomStringUtils;

public class APIRequest {
    private String accessKeyId;
    private String accessKeySecret;

    public Object clientBuilder() throws Exception {
        return new Object();
    }

    public Boolean shoot() {
        return false;
    }

    public void setAccessKeyId(String s) {
        this.accessKeyId = s;
    }

    public void setAccessKeySecret(String s) {
        this.accessKeySecret = s;
    }

    public void setKey(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }
    public String genRandomName() {
        return RandomStringUtils.random(23);
    }

}
