package org.romeda.cookbook;


import org.romeda.cookbook.Cookbook.Recipes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class RecipeViewer extends Activity {
	
    private static final String[] PROJECTION = new String[] {
        Recipes._ID, // 0
        Recipes.RECIPE, // 1
    };
    private static final int COLUMN_INDEX_RECIPE = 1;
    
    private Cursor mCursor;
    private Uri mUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
        final Intent intent = getIntent();
		mUri = intent.getData();
		
		String recipe = "Error retrieving recipe. Hrmm.";
		
		mCursor = managedQuery(mUri, PROJECTION, null, null, null);
		if (mCursor != null) {
	        mCursor.moveToFirst();
			recipe = mCursor.getString(COLUMN_INDEX_RECIPE);
		}
		
        TextView tv = new TextView(this);
        
        tv.setText(recipe);
        setContentView(tv);
	}
	
	static void show(Context context, Cookbook recipe) {
		final Intent intent = new Intent(context, RecipeViewer.class);
		
		context.startActivity(intent);
	}
}