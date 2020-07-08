package fuck.face.custom;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;
import android.os.Bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.RemoteException;

import com.tencent.wxpayface.IWxPayFaceAIDL;
import com.tencent.wxpayface.IWxPayFaceCallbackAIDL;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;

/**
 * This class echoes a string called from JavaScript.
 */
public class FuckFace extends CordovaPlugin { // 必须继承CordovaPlugin
  /**
   * action对应exec传过来的action args对应exec传过来的参数数组 callbackContext：对应exec传过来的回调函数
   */
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("getRawData")) { // 获取 rawData 用来调用后端接口获取 authinfo
      String message = args.getString(0);
      this.getRawData(message, callbackContext);
      return true;
    } else if (action.equals("initFacePay")) { // 扫脸初始化 (打开 app 时调用)
      this.initFacePay(callbackContext);
    }
     else if (action.equals("updatePay")) { // 更新支付结果
      String message = args.getString(0);
      this.updatePay(message, callbackContext);
    } else if (action.equals("releasePayFace")) { // 释放资源 (关闭app时调用)
      this.releasePayFace(callbackContext);
    } else if (action.equals("doGetFaceCode")) { // 获取 face_code
      String message = args.getString(0);
      this.doGetFaceCode(message, callbackContext);
    }
    return false;
  }
  // 插件测试
  private void testCordova (CallbackContext callbackContext) {
    callbackContext.success("初始化成功"); // 成功回调函数
  }
  private void testCordova1 (String message, CallbackContext callbackContext) {
    try {
      JSONObject jObj = new JSONObject(message);
      String phone = jObj.getString("phone");
      callbackContext.success(phone); // 成功回调函数
    } catch(Exception e) {
      callbackContext.error(e.toString());
    }
  }
  // 扫脸初始化
  String InitCode;
  private void initFacePay(CallbackContext callbackContext) {
    InitCode = "";
    Context context = this.cordova.getActivity().getApplicationContext();
    // Map<String, String> m1 = new HashMap<>();
    Map<String, String> m1 = new HashMap<String, String>();
    // Map m1 = new HashMap();
    WxPayFace.getInstance().initWxpayface(context, m1, new IWxPayfaceCallback() {
      @Override
      public void response(Map info) throws RemoteException {
        if (info == null) {
          new RuntimeException("调用返回为空").printStackTrace();
          return;
        }
        String code = (String) info.get("return_code");
        InitCode = code;
        String msg = (String) info.get("return_msg");
        if (code == null || !code.equals("SUCCESS")) {
          new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
          return;
        }

      }
    });
    for(int i = 0; i<30; i++){
      try {
          Thread.sleep(1000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      try {
        if (InitCode.equals("SUCCESS")) {
          callbackContext.success("初始化成功"); // 成功回调函数
          return;
        }
      } catch(Exception e) {
      }
    }
    callbackContext.error("初始化失败:" + InitCode);
  }
  // 获取 rawData 数据
  String Rawdata = "";
  String RawdataCode;
  String DeviceCode = android.os.Build.SERIAL;
  private void getRawData(String message, CallbackContext callbackContext) {
    Rawdata = "";
    RawdataCode = "";
    // 获取 rawdata
    WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {

      @Override
      public void response(Map info) throws RemoteException {
        if (info == null) {
          new RuntimeException("调用返回为空").printStackTrace();
          return;
        }
        String code = (String) info.get("return_code");
        RawdataCode = code;
        String msg = (String) info.get("return_msg");
        String rawdata = info.get("rawdata").toString();
        Rawdata = rawdata;
        // alert(code,msg,Rawdata);
        if (code == null || rawdata == null || !code.equals("SUCCESS")) {
          new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
          return;
        }
      }
    });
    for(int i = 0; i<30; i++){
      try {
          Thread.sleep(1000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      try {
        if (RawdataCode.equals("SUCCESS")) {
          // callbackContext.success(Rawdata + '|' + DeviceCode); // 成功回调函数
          callbackContext.success(Rawdata); // 成功回调函数
          return;
        }
      } catch(Exception e) {
      }
    }
    callbackContext.error("Rawdata获取失败:" + RawdataCode);
  }
  String appid = ""; // appid
  String authinfo = "";
  String mch_id = ""; // 商户号
  String sub_app_id = "";
  String sub_mch_id = "";
  String store_id = "";
  String out_trade_no = "";
  String total_fee = "";
  String FaceCode = "";
  String OpenID = "";
  String face_authtype = "";
  String Code;
  String Msg;
  // 获取face_code
  public boolean doGetFaceCode(String message, CallbackContext callbackContext) {
    FaceCode = "";
    OpenID = "";
    Code = "";
    Msg = "";
    try {
      JSONObject jObj = new JSONObject(message);
      appid = jObj.getString("appid"); // appid
      authinfo = jObj.getString("authinfo");
      mch_id = jObj.getString("mch_id"); // 商户号
      sub_app_id = jObj.getString("sub_app_id");
      sub_mch_id = jObj.getString("sub_mch_id");
      store_id = jObj.getString("store_id");
      out_trade_no = jObj.getString("out_trade_no");
      total_fee = jObj.getString("total_fee");
      face_authtype = jObj.getString("face_authtype");

    }catch(Exception e) {

    }

    Map<String, String> m1 = new HashMap<String, String>();
      m1.put("appid", appid); // 公众号，必填
      // m1.put("mch_id", "1303530001"); // 商户号，必填
      m1.put("mch_id", mch_id); // 商户号，必填
      m1.put("authinfo", authinfo); // 调用凭证，必填
      m1.put("sub_appid", sub_app_id); // 子商户公众账号ID(非服务商模式不填)
      m1.put("sub_mch_id", sub_mch_id); // 子商户号(非服务商模式不填)
      m1.put("store_id", store_id); // 门店编号，必填
      // m1.put("telephone", "用户手机号"); // 用户手机号，用于传递会员手机号到界面输入栏，非必填
      m1.put("out_trade_no", out_trade_no); // 商户订单号， 必填
      m1.put("total_fee", total_fee); // 订单金额（数字），单位：分，必填
      m1.put("face_authtype", face_authtype); // FACEPAY：人脸凭证，常用于人脸支付 FACEPAY_DELAY：延迟支付 必填
      m1.put("ask_face_permit", "0"); // 展开人脸识别授权项，详情见上方接口参数，必填

    // m1.put("ask_ret_page", "0"); // 是否展示微信支付成功页，可选值："0"，不展示；"1"，展示，非必填
    WxPayFace.getInstance().getWxpayfaceCode(m1, new IWxPayfaceCallback() {
      @Override
      public void response(final Map info) throws RemoteException {
        if (info == null) {
          new RuntimeException("调用返回为空").printStackTrace();
          return;
        }
        // if (info.get("return_code") != null) {
          String code = (String) info.get("return_code"); // 错误码
          Code = code;
        // }
        // if (info.get("return_msg") != null) {
          String msg = (String) info.get("return_msg"); // 错误码描述
          Msg = msg;
        // }
        if (info.get("face_code") != null) {
          String faceCode = info.get("face_code").toString(); // 人脸凭证，用于刷脸支付
          FaceCode = faceCode;
        }
        if (info.get("openid") != null) {
          String openid = info.get("openid").toString(); // openid
          OpenID = openid;
        }
        String sub_openid = ""; // 子商户号下的openid(服务商模式)
        int telephone_used = 0; // 获取的`face_code`，是否使用了请求参数中的`telephone`
        int underage_state = 0; // 用户年龄信息（需联系微信支付开通权限）
        if (info.get("sub_openid") != null)
          sub_openid = info.get("sub_openid").toString();
        if (info.get("telephone_used") != null)
          telephone_used = Integer.parseInt(info.get("telephone_used").toString());
        if (info.get("underage_state") != null)
          underage_state = Integer.parseInt(info.get("underage_state").toString());
        if (code == null || FaceCode == null || OpenID == null || !code.equals("SUCCESS")) {
          new RuntimeException("调用返回非成功信息,return_msg:" + msg + "  ").printStackTrace();
          return;
        }
      }
    });

    // 延迟获取微信接口返回数据
    for(int i = 0; i<70; i++){
      try {
          Thread.sleep(1000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      try {
        if (Code.equals("SUCCESS") && FaceCode != "" && OpenID != "" ) {
          callbackContext.success(FaceCode + '|' + OpenID + '|' + appid + '|' + authinfo + '|'+ mch_id + '|'+ store_id + '|'+ out_trade_no + '|'+ total_fee + '|' + face_authtype + '|' + "0" + '|' + Code + '|' + Msg); // 成功回调函数
          return true;
        } else if (Code.equals("USER_CANCEL")) { // 用户手动关闭返回
          callbackContext.error("请重新扫脸或选择扫码支付");
          return true;
        }
      } catch(Exception e) {
      }
    }

    callbackContext.error("请重新扫脸或选择扫码支付:" + Code + Msg);

    return true;
  }

  // 停止识别
  // private void stopFaceRecognize() {
  //   HashMap<String, String> map = new HashMap<String, String>();
  //   map.put("authinfo", "填您的调用凭证"); // 调用凭证，必填
  //   WxPayFace.getInstance().stopWxpayface(map, new IWxPayfaceCallback() {
  //     @Override
  //     public void response(Map info) throws RemoteException {
  //       if (info == null) {
  //         new RuntimeException("调用返回为空").printStackTrace();
  //         return;
  //       }
  //       String code = (String) info.get("return_code"); // 错误码
  //       String msg = (String) info.get("return_msg"); // 错误码描述
  //       if (code == null || !code.equals("SUCCESS")) {
  //         new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
  //         return;
  //       }
  //       /*
  //        * 在这里处理您自己的业务逻辑
  //        */
  //     }
  //   });
  // }

  // 释放资源
  private void releasePayFace(CallbackContext callbackContext) {
    Context context = this.cordova.getActivity().getApplicationContext();
    WxPayFace.getInstance().releaseWxpayface(context);
     try {
      callbackContext.success("人脸关闭成功"); // 成功回调函数
    } catch(Exception e) {
      callbackContext.error("错误原因:");
    }
  }
  // 更新支付结果
  String UpdateCode;
  private void updatePay(String message, CallbackContext callbackContext) {
    UpdateCode = "";
     try {
      JSONObject jObj = new JSONObject(message);
      appid = jObj.getString("appid"); // appid
      authinfo = jObj.getString("authinfo"); // 调用凭证
      mch_id = jObj.getString("mch_id"); // 商户号
      store_id = jObj.getString("store_id"); // 店铺id

    }catch(Exception e) {

    }
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("appid", appid); // 公众号，必填
    map.put("mch_id", mch_id); // 商户号，必填
    map.put("store_id", store_id); // 门店编号，必填
    map.put("authinfo", authinfo); // 调用凭证，必填
    map.put("payresult", "SUCCESS"); // 支付结果，SUCCESS:支付成功   ERROR:支付失败   必填
    WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
        @Override
        public void response(Map info) throws RemoteException {
            if (info == null) {
                new RuntimeException("调用返回为空").printStackTrace();
                return;
            }
            String code = (String) info.get("return_code"); // 错误码
            UpdateCode = code;
            String msg = (String) info.get("return_msg"); // 错误码描述
            if (code == null || !code.equals("SUCCESS")) {
                new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                return ;
            }
        }
    });
    // 延迟获取微信接口返回数据
    for(int i = 0; i<30; i++){
      try {
          Thread.sleep(1000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      try {
        if (UpdateCode.equals("SUCCESS")) {
          callbackContext.success("更新支付结果成功" + UpdateCode); // 成功回调函数
          return;
        }
      } catch(Exception e) {
      }
    }
    callbackContext.error("错误原因:" + UpdateCode);
  }
}
