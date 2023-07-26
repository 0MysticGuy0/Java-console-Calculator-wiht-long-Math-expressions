package MyCalculator;
import MyCalculator.CalculatorElements.*;
import MyCalculator.CalculatorElements.Operations.BinaryOperation;
import MyCalculator.CalculatorElements.Operations.Operation;
import MyCalculator.CalculatorElements.Operations.UnaryOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    static final CalculatorElement[]AviableOperations = //Все доступные операции/переменные
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

    private void CalculateByPriority(ArrayList<String> expArr){//просчитать основные действия по приоритету

        for(var priority: CalculatorElement.getAllPriorities()){
            solveOperations(expArr, CalculatorElement.getOperationsWithPriority(priority));
             SolutionBySteps.add(getExpr());
        }
        if(expArr.size()==1 && expArr.get(0).equals("P")) expArr.set(0,Double.toString(Math.PI));
    }

    public ArrayList<String> ExprStack; //массив с эелементами выражения
    private final ArrayList<String> SolutionBySteps=new ArrayList<>();//для вывода решения по шагам
    public Calculator()
    {
        ExprStack=new ArrayList<>();
    }
    public static void printInfo(){
       CalculatorElement.printInfo();
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
        for(int i=(int)n;i>=1;i--) res*=i;
        return res;
    }
    private  String getExpr(){      return ExprStack.stream().collect(Collectors.joining());    }

    private void solveOperations(ArrayList<String> ar, List<Operation> operations)//сократить выражение, решив некоторые действия
    {
        for(int i=0;i<ar.size();i++) {//проходмся по всем элементам выражения
            if(ar.get(i).equals("(")) solveBrackets(i);
            else {
                for (var operation : operations) {
                    //if (i == ar.size() - 2 && operation.isUnaryPostfix()) i += 1; //?????если в конце выражения унарная-постфиксная операция

                    if (ar.get(i).equals(Character.toString(operation.sign))) {

                        double n1 = 1.0, n2 = 1.0, r = 0;

                        if (i != 0 && !operation.isUnaryPrefix())//если не унарная-префиксная операция
                        {
                            if (ar.get(i - 1).equals("P")) n1 = Math.PI;
                            else n1 = Double.parseDouble(ar.get(i - 1));
                            if (!(operation instanceof BinaryOperation)) r = operation.calculate(n1);
                        }

                        if (!operation.isUnaryPostfix())//если не унарная-постфиксная операция
                        {
                            if(i==ar.size()-1 ) break;//если встретили не постфиксную операцию и она - послежний символ в выражении(опечатка ползователя)
                            if (ar.get(i + 1).equals("(")) solveBrackets(i + 1);
                            if (ar.get(i + 1).equals("P")) n2 = Math.PI;
                            else n2 = Double.parseDouble(ar.get(i + 1));
                            if (!(operation instanceof BinaryOperation)) r = operation.calculate(n2);
                        }
                        if (operation instanceof BinaryOperation) r = operation.calculate(n1, n2);

                        String res = Double.toString(r);

                        if (!operation.isUnaryPostfix())//если не унарная-постфиксная операция
                            ar.remove(i + 1);
                        if (i != 0)
                            if (!operation.isUnaryPrefix())//если не унарная-префиксная операция
                            {
                                ar.remove(i);
                                i -= 1;
                            }
                        ar.set(i, res);
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
                        if(ExprStack.size()>0 || AviableOperations[j] instanceof UnaryOperation || AviableOperations[j] instanceof Variable || AviableOperations[j] instanceof Scopes || s=='-')//Если операция в начале выражения и не является  унарной-префиксной - не добавлять
                        {
                            if(s!='-') ExprStack.add(Character.toString(s));
                            else if(i<expr.length()-1)//если встретили -, заменяем на -1* или +(-1)*
                            {
                                if(ExprStack.size()==0 ||( !((expr.charAt(i-1) >='0' && expr.charAt(i-1)<='9') || expr.charAt(i-1)=='.') && !(CalculatorElement.getCalulatorElement(expr.charAt(i-1)) instanceof UnaryOperation && ((Operation)CalculatorElement.getCalulatorElement(expr.charAt(i-1))).isUnaryPostfix() )) ){
                                    //если - в начале выражения
                                    //или перед минусом не число и не унарная-постфиксная операция
                                    ExprStack.add("-1");
                                    ExprStack.add("*");
                                }
                                else{
                                    ExprStack.add("+");
                                    ExprStack.add("-1");
                                    ExprStack.add("*");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
