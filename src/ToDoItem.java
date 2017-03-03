import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jeffbrinkley on 2/6/17.
 */
public class ToDoItem {
    int id;
    public String text;
    public boolean isDone;
    public int user_id; // or maybe owner? - in the db, it will be an id
    public String user_name;


    public ToDoItem(int id, String text, boolean isDone, int user_id) {
        this.id = id;
        this.text = text;
      this.isDone = isDone;
      this.user_id = user_id;
    }

    public ToDoItem(int id, String text, boolean isDone, String user_name) {
        this.id = id;
        this.text = text;
        this.isDone = isDone;
        this.user_name = user_name;

    }

}
