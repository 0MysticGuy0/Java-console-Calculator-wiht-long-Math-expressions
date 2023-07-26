package MyCalculator.CalculatorElements.Operations;

public  class UnaryOperation extends Operation{
    public final boolean isPrefix;
    public UnaryOperation(int priority, char sign, UnaryOperationFunction operationFunction, boolean isPrefix, String syntax, String description){
        super(priority, sign, operationFunction, syntax, description);
        this.isPrefix=isPrefix;
    }

    @Override
    public double calculate(double a) {
        return ((UnaryOperationFunction)action).calculate(a);
    }
}



