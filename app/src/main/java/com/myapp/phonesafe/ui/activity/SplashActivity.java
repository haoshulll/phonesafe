package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.service.AddressShowService;
import com.myapp.phonesafe.service.BlackIntercepterService;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.Constants;
import com.myapp.phonesafe.utils.GZipUtils;
import com.myapp.phonesafe.utils.LogUtil;
import com.myapp.phonesafe.utils.PackageUtils;
import com.myapp.phonesafe.utils.ToastUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity {
    private static final int DELAY_MILLIS = 3000;// splash界面要求展示 3秒
    private static final int SHOW_UPDATE_DIALOG = 110;// 显示更新对话框
    private static final int SHOW_NO_NEW_VERSION = 120;// 没有新版本
    private static final int SHOW_ERROR = 130;// 显示错误
    private static final int SHOW_NET_ACCESS_FAILURE = 140;// 网络访问失败
    private static final int INSTALL_REQUEST_CODE = 150;// 安装的请求码
    public static final String TAG = "SplashActivity";
    private ProgressDialog mProgressDialog;// 进度条
    private TextView mVersionNameTv;// 版本名称的文本框
    private Context context;
    private String mDesc;// 新版本的描述信息
    private String mUrl;// 新版本的apk地址
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // Log.d(TAG, "handleMessage");
            LogUtil.d(TAG, "handleMessage");
            switch (msg.what) {
                case SHOW_NO_NEW_VERSION:// 没有新版本
                    load2Home();
                    break;
                case SHOW_NET_ACCESS_FAILURE:
                case SHOW_ERROR:
                    // Toast.makeText(context, msg.obj.toString(),
                    // Toast.LENGTH_SHORT).show();
                    // 用Toast工具类
                    ToastUtil.show(context, msg.obj.toString());
                    // 进入Home界面
                    load2Home();
                    break;
                case SHOW_UPDATE_DIALOG:// 显示更新版本对话框
                    showUpdateDialog();
                    break;
                default:
                    break;
            }

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        mVersionNameTv = (TextView) findViewById(R.id.version_name_tv);

        // 1. 取得本项目的版本号和版本名称
        mVersionNameTv.setText(PackageUtils.getVersionName(context));
        // 2. 先检查配置文件的版本更新的配置情况，依据配置情况来决定是否访问网络，获取版本信息
        if (CacheConfigUtils.getBoolean(context,
                CacheConfigUtils.IS_UPDATE_VERSION, true)) {
            checkVersionUpdate();
        } else {
            // Thread.sleep(3000);//主线程休眠3秒，不能在主线程休眠
            // 延迟3秒发送发语句队列，进入HomeActivity界面
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    LogUtil.d(TAG, "postDelayed+utils");
                    load2Home();
                }
            }, DELAY_MILLIS);
        }

        // 3. 是否开启黑名单的拦截服务
        if (CacheConfigUtils.getBoolean(context,
                CacheConfigUtils.IS_BLACK_INTERCEPT)) {
            Intent intent = new Intent();
            intent.setClass(context, BlackIntercepterService.class);
            startService(intent);
        }

        // 4. 拷贝号码归属地数据库
        copyAddressDb();

        // 5. 拷贝常用号码数据库
        copyCommonNumberDB();


        // 6. 是否开启号码归属地显示的服务
        if (CacheConfigUtils.getBoolean(context,
                CacheConfigUtils.SHOW_ADDRESS)) {
            Intent intent = new Intent();
            intent.setClass(context, AddressShowService.class);
            startService(intent);
        }
    }
    // 拷贝常用号码数据库
    private void copyCommonNumberDB() {
        // data/data/<包名>/files 或者 databaes 目录中

        // File dataBaseFile=new File(getFilesDir().getParentFile(),
        // "/databases/address.db");
        //判断databases 目录是否存在，不存在，则创建
        File databasePath=new File(getFilesDir().getParentFile(),"/databases");
        if(!databasePath.exists()){
            databasePath.mkdir();//创建目录
        }
        // 拷贝到的目标文件
        final File targetFile = new File(databasePath,
                "commonnum.db");
//		final File targetFile = new File(getFilesDir().getParentFile(),
//				"/databases/commonnum.db");
        if (!targetFile.exists()) { // 假如数据库文件不存在，则拷贝
            new Thread() {
                public void run() {
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        System.out.println("拷贝常用号码数据库");
                        // 要拷贝的数据库文件来自assets目录
                        is = getAssets().open("commonnum.db");// 输入流，读取数据
                        fos = new FileOutputStream(targetFile);// 输出流，写数据
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                };

            }.start();
        }
    }

    // 拷贝号码归属地数据库
    private void copyAddressDb() {
        // data/data/<包名>/files 或者 databaes 目录中

        // File dataBaseFile=new File(getFilesDir().getParentFile(),
        // "/databases/address.db");
        // 拷贝到的目标文件
        final File targetFile = new File(getFilesDir(), "address.db");

        if (!targetFile.exists()) { // 假如数据库文件不存在，则拷贝
            new Thread() {
                public void run() {
                    try {
                        System.out.println("拷贝地址数据库");
                        // 要拷贝的数据库文件来自assets目录
                        InputStream is = getAssets().open("address.zip");
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        GZipUtils.unzip(is, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

            }.start();
        }
    }

    // 2. 检测版本更新，访问服务端网络
    private void checkVersionUpdate() {
        new Thread(new CheckVersionTask()).start();

    }

    // 3. 显示更新版本的对话框
    private void showUpdateDialog() {
        // 构建一个对话框的构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("版本更新提示");
        builder.setMessage(mDesc);
        builder.setPositiveButton("立即更新",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 4. 下载及安装apk
                        downloadApk();
                    }
                });
        builder.setNegativeButton("稍后再说",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 直接进入Home界面
                        load2Home();
                    }
                });
        builder.show();
    }

    // 4. 从网络中下载新版本的apk
    private void downloadApk() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);// 不能中断
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置样式
        // mProgressDialog.setTitle("下载进度");
        mProgressDialog.show();// 显示
        // 用框架来解决下载文件的问题
        // 用xUtils 的 httpUtils模块来访问网络，下载apk文件
        HttpUtils httpUtils = new HttpUtils();
        /**
         * 下载文件 url：要下载的文件的url地址 target:下载的文件存放的位置 ：存放在 sdcard 的外部存储的私有缓存路径
         */
        // 路径： /mnt/sdcard/android/data/cn.itcast.phonesafe/cache
        File file = new File(context.getExternalCacheDir(), "safe.apk");
        final String target = file.getAbsolutePath();
        httpUtils.download(mUrl, target, new RequestCallBack<File>() {
            // 当网络访问成功，则回调
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                // 在主线程运行 ，已经把网络的文件输入流写入到指定的文件
                LogUtil.d(TAG, "onSuccess");
                // 关闭进度条
                mProgressDialog.dismiss();
                // 5.安装apk
                installApk(target);

            }

            // 当网络访问失败，则回调
            @Override
            public void onFailure(HttpException error, String msg) {
                mProgressDialog.dismiss();
                // 在主线程运行
                LogUtil.d(TAG, "onFailure");
                // 直接进入home界面
                load2Home();
            }

            /**
             * 正在下载时，回调 total: 下载的总的字节数 current：当前下载的字节数
             */
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                mProgressDialog.setMax(100);// 设置最大值
                mProgressDialog.setProgress((int) ((current / total) * 100));// 设置当前进度值
            }
        });

    }

    // 5. 安装apk，调用系统的安装器 来安装apk
    protected void installApk(String target) {
		/*
		 * <intent-filter> <action android:name="android.intent.action.VIEW" />
		 * <action android:name="android.intent.action.INSTALL_PACKAGE" />
		 * <category android:name="android.intent.category.DEFAULT" /> <data
		 * android:scheme="file" /> <data
		 * android:mimeType="application/vnd.android.package-archive" />
		 * </intent-filter>
		 */
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file:" + target),
                "application/vnd.android.package-archive");
        // startActivity(intent);
        startActivityForResult(intent, INSTALL_REQUEST_CODE);// 要求目标组件关闭后，返回结果

    }

    // 处理安装apk，返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (INSTALL_REQUEST_CODE == requestCode) {// 安装操作
            if (resultCode == RESULT_OK) {
                LogUtil.d(TAG, "安装成功");
            }
            if (resultCode == RESULT_CANCELED) {// 中断安装、取消安装
                LogUtil.d(TAG, "中断安装");
                load2Home();
            }
        }
    }

    private class CheckVersionTask implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "CheckVersionTask+run");
            LogUtil.d(TAG, "CheckVersionTask+run+util");
            // 采用HttpUrlConnection 或者HttpClient 访问网络
            // 采用HttpClient访问网络
            // 2.1 准备一个http客户端
            AndroidHttpClient httpClient = AndroidHttpClient
                    .newInstance("itcast");
            // 2.2 设置请求参数
            HttpParams params = httpClient.getParams();
            // 设置延迟时间
            HttpConnectionParams.setConnectionTimeout(params, 15000);// 默认15秒-30秒之间
            // 2.3 设置请求方式
            HttpGet httpGet = new HttpGet(Constants.VERSION_UPDATE_URL);
            // 保存访问网络的当前时间
            long currentTime = System.currentTimeMillis();
            Message message = Message.obtain();// 从消息池中取消息
            try {
                // 2.4 执行请求
                HttpResponse response = httpClient.execute(httpGet);

                // 2.5 取返回的结果 ,取得状态码
                int statusCode = response.getStatusLine().getStatusCode();
                if (200 == statusCode) {// 访问网络成功
                    // InputStream content = response.getEntity().getContent();
                    // 从服务端获取的结果
                    String result = EntityUtils.toString(response.getEntity(),
                            "utf-8");
                    // Log.d(TAG, "获取的结果："+result);
                    LogUtil.d(TAG, "获取的结果：" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    // 获取网络断的版本号
                    int netCode = jsonObject.getInt("versionCode");
                    if (netCode > PackageUtils.getVersionCode(context)) {
                        mDesc = jsonObject.getString("desc");
                        mUrl = jsonObject.getString("url");
                        // 显示更新版本的对话框
                        message.what = SHOW_UPDATE_DIALOG;
                        // handler.sendMessage(message);

                        // 测试Hanlder发送语句队列
						/*
						 * Runnable r=new Runnable() {
						 *
						 * @Override public void run() { //操作UI控件
						 * ,把语句封装打包发布到主线程语句队列中处理 System.out.println("run+post");
						 * mVersionNameTv.setTextColor(Color.RED); } };
						 * handler.post(r);
						 */
                    } else {
                        // 直接的进入Home界面
                        message.what = SHOW_NO_NEW_VERSION;
                        // handler.sendMessage(message);
                    }
                } else {
                    // 网络访问失败
                    message.what = SHOW_NET_ACCESS_FAILURE;
                    message.obj = "网络访问失败";
                }
            } catch (ClientProtocolException e) {
                // 客户端协议异常
                message.what = SHOW_ERROR;
                message.obj = "客户端协议异常";
                e.printStackTrace();
            } catch (IOException e) {
                // IO连接异常
                message.what = SHOW_ERROR;
                message.obj = "IO连接异常";
                e.printStackTrace();
            } catch (JSONException e) {
                // json 异常
                message.what = SHOW_ERROR;
                message.obj = "json 异常";
                e.printStackTrace();
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                    httpClient = null;
                }
                // 延迟时间
                long delay = 0;
                // 网络访问结束 ，
                if (System.currentTimeMillis() - currentTime >= DELAY_MILLIS) {
                    delay = 0;
                } else {
                    delay = DELAY_MILLIS
                            - (System.currentTimeMillis() - currentTime);
                }
                // 延迟发消息
                handler.sendMessageDelayed(message, delay);
            }
        }
    }
    // 进入HomeActivity
    private void load2Home() {
        Intent intent = new Intent();
        intent.setClass(context, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
