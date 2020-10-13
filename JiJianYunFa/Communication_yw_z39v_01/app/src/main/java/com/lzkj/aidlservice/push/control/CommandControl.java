package com.lzkj.aidlservice.push.control;

import android.util.Log;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.util.Command;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年4月14日 下午5:21:41
 * @parameter 命令控制类
 */
public class CommandControl {

    private static final String TAG = "CommandControl";

    /**
     * 终端控制命令
     **/
    private DeviceCommandControl deviceCommandControl;
    /**
     * 节目控制命令
     **/
    private ProgramCommanndControl programCommanndControl;
    /**
     * 授权控制命令
     **/
    private AuthorizeCommandControl authorizeCommandControl;
    /**
     * app控制命令
     **/
    private AppCommandControl appCommandControl;

    public CommandControl() {
        authorizeCommandControl = new AuthorizeCommandControl();
        programCommanndControl = new ProgramCommanndControl();
        deviceCommandControl = new DeviceCommandControl();
        appCommandControl = new AppCommandControl();
    }

    /**
     * 处理负载消息
     *
     * @param payloadMessage 负载消息
     */
    public void handlerPayloadMsg(String payloadMessage) {
        if (null == payloadMessage || payloadMessage.isEmpty()) {
            Log.i(TAG, "payloadMessage is null or empty");
            return;
        }
        AdpressDataPackage adpressDataPackage = null;
        try {
            adpressDataPackage = AdpressDataPackage.parser(payloadMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != adpressDataPackage) {
            responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_RECEIVED, null);
            switchCommand(adpressDataPackage);
        }
    }

    /**
     * 根据不同的命令进行调用不同的
     *
     * @param adpressDataPackage
     */
    private void switchCommand(AdpressDataPackage adpressDataPackage) {
        int command = Integer.valueOf(adpressDataPackage.getTypeStr(), 16);
        Log.d(TAG, "command: " + command);
        if (Command.CMD_SHUTDOWN == command) {// 立即待机 command = 143363
            deviceCommandControl.shutdown(adpressDataPackage);
        } else if (Command.CMD_REBOOT == command) {// 立刻重启 143365
            deviceCommandControl.reboot(adpressDataPackage);
        } else if (Command.CMD_SCREEN_AUDIO_UPT == command) {// 设置终端声音大小 143373
            deviceCommandControl.updateDeivceVolume(adpressDataPackage);
        } else if (Command.CMD_WORKTIME_UPDATE == command) {// 更新开关机时间 143366
            deviceCommandControl.updateWorkTiming(adpressDataPackage);
        } else if (Command.CMD_SNAPSHOT == command) {// 上传截图 143368
            deviceCommandControl.uploadScreenshot(adpressDataPackage);
        } else if (Command.CMD_PS_UPD == command) {// 下发节目 command = 139278
            programCommanndControl.updateProgram(adpressDataPackage);
        } else if (Command.CMD_CANCEL_PRMLIST == command) {// 删除节目列表 command = 143386
            programCommanndControl.delProgramList(adpressDataPackage);
        } else if (Command.CMD_DEVICE_ACTIVATION_SETUP == command) {// 获取终端授权信息 command = 143392
            authorizeCommandControl.updateDeviceAuthorizeInfo(adpressDataPackage);
        } else if (Command.CMD_DVC_UNBOUND == command) {// 解绑 command = 143377
            authorizeCommandControl.unbindDevice(adpressDataPackage);
        } else if (Command.CMD_SYSTEM_UPDATE_APP == command) {//更新终端应用 command = 143376
            appCommandControl.updateApp(adpressDataPackage);
        } else if (Command.CMD_DEVICE_TEST == command) { //终端推送测试
            deviceCommandControl.testDevicePush(adpressDataPackage);
        } else if (Command.CMD_UPLOAD_LOG == command) {//上传日志
            deviceCommandControl.uploadLog();
        } else if (Command.CMD_DEVICE_LIMIT_FLOW == command) {//更新本地流量阀值
            deviceCommandControl.setFlowMax(adpressDataPackage);
        } else if(Command.CMD_SYNC_PLAYLIST == command){// 同步终端节目 command = 208926
            deviceCommandControl.syncProgram();
        }
    }

    /**
     * 命令接收成功返回给服务器
     *
     * @param adpressDataPackage AdpressDataPackage对象
     * @param responseCode       响应码
     * @param jsonData           说明内容
     */
    private void responseReceiver(AdpressDataPackage adpressDataPackage, int responseCode, String jsonData) {
        CommandReceiptManager.commandReceipt(adpressDataPackage, responseCode, jsonData);
    }
}
