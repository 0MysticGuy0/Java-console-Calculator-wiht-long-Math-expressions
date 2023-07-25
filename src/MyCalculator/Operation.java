package MyCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Operation
{
    public final int priority;//приоритет операции(чем ниже, тем первее идет операция)
    public final char sign;//как эту операцию видит программа
    public final OperationType type;
    public final String syntax;//как эту операцию пишет пользователь
    public final String description;
    private final OperationFunction action;//что должна делать операция
    private static final HashMap<Character,Operation> operationsCash=new HashMap<Character, Operation>();//кэш всех созданных операций


    public Operation(int priority,char operation,OperationType type,String syntax,OperationFunction operationFunction,String description)
    {

        sign =operation;
        this.type=type;
        this.syntax=syntax;
        this.action=operationFunction;
        this.description=description;

        if(priority<0 && !( this.type.equals(OperationType.Other) || this.type.equals(OperationType.Variable))) this.priority=Integer.MAX_VALUE;
        else if(priority>=0 && ( this.type.equals(OperationType.Other) || this.type.equals(OperationType.Variable)))    this.priority=-1;
        else this.priority=priority;

        operationsCash.put(sign,this);
    }

    public double calculate(double a,double b){return action.calculate(a,b);}

    public static Operation getOperation(Character sign){
        Operation res=operationsCash.get(sign);
        if(res!=null) return res;
        throw new NullPointerException("No such operation ("+sign+")!!!");
    }
    public static ArrayList<Operation> getAllOperations(){
        return new ArrayList<>(operationsCash.values());
    }
    public static ArrayList<Operation> getOperationsWithPriority(int prioryty){
        return new ArrayList<>(operationsCash.values().stream()
                .filter(o->o.priority==prioryty).toList());
    }
    public static List<Integer> getAllPriorities(){
        return operationsCash.values().stream()
                .map(o->o.priority)
                .filter(n->n>=0)
                .distinct().sorted().toList();
    }

    public static void printInfo(){
        System.out.println("|-------------------------------|");
        System.out.println("| Доступные операции: ");
        operationsCash.values().
                forEach(o-> System.out.printf("| %s%s%s\t-\t%s\n",(o.type.equals(OperationType.Unary_postfix)?"(...)":""),o.syntax,(o.type.equals(OperationType.Unary_prefix)?"(...)":""),o.description));
        System.out.println("|-------------------------------|");
    }

}
