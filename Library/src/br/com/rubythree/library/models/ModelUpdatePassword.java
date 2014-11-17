package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelUpdatePassword extends Base {

	Context currentContext;
	RestService restService;
	int httpVerb;
	String emailEncoded;
	public ProgressDialog progressdialog;
	public int codeServer;
	JSONObject jsonInf;
	
	public Context getCurrentContext(){
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	public void updatePassword(String id, String email, String currentPass, String newPass) {
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + currentPass + "@" + APIHost + "/users.json";
		httpVerb = RestService.PUT;
		progressdialog = ProgressDialog.show(currentContext, null, "Carregando");
		restService = new RestService(postLoginPassCallback(progressdialog), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(currentPass);
		restService.addParam("user[id]", id);
		restService.addParam("user[password]", newPass);
		restService.addParam("user[password_confirmation]", newPass);
		restService.addParam("user[secret_p]", newPass);
		restService.execute();
	}
	
	private Handler postLoginPassCallback(final ProgressDialog dialog) {
		ModelUpdatePassword mUpPass = this;
		return new ModelHandler((Base) mUpPass) {
			public void handleMessage(Message msg) {
				try {
					codeServer = restService.getResponseCode();
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.object.notifyModel(msg.obj.toString(), currentContext.toString());
				dialog.dismiss();
			}
		};
	}
	
	public void encodedEmail(String email) {
		try {
			emailEncoded = URLEncoder.encode(email, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}
