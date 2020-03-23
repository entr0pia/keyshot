package cn.edu.scu.jiangpeyton.keyshot.requests;

public class APIRequest {
    private String accessKeyId;
    private String accessKeySecret;

    public Object clientBuilder() throws Exception {
        return new Object();
    }

    public Boolean shot() {
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

}
