package lshankarrao.travelatease1;


public class ToDoInfo {

    public String getToDoItem() {
        return toDoItem;
    }

    public void setToDoItem(String toDoItem) {
        this.toDoItem = toDoItem;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    String toDoItem;
    Boolean checked;

    public ToDoInfo(String toDoItem, Boolean checked) {
        this.toDoItem = toDoItem;
        this.checked = checked;
    }
}
