package br.com.rubythree.library;

import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelRegistrate;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class RegisterActivity extends Activity implements ModelDelegate{
	EditText txtName;
	EditText txtEmail;
	EditText txtAddress;
	EditText txtPass;
	EditText txtConfirmPass;
	
	ImageView imgValidPass;
	ImageView imgValidConfirmPass;
	boolean validPass;
	boolean validConfirmPass;
	
	Button btnRegister;
	Button btnReturn;
	
	ModelRegistrate modelRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		txtName = (EditText) findViewById(R.id.register_name);
		txtEmail = (EditText) findViewById(R.id.register_email);
		txtAddress = (EditText) findViewById(R.id.register_address);
		txtPass = (EditText) findViewById(R.id.register_pass);
		txtConfirmPass = (EditText) findViewById(R.id.register_confirmation_pass);
		imgValidPass = (ImageView) findViewById(R.id.register_valid_pass);
		imgValidConfirmPass = (ImageView) findViewById(R.id.register_valid_confirmation_pass);
		btnRegister = (Button) findViewById(R.id.register_register);
		btnReturn = (Button) findViewById(R.id.register_return);
		
		modelRegister = new ModelRegistrate();
		modelRegister.setCurrentContext(this);
		modelRegister.addDelegate(this);
		
		
		txtPass.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (txtPass.getText().toString().length() < 8){
					imgValidPass.setBackgroundResource(R.drawable.x);
					validPass = false;
				} else {
					imgValidPass.setBackgroundResource(R.drawable.ok);
					validPass = true;
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
				if (txtConfirmPass.getText().toString().equals(txtPass.getText().toString())){
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
		
		btnRegister.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				if (verifyConnection() == false) {
					alertConnection();
				} else {
					if (txtName.getText().toString().equals("") || txtEmail.getText().toString().equals("") || txtAddress.getText().toString().equals("") || validPass == false || validConfirmPass == false) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
						dialog.setTitle("Preenchimento Incorreto");
						dialog.setMessage("Verifique os dados preenchidos");
						dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {}
						});
						dialog.show();
					} else {
						try {
							modelRegister.RegistrateUser(txtName.getText().toString(), txtEmail.getText().toString(), txtPass.getText().toString(), txtAddress.getText().toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		btnReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		sizeItems();
	}
	
	public void sizeItems(){
    	
	   	 final DisplayMetrics metrics = new DisplayMetrics();
			 getWindowManager().getDefaultDisplay().getMetrics(metrics);
			 
			 txtName.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 txtEmail.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);

			 
			 txtAddress.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 txtPass.setWidth(metrics.widthPixels/2+metrics.widthPixels/7);
			 txtConfirmPass.setWidth(metrics.widthPixels/2+metrics.widthPixels/7);
			 
			 btnRegister.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
			 btnReturn.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
	   }

	@Override
	public void performedModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performedModel(String string, String className) {
		int code = modelRegister.codeServer;
		if (code == 200){
			AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
			dialog.setMessage("Usuário " + txtName.getText().toString() + " cadastrado !");
			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			dialog.show();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
			dialog.setMessage("Usuário " + txtName.getText().toString() + " Já cadastrado !");
			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialog.show();
		}

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
		AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
		dialog.setTitle("Verifique sua conexão de internet");
		dialog.setMessage("Você precisa estar conectado para utilizar os serviços da biblioteca online");
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialog.show();
	}
}
