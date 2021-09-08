package aga.gps3;

public class ContactList {
    String newNumber;
    String newContact;
    int newCheckBox;

    public ContactList(String newNumber, String newContact, int newCheckBox) {
        this.newNumber = newNumber;
        this.newContact = newContact;
        this.newCheckBox = newCheckBox;
    }
    public ContactList(String string, String cursorString) {

    }

    public String getNewNumber() {
        return newNumber;
    }

    public void setNewNumber(String newNumber) {
        this.newNumber = newNumber;
    }

    public String getNewContact() {
        return newContact;
    }

    public void setNewContact(String newContact) {
        this.newContact = newContact;
    }

    public int getNewCheckBox() {
        return newCheckBox;
    }

    public void setNewCheckBox(int newCheckBox) {
        this.newCheckBox = newCheckBox;
    }
}
