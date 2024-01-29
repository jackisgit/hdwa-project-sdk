package com.hdwa.sdk.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 值 对象工具类
 */
public class ValueObjectUtil {

    public static ValueObject compute(String operator, ValueObject a, ValueObject b) {
        if (a.is_null() || b.is_null()) {
            return new ValueObject(null);
        }

        ValueObject result = new ValueObject();
        if (a.type == 0 && b.type == 0) {
            switch (operator) {
                case "+":
                    result.intValue = a.intValue + b.intValue;
                    break;
                case "-":
                    result.intValue = a.intValue - b.intValue;
                    break;
                case "*":
                    result.intValue = a.intValue * b.intValue;
                    break;
                case "/":
                    result.intValue = a.intValue / b.intValue;
                    break;
                case "%":
                    result.intValue = a.intValue % b.intValue;
                    break;
            }
        } else {
            result.type = 1;
            double valuea;
            if (a.type == 0) {
                valuea = a.intValue;
            } else {
                valuea = a.doubleValue;
            }
            double valueb;
            if (b.type == 0) {
                valueb = b.intValue;
            } else {
                valueb = b.doubleValue;
            }
            switch (operator) {
                case "+":
                    result.doubleValue = valuea + valueb;
                    break;
                case "-":
                    result.doubleValue = valuea - valueb;
                    break;
                case "*":
                    result.doubleValue = valuea * valueb;
                    break;
                case "/":
                    result.doubleValue = valuea / valueb;
                    break;
                case "%":
                    result.doubleValue = valuea % valueb;
                    break;
            }
        }
        return result;
    }

    public static boolean stringCompare(String operator, ValueObject b) {
        if (operator.equals("==")) {
            return b.stringValue == null;
        } else if (operator.equals("!=")) {
            return b.stringValue != null;
        }
        return true;
    }

    public static boolean stringCompare(String operator, ValueObject a, ValueObject b) {
        if (a.is_null() && b.is_null()) {
            return operator.equals("==");
        } else if (a.is_null()) {
            return operator.equals("<") || operator.equals("<=") || operator.equals("!=");
        } else if (b.is_null()) {
            return operator.equals(">") || operator.equals(">=") || operator.equals("!=");
        }

        if (operator.equals("contains")) {
            return a.stringValue.contains(b.stringValue);
        } else if (operator.equals("match")) {
            Pattern pattern = Pattern.compile(b.stringValue);
            Matcher matcher = pattern.matcher(a.stringValue);
            return matcher.matches();
        } else {
            int cmp = a.stringValue.compareTo(b.stringValue);
            switch (operator) {
                case "<":
                    return cmp < 0;
                case "<=":
                    return cmp <= 0;
                case ">":
                    return cmp > 0;
                case ">=":
                    return cmp >= 0;
                case "==":
                    return cmp == 0;
                case "!=":
                    return cmp != 0;
            }
        }
        return true;
    }

    public static boolean compare(String operator, ValueObject b) {
        if (operator.equals("==")) {
            return b.stringValue == null;
        } else if (operator.equals("!=")) {
            return b.stringValue != null;
        }
        return true;
    }

    public static boolean compare(String operator, ValueObject a, ValueObject b) {
        if ((a != null && b != null) && (a.is_null() && b.is_null())) {
            return operator.equals("==");
        } else if (a != null) {
            if (a.is_null()) {
                return operator.equals("<") || operator.equals("<=") || operator.equals("!=");
            } else if (b != null && b.is_null()) {
                return operator.equals(">") || operator.equals(">=") || operator.equals("!=");
            }
        }

        if (a != null) {
            if (b != null) {
                if (a.type == 0 && b.type == 0) {
                    switch (operator) {
                        case "<":
                            return a.intValue < b.intValue;
                        case "<=":
                            return a.intValue <= b.intValue;
                        case ">":
                            return a.intValue > b.intValue;
                        case ">=":
                            return a.intValue >= b.intValue;
                        case "==":
                            return Objects.equals(a.intValue, b.intValue);
                        case "!=":
                            return !Objects.equals(a.intValue, b.intValue);
                    }
                } else {
                    double valuea;
                    if (a.type == 0) {
                        valuea = a.intValue;
                    } else {
                        valuea = a.doubleValue;
                    }
                    double valueb;
                    if (b.type == 0) {
                        valueb = b.intValue;
                    } else {
                        valueb = b.doubleValue;
                    }
                    switch (operator) {
                        case "<":
                            return valuea < valueb;
                        case "<=":
                            return valuea <= valueb;
                        case ">":
                            return valuea > valueb;
                        case ">=":
                            return valuea >= valueb;
                        case "==":
                            return valuea == valueb;
                        case "!=":
                            return valuea != valueb;
                    }
                }
            }
        }
        return true;
    }
}
