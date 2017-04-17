package lshankarrao.travelatease1;

class ContactInfo {
    String name;
    String email;
    int favourite;
    int checked;

    ContactInfo() {
    }

    ContactInfo(String name, String email, int favourite, int checked) {
        this.name = name;
        this.email = email;
        this.favourite = favourite;
        this.checked = checked;
    }


    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }
}
