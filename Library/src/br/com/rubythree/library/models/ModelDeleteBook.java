package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelDeleteBook extends Base{
	
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
	
	public void deletebook(String email, String password, String idBook){
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + password + "@" + APIHost + "/books/" +idBook+".json";
		httpVerb = RestService.DELETE;
		restService = new RestService(postRentCallback(), currentContext, url, httpVerb);
		restService.addParam("book[id]", idBook);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.execute();
	}
	
	private Handler postRentCallback() {
		ModelDeleteBook mBook = this;
		return new ModelHandler((Base) mBook) {
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
