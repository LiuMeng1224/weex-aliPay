package org.weex.plugin.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.taobao.weex.bridge.JSCallback;

public class AliPayUtil {

	private Activity mActivity;

	public static JSCallback myCallback = null;

	/** 支付宝支付业务：入参app_id */
	public static final String APPID = "2016122604637996";

	/** 商户私钥，pkcs8格式 */
	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
	/** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
	//  支付宝私钥
	public static String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAK6oQtt7YV9q69gCq+qq+xEUPeuxnjmB8mGHBEYgyU+4+EL2wv37ljnLC1HSbbqLdik5WCnTGWYfrpAjKNSg84aQoSAtwO9lQhLaxn0MmSYUoAHppjUpU0Rldazwvn26y9vMJdTM6SO3FYl9oTFjofdoi9UjA75/roxXuQuCRjcLAgMBAAECgYEAi/zxGVnawc/BGHGLuZsxgI7o0nCakR1GWEUPd+XvdKac1Y9S2p872TaU7A/tQwdkvcXolB1Ozju2fnCv2Tzn8i9SoaOqW0220VX2mvYeaiYwGB4N3g472yHhL8ggon6wWM41Bc3FbT1aaxXo+nJWeC5LixK8+bO/eSv3KAM+UoECQQDin+KImRalmLJQkNXIaTEhaO7ywDDS6adT8e8PWTz1/8dmGTGdgxK/qB0IAwlb0ejn2MPup0JFOxI14mxhdTrPAkEAxUvwQsSCJ4RroNzBK7wUvhAEAHXngUB5Sa0jwGi10PKkWiEgP1RPzYQm5uY0r4mrcrkuiE3DzuAdXHnPHJMfBQJBAKTPW+K1WqfxZXR78E1AWu+OLYAyzw8Dx6Gf55hE8LWCX1Pb7J49TWw+aWaDNtIk1PmPReaQkvxgGj0/vkweAjcCQQDD2T4HCUqqL6knZfvbjjiI8x0F2ASXKYk8ucJjnY3efP7o+TkppGgTerujOaHTHrYyi+DFLWzST2bWBZSyqNiBAkBHfmhQAyy5ZuQ4v6EoMsf2EQiHzam7pZb9W/65x/z3lFum5ROQDUfcTkSNXBOEEohY4Vgk1gDHG9AB2N7V6w7r";
	public static final String RSA2_PRIVATE = "";


	public static final String SELLER = "zanshangtianxia@aliyun.com";
	public static String PARTNER = "2088421842165988";

	// 支付宝公钥
	// public static final String RSA_PUBLIC = "";
	private static final int SDK_PAY_FLAG = 1;

	public AliPayUtil(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					@SuppressWarnings("unchecked")
					PayResult payResult = new PayResult((Map<String, String>) msg.obj);
					/**
					 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
					 */
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();

					/**
					 * ret
					 {"status":"success","msg":"支付成功"}
					 status:成功success,失败:error
					 msg:信息描述
					 */
					if(myCallback!=null){
						Map<String, Object> map = new HashMap<>();
						map.put("status", payResult.getResultStatus());
						map.put("message", payResult.getResult());
						myCallback.invoke(map);
					}

					// 判断resultStatus 为9000则代表支付成功
					/*if (TextUtils.equals(resultStatus, "9000")) {
						// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
						Toast.makeText(mActivity, "支付成功", Toast.LENGTH_SHORT).show();
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。

						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(mActivity, "支付结果确认中", Toast.LENGTH_SHORT).show();

						} else if (TextUtils.equals(resultStatus, "4000")) {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(mActivity, "订单支付失败", Toast.LENGTH_SHORT).show();


						} else if (TextUtils.equals(resultStatus, "6001")) {
							Toast.makeText(mActivity, "用户途中取消", Toast.LENGTH_SHORT)
									.show();

						} else if (TextUtils.equals(resultStatus, "6002")) {
							Toast.makeText(mActivity, "网络连接取消", Toast.LENGTH_SHORT)
									.show();
						}
					}*/
					break;
				}

			default:
				break;
			}
		};
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(Map<String,Object> orderParams) {

		String total_amount = orderParams.get("price").toString();
		String goodTitle = orderParams.get("title").toString();
		String goodDetail = orderParams.get("body").toString();
		String orderID = orderParams.get("orderID").toString();
		String notify_url = orderParams.get("url").toString();

		boolean rsa2 = (RSA2_PRIVATE.length() > 0);

		/**
		 * timeout_express 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，
		 * 1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
		 * 注：若为空，则默认为15d。
		 * product_code 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
		 * total_amount 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		 * subject 商品的标题/交易标题/订单标题/订单关键字等。
		 * body 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body
		 * out_trade_no 商户网站唯一订单号
		 *
		 */
		String biz_content = "{\"timeout_express\":\"30m\"," +
				"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
				"\"total_amount\":\""+total_amount+"\",\"subject\":\""+goodTitle+"\"," +
				"\"body\":\""+goodDetail+"\",\"out_trade_no\":\""
				+ orderID + "\"}";

		final Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2,biz_content,notify_url);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String orderInfo = orderParam + "&" + sign;

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(mActivity);
				Map<String, String> result = alipay.payV2(orderInfo,true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
}
