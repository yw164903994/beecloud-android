## BeeCloud Android SDK (Open Source)

[![Build Status](https://travis-ci.org/beecloud/beecloud-android.svg)](https://travis-ci.org/beecloud/beecloud-android) ![license](https://img.shields.io/badge/license-MIT-brightgreen.svg) ![version](https://img.shields.io/badge/version-v2.2.0-blue.svg)

## 简介

本项目的官方GitHub地址是 [https://github.com/beecloud/beecloud-android](https://github.com/beecloud/beecloud-android)

本SDK是根据[BeeCloud Rest API](https://github.com/beecloud/beecloud-rest-api) 开发的 Android SDK。目前已经包含微信支付、支付宝支付、银联在线支付、百度钱包支付、PayPal支付和生成二维码方式支付，以及支付订单和退款订单的查询功能，可以作为调用BeeCloud Rest API的示例或者直接用于生产。

## 流程

下图为整个支付的流程:
![pic](http://7xavqo.com1.z0.glb.clouddn.com/UML.png)

其中需要开发者开发的只有：

步骤1：**（在App端）发送支付要素**

做完这一步之后就会跳到相应的支付页面（如微信app中），让用户继续后续的支付步骤

步骤5：**（在App端）处理同步回调结果**

付款完成或取消之后，会回到客户app中，需要做相应界面展示的更新（比如弹出框告诉用户"支付成功"或"支付失败")。非常不推荐用同步回调的结果来作为最终的支付结果，因为同步回调可能（虽然可能性不大）出现结果不准确的情况，最终支付结果应以下面的异步回调为准。

步骤7：**（在客户服务端）处理异步回调结果（[Webhook](https://beecloud.cn/doc/?index=8)）**
 
付款完成之后，根据客户在BeeCloud后台的设置，BeeCloud会向客户服务端发送一个Webhook请求，里面包括了数字签名，订单号，订单金额等一系列信息。客户需要在服务端依据规则要验证**数字签名是否正确，购买的产品与订单金额是否匹配，这两个验证缺一不可**。验证结束后即可开始走支付完成后的逻辑。

## 安装
1. 添加依赖<br/>

>1. 推荐通过添加`model`的方式（适用于`gradle`，推荐直接使用`Android Studio`）
引入`sdk model`，在`project`的`settings.gradle`中`include ':sdk'`，并在需要支付的`model`（比如本项目中的`demo`） `build.gradle`中添加依赖`compile project(':sdk')`，需要JDK版本7或以上。

>2. 对于需要以`jar`方式引入的情况<br/>
添加第三方的支付类，在`beecloud-android\demo_eclipse\libs`目录下<br/>
`gson-2.4.jar`为必须引入的jar，<br/>
`zxing-3.2.0.jar`为生成二维码必须引入的jar，<br/>
微信支付需要引入`libammsdk.jar`，<br/>
支付宝需要引入`alipaysdk.jar`、`alipayutdid.jar`、`alipaysecsdk.jar`，<br/>
银联需要引入`UPPayAssistEx.jar`，<br/>
百度钱包支付需要引入`Cashier_SDK-v4.2.0.jar`，<br/>
最后添加`beecloud android sdk`：`beecloud-2.1.3.jar`

2.对于微信支付，需要注意你的`AndroidManifest.xml`中`package`需要和微信平台创建的移动应用`应用包名`保持一致，关于其`应用签名`请参阅[创建微信应用->B.填写平台信息](https://beecloud.cn/doc/payapply/?index=0)

3.对于银联支付需要将银联插件`beecloud-android\demo\src\main\assets\UPPayPluginEx.apk`引入你的工程`assets`目录下

4.对于百度钱包支付，需要
>1. 将`beecloud-android\sdk\manualres\baidupay\res`添加到你的`res`目录下；
>2. 另外，对于使用`Android Studio`的用户，需要将`beecloud-android\sdk\manualres\baidupay\`目录下的`armeabi`文件夹拷贝到`src\main\jniLibs`目录下，如果没有`jniLibs`目录，请手动创建；对用使用`Eclipse`的用户，需要将`beecloud-android\sdk\manualres\baidupay\`目录下的`armeabi`文件夹拷贝到`libs`目录下。  
  
5.对于需要使用PayPal的用户，由于PayPal官方不再提供单独的jar文件，请通过添加model的方式引入依赖。

## 注册
1. 注册开发者：猛击[这里](http://www.beecloud.cn/register)注册成为BeeCloud开发者。  
2. 注册应用：使用注册的账号登陆[控制台](http://www.beecloud.cn/dashboard/)后，点击"+创建App"创建新应用，并配置支付参数。  

## 使用方法
>具体使用请参考项目中的`demo`

### 1.初始化支付参数
请参考`demo`中的`ShoppingCartActivity.java`
>1. 在主activity或者application的onCreate函数中初始化BeeCloud账户中的AppID和secret，并设置是否开启测试模式（如果不设置默认不开启）；注意：如果是测试模式，secret应该填Test Secret，如果是上线版本，应该填App Secret，例如
```java
//开启测试模式
BeeCloud.setSandbox(true);
//此处第二个参数是控制台的test secret
BeeCloud.setAppIdAndSecret("c5d1cba1-5e3f-4ba0-941d-9b0a371fe719",
        "4bfdd244-574d-4bf3-b034-0c751ed34fee");
```
<br/>
>2. 如果用到微信支付，在用到微信支付的Activity的onCreate函数里调用以下函数，第二个参数需要换成你自己的微信AppID，例如
```java
BCPay.initWechatPay(ShoppingCartActivity.this, "wxf1aa465362b4c8f1");
```
<br/>
>3. 如果用到PayPal，在用到PayPal的Activity的onCreate函数里调用函数，例如
```java
BCPay.initPayPal(
    //在PayPal官网申请的APP Client ID
    "AVT1Ch18aTIlUJIeeCxvC7ZKQYHczGwiWm8jOwhrREc4a5FnbdwlqEB4evlHPXXUA67RAAZqZM0H8TCR", 
    //在PayPal官网申请的APP Secret   
    "EL-fkjkEUyxrwZAmrfn46awFXlX-h2nRkyCVhhpeVdlSRuhPJKXx3ZvUTTJqPQuAeomXA8PZ2MkX24vF",  
    //测试过程中使用BCPay.PAYPAL_PAY_TYPE.SANDBOX，生产环境使用BCPay.PAYPAL_PAY_TYPE.LIVE，不同的环境需要与Client ID和Secret相匹配
    BCPay.PAYPAL_PAY_TYPE.SANDBOX,  
    //是否显示收货地址，如果为TRUE，用户地址没有正确配置可能导致不能付款，该选项可以自行考量
    Boolean.FALSE
    );
```

### 2. 在`AndroidManifest.xml`中添加`permission`
```java
<!-- for all -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- for Baidu -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
<uses-permission android:name="android.permission.READ_SMS" />
```

### 3. 在`AndroidManifest.xml`中注册`activity`
> 如果开启测试模式，需要添加（上线版本不需要）
```java
<activity
    android:name="cn.beecloud.BCMockPayActivity"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```
> 对于微信支付，需要添加
```java
<activity
    android:name="cn.beecloud.BCWechatPaymentActivity"
    android:launchMode="singleTop"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```
```java
<activity-alias
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:targetActivity="cn.beecloud.BCWechatPaymentActivity" />
```
> 对于支付宝，需要添加
```java
<activity
    android:name="com.alipay.sdk.app.H5PayActivity"
    android:configChanges="orientation|keyboardHidden|navigation"
    android:exported="false"
    android:screenOrientation="behind"
    android:windowSoftInputMode="adjustResize|stateHidden" />
```
> 对于银联，需要添加
```java
<activity
    android:name="cn.beecloud.BCUnionPaymentActivity"
    android:configChanges="orientation|keyboardHidden"
    android:excludeFromRecents="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:windowSoftInputMode="adjustResize" />
```
> 对于PayPal，需要添加
```java
<activity
    android:name="cn.beecloud.BCPayPalPaymentActivity"
    android:configChanges="orientation|keyboardHidden"
    android:excludeFromRecents="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:windowSoftInputMode="adjustResize" />
```
> 对于百度钱包，由于需要添加的activity数量众多，请参考demo中的AndroidManifest.xml

### 4.支付

请查看`doc`中的`API`，支付类`BCPay`，参照`demo`中`ShoppingCartActivity`

**原型：** 
 
通过`BCPay`的实例，以`reqWXPaymentAsync`方法发起微信支付请求。 <br/>
通过`BCPay`的实例，以`reqAliPaymentAsync`方法发起支付宝支付请求。<br/>
通过`BCPay`的实例，以`reqUnionPaymentAsync`方法发起银联支付请求。<br/>
通过`BCPay`的实例，以`reqBaiduPaymentAsync`方法发起百度钱包支付请求。<br/>
通过`BCPay`的实例，以`reqPayPalPaymentAsync`方法发起PayPal支付请求。<br/>

参数依次为
> billTitle       商品描述, 32个字节内, 汉字以2个字节计<br/>
> billTotalFee    支付金额，以分为单位，必须是正整数<br/>
> billNum         商户自定义订单号，PayPal不需要该参数<br/>
> optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑<br/>
> callback        支付完成后的回调入口

或者，通过`BCPay`的实例，以`reqPaymentAsync`方法发起所有支持的支付请求，该方法的调用请参考demo中百度钱包的支付调用，BCPay.PayParams参数请参阅[API](https://beecloud.cn/doc/api/beecloud-android/cn/beecloud/BCPay.PayParams.html)。<br/>
参数依次为
> payParam        BCPay.PayParams类型<br/>
> callback        支付完成后的回调入口

在回调函数中将`BCResult`转化成`BCPayResult`之后做后续处理<br/>
**调用：（以微信为例）**

```java
//定义回调
BCCallback bcCallback = new BCCallback() {
    @Override
    public void done(final BCResult bcResult) {
        //此处根据业务需要处理支付结果
        final BCPayResult bcPayResult = (BCPayResult)bcResult;

        ShoppingCartActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	//对于JRE6的用户请参考demo中使用if else判断
                switch (bcPayResult.getResult()) {
                    case BCPayResult.RESULT_SUCCESS:
                        Toast.makeText(ShoppingCartActivity.this, "用户支付成功", Toast.LENGTH_LONG).show();
                        break;
                    case BCPayResult.RESULT_CANCEL:
                        Toast.makeText(ShoppingCartActivity.this, "用户取消支付", Toast.LENGTH_LONG).show();
                        break;
                    case BCPayResult.RESULT_FAIL:
                        Toast.makeText(ShoppingCartActivity.this, "支付失败, 原因: " + bcPayResult.getErrMsg()
                                + ", " + bcPayResult.getDetailInfo(), Toast.LENGTH_LONG).show();
                }
                
                if (bcPayResult.getId() != null) {
                    //你可以把这个id存到你的订单中，下次直接通过这个id查询订单
                    Log.w(TAG, "bill id retrieved : " + bcPayResult.getId());

                    //根据ID查询，详细请查看demo
                    getBillInfoByID(bcPayResult.getId());
                }
            }
        });
    }
};

//调用支付接口
Map<String, String> mapOptional = new HashMap<>();
String optionalKey = "testkey1";
String optionalValue = "测试value值1";

mapOptional.put(optionalKey, optionalValue);

//发起支付
BCPay.getInstance(ShoppingCartActivity.this).reqWXPaymentAsync(
    "微信支付测试",               //订单标题
    1,                           //订单金额(分)
    billNum,  //订单流水号
    mapOptional,            //扩展参数(可以null)
    bcCallback);            //支付完成后回调入口
```
##### 对于PayPal支付的补充说明
PayPal回调返回的成功表示手机支付已经完成，但是订单还没有同步到BeeCloud服务器，由于同步过程中sdk需要先向PayPal服务器请求token，该请求过程失败几率相对比较大，所以可能需要多次请求同步，如果依然失败，请保留订单数据用于下次同步，同步接口为`syncPayPalPayment`，例如：
```java
//如果是PayPal，手机端支付完成后还需要向BeeCloud服务器发送同步请求，并校验支付结果
if (isPayPal) {
    //如果是PayPal，detail info里面包含订单的json字符串
    final String syncStr = bcPayResult.getDetailInfo();
    isPayPal = false;
    Log.i(TAG, "start to sync PayPal result to BeeCloud server...");

    loadingDialog.show();
    //由于同步过程中需要向PayPal服务器请求token，请求失败的几率比较高，此处设置了三次循环
    BCCache.executorService.execute(new Runnable() {
        @Override
        public void run() {
            int i = 0;
            BCPayResult syncResult;
            for (; i < 3; i++) {
                Log.i(TAG, String.format("sync for %d time(s)", i+1));
                syncResult = BCPay.getInstance(ShoppingCartActivity.this).syncPayPalPayment(syncStr);

                if (syncResult.getResult().equals(BCPayResult.RESULT_SUCCESS)) {
                    Log.i(TAG, "sync succ!!!");
                    Log.d(TAG, "this bill id can be stored for query by id: " + syncResult.getId());
                    break;
                } else {
                    Log.e(TAG, "sync fail reason: " + syncResult.getDetailInfo());
                }
            }

            loadingDialog.dismiss();

            //注意，如果一直失败，你需要将该json串保留起来，下次继续同步，否者在你在BeeCloud控制台看不到这笔订单
            if (i == 3) {
                Log.e(TAG, "BAD result!!! Sync failed for three times!!!");
                Log.w(TAG, "please store the json string to somewhere for later sync: " + syncStr);
            }
        }
    });
}
```

### 5.线下支付
请查看`doc`中的`API`，线下支付类`BCOfflinePay`，参照`demo`中`QRCodeEntryActivity`和其关联的activity；一般用于线下门店通过出示二维码由用户扫描付款，或者通过用户出示的付款码收款。<br/><br/>
线下支付基本流程：
> 1 通过二维码或者付款码发起支付；<br/>
> 2 支付结束后调用查询接口确认`BCQuery.getInstance().queryOfflineBillStatusAsync`,请参考查询部分说明。<br/>
第二步必不可少。

**原型：** 
 
通过`BCOfflinePay`的实例，以`reqQRCodeAsync`方法请求生成支付二维码。 <br/>
通过`BCOfflinePay`的实例，以`reqOfflinePayAsync`方法通过获取到的付款码发起收款。<br/>

公用参数依次为
> channelType     BCChannelTypes类型，二维码支持WX_NATIVE，ALI_OFFLINE_QRCODE，扫码支付支持WX_SCAN, ALI_SCAN<br/>
> billTitle       商品描述, 32个字节内, 汉字以2个字节计<br/>
> billTotalFee    支付金额，以分为单位，必须是正整数<br/>
> billNum         商户自定义订单号<br/>
> optional        为扩展参数，可以传入任意数量的key/value对来补充对业务逻辑<br/>
> callback        支付完成后的回调入口

请求生成支付二维码的额外参数
> genQRCode       是否生成QRCode Bitmap
>>如果为false，请自行根据getQrCodeRawContent返回的URL，使用BCPay.generateBitmap方法生成支付二维码，你也可以使用自己熟悉的二维码生成工具

> qrCodeWidth     如果生成二维码(genQRCode为true), QRCode的宽度(以px为单位), null则使用默认参数360px

请求通过获取到的付款码发起收款的额外参数
> authCode        用户出示的收款码<br/>
> terminalId      机具终端编号，支付宝扫码(ALI_SCAN)的选填参数<br/>
> storeId         商户门店编号，支付宝扫码(ALI_SCAN)的选填参数

对于生成二维码的请求，在回调函数中将`BCResult`转化成`BCQRCodeResult`之后做后续处理<br/>
**调用：**
```java
BCOfflinePay.getInstance().reqQRCodeAsync(
        channelType,		//渠道类型
        billTitle,  		//商品描述
        1,          		//总金额, 以分为单位, 必须是正整数
        billNum,          	//流水号
        optional,           //扩展参数
        true,               //是否生成二维码的bitmap
        380,                //二维码的尺寸, 以px为单位, 如果为null则默认为360
        new BCCallback() {
            @Override
            public void done(BCResult bcResult) {

                final BCQRCodeResult bcqrCodeResult = (BCQRCodeResult) bcResult;

                //resultCode为0表示请求成功
                if (bcqrCodeResult.getResultCode() == 0) {
                    //如果你设置了生成二维码参数为true那么此处可以获取二维码
                    qrCodeBitMap = bcqrCodeResult.getQrCodeBitmap();

                    //否则通过 bcqrCodeResult.getQrCodeRawContent() 获取二维码的内容，自己去生成对应的二维码

                } else {
                    errMsg = "err code:" + bcqrCodeResult.getResultCode() +
                            "; err msg: " + bcqrCodeResult.getResultMsg() +
                            "; err detail: " + bcqrCodeResult.getErrDetail();
                    //work with err msg
                }
            }
        });
```

对于通过用户出示的付款码收款，在回调函数中将`BCResult`转化成`BCPayResult`之后做后续处理<br/>
**调用：**
```java
BCOfflinePay.getInstance().reqOfflinePayAsync(
        channelType,
        billTitle, //商品描述
        1,                 			//总金额, 以分为单位, 必须是正整数
        billNum,          			//流水号
        optional,            		//扩展参数
        authCode,           		//付款码
        "fake-terminalId",  		//若机具商接入terminalId(机具终端编号)必填
        null,               		//若系统商接入，storeId(商户门店编号)必填
        new BCCallback() {     	//回调入口
            @Override
            public void done(BCResult bcResult) {

                final BCPayResult payResult = (BCPayResult) bcResult;

                //RESULT_SUCCESS表示请求成功，任然需要查询支付结果
                if (payResult.getResult().equals(BCPayResult.RESULT_SUCCESS)) {
                	//TODO
                } else {

                    errMsg = "支付失败，请重试；错误信息：" +
                            "err code:" + payResult.getResult() +
                            "; err msg: " + payResult.getErrMsg() +
                            "; err detail: " + payResult.getDetailInfo();
                }
            }
        });
```

* **关于订单的撤销**
<br/>
支持WX_SCAN, ALI_OFFLINE_QRCODE, ALI_SCAN <br/>
订单撤销后，用户将不能继续支付，这和退款是不同的操作，具体请参考`GenQRCodeActivity`
```java
BCOfflinePay.getInstance().reqRevertBillAsync(
    channelType,
    billNum,        //需要撤销的订单号
    callback)
```

* **关于支付宝内嵌二维码**
<br/>
支付宝内嵌二维码属于线上产品，支付结果会及时反馈，并不需要额外的查询操作，具体可以参考`QRCodeEntryActivity`和`ALIQRCodeActivity`，注意需要通过`BCPay`调用
```java
BCPay.getInstance(QRCodeEntryActivity.this).reqAliInlineQRCodeAsync(
		"支付宝内嵌二维码支付测试",   	//商品描述
        1,                            	//总金额, 以分为单位, 必须是正整数
        billNum,      	                //流水号
        mapOptional,                    //扩展参数
        "https://beecloud.cn/",  		//returnUrl，支付成功之后的返回url
        "1",                          	//qrPayMode，二维码类型
        callback);
```
请求生成支付宝内嵌支付二维码的特有参数说明
> returnUrl       支付成功后的同步跳转页面, 必填<br/>
> qrPayMode       支付宝内嵌二维码类型
>>>可选项<br/>
   "0": 订单码-简约前置模式, 对应 iframe 宽度不能小于 600px, 高度不能小于 300px<br/>
   "1": 订单码-前置模式, 对应 iframe 宽度不能小于 300px, 高度不能小于 600px<br/>
   "3": 订单码-迷你前置模式, 对应 iframe 宽度不能小于 75px, 高度不能小于 75px<br/>
   
### 6.查询

* **查询支付订单**

请查看`doc`中的`API`，查询类`BCQuery`，参照`demo`中`BillListActivity`

**原型：**

通过构造`BCQuery`的实例，使用`queryBillsAsync`方法发起支付查询，`channel`指代何种支付方式，为`BCReqParams.BCChannelTypes.ALL`时则查询所有的支付渠道订单；在回调函数中将`BCResult`转化成`BCQueryBillsResult`之后做后续处理

**调用：**

```java
//回调入口
final BCCallback bcCallback = new BCCallback() {
    @Override
    public void done(BCResult bcResult) {
    	//根据需求处理结果数据

        final BCQueryBillsResult bcQueryResult = (BCQueryBillsResult) bcResult;

        //resultCode为0表示请求成功
        //count包含返回的订单个数
        if (bcQueryResult.getResultCode() == 0) {

			//订单列表
	        bills = bcQueryResult.getBills();
            Log.i(BillListActivity.TAG, "bill count: " + bcQueryResult.getCount());
        } else {
            bills = null;
            BillListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //错误信息
                    Toast.makeText(BillListActivity.this, "err code:" + bcQueryResult.getResultCode() +
                            "; err msg: " + bcQueryResult.getResultMsg() +
                            "; err detail: " + bcQueryResult.getErrDetail(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
};

//发起查询请求
BCQuery.getInstance().queryBillsAsync(
    BCReqParams.BCChannelTypes.UN_APP,      //渠道
    null,                                   //订单号
    startTime.getTime(),                    //订单生成时间
    endTime.getTime(),                      //订单完成时间
    2,                                      //跳过满足条件的前2条数据
    15,                                     //返回满足条件的15条数据
    bcCallback);
```

* **查询退款订单**

请查看`doc`中的`API`，查询类`BCQuery`，参照`demo`中`RefundOrdersActivity`

**原型：**

通过构造`BCQuery`的实例，使用`queryRefundsAsync`方法发起退款查询，`channel`指代何种支付方式，为`BCReqParams.BCChannelTypes.ALL`时则查询所有的支付渠道退款订单；在回调函数中将`BCResult`转化成`BCQueryRefundsResult`之后做后续处理

**调用：**<br/>
同上，首先初始化回调入口BCCallback
```java
BCQuery.getInstance().queryRefundsAsync(
    BCReqParams.BCChannelTypes.UN,          //渠道
    null,                                   //订单号
    null,                                   //商户退款流水号
    startTime.getTime(),                    //退款订单生成时间
    endTime.getTime(),                      //退款订单完成时间
    1,                                      //跳过满足条件的前1条数据
    15,                                     //返回满足条件的15条数据
    bcCallback);
```

* **查询订单数目**

请查看`doc`中的`API`，查询类`BCQuery`，参照`demo`中`BillListActivity`和`RefundOrdersActivity`

**原型：**

通过构造`BCQuery`的实例，使用`queryBillsCountAsync`方法发起支付订单数目查询，使用`queryRefundsCountAsync`方法发起退款订单数目查询；在回调函数中将`BCResult`转化成`BCQueryCountResult`之后做后续处理

**调用：**<br/>
以查询支付订单数目为例
```java
BCQuery.QueryParams params = new BCQuery.QueryParams();

//以下为可用的限制参数
//渠道类型
params.channel = BCReqParams.BCChannelTypes.ALL;

//支付单号
//params.billNum = "your bill number";

//订单是否支付成功
params.payResult = Boolean.TRUE;

//限制起始时间
params.startTime = startTime.getTime();

//限制结束时间
params.endTime = endTime.getTime();

BCQuery.getInstance().queryBillsCountAsync(params, new BCCallback() {
    @Override
    public void done(BCResult result) {
        
        final BCQueryCountResult countResult = (BCQueryCountResult) result;

        if (countResult.getResultCode() == 0) {
            //TODO with
            countResult.getCount();
        }
    }
});
```

* **查询订单退款状态**

请查看`doc`中的`API`，查询类`BCQuery`，参照`demo`中`RefundStatusActivity`

**原型：**

通过构造`BCQuery`的实例，使用`queryRefundStatusAsync`方法发起支付查询，该方法所有参数都必填，`channel`指代何种支付方式，目前由于第三方API的限制仅支持微信、易宝、快钱和百度；在回调函数中将`BCResult`转化成`BCRefundStatus`之后做后续处理

**调用：**<br/>
同上，首先初始化回调入口BCCallback
```java
BCQuery.getInstance().queryRefundStatusAsync(
    BCReqParams.BCChannelTypes.WX,     //目前仅支持WX、YEE、KUAIQIAN、BD
    "20150520refund001",                   //退款单号
    bcCallback);                           //回调入口
```

* **根据ID查询订单**

请查看`doc`中的`API`，查询类`BCQuery`，注意此处的ID不是订单号，是在发起支付或者退款的时候返回的唯一标识符。

**原型：**

通过`BCQuery`的实例，以`queryBillByIDAsync`方法发起支付订单查询，查询结果转化成`BCQueryBillResult`做后续处理，请参照`demo`中`ShoppingCartActivity`。 <br/>
通过`BCQuery`的实例，以`queryRefundByIDAsync`方法发起退款订单查询，查询结果转化成`BCQueryRefundResult`做后续处理，请参照`demo`中`RefundOrdersActivity`。<br/>

**调用：**<br/>
同上，首先初始化回调入口BCCallback
```java
BCQuery.getInstance().queryBillByIDAsync(
                id,
                new BCCallback(){...});
```

* **查询线下订单状态**

请查看`doc`中的`API`，查询类`BCQuery`，参照`demo`中`GenQRCodeActivity`

**原型：**

通过`BCQuery`的实例，以`queryOfflineBillStatusAsync`方法发起支付订单查询，查询结果转化成`BCBillStatus`做后续处理，请参照`demo`中`ShoppingCartActivity`。 <br/>

**调用：**<br/>
同上，首先初始化回调入口BCCallback
```java
BCQuery.getInstance().queryOfflineBillStatusAsync(
        channelType,		//渠道类型
        billNum,			//支付订单号
        new BCCallback() {
            @Override
            public void done(BCResult result) {

                BCBillStatus billStatus = (BCBillStatus) result;

                //表示支付成功
                if (billStatus.getResultCode() == 0 &&
                        billStatus.getPayResult()) {
                    //TODO
                } else {
                    errMsg = "支付失败：" + billStatus.getResultCode() + " # " +
                                    billStatus.getResultMsg() + " # " +
                                    billStatus.getErrDetail();
                }
            }
        }
);
```

## Demo
考虑到个人的开发习惯，本项目提供了`Android Studio`和`Eclipse ADT`两种工程的`demo`，为了使demo顺利运行，请注意以下细节
>1. 对于使用`Android Studio`的开发人员，下载源码后可以将`demo_eclipse`移除，`Import Project`的时候选择`beecloud-android`，`sdk`为`demo`的依赖`model`，`gradle`会自动关联。
>2. 对于使用`Eclipse ADT`的开发人员，`Import Project`的时候选择`beecloud-android`下的`demo_eclipse`，该`demo`下面已经添加所有需要的`jar`。

## ProGuard
请根据自己引进的jar做增删
```
#第三方库的申明，注意在Android Studio中不需要
#BeeCloud及依赖jar
-libraryjars libs/beecloud-x.x.x.jar
-libraryjars libs/gson-2.4.jar
-libraryjars libs/zxing-3.2.0.jar
#支付宝
-libraryjars libs/alipaysdk.jar
-libraryjars libs/alipaysecsdk.jar
-libraryjars libs/alipayutdid.jar
#微信
-libraryjars libs/libammsdk.jar
#银联
-libraryjars libs/UPPayAssistEx.jar
#百度
-libraryjars libs/Cashier_SDK-v4.2.0.jar

#以下是Android Studio和Eclipse都必须的
#BeeCloud
-dontwarn cn.beecloud.**

#保留类签名声明
-keepattributes Signature
#BeeCloud
-keep class cn.beecloud.** { *; }
-keep class com.google.** { *; }
#支付宝
-keep class com.alipay.** { *; } 
#微信
-keep class com.tencent.** { *; } 
#银联
-keep class com.unionpay.** { *; } 
#百度
-keep class com.baidu.** { *; }
-keep class com.dianxinos.** { *; }

#Android Studio中包含PayPal依赖，需要添加
-dontwarn com.paypal.**
-dontwarn io.card.payment.**
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class com.paypal.** { *; }
-keep class io.card.payment.** { *; }

-keep interface okhttp3.** { *; }
-keep interface okio.** { *; }

-keep class okhttp3.** { *; }
-keep class okio.** { *; }
```

## 常见问题
* 微信支付返回`一般错误`，可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等，请按如下方法依次排查<br/>

>1. 项目包名与在微信开放平台填写的包名是否一致
>2. 项目签名与微信开放平台填写的应用签名是否一致；获取项目签名的方法：到微信官网下载[签名工具](https://open.weixin.qq.com/zh_CN/htmledition/res/dev/download/sdk/Gen_Signature_Android.apk)，安装到手机后按提示操作
>3. 订单流水号是否包含横杠`-`，如果有请去除
>4. 请尝试清除微信数据（设置->应用程序管理->找到微信，点击进入应用程序信息-清除数据），或者删除微信重新安装再试
>5. 如果所有检查没问题，尝试换一个手机测试，如果测试没问题，该错误可在正式发布后消除，而不需要用户清除微信数据

* demo中支付宝无法支付，提示缺少参数类似错误信息：  
由于支付宝对企业账号监控严格，故不再提供支付宝支付的测试功能，请在BeeCloud平台配置正确参数后，使用自行创建的APP的appID和appSecret。给您带来的不便，敬请谅解。

* APP_INVALID, 根据app_id找不到对应的APP/keyspace或者app_sign不正确,或者timestamp不是当前UTC：<br/>
一般是测试设备时钟没有校准，或者你创建的APP出现了故障，请联系BeeCloud；如果你不需要BeeCloud为你做时间校验，你可以到控制台关闭该功能

* BC验签失败：<br/>
检查一下，是不是设置了测试模式（setSandbox(true)），但是secret填的是app secret
