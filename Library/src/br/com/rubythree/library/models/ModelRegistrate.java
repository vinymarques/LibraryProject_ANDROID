package br.com.rubythree.library.models;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ModelRegistrate extends Base{
	Context currentContext;
	RestService restService;
	int httpVerb;
	public int codeServer;
	public String userEmail, userName, userId;
	
	public Context getCurrentContext(Context currentContext){
		return currentContext;
	}
	
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	public void RegistrateUser(String name, String email, String password, String address) {
		String url = "http://" + APIHost + "/users.json";
		httpVerb = RestService.POST;
		restService = new RestService(registrateUserCallback(), currentContext, url, httpVerb);
		restService.addParam("user[nome]", name);
		restService.addParam("user[email]", email);
		restService.addParam("user[password]", password);
		restService.addParam("user[password_confirmation]", password);
		restService.addParam("user[secret_p]", password);
		restService.addParam("user[endereco]", address);
		restService.execute();
		
	}
	
	public Handler registrateUserCallback () {
		ModelRegistrate mRegistrate = this;
		return new ModelHandler((Base) mRegistrate) {
			public void handleMessage (Message msg) {
				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(msg.obj.toString());
					getUserInformations(jsonArray);
					codeServer = restService.getResponseCode();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.object.notifyModel(msg.obj.toString(), currentContext.toString());
			}
		};
	}
	
	public void getUserInformations (JSONArray jsonArray) {
		try {
			userEmail = jsonArray.getJSONObject(0).getJSONObject("user").getString("email");
			userName = jsonArray.getJSONObject(0).getJSONObject("user").getString("nome");
			userId = jsonArray.getJSONObject(0).getJSONObject("user").getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
