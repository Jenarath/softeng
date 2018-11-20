package delish.jenarath.com.delishapplication;

/**
 * Created by admin on 18/11/2018 AD.
 */

public class Category {

    private int CategoryID;
    private String categoryName;

    public Category(int CategoryID, String categoryName){
        this.CategoryID = CategoryID;
        this.categoryName = categoryName;
    }

    public int getId(){
        return this.CategoryID;
    }

    public String getName(){
        return this.categoryName;
    }

}
