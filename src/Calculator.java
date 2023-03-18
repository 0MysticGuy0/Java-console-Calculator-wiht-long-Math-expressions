/*
* Made by Bychkovsky Vladislav R.
* */

import java.util.ArrayList;
public class Calculator {

     ArrayList<String> ExprStack; //массив с эелементами выражения
    static final char []AviableOperations ={'*','/','+','-','^','s','(',')','P','i','c','f'};//s-корень i-синус c - косинус P-ПИ
    public static void printInfo(){
        System.out.println("|-------------------------------|");
        System.out.println("| Доступные операции: ");
        System.out.println("| * - умножение");
        System.out.println("| / - деление");
        System.out.println("| + - сложение");
        System.out.println("| - - вычитание");
        System.out.println("| ^ - возведение в степень");
        System.out.println("| sqrt - квадратный корень");
        System.out.println("| (...) - скобки");
        System.out.println("| PI - число ПИ");
        System.out.println("| sin - синус(в градусах!)");
        System.out.println("| cos - косинус(в градусах!)");
        System.out.println("| ! - факториал");
        System.out.println("|-------------------------------|");
    }

    public Calculator(){
        ExprStack=new ArrayList<>();
    }
    public double solve(String expr){//результат!!!!!!!!!!!!1
       // double res=0;
        exprToStack(expr);
        System.out.println(ExprStack);
        solveOperations(ExprStack,'(');
        CalculateByPriority(ExprStack);

        return Double.parseDouble(ExprStack.get(0));
    }

   private void CalculateByPriority(ArrayList<String> expArr){//просчитать основные действия по приоритету
       solveOperations(expArr,'f');
       solveOperations(expArr,'i','c');
       solveOperations(expArr,'^','s');
       solveOperations(expArr,'*','/');
       solveOperations(expArr,'+','-');
       if(expArr.size()==1 && expArr.get(0).equals("P")) expArr.set(0,Double.toString(Math.PI));
   }
    static double calculateSimple(double a,char c,double b){//действия над двумя числами
        switch(c){
            case 'f': return fact(a);
            case 'c': return Math.cos(Math.toRadians(b));
            case 'i': return Math.sin(Math.toRadians(b));
            case 's': return Math.sqrt(b);
            case '^': return Math.pow(a,b);
            case '*': return a*b;
            case '/': return a/b;
            case '+': return a+b;
            case '-': return a-b;
            default: return 0.0;
        }
    }

    static double fact(double n){
        double res=1;
        for(int i=(int)n;i>=1;i--){
            res*=i;
        }
        return res;
    }
    private void solveOperations(ArrayList<String> ar,char ...operations){ //сократить выражение, решив некоторые действия
        for(char c:operations){

            for(int i=0;i<ar.size()-1;i++){
                if(i==ar.size()-2 && ar.get(i+1).equals(Character.toString('f'))) i+=1;
                  if( ar.get(i).equals( Character.toString(c) ) )
                  {
                      if (c == '(') {
                          solveBrackets(i);
                      }
                      else
                      {
                         double n1 = 1.0, n2=1.0;
                         if (c != 's' && c!='i' && c!='c')//если операция не корень и не синус/косинус
                         {
                             if(ar.get(i-1).equals("P")) n1=Math.PI;
                             else       n1 = Double.parseDouble(ar.get(i - 1));
                         }
                         if(c!='f')
                          {
                             if (ar.get(i + 1).equals("P")) n2 = Math.PI;
                             else n2 = Double.parseDouble(ar.get(i + 1));
                          }
                         double r = calculateSimple(n1, c, n2);
                         String res = Double.toString(r);

                         if(c!='f') ar.remove(i + 1);
                         if (c != 's' && c!='i' && c!='c')//если операция не корень и не синус/косинус
                          {
                              ar.remove(i);
                              i -= 1;
                          }
                         ar.set(i, res);
                         // System.out.println(ExprStack);
                  }
                }
            }
        }
        //System.out.println(ar);
    }

    private void solveBrackets(int index){//решение скобок
        ArrayList<String> subExpr=new ArrayList<>();//подвыражение
        int end;//конец подвыражения

        for(end=index+1;end<ExprStack.size();end++){
            if(ExprStack.get(end).equals("("))
            {
                solveBrackets(end);
                end-=1;
            }
            else if(ExprStack.get(end).equals(")"))
            {
                break;
            }
            else{
                String el=ExprStack.get(end);
                subExpr.add(el);
            }
        }
        CalculateByPriority(subExpr);
        for(int j=end;j>index;j--){
            ExprStack.remove(j);
        }
        ExprStack.set(index,subExpr.get(0));
    }

    private String fixExpression(String expr){//убираем ненужное, заменяем нужное
        String fixed=expr;
        fixed =fixed.toLowerCase();
        fixed = fixed.replaceAll(" ","");//удалить пробелы
        fixed = fixed.replaceAll(",",".");//правильный вид десятичной точки
        fixed = fixed.replaceAll("sqrt","s");//корень
        fixed = fixed.replaceAll("pi","P");//ПИ
        fixed = fixed.replaceAll("sin","i");//синус
        fixed = fixed.replaceAll("cos","c");//косинус
        fixed = fixed.replaceAll("!","f");//косинус
        return fixed;
    }

    public void exprToStack(String expr){//помещаем каждый элемент в массив
        ExprStack.clear();
        expr = fixExpression(expr);
        String cur="";

        for(int i=0;i<expr.length();i++){

            if((expr.charAt(i) >='0' && expr.charAt(i)<='9') || expr.charAt(i)=='.'){//сборка числа
                cur+=expr.charAt(i);
                if(i==expr.length()-1 && expr.charAt(i)!='.'){
                    ExprStack.add(cur);
                    cur = "";
                }
            }
            else{//сборка операций
                if(!cur.equals("")) {
                    ExprStack.add(cur);
                    cur = "";
                }
                for(char s:AviableOperations){
                    if(expr.charAt(i) == s){
                        ExprStack.add(Character.toString(s));
                    }
                }
            }
        }

    }
}
