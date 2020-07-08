var exec = require('cordova/exec');

exports.getRawData = function (arg0, success, error) {
  /**
   * Cordova.exec（）方法说明
   * function(winParam) {}：成功回调函数。假设您的 exec成功完成，此功能将随您传递给它的任何参数一起执行
   * function(error) {}：错误回调函数。如果操作未成功完成，则此功能将执行可选的错误参数
   * "service"：在本机端呼叫的服务名称,与原生端的类名保持一致
   * "action"：在本机端调用的动作名称，对应原生类execute（）的入参，原生代码通过对action进行判断，从而知道JS让原生端执行什么样的功能
   * [ arguments ]：传到原生环境的参数数组
   */

  exec(success, error, 'FuckFace', 'getRawData', [arg0]);
};
exports.initFacePay = function (success, error) {
  exec(success, error, 'FuckFace', 'initFacePay');
};
exports.updatePay = function (arg0, success, error) {
  exec(success, error, 'FuckFace', 'updatePay', [arg0]);
}
exports.releasePayFace = function (success, error) {
  exec(success, error, 'FuckFace', 'releasePayFace');
}
exports.doGetFaceCode = function (arg0, success, error) {
  exec(success, error, 'FuckFace', 'doGetFaceCode', [arg0]);
}
