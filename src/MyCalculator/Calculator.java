package MyCalculator;
import MyCalculator.CalculatorElements.*;
import MyCalculator.CalculatorElements.Operations.BinaryOperation;
import MyCalculator.CalculatorElements.Operations.Operation;
import MyCalculator.CalculatorElements.Operations.UnaryOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    static final CalculatorElement[] availableElements = //Все доступные операции/переменные
            {
                    new Scopes('(',"(","открывающая скобка приоритета"),
                    new Scopes(')',")","закрывающая скобка приоритета"),
                    /////
                    new Variable('P',"pi","число Пи"),
                    //
                    new BinaryOperation(5,'+',(Double::sum),                                 "+",   "сложение"),
                    new BinaryOperation(5,'-',((a,b)->a-b),                                  "-",   "вычитание"),
                    new BinaryOperation(4,'*',((a,b)->a*b),                                  "*",   "умножение"),
                    new BinaryOperation(4,'/',((a,b)->a/b),                                  "/",   "деление"),
                    new BinaryOperation(3,'^',(Math::pow),                                   "^",   "возведение в степень"),

                    new UnaryOperation( 3,'s',(Math::sqrt),                     true, "sqrt","извлечение квадратного корня"),
                    new UnaryOperation( 2,'f',(Calculator::fact),               false,"!",   "факториал числа"),
                    new UnaryOperation( 1,'i',(a->Math.sin(Math.toRadians(a)) ),true, "sin", "синус(в градусах!)"),
                    new UnaryOperation( 1,'c',(a->Math.cos(Math.toRadians(a)) ),true, "cos", "косинус(в градусах!)"),
                    new UnaryOperation( 0,'a',( Math::abs ),                    true, "abs", "модуль числа"),
            };
    private ExpressionParser<ArrayList<String>> expressionParser=new MathExpressionParser(availableElements);
    public ArrayList<String> ExprStack=new ArrayList<>(); //массив с эелементами выражения
    private final ArrayList<String> SolutionBySteps=new ArrayList<>();//для вывода решения по шагам


    public double solve(String expr){//результат
        ExprStack = (ArrayList<String>) expressionParser.parse(expr);
        CalculateByPriority(ExprStack);

        SolutionBySteps.stream().distinct().toList()//убираем повторы
                .forEach(System.out::println);//вывод решения по шагам

        return Double.parseDouble(ExprStack.get(0));
    }
    private void CalculateByPriority(ArrayList<String> expArr){//просчитать основные действия по приоритету

        for(var priority: CalculatorElement.getAllPriorities()){
            solveOperations(expArr, CalculatorElement.getOperationsWithPriority(priority));
             SolutionBySteps.add(getExpr());
        }
        if(expArr.size()==1 && expArr.get(0).equals("P")) expArr.set(0,Double.toString(Math.PI));
    }
    public static void printInfo(){
       CalculatorElement.printInfo();
    }

    public static double fact(double n)//Факториал
    {
        double res=1;
        for(int i=(int)n;i>=1;i--) res*=i;
        return res;
    }
    private  String getExpr(){      return ExprStack.stream().collect(Collectors.joining(" "));    }

    private void solveOperations(ArrayList<String> exp, List<Operation> operationsToSolve)//сократить выражение, решив некоторые действия
    {
        for(int i=0;i<exp.size();i++) {//проходмся по всем элементам выражения

            if(exp.get(i).equals("(")) solveBrackets(i);
            else {
                for (var operation : operationsToSolve) {
                    //if (i == exp.size() - 2 && operation.isUnaryPostfix()) i += 1; //?????если в конце выражения унарная-постфиксная операция

                    if (exp.get(i).equals(Character.toString(operation.sign))) {

                        double n1 = 1.0, n2 = 1.0, r = 0;

                        if (i != 0 && !operation.isUnaryPrefix())//если не унарная-префиксная операция
                        {
                            if (exp.get(i - 1).equals("P")) n1 = Math.PI;
                            else n1 = Double.parseDouble(exp.get(i - 1));
                            if (!(operation instanceof BinaryOperation)) r = operation.calculate(n1);
                        }

                        if (!operation.isUnaryPostfix())//если не унарная-постфиксная операция
                        {
                            if(i==exp.size()-1 ) break;//если встретили не постфиксную операцию и она - послежний символ в выражении(опечатка ползователя)
                            if (exp.get(i + 1).equals("(")) solveBrackets(i + 1);
                            if (exp.get(i + 1).equals("P")) n2 = Math.PI;
                            else n2 = Double.parseDouble(exp.get(i + 1));
                            if (!(operation instanceof BinaryOperation)) r = operation.calculate(n2);
                        }
                        if (operation instanceof BinaryOperation) r = operation.calculate(n1, n2);

                        String res = Double.toString(r);

                        if (!operation.isUnaryPostfix())//если не унарная-постфиксная операция
                            exp.remove(i + 1);
                        if (i != 0)
                            if (!operation.isUnaryPrefix())//если не унарная-префиксная операция
                            {
                                exp.remove(i);
                                i -= 1;
                            }
                        exp.set(i, res);
                        break;
                    }
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
        ExprStack.set(index,subExpr.get(0));
    }

}
