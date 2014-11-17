package br.com.rubythree.library.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelGetRents extends Base{
	private Context currentContext;
	RestService restService;
	String emailEncoded;
	Integer httpVerb;
	public int codeServer;
	
	public JSONArray jsonRent;
	
	public Context getCurrentContext(){
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	public void getRent(String email, String password, String idUser){
		encodedEmail(email);
		String url = "http://" + emailEncoded + ":" + password + "@" + APIHost + "/rents.json" + "?id=" + idUser;
		httpVerb = RestService.GET;
		restService = new RestService(getRentCallback(), currentContext, url, httpVerb);
		restService.setHttpUsername(emailEncoded);
		restService.setHttpPassword(password);
		restService.execute();
		
	}
	
	private Handler getRentCallback() {
		ModelGetRents mGetRent = this;
		return new ModelHandler((Base) mGetRent) {
			public void handleMessage(Message msg) {
				JSONArray jsonA;
				try {
					jsonA = new JSONArray(msg.obj.toString());
					getRentArray(jsonA);
					codeServer = restService.getResponseCode();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.object.notifyModel(msg.obj.toString(), currentContext.toString());
			}
		};
	}
	
	public void getRentArray(JSONArray jsonArray) {
		jsonRent = jsonArray;
	}
	

	
	public void encodedEmail(String email) {
		try {
			emailEncoded = URLEncoder.encode(email, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
