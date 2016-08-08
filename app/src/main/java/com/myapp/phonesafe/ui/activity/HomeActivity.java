package com.myapp.phonesafe.ui.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.MD5;
import com.myapp.phonesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends Activity {
    private Context context;
    private final static String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
    private final static String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };
    private final static int[] ICONS = new int[] { R.drawable.sjfd,
            R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
            R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };

    private ImageView logoImageView;// logo图标控件
    private GridView mGridView;// 网格视图
    private List<Map<String, Object>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub的的
        super.onCreate(savedInstanceState);
        context=this;
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**
         * 注意事项： 1. 要写在setContentView 2. 假如要用ActionBar，则不能设置没有标题 不足：
         * 假如每个activity都标题，则都要写代码
         */
        setContentView(R.layout.activity_home);
        logoImageView = (ImageView) findViewById(R.id.home_logo_iv);
        mGridView = (GridView) findViewById(R.id.home_gv);
        // 1. 实现logo的旋转动画
        initAnimation();
        // 2.1 初始化网格视图的数据
        initGridViewData();

        //2.2 GridView 设置适配器 ，装配数据及展示数据   ，通过适配器来装配
        SimpleAdapter adapter=new SimpleAdapter(this, mData, R.layout.item_home_gird,
                new String[]{"icon","title","desc"},
                new int[]{R.id.home_icon_iv,R.id.home_title_tv,R.id.home_desc_tv});

        mGridView.setAdapter(adapter);

        //对网格功能项 设置点击监听事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**当点击网格项，则回调该方法
             *
             * @param parent ： GridView
             * @param view ：当前点击的网格项视图对象
             * @param position ： 当前点击的网格项在 网格视图中的索引号，位置，也是在装配到GridView的适配器中的位置
             * @param id ： 网格项数据的id属性值
             */
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                    switch (position) {
                        case 0://手机防盗
                            performPhoneDefind();
                            break;
                        case 1://骚扰拦截
                            performCallSmsSafe();
                            break;
                        case 2://软件管家
                            performAppManager();
                            break;
                        case 3://进程管理
                            performProcessManager();
                            break;
                        case 4://流量统计
                            performTraffic();
                            break;
                        case 5://手机杀毒
                            performVirus();
                            break;
                        case 6://缓存清理
                            ToastUtil.show(context, "缓存清理");
                            break;
                        case 7://常用工具
                            performCommonTools();

                            break;

                        default:
                            break;
                    }
            }
        });

    }
    //进入手机杀毒
    protected void performVirus() {
        Intent intent=new Intent(context, AntiVirusActivity.class);
        startActivity(intent);

    }
    //进入流量统计
    protected void performTraffic() {
        Intent intent=new Intent(context, TrafficActivity.class);
        startActivity(intent);
    }
    //进入进程管理
    protected void performProcessManager() {
        Intent intent=new Intent(context, ProcessManagerActivity.class);
        startActivity(intent);
    }
    //进入软件管家模块
    protected void performAppManager() {
        Intent intent=new Intent(context, AppManagerActivity.class);
        startActivity(intent);
    }

    // 点击 常用工具 网格项
    protected void performCommonTools() {
        Intent intent=new Intent(context, CommonToolsActivity.class);
        startActivity(intent);

    }
    // 点击 骚扰拦截 网格项
    private  void performCallSmsSafe() {
        Intent intent=new Intent(context, CallSmsSafeActivity.class);
        startActivity(intent);

    }

    // 点击 手机防盗 网格个
    private  void performPhoneDefind() {
        // 从配置文件中取密码信息，假如为空，则显示初始化密码对话框，否则显示验证密码对话框
        String pwd=CacheConfigUtils.getString(context,CacheConfigUtils.DEFIND_PASSWORD);
        if(TextUtils.isEmpty(pwd)){
            showInitDefindPwdDialog();
        }else{
            showArthDefindPwdDialog();
        }

    }

    //显示验证密码对话框
    private void showArthDefindPwdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.dialog_auth_defind_password, null);
        builder.setView(dialogView);
        final AlertDialog dialog=builder.create();

        // 通过父控件引用子控件
        final EditText authPwdEt=(EditText)dialogView.findViewById(R.id.dialog_auth_pwd_et);
        Button okBtn=(Button)dialogView.findViewById(R.id.dialog_ok_btn);
        Button cancelBtn=(Button)dialogView.findViewById(R.id.dialog_cancel_btn);

        //对确定按钮设置点击监听事件
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //取得输入的密码
                String authPwd=authPwdEt.getText().toString().trim();
                //密码为空情况
                if(TextUtils.isEmpty(authPwd)){
                    ToastUtil.show(context, "密码不能为空");
                    authPwdEt.requestFocus();//获取焦点
                    return;
                }

                //验证密码
                String pwd = CacheConfigUtils.getString(context, CacheConfigUtils.DEFIND_PASSWORD);
                if(MD5.getMD5(authPwd).equals(pwd)){
                    // todo :进入到手机防盗界面
                    enterPhoneDifend();
                    dialog.dismiss();

                }else{
                    ToastUtil.show(context, "验证不成功!");
                    authPwdEt.requestFocus();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //隐藏对话框
                dialog.dismiss();
            }
        });

        dialog.show();//显示对话框


    }

    // 显示初始化密码对话框
    private void showInitDefindPwdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.dialog_init_defind_password, null);
        builder.setView(dialogView);
        final AlertDialog dialog=builder.create();

        // 通过父控件引用子控件
        final EditText pwdEt=(EditText)dialogView.findViewById(R.id.dialog_pwd_et);
        final EditText confirmPwdEt=(EditText)dialogView.findViewById(R.id.dialog_pwd_confirm_et);
        Button okBtn=(Button)dialogView.findViewById(R.id.dialog_ok_btn);
        Button cancelBtn=(Button)dialogView.findViewById(R.id.dialog_cancel_btn);

        //对确定按钮设置点击监听事件
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //取得输入的密码
                String pwd=pwdEt.getText().toString().trim();
                String confirmPwd=confirmPwdEt.getText().toString().trim();
                //密码为空情况
                if(TextUtils.isEmpty(pwd)){
                    ToastUtil.show(context, "密码不能为空");
                    pwdEt.requestFocus();//获取焦点
                    return;
                }
                //确认密码为空的情况
                if(TextUtils.isEmpty(confirmPwd)){
                    ToastUtil.show(context, "确认密码不能为空");
                    confirmPwdEt.requestFocus();//获取焦点
                    return;
                }

                //密码与确认密码一致
                if(!pwd.equals(confirmPwd)){
                    ToastUtil.show(context, "两次输入密码不一致");
                    return;
                }
                //保存通过md5 加密后的密码
                String encrypPwd=MD5.getMD5(pwd);
                CacheConfigUtils.putString(context, CacheConfigUtils.DEFIND_PASSWORD, encrypPwd);

                // todo :进入到手机防盗界面
                enterPhoneDifend();
                dialog.dismiss();

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //隐藏对话框
                dialog.dismiss();
            }
        });

        dialog.show();//显示对话框

    }
    // 进入手机防盗界面
    protected void enterPhoneDifend() {
        //假如已经对手机防盗进行了设置 ，则进入手机防盗状态展示界面，否则进入设置界面
        //
        Intent intent=new Intent();
        if(CacheConfigUtils.getBoolean(context, CacheConfigUtils.DEFIND_IS_SETUP)){
            //DefindActivity  : 手机防盗状态展示的界面
            intent.setClass(context, DefindActivity.class);

        }else{
            //  进入设置向导1的Activity ： DefindSetup1Activity
            intent.setClass(context, DefindSetup1Activity.class);
            //进入用于嵌入片段的设置向导home页面
//			intent.setClass(context, DefindSetupHomeActivity.class);
        }
        startActivity(intent);

    }

    // 2. 初始化网格视图的数据
    private void initGridViewData() {
        mData = new ArrayList<Map<String,Object>>();
        for(int i=0;i<TITLES.length;i++){
            HashMap<String, Object> map=new HashMap<String, Object>();
            map.put("title", TITLES[i]);
            map.put("desc", DESCS[i]);
            map.put("icon", ICONS[i]);
            mData.add(map);
        }

    }

    // 1. 实现logo的旋转动画
    private void initAnimation() {
        // 1.1 设置哪个属性
        // logoImageView.setRotationY(rotationY);//绕着y轴旋转
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(logoImageView,
                "rotationY", 0, 90, 180, 270, 360);
        objectAnimator.setDuration(3000);// 一次动画的持续时间
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);// 无限旋转
        // objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator.start();

    }
    //进入到SettingActivity界面
    public void setting(View v) {
        Intent intent=new Intent(this, SettingActivity.class);
        startActivity(intent);
//		finish();

    }

}
