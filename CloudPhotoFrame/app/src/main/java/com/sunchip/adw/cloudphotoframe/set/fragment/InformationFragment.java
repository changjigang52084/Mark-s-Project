package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.util.DeviceUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;

public class InformationFragment extends BaseFragment {

    private View view = null;
    private TextView device_name_tv;
    private TextView device_location_tv;
    private TextView device_model_tv;
    private TextView device_user_name_tv;
    private TextView device_serial_number_tv;
    private TextView device_frame_id_tv;
    private TextView device_firmware_tv;
    private TextView device_system_tv;
    private TextView device_application_tv;
    private TextView device_revision_tv;
    private TextView device_fcc_id_tv;
    private TextView device_ic_tv;
    private TextView device_ip_address_tv;
    private TextView device_mac_address_wifi_tv;
    private TextView device_download_queue_count_tv;
    private TextView SdcardCunchu_Tv;

    public InformationFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.information_fragment_layout, container, false);
            device_name_tv = view.findViewById(R.id.device_name_tv);
            device_location_tv = view.findViewById(R.id.device_location_tv);
            device_model_tv = view.findViewById(R.id.device_model_tv);
            device_user_name_tv = view.findViewById(R.id.device_user_name_tv);
            device_serial_number_tv = view.findViewById(R.id.device_serial_number_tv);
            device_frame_id_tv = view.findViewById(R.id.device_frame_id_tv);
            device_firmware_tv = view.findViewById(R.id.device_firmware_tv);
            device_system_tv = view.findViewById(R.id.device_system_tv);
            device_application_tv = view.findViewById(R.id.device_application_tv);
            device_revision_tv = view.findViewById(R.id.device_revision_tv);
            device_fcc_id_tv = view.findViewById(R.id.device_fcc_id_tv);
            device_ic_tv = view.findViewById(R.id.device_ic_tv);
            device_ip_address_tv = view.findViewById(R.id.device_ip_address_tv);
            device_mac_address_wifi_tv = view.findViewById(R.id.device_mac_address_wifi_tv);
            device_download_queue_count_tv = view.findViewById(R.id.device_download_queue_count_tv);
            SdcardCunchu_Tv = view.findViewById(R.id.SdCunchuTexi);

            device_name_tv.setText(DeviceUtils.getDeviceName());
            device_name_tv.setTypeface(CloudFrameApp.typeFace);
            device_fcc_id_tv.setTypeface(CloudFrameApp.typeFace);
            device_model_tv.setText(DeviceUtils.getDeviceMode().substring(0,DeviceUtils.getDeviceMode().indexOf("-")));
            device_model_tv.setTypeface(CloudFrameApp.typeFace);
            device_serial_number_tv.setText(DeviceUtils.getDeviceSerialNumber());
            device_serial_number_tv.setTypeface(CloudFrameApp.typeFace);
            device_firmware_tv.setText(DeviceUtils.getDeviceId());
            device_firmware_tv.setTypeface(CloudFrameApp.typeFace);
            device_system_tv.setText(DeviceUtils.getDeviceFirmware());
            device_system_tv.setTypeface(CloudFrameApp.typeFace);
            device_ip_address_tv.setText(DeviceUtils.getIpAddress(getActivity()));
            device_ip_address_tv.setTypeface(CloudFrameApp.typeFace);
            device_mac_address_wifi_tv.setText(DeviceUtils.getMac(getActivity()));
            device_mac_address_wifi_tv.setTypeface(CloudFrameApp.typeFace);
            device_application_tv.setText(DeviceUtils.getAppVersionName(getActivity().getPackageName(), getContext()));
            device_application_tv.setTypeface(CloudFrameApp.typeFace);
            device_location_tv.setText(SharedUtil.newInstance().getString("Location"));
            device_location_tv.setTypeface(CloudFrameApp.typeFace);
            device_user_name_tv.setText(SharedUtil.newInstance().getString("Username"));
            device_user_name_tv.setTypeface(CloudFrameApp.typeFace);

            float pressent = (float) DeviceUtils.getAvailaleSize() / DeviceUtils.getAllSize() * 100;

            TextView model_tv = view.findViewById(R.id.model_tv);
            model_tv.setTypeface(CloudFrameApp.typeFace);

            TextView location_tv = view.findViewById(R.id.location_tv);
            location_tv.setTypeface(CloudFrameApp.typeFace);

            TextView user_name_tv = view.findViewById(R.id.user_name_tv);
            user_name_tv.setTypeface(CloudFrameApp.typeFace);


            TextView serial_number_tv = view.findViewById(R.id.serial_number_tv);
            serial_number_tv.setTypeface(CloudFrameApp.typeFace);

            TextView firmware_tv = view.findViewById(R.id.firmware_tv);
            firmware_tv.setTypeface(CloudFrameApp.typeFace);

            TextView system_tv = view.findViewById(R.id.system_tv);
            system_tv.setTypeface(CloudFrameApp.typeFace);

            TextView application_tv = view.findViewById(R.id.application_tv);
            application_tv.setTypeface(CloudFrameApp.typeFace);


            TextView fcc_id_tv = view.findViewById(R.id.fcc_id_tv);
            fcc_id_tv.setTypeface(CloudFrameApp.typeFace);

            TextView ip_address_tv = view.findViewById(R.id.ip_address_tv);
            ip_address_tv.setTypeface(CloudFrameApp.typeFace);

            TextView mac_address_wifi_tv = view.findViewById(R.id.mac_address_wifi_tv);
            mac_address_wifi_tv.setTypeface(CloudFrameApp.typeFace);


            TextView text1 = view.findViewById(R.id.text1);
            text1.setTypeface(CloudFrameApp.typeFace);

            TextView text2 = view.findViewById(R.id.text2);
            text2.setTypeface(CloudFrameApp.typeFace);

            TextView text3 = view.findViewById(R.id.text3);
            text3.setTypeface(CloudFrameApp.typeFace);
            //free of

            SdcardCunchu_Tv.setText((Math.round(pressent)) + "% " + getResources().getString(R.string.freeof) + " " + DeviceUtils.AvailCount(DeviceUtils.getAllSize()));
            SdcardCunchu_Tv.setTypeface(CloudFrameApp.typeFace);
        }
        return view;
    }

    @Override
    public boolean onKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            case KeyEvent.KEYCODE_BACK:
                break;

        }
        return ret;
    }

    @Override
    public void Select(int postion) {

    }
}
