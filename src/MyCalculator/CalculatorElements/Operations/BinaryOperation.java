package MyCalculator.CalculatorElements.Operations;

public class BinaryOperation extends Operation{
    public BinaryOperation(int priority, char sign, BinaryOperationFunction operationFunction, String syntax, String description){
        super(priority, sign, operationFunction, syntax, description);
    }

    @Override
    public double calculate(double a, double b) {
        return ((BinaryOperationFunction)action).calculate(a,b);
    }
}
