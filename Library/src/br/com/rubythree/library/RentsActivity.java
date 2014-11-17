package br.com.rubythree.library;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.rubythree.library.adapter.RentArrayAdapter;
import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelDeleteRent;
import br.com.rubythree.library.models.ModelGetRents;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RentsActivity extends Activity implements ModelDelegate{

	ListView listRents;
	SharedPreferences prefs;
	static String userMail, userPassword, userId;
	public static ModelGetRents modelRent;
	static ModelDeleteRent modelDeleteRent;
	
	public static ArrayList<String> listRentId = new ArrayList<String>();
	public static ArrayList<String> listRentBookId = new ArrayList<String>();
	public static ArrayList<String> listRentName = new ArrayList<String>();
	public static ArrayList<String> listRentAuthor = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rents);
		
		clearAllItems();
		
		prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
		userMail = prefs.getString("usermail", null);
		userPassword = prefs.getString("password", null);
		userId = prefs.getString("userId", null);
		
		modelDeleteRent = new ModelDeleteRent();
		modelRent = new ModelGetRents();
		modelDeleteRent.setCurrentContext(this);
        modelRent.setCurrentContext(this);
        modelRent.addDelegate(this);
        if (verifyConnection() == false) {
        	alertConnection();
        } else {
        	modelRent.getRent(userMail, userPassword, userId);
        }
	}

	private void clearAllItems() {
		listRentId.clear();
		listRentBookId.clear();
		listRentName.clear();
		listRentAuthor.clear();
	}
	
	public void refreshList(){
		listRentId.clear();
		listRentBookId.clear();
		listRentName.clear();
		listRentAuthor.clear();
		modelRent.getRent(userMail, userPassword, userId);
	}

	@Override
	public void performedModel() {}

	@Override
	public void performedModel(String string, String className) {
		JSONArray jsonArray = modelRent.jsonRent;
			int r;
			for (r = 0; r < jsonArray.length(); r ++) {
				try {
					JSONObject object = jsonArray.getJSONObject(r);
					JSONObject rents = object.getJSONObject("rent");
					listRentName.add(rents.getString("book_name"));
					listRentAuthor.add(rents.getString("book_author"));
					listRentBookId.add(rents.getString("book_id"));
					listRentId.add(rents.getString("id"));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			carregaList();
	}
	
	private void carregaList() {
		final RentArrayAdapter adapterRent = new RentArrayAdapter(this, listRentName, listRentAuthor);
		listRents = (ListView) findViewById(R.id.list_rents);
		listRents.setAdapter(adapterRent);
		
		listRents.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(RentsActivity.this);
				dialog.setMessage("Fazer a devolução do livro ?");
				dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							
							if (verifyConnection() == false) {
					        	alertConnection();
					        } else {
					        	modelDeleteRent.deleteRent(userMail, userPassword, listRentId.get(position), listRentBookId.get(position));
								listRentId.remove(position);
								listRentBookId.remove(position);
								listRentName.remove(position);
								listRentAuthor.remove(position);
								adapterRent.notifyDataSetChanged();	
								Toast.makeText(RentsActivity.this, "Devolução concluída !", Toast.LENGTH_SHORT).show();	
					        }
						
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
				
				dialog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				
				dialog.show();
			}
			
		});
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(RentsActivity.this);
		dialog.setTitle("Verifique sua conexão de internet");
		dialog.setMessage("Você precisa estar conectado para utilizar os serviços da biblioteca online");
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialog.show();
	}
}
