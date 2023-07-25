package MyCalculator;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    static final Operation []AviableOperations = //Все доступные операции/переменные
            {
                    new Operation(4,'*',OperationType.Binary,         "*",    ((a,b)->a*b),                           "Умножение"),
                    new Operation(4,'/',OperationType.Binary,         "/",    ((a,b)->a/b),                           "Деление"),
                    new Operation(5,'+',OperationType.Binary,         "+",    (Double::sum),                          "Сумма"),
                    new Operation(5,'-',OperationType.Binary,         "-",    ((a,b)->a-b),                           "Разность"),
                    new Operation(3,'^',OperationType.Binary,         "^",    (Math::pow),                            "Возведение в степень"),
                    new Operation(3,'s',OperationType.Unary_prefix,   "sqrt", ((a,b)->Math.sqrt(b)),                  "Квадратный корень"),
                    new Operation(-1,'(',OperationType.Other,         "(",    ((a,b)->a*b),                           "Скобка"),
                    new Operation(-1,')',OperationType.Other,         ")",    ((a,b)->a*b),                           "Скобка"),
                    new Operation(0,'P',OperationType.Variable,       "pi",   ((a,b)->Math.PI),                       "Число ПИ"),
                    new Operation(1,'i',OperationType.Unary_prefix,   "sin",  ((a,b)->Math.sin(Math.toRadians(b)) ),  "Синус (в градусах)"),
                    new Operation(1,'c',OperationType.Unary_prefix,   "cos",  ((a,b)->Math.cos(Math.toRadians(b))),   "Косинус (в градусах)"),
                    new Operation(2,'f',OperationType.Unary_postfix,  "!",    ((a,b)->fact(a)),                       "Факториал числа"),
                    new Operation(0,'a',OperationType.Unary_prefix,   "abs",  ((a,b)->Math.abs(b)),                   "Модуль числа"),

            };

    private void CalculateByPriority(ArrayList<String> expArr){//просчитать основные действия по приоритету

        for(var priority:Operation.getAllPriorities()){
            solveOperations(expArr,Operation.getOperationsWithPriority(priority));
             SolutionBySteps.add(getExpr());
        }
        if(expArr.size()==1 && expArr.get(0).equals("P")) expArr.set(0,Double.toString(Math.PI));
    }

    public ArrayList<String> ExprStack; //массив с эелементами выражения
    private ArrayList<String> SolutionBySteps=new ArrayList<>();//для вывода решения по шагам
    public Calculator()
    {
        ExprStack=new ArrayList<>();
    }
    public static void printInfo(){
       Operation.printInfo();
    }
    public double solve(String expr){//результат
        SetExpr(expr);
        CalculateByPriority(ExprStack);
        SolutionBySteps.stream().distinct().toList()//убираем повторы
                .forEach(System.out::println);//вывод решения по шагам
        return Double.parseDouble(ExprStack.get(0));
    }

    public static double fact(double n)//Факториал
    {
        double res=1;
        for(int i=(int)n;i>=1;i--){
            res*=i;
        }
        return res;
    }

    private  String getExpr()
    {
        return ExprStack.stream().collect(Collectors.joining());
    }
    private void solveOperations(ArrayList<String> ar, List<Operation> operations)//сократить выражение, решив некоторые действия
    {
        for(int i=0;i<ar.size()-1;i++) {

            for(var operation:operations) {
                if (i == ar.size() - 2 && operation.type.equals(OperationType.Unary_postfix))    i += 1; //если в конце выражения унарная-постфиксная операция

                if(ar.get(i).equals("(")) solveBrackets(i);
                else if (ar.get(i).equals(Character.toString(operation.sign))) {

                        double n1 = 1.0, n2 = 1.0;
                        if (i != 0)
                            if (!operation.type.equals(OperationType.Unary_prefix))//если не унарная-префиксная операция
                            {
                                if (ar.get(i - 1).equals("P")) n1 = Math.PI;
                                else n1 = Double.parseDouble(ar.get(i - 1));
                            }
                        if (!operation.type.equals(OperationType.Unary_postfix))//если не унарная-постфиксная операция
                        {
                            if (ar.get(i + 1).equals("P")) n2 = Math.PI;
                            else n2 = Double.parseDouble(ar.get(i + 1));
                        }
                        double r = operation.calculate(n1, n2);
                        String res = Double.toString(r);

                        if (!operation.type.equals(OperationType.Unary_postfix))//если не унарная-постфиксная операция
                            ar.remove(i + 1);
                        if (i != 0)
                            if (!operation.type.equals(OperationType.Unary_prefix))//если не унарная-префиксная операция
                            {
                                ar.remove(i);
                                i -= 1;
                            }
                        //System.out.println("ggg "+ar);
                        ar.set(i, res);
                        // System.out.println(ExprStack);
                }
            }
        }
    }

    private void solveBrackets(int index)//решение скобок
    {
        ArrayList<String> subExpr=new ArrayList<>();//подвыражение
        int end;//конец подвыражения

        for(end=index+1;end<ExprStack.size();end++)
        {
            if(ExprStack.get(end).equals("("))
            {
                solveBrackets(end);
                end-=1;
            }
            else if(ExprStack.get(end).equals(")"))
            {
                break;
            }
            else
            {
                subExpr.add( ExprStack.get(end) );
            }
        }
        CalculateByPriority(subExpr);
        ExprStack.subList(index+1,end+1).clear();
        //for(int j=end;j>index;j--) ExprStack.remove(j);
        ExprStack.set(index,subExpr.get(0));
        // System.out.println(ExprStack);
    }

    private String fixExpression(String expr){//убираем ненужное, заменяем нужное
        String fixed=expr;
        fixed =fixed.toLowerCase();
        fixed = fixed.replaceAll(" ","");//удалить пробелы
        fixed = fixed.replaceAll(",",".");//правильный вид десятичной точки
        for(int i=0;i<AviableOperations.length;i++)//Замена из пользоваткльского вида на "компьтерный"
        {
            if(!AviableOperations[i].syntax.equals(Character.toString(AviableOperations[i].sign))) //если синтаксис и вид у операции разные
                fixed=fixed.replaceAll(AviableOperations[i].syntax,Character.toString(AviableOperations[i].sign));
        }
        return fixed;
    }

    public void SetExpr(String expr){//получение выражения, разделение для дальнейших расчетов
        ExprStack.clear();//очищаем от прошлого выражения
        expr = fixExpression(expr);
        String cur="";

        for(int i=0;i<expr.length();i++)
        {
            if((expr.charAt(i) >='0' && expr.charAt(i)<='9') || expr.charAt(i)=='.')//сборка числа
            {
                if(ExprStack.size()>0 && ExprStack.get(ExprStack.size()-1).equals("-"))//Если прошлым элементом был -
                {
                    if(ExprStack.size()==1) ExprStack.remove(0); //если выражение начиналось с -, удаляем -
                    else ExprStack.set(ExprStack.size()-1,"+"); //иначе можно заменить - на +
                    ExprStack.add("-1");
                    ExprStack.add("*");
                    System.out.println("TTTTTT");
                }
                cur+=expr.charAt(i);//собираем число
                if(i==expr.length()-1){
                    ExprStack.add(cur);
                    cur = "";
                }
            }

            else//сборка операций/переменных
            {
                if(!cur.equals("")) //если прошлым элементом было число
                {
                    ExprStack.add(cur);
                    cur = "";
                }
                for(int j=0;j<AviableOperations.length;j++)//проверяем встретившиеся операции/переменный
                {
                    char s=AviableOperations[j].sign;
                    if(expr.charAt(i) == s)
                    {
                        if(ExprStack.size()>0 || AviableOperations[j].type.equals(OperationType.Unary_prefix) || AviableOperations[j].type.equals(OperationType.Variable) || AviableOperations[j].type.equals(OperationType.Other) || s=='-')//Если операция в начале выражения и не является  унарной-префиксной - не добавлять
                        {
                            if(s!='-') ExprStack.add(Character.toString(s));
                            else
                            {
                                if(ExprStack.size()>0) ExprStack.add("+");
                                ExprStack.add("-1");ExprStack.add("*");
                            }
                        }
                    }
                }
            }
        }
    }
}

///////////////////////////////////////////////////////

