package org.romeda.cookbook;

import org.romeda.cookbook.Cookbook.Recipes;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class RecipeList extends ListActivity {
	private static final String[] PROJECTION = new String[] {
		Recipes._ID,
		Recipes.TITLE,
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Recipes.CONTENT_URI);
        }
        
        ContentValues values = new ContentValues();
        values.put(Recipes.TITLE, "Bread");
        values.put(Recipes.RECIPE, "Bake-ah th-a Bread-a.");
        Uri mUri = getContentResolver().insert(Recipes.CONTENT_URI, values);
        Toast.makeText(this, mUri.toString(), 10);
      
        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, Recipes.DEFAULT_SORT_ORDER);
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
        		cursor, new String[] { Recipes.TITLE }, new int[] { android.R.id.text1 });
        
        setListAdapter(adapter);
        getListView().setTextFilterEnabled(true);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        
        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
            // The caller is waiting for us to return a note selected by
            // the user.  The have clicked on one, so return it now.
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {
            // Launch activity to view/edit the currently selected item
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
    
}

