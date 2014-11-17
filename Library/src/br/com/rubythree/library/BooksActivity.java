package br.com.rubythree.library;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import br.com.rubythree.library.adapter.BookArrayAdapter;
import br.com.rubythree.library.models.ModelDelegate;
import br.com.rubythree.library.models.ModelDeleteBook;
import br.com.rubythree.library.models.ModelGetBooks;
import br.com.rubythree.library.models.ModelRegisterRent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class BooksActivity extends Activity implements ModelDelegate {
	
	ListView listView;
	ImageButton RegisterBook, searchCommit;
	
	BookArrayAdapter adapter;
	ModelDeleteBook modelDeleteBook;
	SharedPreferences prefs;
	static String userMail, userPassword, userId, search;
	static ModelGetBooks modelBook;
	static ModelRegisterRent modelRegisterRent;
	static boolean excludeModelBoolean = false;
	static int page = 1;
	static Boolean userAdminPermitted;
	static boolean pagesFinished = false;
	private AlertDialog alerta;
	public static int total = 0;
	public static int countItems = 10;
	public static boolean searchActive;
	boolean scrollPrimaryOrder = false;
	public EditText FindBook;
	
	private ArrayList<String> listID = new ArrayList<String>();
	private ArrayList<String> listName = new ArrayList<String>();
	private ArrayList<String> listAuthor = new ArrayList<String>();
	private ArrayList<String> listDescription= new ArrayList<String>();
	public static ArrayList<String> listQuantity = new ArrayList<String>();
	public static ArrayList<String> listRented = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.books);
		
		clearAllItems();
		
		prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
		userMail = prefs.getString("usermail", null);
		userPassword = prefs.getString("password", null);
		userId = prefs.getString("userId", null);
		userAdminPermitted = Boolean.parseBoolean(prefs.getString("userAdminPermitted", null));
		
		modelDeleteBook = new ModelDeleteBook();
		modelDeleteBook.setCurrentContext(this);
		
		modelBook = new ModelGetBooks();
		modelBook.setCurrentContext(this);
		modelBook.addDelegate(this);
		if (verifyConnection() == false) {
        	alertConnection();
        } else {
        	modelBook.getBook(userMail, userPassword, page, search, searchActive);
        }

	}

	@Override
	public void performedModel() {}

	@Override
	public void performedModel(String string, String className) {
		if (excludeModelBoolean == true){
			
			excludeModelBoolean = false;
		} else {
			JSONArray jsonArray = modelBook.bookArray;
			int i;
			for (i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject object = jsonArray.getJSONObject(i);
					JSONObject books = object.getJSONObject("book");
					
					if (RentsActivity.listRentBookId.contains(books.getString("id"))) {
						countItems--;
						total++;
					} else {
						listID.add(books.getString("id"));
						listName.add(books.getString("nome"));
						listAuthor.add(books.getString("autor"));
						listDescription.add(books.getString("descricao"));
						listQuantity.add(books.getString("quantidade_estoque"));
						
						if (books.getString("quantidade_alugada").equals("null")) {
							listRented.add("0");
				    	} else {
				    		listRented.add(books.getString("quantidade_alugada")); 
				    	}
						
						total++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				carregaSpec1();	
		}
	}
	
	public void onScroll(AbsListView view, ArrayList<String> name, int position,
			Context context) {
		
			if (position == name.size()) {
				if (total < 10) {
					total = 0;
					countItems = 10;
					pagesFinished = true;
				} else {
					total = 0;
					ProgressDialog progressdialog = ProgressDialog.show(context, null, "Carregando");
					nextPage(progressdialog);
				}
		}
	}
	
		public void nextPage(ProgressDialog dialog) {
			if (pagesFinished == false) {
			page++;
			modelBook.getBook(userMail, userPassword, page, null, false);
		}
			dialog.dismiss();
		}
		
		public void carregaSpec1() {
			
		modelRegisterRent = new ModelRegisterRent();
		modelRegisterRent.setCurrentContext(this);
		modelRegisterRent.addDelegate(this);
		
		RegisterBook = (ImageButton) findViewById(R.id.btn_register_book);
		FindBook = (EditText) findViewById(R.id.find_book);
		
		FindBook.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (event.getAction() != KeyEvent.ACTION_DOWN)

			return false;

			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				
			searchEntered();
				}
				return false;
			}
			});
		
		adapter = new BookArrayAdapter(this, listName, listAuthor);
		listView = (ListView) findViewById(R.id.list_book);
		listView.setAdapter(adapter);
		if (listView.getCount() >= 11 && scrollPrimaryOrder == true) {
			listView.setSelection(listView.getCount() - 12);	
		} 
		if (listView.getCount() > 10) {
			scrollPrimaryOrder = true;
		}
		
		searchCommit = (ImageButton) findViewById(R.id.search_book_commit);
		
		searchCommit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchEntered();
			}
		});
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                    final int index, long arg3) {
            	if (userAdminPermitted == true ){
            		ArrayList<String> itens = new ArrayList<String>();
	            	
					  itens.add("Editar");
					  itens.add("Excluir");
					
					  ArrayAdapter<String> adapterOp = new ArrayAdapter<String>(BooksActivity.this, R.layout.item_alerta_detail_book, itens);
					  final AlertDialog.Builder dialog = new AlertDialog.Builder(BooksActivity.this);
					  dialog.setTitle(listName.get(index));
					
					  dialog.setSingleChoiceItems(adapterOp, 0, new DialogInterface.OnClickListener() {
						  
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
			            	arg0.dismiss();
			                if (arg1 == 0){
			                	if (verifyConnection() == false) {
						        	alertConnection();
						        } else {
				                	Intent i = new Intent(BooksActivity.this, EditBookActivity.class);
				                	i.putExtra("idBook", listID.get(index));
				    	        	i.putExtra("currentName", listName.get(index));
				    	        	i.putExtra("currentAuthor", listAuthor.get(index));
				    	        	i.putExtra("currentQuantity", listQuantity.get(index));
				    	        	i.putExtra("currentDescription", listDescription.get(index));
				                	startActivity(i);
				                	finish();
						        }
			                } else 
			                	if (arg1 == 1){
			                		AlertDialog.Builder dialog = new AlertDialog.Builder(BooksActivity.this);
			                		dialog.setTitle(listName.get(index));
			                		dialog.setMessage("Deseja realmente excluir este livro ?");
			                		dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if (verifyConnection() == false) {
									        	alertConnection();
									        } else {
									        	excludeModelBoolean = true;
									        	modelDeleteBook.deletebook(userMail, userPassword, listID.get(index));
									        	
												listID.remove(index);
												listName.remove(index);
												listAuthor.remove(index);
												listQuantity.remove(index);
												listRented.remove(index);
												listDescription.remove(index);
												adapter.notifyDataSetChanged();
									        }
										}
									});
			                		
			                		dialog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
			                		
			                		dialog.show();
			                }
			                
			            }
			        });
				
				dialog.show();
            	} else {
            		
            	}
                return true;
            }
		}); 
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
    	  @Override
    	  public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
    		  
    		  ArrayList<String> itens = new ArrayList<String>();
    		  itens.add("Autor: "+ listAuthor.get(position));
    		  itens.add("Quantidade: "+ listQuantity.get(position));
    		  
    		  final int disponiveis =  Integer.parseInt(listQuantity.get(position)) - Integer.parseInt(listRented.get(position));
    		  
    		  itens.add("Disponíveis: " + disponiveis);
    		  itens.add(listDescription.get(position));

    		  ArrayAdapter<String> adapterOp = new ArrayAdapter<String>(BooksActivity.this, R.layout.item_alerta_detail_book, itens);
    		  AlertDialog.Builder builder = new AlertDialog.Builder(BooksActivity.this);
    		  builder.setTitle(listName.get(position));

    		  builder.setSingleChoiceItems(adapterOp, 0, new DialogInterface.OnClickListener() {
		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
    		  
    		  if (disponiveis > 0) {
    			  builder.setPositiveButton("Alugar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
								AlertDialog.Builder dialogRegister = new AlertDialog.Builder(BooksActivity.this);
								dialogRegister.setMessage("Deseja realmente alugar ?");
								dialogRegister.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										try {
											
											if (verifyConnection() == false) {
									        	alertConnection();
									        } else {
									        	modelRegisterRent.registrateRent(userMail, userPassword, userId, listID.get(position), true);
												listID.remove(position);
												listName.remove(position);
												listAuthor.remove(position);
												listQuantity.remove(position);
												listRented.remove(position);
												listDescription.remove(position);
												adapter.notifyDataSetChanged();
												
												RentsActivity rents = new RentsActivity();
												rents.refreshList();
												
												Toast.makeText(BooksActivity.this, "Livro alugado com sucesso", Toast.LENGTH_SHORT).show();
									        }
											
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
								
								dialogRegister.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								
								dialogRegister.show();
						}
					});
    		  }
		    		  
		    		  builder.setNeutralButton("Retornar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});

		    		  alerta = builder.create();
		    	      alerta.show();

		    	  }
		    }); 
	    
		RegisterBook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BooksActivity.this, RegisterBookActivity.class);
				startActivity(i);
			}
		});
		}
		
		private void clearAllItems() {
			super.onDestroy();
			searchActive = false;
			page = 1;
			total = 0;
			countItems = 10;
			pagesFinished = false;
			listQuantity.clear();
			listRented.clear();
			excludeModelBoolean = false;
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
			AlertDialog.Builder dialog = new AlertDialog.Builder(BooksActivity.this);
			dialog.setTitle("Verifique sua conexão de internet");
			dialog.setMessage("Você precisa estar conectado para utilizar os serviços da biblioteca online");
			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialog.show();
		} 
		
		public void searchEntered(){
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(FindBook.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			if (verifyConnection() == false) {
	        	alertConnection();
	        } else {
	        	if (FindBook.getText().toString().equals("")) {	
					searchActive = false;
					search = FindBook.getText().toString();
					page = 1;
					total = 0;
					countItems = 10;
					pagesFinished = false;
					listID.clear();
					listName.clear();
					listAuthor.clear();
					listQuantity.clear();
					listRented.clear();
					listDescription.clear();
					adapter.clear();
					
					modelBook.getBook(userMail, userPassword, page, null, searchActive);
					adapter.notifyDataSetChanged();
					
				} else {
					searchActive = true;
					search = FindBook.getText().toString();
					page = 1;
					total = 0;
					countItems = 10;
					pagesFinished = false;
					listID.clear();
					listName.clear();
					listAuthor.clear();
					listQuantity.clear();
					listRented.clear();
					listDescription.clear();
					adapter.clear();
					
					modelBook.getBook(userMail, userPassword, page, search, searchActive);
					adapter.notifyDataSetChanged();
				}
	        }
		}
}
