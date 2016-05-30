package com.zhangheng.mymusicplayer.utils.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by zhangH on 2016/5/15.
 */
public class BitmapLoader {

    private Context mContext;
    private static BitmapLoader mBitmapLoader;
    private MemoryBitmapCache mMemoryBitmapCache;
    private DiskBitmapCache mDiskBitmapCache;
    private NetworkBitmapUtils mNetworkBitmapUtils;

    private BitmapLoader(Context context) {
        mContext = context;
        mMemoryBitmapCache = new MemoryBitmapCache(mContext);
        mDiskBitmapCache = new DiskBitmapCache(mContext);
        mNetworkBitmapUtils = new NetworkBitmapUtils(mContext);
    }

    public static BitmapLoader newInstance(Context context) {
        if (mBitmapLoader == null) {
            mBitmapLoader = new BitmapLoader(context.getApplicationContext());
        }
        return mBitmapLoader;
    }

    /**
     * @param url       作为请求地址,且是图片的唯一标识.
     * @param imageView 作为赋值容器,当图片加载完成,自动赋值给该ImageView对象
     */
    public void decodeBitmap(ImageView imageView, String url) {
        imageView.setTag(url);
        Log.w("BitmapLoader", "setTag: url: "+url );
//      #BitmapUtils的的display() 方法调用解析的流程:
//      - 判断是否有内存缓存, 有则调用, 没有向下
        Bitmap bitmapFromMem = mMemoryBitmapCache.getCache(url);
        if (bitmapFromMem != null){
            imageView.setImageBitmap(bitmapFromMem);
            Log.w("BitmapLoader", "decodeBitmap: 从内存中取出了图片缓存");
        }else {
//          - 判断是否有本地缓存, 有则调用, 没有向下
            Bitmap bitmapFromDisk = mDiskBitmapCache.getCache(url);
            if (bitmapFromDisk != null){
                imageView.setImageBitmap(bitmapFromDisk);
                mMemoryBitmapCache.saveCache(bitmapFromDisk,url);
                Log.w("BitmapLoader", "decodeBitmap: 从外部存储中取出了图片缓存");
            }else {
//              - 从网络加载.请求网络, 存储缓存到本地和内存
                mNetworkBitmapUtils.getBitmap(imageView,url);
                Log.w("BitmapLoader", "decodeBitmap: 执行了mNetworkBitmapUtils.getBitmap(url);");
            }
        }
    }
}
