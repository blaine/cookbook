package org.romeda.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for RecipeProvider
 */
public final class Recipe {
    /**
     * Recipes table
     */
    public static final class Recipes implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI
                = Uri.parse("content://org.romeda.provider.Recipe/recipes");

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
         * The source URI for the recipe
         * <P>Type: URI</P>
         */
        public static final String SOURCE_URI = "source_uri";

        /**
         * The timestamp for when the recipe was created
         * <P>Type: INTEGER (long)</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the recipe was last modified
         * <P>Type: INTEGER (long)</P>
         */
        public static final String MODIFIED_DATE = "modified";
    }
}
