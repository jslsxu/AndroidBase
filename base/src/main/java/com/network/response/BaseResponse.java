package com.haocai.app.network.base.response;

/**
 * Created by jslsxu on 16/12/19.
 */

public class BaseResponse extends BaseObject {
    public static final int NETWORK_ERROR = -1;
    public static final int RESPONSE_SUCCESS = 0;

    private String errmsg;
    private int errno;

    public boolean success() {
        return errno == RESPONSE_SUCCESS;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }
}
