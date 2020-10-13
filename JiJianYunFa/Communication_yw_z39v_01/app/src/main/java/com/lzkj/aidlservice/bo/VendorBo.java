package com.lzkj.aidlservice.bo;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2017/5/4.
 * @Parameter 售货机bo
 */
public class VendorBo {
    private String vendorOrderKey;
    private int vendorOrderResult;
    private String vendorOrderDate;

    /**
     * 订单key
     * @return
     */
    public String getVendorOrderKey() {
        return vendorOrderKey;
    }

    public void setVendorOrderKey(String vendorOrderKey) {
        this.vendorOrderKey = vendorOrderKey;
    }

    /**
     * 订单结果(-1 开始出货，0成功，1失败)
     *  {@link com.lzkj.aidlservice.receiver.VendorShipmentResultReceiver.SUCCESS}
     * @return
     */
    public int getVendorOrderResult() {
        return vendorOrderResult;
    }

    public void setVendorOrderResult(int vendorOrderResult) {
        this.vendorOrderResult = vendorOrderResult;
    }

    /**
     * 订单的时间
     * @return
     */
    public String getVendorOrderDate() {
        return vendorOrderDate;
    }

    public void setVendorOrderDate(String vendorOrderDate) {
        this.vendorOrderDate = vendorOrderDate;
    }
}
