package org.weex.plugin.alipayplugin;

import android.app.Activity;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import org.weex.plugin.utils.AliPayUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/25.
 * @author liumeng
 * 集成支付宝支付功能
 *
 * */

public class AliPayPluginModule extends WXModule {

    //MprivateKey:私钥
    //appid
    @JSMethod (uiThread = true)
    public void conifg(Map<String,Object> params){
        PayConstant.ALIPAY_APPID = params.get("appID").toString();
        PayConstant.ALIPAY_RSA_PRIVATE = params.get("MprivateKey").toString();
    }

    //orderID; //订单ID（由商家自行制定）
    //title; //商品标题
    //body; //商品描述
    //price; //商品价格
    //url; //回调URL
    @JSMethod (uiThread = true)
    public void alipay(Map<String,Object> params, JSCallback jsCallback){
        AliPayUtil.myCallback = jsCallback;
        AliPayUtil aliPayUtil = new AliPayUtil((Activity) mWXSDKInstance.getContext());
        aliPayUtil.pay(params);
    }

}
