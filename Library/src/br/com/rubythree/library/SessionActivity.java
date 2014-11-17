package br.com.rubythree.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SessionActivity extends Activity{

	Button BtnLogOut;
	Button BtnAlter;
	
	SharedPreferences prefs;
	static String userMail;
	static String userPassword;
	static String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_account);
		
		prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
		userMail = prefs.getString("usermail", null);
		userPassword = prefs.getString("password", null);
		userId = prefs.getString("userId", null);
		
		BtnLogOut = (Button) findViewById(R.id.log_out_session);
		BtnAlter = (Button) findViewById(R.id.btn_alter_account);
		
		BtnLogOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogConfirmLogOutSession();
			}
		});
		
		BtnAlter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SessionActivity.this, AlterPasswordActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	
	public void DialogConfirmLogOutSession(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(SessionActivity.this);
		dialog.setMessage("Deseja realmente Sair ?");
		dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = prefs.edit();
				editor.putString(LoginActivity.USERPASSWORD_KEY, null);
				editor.commit();
				
				Intent i = new Intent(SessionActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		dialog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		
		dialog.show();
	}
}
