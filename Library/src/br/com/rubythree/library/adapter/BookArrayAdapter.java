package br.com.rubythree.library.adapter;

import java.util.ArrayList;

import br.com.rubythree.library.BooksActivity;
import br.com.rubythree.library.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BookArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  public ArrayList<String> name = new ArrayList<String>();
  public ArrayList<String> author = new ArrayList<String>();
  BooksActivity books;

  public BookArrayAdapter(Context context, ArrayList<String> name, ArrayList<String> author) {
    super(context, R.layout.row_list_book, name);
    this.context = context;
    this.name = name;
    this.author = author;
  }

  @SuppressLint("ResourceAsColor")
@Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.row_list_book, parent, false);
    
    books = new BooksActivity();
    
    TextView txtName = (TextView) rowView.findViewById(R.id.name_book_item);
    txtName.setText(name.get(position));
    
    TextView txtAuthor = (TextView) rowView.findViewById(R.id.author_book_item);
    txtAuthor.setText(author.get(position));
    
    TextView txtStatus = (TextView) rowView.findViewById(R.id.txt_book_status);
    
    int disponiveis =  Integer.parseInt(BooksActivity.listQuantity.get(position)) - Integer.parseInt(BooksActivity.listRented.get(position));
    String status;
    
    if (disponiveis > 0 ) {
    	status = "";
    } else {
    	status = "Indisponível";
    	txtStatus.setTextColor(Color.GRAY);
    	txtName.setTextColor(Color.GRAY);
    	txtAuthor.setTextColor(Color.GRAY);
    	txtStatus.setText(status);
    }
    
    
   int posicao = (position + 1);
   
	if (BooksActivity.searchActive == false){
		books.onScroll(null, name, posicao, context);
	}
   
   return rowView;
  }
} 