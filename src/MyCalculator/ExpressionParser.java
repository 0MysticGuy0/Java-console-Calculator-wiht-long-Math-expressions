package MyCalculator;

public interface ExpressionParser<T> {
    T parse(String expression);
}
