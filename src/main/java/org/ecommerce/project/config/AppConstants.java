package org.ecommerce.project.config;

// Creating this was important, so we could change anything here and that change will be reflected where-ever these constants were used, rather than hard-coupling the things

public class AppConstants {
    public static final String PAGE_NUMBER = "0";   // See we have kept their data type as String because, defaultValue attribute takes the value in String format, which is converted into the required type automatically
    public static final String PAGE_SIZE = "5";
    public static final String SORT_CATEGORIES_BY = "categoryName";
    public static final String SORT_DIRECTION = "asc";


    public static final String SORT_PRODUCTS_BY = "productName";

}
