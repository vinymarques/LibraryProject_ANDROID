package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelDeleteRent extends Base{
	
	Context currentContext;
	RestService restService;
	int httpVerb;
	String emailEncoded;

	public Context getCurrentContext(){
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	public void deleteRent(String email, String password, String idRent, String idBook){
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + password + "@" + APIHost + "/rents/" +idRent+".json";
		httpVerb = RestService.DELETE;
		restService = new RestService(postRentCallback(), currentContext, url, httpVerb);
		restService.addParam("rent[book_id]", idBook);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.execute();
	}
	
	private Handler postRentCallback() {
		ModelDeleteRent mRent = this;
		return new ModelHandler((Base) mRent) {
			public void handleMessage(Message msg) {
				this.object.notifyModel(msg.obj.toString(), currentContext.toString());
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
