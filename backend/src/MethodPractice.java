public class MethodPractice {
    

    public static double calculateBalance(double income , double expense){
        double balance = income -expense;
        return balance;
        
    }
    public static void main(String[] args){
        double result = calculateBalance(15000,6500);
        System.out.println("Remaining Balance: " + result);
        
    }
}
