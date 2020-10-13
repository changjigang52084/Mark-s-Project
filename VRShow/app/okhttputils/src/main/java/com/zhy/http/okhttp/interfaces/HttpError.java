package com.zhy.http.okhttp.interfaces;

import okhttp3.Call;

public interface HttpError {
	
	 void onError(Call call, Exception e, int id) ;

}
