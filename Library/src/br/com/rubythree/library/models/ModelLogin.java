package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelLogin extends Base {
	private Context currentContext;
	RestService restService;
	int httpVerb;
	String emailEncoded;
	public JSONArray informationArray;
	public String userEmail, userName, userID, userAddress, userPassword, userAdminPermitted;
	public int codeServer;
	public ProgressDialog progressdialog;
	
	public Context getCurrentContext() {
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	public void postUser(String email, String password) {
		encodedEmail(email);
		String url = "http://" + APIHost + "/login.json";
		httpVerb = RestService.GET;
		progressdialog = ProgressDialog.show(currentContext, null, "Validando");
		restService = new RestService(postUserCallBack(progressdialog), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.execute();
	}
	
	public Handler postUserCallBack(final ProgressDialog dialog){
		ModelLogin mLogin = this;
		return new ModelHandler((Base) mLogin) {
			public void handleMessage(Message msg) {
				JSONObject jsonO;
				try {
					jsonO = new JSONObject(msg.obj.toString());
					getUserInformations(jsonO);
					codeServer = restService.getResponseCode();
					dialog.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.object.notifyModel(msg.obj.toString(), currentContext.toString());
			}
		};
	}
	
	public void getUserInformations(JSONObject jsonObject) {
		try {
			userEmail = jsonObject.getJSONObject("user").getString("email");
			userName = jsonObject.getJSONObject("user").getString("nome");
			userID = jsonObject.getJSONObject("user").getString("id");
			userAddress = jsonObject.getJSONObject("user").getString("endereco");
			userPassword = jsonObject.getJSONObject("user").getString("secret_p");
			userAdminPermitted = jsonObject.getJSONObject("user").getString("administrator_permitted");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void encodedEmail(String email) {
		try {
			emailEncoded = URLEncoder.encode(email, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
