package com.yywspace.purepic.hook;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yywspace.purepic.setting.Setting;

import java.util.Map;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class PicLayoutHook implements IXposedHookInitPackageResources {
    private static final String TAG = "PicLayoutHook";
    private boolean hideHomeBanners;

    public void initPrefs() {
        Log.d(TAG, "initPrefs: 初始化配置");
        Map<String, Boolean> prefs = Setting.getSetting();
        hideHomeBanners = prefs.get("hide_home_banners");

    }

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.picacomic.fregata"))
            return;
        Log.d(TAG, "handleInitPackageResources: 加载资源 ");
        //initPrefs();
        //if (hideHomeBanners)
        hookToRemoveHomeBanner(resparam);
        hookToRemoveCategoryTagList(resparam);

    }

    private void hookToRemoveHomeBanner(InitPackageResourcesParam resparam) {
        resparam.res.hookLayout("com.picacomic.fregata", "layout", "fragment_home", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                Log.d(TAG, "hookToRemoveHomeBanner: 移除主页面上方滑动栏");

                ViewPager view = liparam.view.findViewById(liparam.res.getIdentifier(
                        "viewPager_home_banner",
                        "id",
                        "com.picacomic.fregata"));

                View parentView = (View) view.getParent();
                parentView.setVisibility(View.GONE);
            }
        });
        Log.d(TAG, "hookToRemoveHomeBanner: 移除主页面上方滑动栏asdfasdf");
    }

    private void hookToRemoveCategoryTagList(InitPackageResourcesParam resparam) {
        //Log.d(TAG, "hookToRemoveCategoryTagList: 移除目录上方Tag栏");
        resparam.res.hookLayout("com.picacomic.fregata", "layout", "fragment_category", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                Log.d(TAG, "hookToRemoveCategoryTagList: 移除目录上方Tag栏");
                RecyclerView category_keywords_view = liparam.view.findViewById(liparam.res.getIdentifier(
                        "recyclerView_category",
                        "id",
                        "com.picacomic.fregata"));

                View parentView = (View) category_keywords_view.getParent();
                parentView.setVisibility(View.GONE);
            }
        });
        Log.d(TAG, "hookToRemoveCategoryTagList: 移除目录上方Tag栏asdfasdf");
    }
}
