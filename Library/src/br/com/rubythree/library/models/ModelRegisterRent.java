package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelRegisterRent extends Base{

	Context currentContext;
	RestService restService;
	int httpVerb;
	String emailEncoded;
	
	public Context getCurrentContext(){
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext){
		this.currentContext = currentContext;
	}
	
	public void registrateRent(String email, String password, String user_id, String book_id, boolean status){
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + password + "@" + APIHost + "/rents.json";
		httpVerb = RestService.POST;
		restService = new RestService(postRentCallBack(), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.addParam("rent[user_id]", user_id);
		restService.addParam("rent[book_id]", book_id);
		restService.addParam("rent[status]", String.valueOf(status));
		restService.execute();
	}
	
	private Handler postRentCallBack() {
		ModelRegisterRent mRent = this;
		return new ModelHandler((Base) mRent) {
			public void handleMessage(Message msg){
				this.object.notifyModel(msg.obj.toString(),currentContext.toString());
			}
		};
	}

	public void encodedEmail(String email){
		try {
			emailEncoded = URLEncoder.encode(email, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}
