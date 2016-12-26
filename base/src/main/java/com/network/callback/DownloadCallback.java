package com.haocai.app.network.base.callback;

import java.io.File;

/**
 * Created by jslsxu on 16/12/20.
 */

public abstract class DownloadCallback {
    public abstract void onSuccess(File response);

    public abstract void onFail(Exception e);

    public abstract void onProgress(float progress, long total);
}
