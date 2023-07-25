import MyCalculator.Calculator;

import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner inp=new Scanner(System.in);
        Calculator.printInfo();
        Calculator calc=new Calculator();

        System.out.println("Enter math expression: ");
        String exp=inp.nextLine();
        System.out.println(calc.solve(exp));
    }
}