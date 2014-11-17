package br.com.rubythree.library.models;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.ResultReceiver;

public class RestService {
	private ArrayList<ParcelableNameValuePair> params;
	private ArrayList<ParcelableNameValuePair> headers;
	
	private String url;
	private Handler mHandler;
	private Context mContext;
	private int RequestType;
	private String entity; 
	private String httpUsername;
	private String httpPassword;
	
	private static final String TAG = "[RestService]";
	private int responseCode;
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
		
	}
	
	public String getHttpUsername(){
		return httpUsername;
	}

	public void setHttpUsername(String httpUsername) {
		this.httpUsername = httpUsername;
	}
	
	public String getHttpPassword() {
		return httpPassword;
	}
	
	public void setHttpPassword(String httpPassword) {
		this.httpPassword = httpPassword;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode() {
		this.responseCode = responseCode;
	}
	
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DELETE = 4;
	
	public RestService(Handler mHandler, Context mContext, String url, int RequestType) {
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.url = url;
		this.RequestType = RequestType;
		params = new ArrayList<ParcelableNameValuePair>();
		headers = new ArrayList<ParcelableNameValuePair>();
	}
	
	public void addParam(String name, String name2) {
		params.add(new ParcelableNameValuePair(name, name2));
	}
	
	public void addHeader(String name, String value) {
		headers.add(new ParcelableNameValuePair(name, value));
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public void execute() {
		ResultReceiver receiver;
		final RestService rs = this;
		receiver = new ResultReceiver(mHandler) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				rs.setResponseCode(resultData.getInt("responseCode"));
				if (mHandler != null) {
					Message message = mHandler.obtainMessage(0, 0, 0, resultData.getString("result"));
					message.sendToTarget();
				}
			}
		};
		
		final Intent intent = new Intent(mContext, ExecuteRequest.class);
		intent.putParcelableArrayListExtra("headers", (ArrayList<? extends Parcelable>) headers);
		intent.putExtra("params", params);
		intent.putExtra("url", url);
		intent.putExtra("receiver", receiver);
		intent.putExtra("method", RequestType);
		intent.putExtra("entity", entity);
		
		if (httpUsername != null) {
			intent.putExtra("httpUsername", httpUsername);
			intent.putExtra("httpPassword", httpPassword);
		}
		
		mContext.startService(intent);
	}
}

