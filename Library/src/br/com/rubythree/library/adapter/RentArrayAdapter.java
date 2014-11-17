package br.com.rubythree.library.adapter;

import java.util.ArrayList;

import br.com.rubythree.library.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RentArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  ArrayList<String> name = new ArrayList<String>();
  ArrayList<String> author = new ArrayList<String>();

  public RentArrayAdapter(Context context, ArrayList<String> name, ArrayList<String> author) {
    super(context, R.layout.row_list_rent, name);
    this.context = context;
    this.name = name;
    this.author = author;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.row_list_rent, parent, false);
    
    TextView txtName = (TextView) rowView.findViewById(R.id.name_book_item);
    txtName.setText(name.get(position));
    
    TextView txtAuthor = (TextView) rowView.findViewById(R.id.author_book_item);
    txtAuthor.setText(author.get(position));
   
   return rowView;
  }
} 