package MyCalculator.CalculatorElements.Operations;

import MyCalculator.CalculatorElements.CalculatorElement;

public abstract class Operation extends CalculatorElement {
    public final int priority;//приоритет операции(чем ниже, тем первее идет операция)
    protected final OperationFunction action;//что должна делать операция

    Operation(int priority, char sign, OperationFunction operationFunction, String syntax, String description){
        super(sign, syntax, description);
        this.priority=priority;
        this.action=operationFunction;
    }

    public boolean isBinary(){return this instanceof BinaryOperation;}
    public boolean isUnaryPrefix(){return (this instanceof UnaryOperation && ((UnaryOperation)this).isPrefix);}
    public boolean isUnaryPostfix(){return (this instanceof UnaryOperation && !((UnaryOperation)this).isPrefix);}

    public double calculate(double a,double b){return 0;}
    public double calculate(double a){return calculate(a,1);}
}
