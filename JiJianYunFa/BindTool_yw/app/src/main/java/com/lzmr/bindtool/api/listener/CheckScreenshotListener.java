package com.lzmr.bindtool.api.listener;

import com.baize.adpress.core.protocol.dto.DeviceScreenShotViewDto;

/**
 * 项目名称：BindTool
 * 类描述：查看截屏图片回调接口
 * 创建人：longyihuang
 * 创建时间：16/11/8 14:14
 * 邮箱：huanglongyi@17-tech.com
 */

public interface CheckScreenshotListener extends SessionRequestListener {
    public void onCheckScreenshot(DeviceScreenShotViewDto deviceScreenShotViewDto);
}
