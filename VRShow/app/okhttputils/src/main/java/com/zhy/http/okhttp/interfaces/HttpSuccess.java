package com.zhy.http.okhttp.interfaces;

public interface HttpSuccess<T> {

	void onSuccess(T result, int id);

}
