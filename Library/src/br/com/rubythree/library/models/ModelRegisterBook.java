package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelRegisterBook extends Base {

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
	
	public void registrateBook(String email, String password, String name, String author, String quantity, String description) {
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + password + "@" + APIHost + "/books.json";
		httpVerb = RestService.POST;
		restService = new RestService(postBookCallback(), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.addParam("book[nome]", name);
		restService.addParam("book[autor]", author);
		restService.addParam("book[quantidade_estoque]", quantity);
		restService.addParam("book[descricao]", description);
		restService.execute();
	}
	
	private Handler postBookCallback() {
		ModelRegisterBook mBook = this;
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
