
public class Transaction {

    private int id;
    private String title;
    private double amount;
    private String type;
    private String category;
    private String date;

    // Constructor
    public Transaction(int id, String title, double amount,
                       String type, String category, String date) {

        this.id = id;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    // Display transaction details
    public void displayDetails() {

        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Amount: " + amount);
        System.out.println("Type: " + type);
        System.out.println("Category: " + category);
        System.out.println("Date: " + date);
    }
}
