package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelEditBook extends Base {

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
	
	public void alterBook(String email, String password, String id, String name, String author, String quantity, String description) {
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + password + "@" + APIHost + "/books/"+id+".json";
		httpVerb = RestService.PUT;
		progressdialog = ProgressDialog.show(currentContext, null, "Carregando");
		restService = new RestService(postEditBookCallback(progressdialog), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.addParam("book[id]", id);
		restService.addParam("book[nome]", name);
		restService.addParam("book[autor]", author);
		restService.addParam("book[quantidade_estoque]", quantity);
		restService.addParam("book[descricao]", description);
		restService.execute();
	}
	
	private Handler postEditBookCallback(final ProgressDialog dialog) {
		ModelEditBook mEditBook = this;
		return new ModelHandler((Base) mEditBook) {
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
