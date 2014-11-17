package br.com.rubythree.library;

import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelUpdatePassword;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AlterPasswordActivity extends Activity implements ModelDelegate{

	SharedPreferences prefs;
	static String userMail;
	static String userPassword;
	static String userId;
	
	EditText txtCurrentPass;
	EditText txtNewPass;
	EditText txtConfirmPass;
	
	ImageView imgValidNewPass;
	ImageView imgValidConfirmPass;
	
	Button btnRegister;
	Button btnReturn;
	
	boolean validCurrentPass;
	boolean validNewPass;
	boolean validConfirmPass;
	
	ModelUpdatePassword modelUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alter_pass);
		
		prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
		userMail = prefs.getString("usermail", null);
		userPassword = prefs.getString("password", null);
		userId = prefs.getString("userId", null);
		
		modelUpdate = new ModelUpdatePassword();
		modelUpdate.setCurrentContext(this);
		modelUpdate.addDelegate(this);
		
		txtCurrentPass = (EditText) findViewById(R.id.current_pass);
		txtNewPass = (EditText) findViewById(R.id.new_pass);
		txtConfirmPass = (EditText) findViewById(R.id.confirmation_pass);
		
		imgValidNewPass = (ImageView) findViewById(R.id.valid_new_pass);
		imgValidConfirmPass = (ImageView) findViewById(R.id.valid_confirmation_pass);
		
		btnRegister = (Button) findViewById(R.id.btn_register);
		btnReturn = (Button) findViewById(R.id.btn_return);

		sizeItems();
		
		txtCurrentPass.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (txtCurrentPass.getText().toString().equals(userPassword)){
					validCurrentPass = true;
				} else {
					validCurrentPass = false;
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		txtNewPass.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (txtNewPass.getText().toString().length() < 8){
					imgValidNewPass.setBackgroundResource(R.drawable.x);
					validNewPass = false;
				} else {
					imgValidNewPass.setBackgroundResource(R.drawable.ok);
					validNewPass = true;
				}
			}	
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		txtConfirmPass.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (txtConfirmPass.getText().toString().equals(txtNewPass.getText().toString())){
					imgValidConfirmPass.setBackgroundResource(R.drawable.ok);
					validConfirmPass = true;
				} else {
					imgValidConfirmPass.setBackgroundResource(R.drawable.x);
					validConfirmPass = false;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		btnReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validCurrentPass == false || validNewPass == false || validConfirmPass == false){
					AlertDialog.Builder dialog = new AlertDialog.Builder(AlterPasswordActivity.this);
					dialog.setMessage("Preencha corretamente os campos");
					dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialog.show();
					
				} else {
					try {
						if (verifyConnection() == true) {
							modelUpdate.updatePassword(userId, userMail, userPassword, txtNewPass.getText().toString());
						} else {
							alertConnection();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public void sizeItems(){
		
	   	 final DisplayMetrics metrics = new DisplayMetrics();
		 getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 
		 txtCurrentPass.setWidth(metrics.widthPixels/2+metrics.widthPixels/7);
		 txtNewPass.setWidth(metrics.widthPixels/2+metrics.widthPixels/7);
		 txtConfirmPass.setWidth(metrics.widthPixels/2+metrics.widthPixels/7);
		 
		 btnRegister.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
		 btnReturn.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);

	   }

	@Override
	public void performedModel() {
	}

	@Override
	public void performedModel(String string, String className) {
		int code = modelUpdate.codeServer;
		if (code == 200){
			Editor editor = prefs.edit();
			editor.putString(LoginActivity.USERPASSWORD_KEY, txtNewPass.getText().toString());
			editor.commit();
			Toast.makeText(AlterPasswordActivity.this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
			onBackPressed();
		} else {
			modelUpdate.progressdialog.dismiss();
		}
		modelUpdate.codeServer = 0;
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
	
	public void alertConnection(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(AlterPasswordActivity.this);
		dialog.setTitle("Verifique sua conexão de internet");
		dialog.setMessage("Você precisa estar conectado para utilizar os serviços da biblioteca online");
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialog.show();
	}
	
	@Override
	public void onBackPressed() {
		LibraryTabActivity.setTab =2;
		Intent i = new Intent(AlterPasswordActivity.this, LibraryTabActivity.class);
		startActivity(i);
		finish();
    }
}
