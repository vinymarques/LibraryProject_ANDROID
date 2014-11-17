package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelGetBooks extends Base{

	private Context currentContext;
	RestService restService;
	String emailEncoded;
	String searchEncoded;
	Integer httpVerb;
	public JSONArray bookArray;
	public int codeServer;
	
	public Context getCurrentContext(){
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	public void getBook(String email, String password, int page, String search, boolean searchActive){
		encodedEmail(email);
		String url;
		if (searchActive == false){
			url = "http://" + APIHost + "/books.json?page=" + page;
		} else {
			encodedSearch(search);
			url = "http://" + APIHost + "/books.json?&search=" + searchEncoded + "&commit=Buscar";
		}
		httpVerb = RestService.GET;
		ProgressDialog progressdialog = ProgressDialog.show(currentContext, null, "Carregando");
		restService = new RestService(getBookCallback(progressdialog), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.execute();
		
	}
	
	private Handler getBookCallback(final ProgressDialog dialog) {
		ModelGetBooks mGetBook = this;
		return new ModelHandler((Base) mGetBook) {
			public void handleMessage(Message msg) {
				JSONArray jsonA;
				try {
					jsonA = new JSONArray(msg.obj.toString());
					getBookObjects(jsonA);
					codeServer = restService.getResponseCode();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.object.notifyModel(msg.obj.toString(), currentContext.toString());
				dialog.dismiss();
			}
		};
	}
	
	public void getBookObjects(JSONArray jsonArray) {
		bookArray = jsonArray;
	}
	

	
	public void encodedEmail(String email) {
		try {
			emailEncoded = URLEncoder.encode(email, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void encodedSearch(String search) {
		try {
			searchEncoded = URLEncoder.encode(search, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}