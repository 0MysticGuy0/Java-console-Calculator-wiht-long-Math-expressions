package MyCalculator;

import MyCalculator.CalculatorElements.CalculatorElement;
import MyCalculator.CalculatorElements.Operations.Operation;
import MyCalculator.CalculatorElements.Operations.UnaryOperation;
import MyCalculator.CalculatorElements.Scopes;
import MyCalculator.CalculatorElements.Variable;

import java.util.ArrayList;

public class MathExpressionParser implements ExpressionParser<ArrayList<String>>{

    private CalculatorElement[] availableElements;

    public MathExpressionParser(CalculatorElement[] availableElements) {
        this.availableElements = availableElements;
    }

    private String fixExpression(String expr){//убираем ненужное, заменяем нужное
        String fixed=expr.toLowerCase()
                .replaceAll(" ","")//удалить пробелы
                .replaceAll(",",".");//правильный вид десятичной точки

        //Замена из пользоваткльского вида на "компьтерный"
        for (CalculatorElement availableElement : availableElements) {
            if (!availableElement.syntax.equals(Character.toString(availableElement.sign))) //если синтаксис и вид у операции разные
                fixed = fixed.replaceAll(availableElement.syntax, Character.toString(availableElement.sign));
        }
        return fixed;
    }

    public ArrayList<String> parse(String expr){//получение выражения, разделение для дальнейших расчетов

        ArrayList<String> parsedExpression=new ArrayList<>();
        expr = fixExpression(expr);
        String currentElement="";

        for(int i=0;i<expr.length();i++)
        {
            if((expr.charAt(i) >='0' && expr.charAt(i)<='9') || expr.charAt(i)=='.')//сборка числа
            {
                currentElement+=expr.charAt(i);//собираем число
                if(i==expr.length()-1){
                    parsedExpression.add(currentElement);
                    currentElement = "";
                }
            }

            else//сборка операций/переменных
            {
                if(!currentElement.equals("")) //если прошлым элементом было число
                {
                    parsedExpression.add(currentElement);
                    currentElement = "";
                }
                for(int j = 0; j< availableElements.length; j++)//проверяем встретившиеся операции/переменный
                {
                    char s= availableElements[j].sign;
                    if(expr.charAt(i) == s)
                    {
                        if(parsedExpression.size()>0 || availableElements[j] instanceof UnaryOperation || availableElements[j] instanceof Variable || availableElements[j] instanceof Scopes || s=='-')//Если операция в начале выражения и не является  унарной-префиксной - не добавлять
                        {
                            if(s!='-') parsedExpression.add(Character.toString(s));
                            else if(i<expr.length()-1)//если встретили -, заменяем на -1* или +(-1)*
                            {
                                if(parsedExpression.size()==0 ||( !((expr.charAt(i-1) >='0' && expr.charAt(i-1)<='9') || expr.charAt(i-1)=='.') && !(CalculatorElement.getCalulatorElement(expr.charAt(i-1)) instanceof UnaryOperation && ((Operation)CalculatorElement.getCalulatorElement(expr.charAt(i-1))).isUnaryPostfix() )) ){
                                    //если - в начале выражения
                                    //или перед минусом не число и не унарная-постфиксная операция
                                    parsedExpression.add("-1");
                                    parsedExpression.add("*");
                                }
                                else{
                                    parsedExpression.add("+");
                                    parsedExpression.add("-1");
                                    parsedExpression.add("*");
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        return parsedExpression;
    }
}
