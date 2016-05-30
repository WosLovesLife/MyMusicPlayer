package com.project.myutilslibrary.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhangH on 2016/5/15.
 */
public class NetworkBitmapUtils {
    private Context mContext;
    private MemoryBitmapCache mMemoryBitmapCache;
    private DiskBitmapCache mDiskBitmapCache;

    protected NetworkBitmapUtils(Context context) {
        mContext = context;
        mMemoryBitmapCache = new MemoryBitmapCache(mContext);
        mDiskBitmapCache = new DiskBitmapCache(mContext);
    }

    protected void getBitmap(ImageView imageView, String url) {
        NetworkBitmapAsyncTask networkBitmapAsyncTask = new NetworkBitmapAsyncTask(imageView, url);
        networkBitmapAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private class NetworkBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;
        private String mUrl;

        public NetworkBitmapAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            SystemClock.sleep(300);
            HttpURLConnection httpURLConnection = null;
            Bitmap bitmap = null;
            try {
                URL urll = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) urll.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(2000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null) {
                        mDiskBitmapCache.saveCache(bitmap, mUrl);
                        mMemoryBitmapCache.saveCache(bitmap, mUrl);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Log.w("BitmapLoader", "getTag: url: " + (String) mImageView.getTag()
                    + "TextUtils.equals(mUrl, (String)mImageView.getTag()): " + TextUtils.equals(mUrl, (String) mImageView.getTag()));
            if (bitmap != null && TextUtils.equals(mUrl, (String) mImageView.getTag())) {
//                  - 从本地取到也存储到内存. (因为如果本地有就不会走网络了)
                mImageView.setImageBitmap(bitmap);
                Log.w("BitmapLoader", "onPostExecute: 从网络中加载了图片");
            }
        }
    }
}
