package org.romeda.cookbook;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for RecipeProvider
 */
public final class Cookbook {
    public static final String AUTHORITY = "org.romeda.provider.Cookbook";

    // This class cannot be instantiated
    private Cookbook() {}
    
    /**
     * Recipes table
     */
    public static final class Recipes implements BaseColumns {
        // This class cannot be instantiated
        private Recipes() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of recipes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.romeda.recipe";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single recipe.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.romeda.recipe";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The title of the recipe
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The recipe itself
         * <P>Type: TEXT</P>
         */
        public static final String RECIPE = "recipe";
        
        /**
         * The source of the recipe
         * <P>Type: URI</P>
         */
        public static final String SOURCE_URI = "source_uri";

        /**
         * The timestamp for when the recipe was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the recipe was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
    }
}