package br.com.rubythree.library;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class LibraryTabActivity extends TabActivity {

	 private static final String TAB1 = "Biblioteca";
     private static final String TAB2 = "Alugados";
     private static final String TAB3 = "Sessão";
     public static int setTab = 1;
     
		@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.library_tabs);

			TabHost tabHost = getTabHost();

            // TAB BOOKS
            TabSpec booksSpec = tabHost.newTabSpec(TAB1);
            Intent bookIntent = new Intent(this, BooksActivity.class);
            booksSpec.setIndicator(TAB1);
            booksSpec.setContent(bookIntent);

            // TAB RENTS
            TabSpec rentsSpec = tabHost.newTabSpec(TAB2);
            Intent rentIntent = new Intent(this, RentsActivity.class);
            rentsSpec .setIndicator(TAB2);
            rentsSpec.setContent(rentIntent);

            TabSpec settingsSpec = tabHost.newTabSpec(TAB3);
            Intent settingIntent = new Intent(this, SessionActivity.class);
            settingsSpec.setIndicator(TAB3);
            settingsSpec.setContent(settingIntent);
            
            tabHost.addTab(rentsSpec); 
            tabHost.addTab(booksSpec); 
            tabHost.addTab(settingsSpec);
            tabHost.setCurrentTab(setTab);
            
            final int height = 60;
            tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = height;
            tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = height;
            tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = height;
        }
}