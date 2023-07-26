package MyCalculator.CalculatorElements;

import MyCalculator.CalculatorElements.Operations.Operation;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class CalculatorElement
{
    public final char sign;//как эту операцию видит программа
    public final String syntax;//как эту операцию пишет пользователь
    public final String description;
    private static final HashMap<Character, CalculatorElement> elementsCash=new HashMap<Character, CalculatorElement>();//кэш всех созданных операций

    protected CalculatorElement(char sign, String syntax, String description){
        this.sign=sign;
        this.syntax=syntax;
        this.description=description;

        elementsCash.put(this.sign,this);
    }

    public static CalculatorElement getCalulatorElement(Character sign){
        CalculatorElement res=elementsCash.get(sign);
        if(res!=null) return res;
        throw new NullPointerException("No such operation ("+sign+")!!!");
    }
    public static ArrayList<Operation> getAllOperations(){
        return new ArrayList<>(
                elementsCash.values().stream()
                        .filter(el->el instanceof Operation)
                        .map(el->(Operation)el).toList() );
    }
    public static ArrayList<Operation> getOperationsWithPriority(int prioryty){
        return new ArrayList<>(getAllOperations().stream()
                .filter(o->o.priority==prioryty).toList() );
    }
    public static Integer[] getAllPriorities(){
        return getAllOperations().stream()
                .map(o->o.priority)
                .distinct().sorted().toList().toArray(Integer[]::new);
    }

    public static void printInfo(){
        System.out.println("|-------------------------------|");
        System.out.println("|>---{ Доступные переменные: ");
        elementsCash.values().stream().filter(el->el instanceof Variable).toList().forEach(o-> System.out.printf("| %s\t-\t%s\n",o.syntax,o.description));
        System.out.println("|-------------------------------|");
        System.out.println("|>---{ Доступные операции: ");
        getAllOperations().
                forEach(o-> System.out.printf("| %s%s%s\t-\t%s\n",(o.isUnaryPostfix() ?"(...)":""),o.syntax,(o.isUnaryPrefix() ?"(...)":""),o.description));
        System.out.println("|-------------------------------|");
        System.out.println("|>---{ Другое: ");
        elementsCash.values().stream().filter(el->el instanceof Scopes).toList().forEach(o-> System.out.printf("| %s\t-\t%s\n",o.syntax,o.description));
        System.out.println("|-------------------------------|");
    }

}
