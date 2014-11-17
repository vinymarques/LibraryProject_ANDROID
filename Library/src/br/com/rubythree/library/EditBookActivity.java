package br.com.rubythree.library;

import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelEditBook;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditBookActivity extends Activity implements ModelDelegate{

	EditText txtName;
	EditText txtAutor;
	EditText txtQuantity;
	EditText txtDescription;
	Button btnAlter;
	Button btnReturn;
	
	String idBook;
	String currentName;
	String currentAuthor;
	String currentQuantity;
	String currentDescription;
	
	SharedPreferences prefs;
	static String userMail;
	static String userPassword;
	static String userId;
	
	ModelEditBook modelEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_book);
		
		prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
		userMail = prefs.getString("usermail", null);
		userPassword = prefs.getString("password", null);
		userId = prefs.getString("userId", null);
		
		modelEdit = new ModelEditBook();
		modelEdit.setCurrentContext(this);
		modelEdit.addDelegate(this);
		
		
		txtName = (EditText) findViewById(R.id.book_name);
		txtAutor = (EditText) findViewById(R.id.book_autor);
		txtQuantity = (EditText) findViewById(R.id.book_quantity);
		txtDescription = (EditText) findViewById(R.id.book_description);
		btnAlter = (Button) findViewById(R.id.book_alter);
		btnReturn = (Button) findViewById(R.id.book_return);
		
		sizeItems();
		
		idBook = getIntent().getStringExtra("idBook");
		currentName = getIntent().getStringExtra("currentName");
		currentAuthor = getIntent().getStringExtra("currentAuthor");
		currentQuantity = getIntent().getStringExtra("currentQuantity");
		currentDescription = getIntent().getStringExtra("currentDescription");
		
		txtName.setText(currentName);
		txtAutor.setText(currentAuthor);
		txtQuantity.setText(currentQuantity);
		txtDescription.setText(currentDescription);
		
		 btnAlter.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					
					if (verifyConnection() == false) {
						alertConnection();
					} else {
						
						if (txtName.getText().toString().equals("") || txtAutor.getText().toString().equals("")|| txtQuantity.getText().toString().equals("") || txtDescription.getText().toString().equals("") ) {
							AlertDialog.Builder dialog = new AlertDialog.Builder(EditBookActivity.this);
							dialog.setTitle("Preenchimento Incorreto");
							dialog.setMessage("Verifique os dados preenchidos");
							dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {}
							});
							dialog.show();
						} else {
							try {
								modelEdit.alterBook(userMail, userPassword, idBook, txtName.getText().toString(), txtAutor.getText().toString(), txtQuantity.getText().toString(), txtDescription.getText().toString());
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
				onBackPressed();
			}
		});
	}
	
	@Override
	public void performedModel() {}

	@Override
	public void performedModel(String string, String className) {
		int code = modelEdit.codeServer;
		if (code == 200){
		AlertDialog.Builder dialog = new AlertDialog.Builder(EditBookActivity.this);
		dialog.setMessage("Livro alterado com sucesso");
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onBackPressed();
			}
		});
		dialog.show();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(EditBookActivity.this);
			dialog.setMessage("Falha na alteração do Livro");
			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			dialog.show();
		}
	}
	
	public void sizeItems(){
    	
	   	 final DisplayMetrics metrics = new DisplayMetrics();
			 getWindowManager().getDefaultDisplay().getMetrics(metrics);
			 
			 txtName.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 txtAutor.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 
			 txtQuantity.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 
			 txtDescription.setWidth(metrics.widthPixels/2+metrics.widthPixels/3);
			 
			 btnAlter.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
			 btnReturn.setWidth(metrics.widthPixels/5+metrics.widthPixels/5+metrics.widthPixels/12);
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(EditBookActivity.this);
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
		LibraryTabActivity.setTab = 1;
		Intent i = new Intent(EditBookActivity.this, LibraryTabActivity.class);
		startActivity(i);
		finish();
    }
}
