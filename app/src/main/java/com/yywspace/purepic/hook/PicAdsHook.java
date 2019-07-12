package com.yywspace.purepic.hook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import com.yywspace.purepic.setting.Setting;

import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class PicAdsHook implements IXposedHookLoadPackage {
    private static final String TAG = "PicAdsHook";
    private boolean hideComicListAd;
    private boolean hideComicPageAd;
    private boolean hideBannerAd;
    private boolean hidePopupAd;
    private boolean hideHomeBanners;

    public PicAdsHook() {
        Log.d(TAG, "PicAdsHook: 构造函数");
    }

    public void initPrefs() {
        Log.d(TAG, "initPrefs: ");
        Map<String, Boolean> prefs = Setting.getSetting();
        hideComicListAd = prefs.get("hide_comic_list_ad");
        hideComicPageAd = prefs.get("hide_comic_page_ad");
        hideBannerAd = prefs.get("hide_banner_ad");
        hidePopupAd = prefs.get("hide_popup_ad");
        hideHomeBanners = prefs.get("hide_home_banners");
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        //通过hook自己方法isModuleActive判断模块是否激活
        if (lpparam.packageName.equals("com.yywspace.purepic")) {
            XposedHelpers.findAndHookMethod("com.yywspace.purepic.setting.SettingActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
            Log.d(TAG, "handleLoadPackage: isModuleActive");
        }
        //开始hook应用
        if (lpparam.packageName.equals("com.picacomic.fregata")) {
            Log.d(TAG, "handleLoadPackage: picacg");
            initPrefs();
            // 去广告
            //hookToAvoidComicPageListAd(lpparam);
            if (hideComicListAd) {
                Log.d(TAG, "hideComicListAd");
                hookToAvoidComicListAd(lpparam);
            }
            if (hideComicPageAd) {
                Log.d(TAG, "hideComicPageAd");
                hookToVoidAdListViewHolder(lpparam);//替代hookToAvoidComicPageListAd
            }
            if (hideBannerAd) {
                Log.d(TAG, "hideBannerAd");
                hookToVoidBannerAd(lpparam);
            }
            if (hidePopupAd) {
                Log.d(TAG, "hidePopupAd");
                hookToVoidPopupAd(lpparam);
            }

        }
    }

    /**
     * hook ComicListRecyclerViewAdapter类中的getItemViewType方法
     * 改变ViewHolder的类型<br/>
     * 如果返回类型为2（ad）则设置其为1（default:被封印的。。）
     *
     * @param lpparam
     * @throws
     */
    private void hookToAvoidComicListAd(LoadPackageParam lpparam) throws Throwable {
        Class clazz = lpparam.classLoader.loadClass("com.picacomic.fregata.adapters.ComicListRecyclerViewAdapter");
        XposedHelpers.findAndHookMethod(clazz, "getItemCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "ComicListRecyclerViewAdapter.getItemCount: " + param.getResult());
            }
        });
        XposedHelpers.findAndHookMethod(clazz, "getItemViewType", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if ((int) param.getResult() == 2) {
                    Log.d(TAG, "Current comic type is 2(ad), change to 1(default)");
                    param.setResult(1);
                }
            }
        });
    }


    /**
     * hook ComicPageRecyclerViewAdapter类中的getItemViewType方法
     * 改变ViewHolder的类型<br/>
     * 如果返回类型为2（ad）则设置其为0（正常页面）<br/>
     * TODO 当前广告页面会被替换成重复的一张漫画，需要解决<br/>
     * <b>已经被hookToVoidAdListViewHolder取代<b/>
     *
     * @param lpparam
     * @throws
     */
    @Deprecated
    private void hookToAvoidComicPageListAd(LoadPackageParam lpparam) throws Throwable {
        Class clazz = lpparam.classLoader.loadClass("com.picacomic.fregata.adapters.ComicPageRecyclerViewAdapter");
        XposedHelpers.findAndHookMethod(clazz, "getItemViewType", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if ((int) param.getResult() == 2) {
                    Log.d(TAG, "Current page type is 2(ad), change to 0(normal)");
                    param.setResult(0);
                }
            }
        });
    }

    /**
     * hook BannerWebview类中的show&show(string)方法
     * 在此方法运行后，call其提供的hide方法将ad隐藏
     *
     * @param lpparam
     * @throws
     */
    private void hookToVoidBannerAd(LoadPackageParam lpparam) throws Exception {
        Class clazz = lpparam.classLoader.loadClass("com.picacomic.fregata.utils.BannerWebview");

        XposedHelpers.findAndHookMethod(clazz, "show", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "Banner ad is hided.");
                XposedHelpers.callMethod(param.thisObject, "hide");
                Log.d(TAG, "Banner ad is hidedaaa.");
            }
        });

        XposedHelpers.findAndHookMethod(clazz, "show", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "Banner ad is hided.");
                XposedHelpers.callMethod(param.thisObject, "hide");
                Log.d(TAG, "Banner ad is hidedaaa.");
            }
        });
    }

    /**
     * hook PopupWebview类中的show&show(string)方法
     * 在此方法运行后，call其提供的hide方法将ad隐藏
     *
     * @param lpparam
     * @throws
     */
    private void hookToVoidPopupAd(LoadPackageParam lpparam) throws Exception {
        Class clazz = lpparam.classLoader.loadClass("com.picacomic.fregata.utils.PopupWebview");
        XposedHelpers.findAndHookMethod(clazz, "show", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "Popup ad is hided.");
                XposedHelpers.callMethod(param.thisObject, "hide");
            }
        });

        XposedHelpers.findAndHookMethod(clazz, "show", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "Popup ad is hided.");
                XposedHelpers.callMethod(param.thisObject, "hide");
            }
        });
    }

    /**
     * set adviewholder invisible for comic page or comic list<br>
     * but in comic list it can't load other comic so must continue to use hookToAvoidComicListAd method
     *
     * @param lpparam
     */

    private void hookToVoidAdListViewHolder(LoadPackageParam lpparam) throws Exception {
        Class clazz = lpparam.classLoader.loadClass("com.picacomic.fregata.holders.AdvertisementListViewHolder");
        XposedHelpers.findAndHookConstructor(clazz, View.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "Set adviewholder to invisible.");
                View view = ((View) param.args[0]);
                view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                view.setVisibility(View.GONE);
            }
        });
    }

    //com.picacomic.fregata.utils.Tools 有一些ad显示方法和更新软件方法

    /**
     * hook HomeFragment类中onCreateView方法<br>
     * 去除home页面上方滑动广告
     *
     * @param lpparam
     * @throws Exception
     */
    private void hookToAvoidHomeBanners(LoadPackageParam lpparam) throws Exception {
        Class clazz = lpparam.classLoader.loadClass("com.picacomic.fregata.fragments.HomeFragment");
        XposedHelpers.findAndHookMethod(clazz, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @SuppressLint("ResourceType")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View homeView = (View) param.getResult();
                //根据ID获取viewPager_banner
                View viewPager_banner = homeView.findViewById(2131297031);
                //设置其父View(滑动广告栏)为空
                View vp = (View) viewPager_banner.getParent();
                vp.setVisibility(View.GONE);
            }
        });
    }
}
