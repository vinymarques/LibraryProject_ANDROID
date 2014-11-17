package br.com.rubythree.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelLogin;

public class LoginActivity extends Activity implements ModelDelegate{

	EditText txtLogin;
	public EditText txtSenha;
	ModelLogin loginModel;
	ProgressDialog dialog;
	String mailConfirmed;
	boolean accessServer;
	Button btnLogin;
	
	SharedPreferences prefs;
	final static String USERNAME_KEY = "usermail";
	final static String USERPASSWORD_KEY = "password";
	final static String USERID_KEY = "userId";
	final static String USER_ADMIN_PERMITTED_KEY = "userAdminPermitted";
	
	
	String passConfirmation;
	String loginConfirmation;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        ImageView Register = (ImageView) findViewById(R.id.image_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        
        txtLogin = (EditText) findViewById(R.id.txt_login);
        txtSenha = (EditText) findViewById(R.id.txt_senha);
        loginModel = new ModelLogin();
        loginModel.setCurrentContext(this);
        loginModel.addDelegate(this);
        
        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        
        loginConfirmation = (prefs.getString(USERNAME_KEY, null));
        txtLogin.setText(loginConfirmation);
        
        passConfirmation = (prefs.getString(USERPASSWORD_KEY, null));
        txtSenha.setText(passConfirmation);
        
        txtLogin.requestFocus();
        
        verifyLogon();
        
        btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (verifyConnection() == false) {
					alertConnection();
				} else {
					if (txtLogin.getText().toString().equals("") || txtSenha.getText().toString().equals("")) {
						alertVeriry();
					} else {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtSenha.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						loginModel.postUser(txtLogin.getText().toString(), txtSenha.getText().toString());
					}
				}
			}
		});
        
        Register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (verifyConnection() == false) {
					alertConnection();
				} else {
					Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
					startActivity(i);
				}
			}
		});
    }

	@Override
	public void performedModel() {

	}

	@Override
	public void performedModel(String string, String className) {
		int code = loginModel.codeServer;
		if (code == 200){
			putUserInformations();
			LibraryTabActivity.setTab = 1;
			Intent i = new Intent(LoginActivity.this, LibraryTabActivity.class);
			startActivity(i);
			finish();
		} else {
			loginModel.progressdialog.dismiss();
			alertVeriry();
		}
		loginModel.codeServer = 0;
	}

	private void alertVeriry(){
		AlertDialog.Builder dialogL = new AlertDialog.Builder(LoginActivity.this);
		dialogL.setTitle("Preenchimento Incorreto");
		dialogL.setMessage("Verifique os dados preenchidos");
		dialogL.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialogL.show();
	}
	private void putUserInformations(){
		Editor editor = prefs.edit();
		editor.putString(LoginActivity.USERNAME_KEY, txtLogin.getText().toString());
		editor.putString(LoginActivity.USERPASSWORD_KEY, txtSenha.getText().toString());
		editor.putString(LoginActivity.USERID_KEY, loginModel.userID);
		editor.putString(LoginActivity.USER_ADMIN_PERMITTED_KEY, loginModel.userAdminPermitted);
		editor.commit();
	}
	
	
	
	public  boolean verifyConnection() {  
	    boolean conectado;  
	    ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if (conectivtyManager.getActiveNetworkInfo() != null  
	            && conectivtyManager.getActiveNetworkInfo().isAvailable()  
	            && conectivtyManager.getActiveNetworkInfo().isConnected()) {  
	        conectado = true;  
	    } else {  
	        conectado = false;  
	    }  
	    return conectado;  
	}
	
	public void verifyLogon(){
		if (passConfirmation == null){
		} else {
			if (verifyConnection() == false) {
				alertConnection();
			} else {
				LibraryTabActivity.setTab = 1;
				Intent i = new Intent(LoginActivity.this, LibraryTabActivity.class);
				startActivity(i);
				finish();
			}
		}
	}
	
	public void alertConnection(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
		dialog.setTitle("Verifique sua conexão de internet");
		dialog.setMessage("Você precisa estar conectado para utilizar os serviços da biblioteca online");
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialog.show();
	}
}
