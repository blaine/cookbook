package org.romeda.cookbook;

import java.util.HashMap;

import org.romeda.cookbook.Cookbook.Recipes;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of recipes. Each recipe has a title, the recipe
 * itself, a creation date and a modified data.
 */
public class RecipeProvider extends ContentProvider {

    private static final String TAG = "RecipeProvider";

    private static final String DATABASE_NAME = "cookbook.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RECIPES_TABLE_NAME = "recipes";

    private static HashMap<String, String> sRecipesProjectionMap;

    private static final int RECIPES = 1;
    private static final int RECIPE_ID = 2;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + RECIPES_TABLE_NAME + " ("
                    + Recipes._ID + " INTEGER PRIMARY KEY,"
                    + Recipes.TITLE + " TEXT,"
                    + Recipes.RECIPE + " TEXT,"
                    + Recipes.SOURCE_URI + " TEXT,"
                    + Recipes.CREATED_DATE + " INTEGER,"
                    + Recipes.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS recipes");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            qb.setTables(RECIPES_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
            break;

        case RECIPE_ID:
            qb.setTables(RECIPES_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
            qb.appendWhere(Recipes._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Cookbook.Recipes.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            return Recipes.CONTENT_TYPE;

        case RECIPE_ID:
            return Recipes.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != RECIPES) {
            throw new IllegalArgumentException("Unknown URI on insert attempt: " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(Cookbook.Recipes.CREATED_DATE) == false) {
            values.put(Cookbook.Recipes.CREATED_DATE, now);
        }

        if (values.containsKey(Cookbook.Recipes.MODIFIED_DATE) == false) {
            values.put(Cookbook.Recipes.MODIFIED_DATE, now);
        }

        if (values.containsKey(Cookbook.Recipes.TITLE) == false) {
            Resources r = Resources.getSystem();
            values.put(Cookbook.Recipes.TITLE, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(Cookbook.Recipes.RECIPE) == false) {
            values.put(Cookbook.Recipes.RECIPE, "");
        }
        
        if (values.containsKey(Cookbook.Recipes.SOURCE_URI) == false) {
        	values.put(Cookbook.Recipes.SOURCE_URI, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(RECIPES_TABLE_NAME, Recipes.RECIPE, values);
        if (rowId > 0) {
            Uri recipeUri = ContentUris.withAppendedId(Cookbook.Recipes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(recipeUri, null);
            return recipeUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            count = db.delete(RECIPES_TABLE_NAME, where, whereArgs);
            break;

        case RECIPE_ID:
            String recipeId = uri.getPathSegments().get(1);
            count = db.delete(RECIPES_TABLE_NAME, Recipes._ID + "=" + recipeId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            count = db.update(RECIPES_TABLE_NAME, values, where, whereArgs);
            break;

        case RECIPE_ID:
            String recipeId = uri.getPathSegments().get(1);
            count = db.update(RECIPES_TABLE_NAME, values, Recipes._ID + "=" + recipeId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Cookbook.AUTHORITY, "recipes", RECIPES);
        sUriMatcher.addURI(Cookbook.AUTHORITY, "recipes/#", RECIPE_ID);

        sRecipesProjectionMap = new HashMap<String, String>();
        sRecipesProjectionMap.put(Recipes._ID, Recipes._ID);
        sRecipesProjectionMap.put(Recipes.TITLE, Recipes.TITLE);
        sRecipesProjectionMap.put(Recipes.RECIPE, Recipes.RECIPE);
        sRecipesProjectionMap.put(Recipes.CREATED_DATE, Recipes.CREATED_DATE);
        sRecipesProjectionMap.put(Recipes.MODIFIED_DATE, Recipes.MODIFIED_DATE);
    }
}