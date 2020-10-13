package com.unccr.zclh.dsdps.board.ly;

public class LYPowerOnOffManager {

    private static volatile LYPowerOnOffManager lyPowerOnOffManager;
    /**喂狗的action*/
    private static final String WATHC_DOG_ACTION = "android.intent.action.pubds_watchdogenable";
    /**取消喂狗的action*/
    private static final String DISABLE_WATHC_DOG_ACTION = "android.intent.action.pubds_watchdogdisable";

    /**休眠的action*/
    private static final String SLEEP_ACTION = "android.intent.action.pubds_sleep";

    public boolean isSeelp = false;

    public static LYPowerOnOffManager get() {
        if (null == lyPowerOnOffManager) {
            synchronized (LYPowerOnOffManager.class) {
                if (null == lyPowerOnOffManager) {
                    lyPowerOnOffManager = new LYPowerOnOffManager();
                }
            }
        }
        return lyPowerOnOffManager;
    }






    /**
     * 唤醒
     */
//	public void wakeup() {
//		disableWatchDog();
//		RunCommandeUtile.execLYWakeup();
//		Util.reboot();
//	}
}
