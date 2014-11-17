package br.com.rubythree.library;

import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelRegisterBook;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterBookActivity extends Activity implements ModelDelegate{
	EditText txtName;
	EditText txtAutor;
	EditText txtQuantity;
	EditText txtDescription;
	Button btnRegister;
	Button btnReturn;
	
	SharedPreferences prefs;
	static String userMail;
	static String userPassword;
	ModelRegisterBook modelBook;

	
	@Override
	protected void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_book);
		
		prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
		userMail = prefs.getString("usermail", null);
		userPassword = prefs.getString("password", null);
		
		txtName = (EditText) findViewById(R.id.register_book_name);
		txtAutor = (EditText) findViewById(R.id.register_book_autor);
		txtQuantity = (EditText) findViewById(R.id.register_book_quantity);
		txtDescription = (EditText) findViewById(R.id.register_book_description);
		btnRegister = (Button) findViewById(R.id.register_book_register);
		btnReturn = (Button) findViewById(R.id.register_book_return);
		
		sizeItems();
		
		modelBook = new ModelRegisterBook();
		modelBook.setCurrentContext(this);
		modelBook.addDelegate(this);
		
		btnRegister.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				if (verifyConnection() == false) {
					alertConnection();
				} else {
					
					if (txtName.getText().toString().equals("") || txtAutor.getText().toString().equals("")|| txtQuantity.getText().toString().equals("") || txtDescription.getText().toString().equals("") ) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterBookActivity.this);
						dialog.setTitle("Preenchimento Incorreto");
						dialog.setMessage("Verifique os dados preenchidos");
						dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {}
						});
						dialog.show();
					} else {
						try {
							modelBook.registrateBook(userMail, userPassword, txtName.getText().toString(), txtAutor.getText().toString(), txtQuantity.getText().toString(), txtDescription.getText().toString());
							AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterBookActivity.this);
							dialog.setMessage("Livro " + txtName.getText().toString() + " registrado !");
							dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							});
							dialog.show();
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
	}
	
	public void sizeItems(){
    	
	   	 final DisplayMetrics metrics = new DisplayMetrics();
			 getWindowManager().getDefaultDisplay().getMetrics(metrics);
			 
			 txtName.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 txtAutor.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 
			 txtQuantity.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 
			 txtDescription.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 
			 btnRegister.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
			 btnReturn.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
	   }

	@Override
	public void performedModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performedModel(String string, String className) {
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterBookActivity.this);
		dialog.setTitle("Verifique sua conexão de internet");
		dialog.setMessage("Você precisa estar conectado para utilizar os serviços da biblioteca online");
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialog.show();
	}

}
