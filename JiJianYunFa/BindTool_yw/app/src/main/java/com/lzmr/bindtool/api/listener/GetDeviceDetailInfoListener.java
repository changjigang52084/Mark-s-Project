package com.lzmr.bindtool.api.listener;

import com.baize.adpress.core.protocol.dto.DeviceDetailDto;

/**
 * 项目名称：BindTool
 * 类描述：获取终端详情回调接口
 * 创建人：longyihuang
 * 创建时间：16/11/4 15:43
 * 邮箱：huanglongyi@17-tech.com
 */

public interface GetDeviceDetailInfoListener extends SessionRequestListener{
    public void onGetDeviceDetailInfo(DeviceDetailDto deviceDetailDto);
}
