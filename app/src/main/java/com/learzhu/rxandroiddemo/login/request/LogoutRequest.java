package com.learzhu.rxandroiddemo.login.request;

/**
 * LogoutRequest.java是RxAndroidDemo的类。
 *
 * @author learzhu
 * @version 1.8.2.0 2021/9/14 10:25
 * @update Learzhu 2021/9/14 10:25
 * @updateDes
 * @include {@link }
 * @used {@link }
 * @goto {@link }
 */
public class LogoutRequest {
    private int time;
    private String status;

    public LogoutRequest(int time, String status) {
        this.time = time;
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
