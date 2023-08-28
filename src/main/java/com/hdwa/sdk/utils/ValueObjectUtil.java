package com.hdwa.sdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueObjectUtil {

    public static ValueObject compute(String operator, ValueObject a, ValueObject b) {
        if (a.is_null() || b.is_null()) {
            return new ValueObject(null);
        }

        ValueObject result = new ValueObject();
        if (a.type == 0 && b.type == 0) {
            if (operator.equals("+")) {
                result.intValue = a.intValue + b.intValue;
            } else if (operator.equals("-")) {
                result.intValue = a.intValue - b.intValue;
            } else if (operator.equals("*")) {
                result.intValue = a.intValue * b.intValue;
            } else if (operator.equals("/")) {
                result.intValue = a.intValue / b.intValue;
            } else if (operator.equals("%")) {
                result.intValue = a.intValue % b.intValue;
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
            if (operator.equals("+")) {
                result.doubleValue = valuea + valueb;
            } else if (operator.equals("-")) {
                result.doubleValue = valuea - valueb;
            } else if (operator.equals("*")) {
                result.doubleValue = valuea * valueb;
            } else if (operator.equals("/")) {
                result.doubleValue = valuea / valueb;
            } else if (operator.equals("%")) {
                result.doubleValue = valuea % valueb;
            }
        }
        return result;
    }

    public static boolean stringcompare(String operator, ValueObject b) {
        if (operator.equals("==")) {
            return b.stringValue == null;
        } else if (operator.equals("!=")) {
            return b.stringValue != null;
        }
        return true;
    }

    public static boolean stringcompare(String operator, ValueObject a, ValueObject b) {
        if (a.is_null() && b.is_null()) {
            if (operator.equals("==")) {
                return true;
            } else {
                return false;
            }
        } else if (a.is_null()) {
            if (operator.equals("<") || operator.equals("<=") || operator.equals("!=")) {
                return true;
            } else {
                return false;
            }
        } else if (b.is_null()) {
            if (operator.equals(">") || operator.equals(">=") || operator.equals("!=")) {
                return true;
            } else {
                return false;
            }
        }

        if (operator.equals("contains")) {
            return a.stringValue.contains(b.stringValue);
        } else if (operator.equals("match")) {
            Pattern pattern = Pattern.compile(b.stringValue);
            Matcher matcher = pattern.matcher(a.stringValue);
            return matcher.matches();
        } else {
            int cmp = a.stringValue.compareTo(b.stringValue);
            if (operator.equals("<")) {
                return cmp < 0;
            } else if (operator.equals("<=")) {
                return cmp <= 0;
            } else if (operator.equals(">")) {
                return cmp > 0;
            } else if (operator.equals(">=")) {
                return cmp >= 0;
            } else if (operator.equals("==")) {
                return cmp == 0;
            } else if (operator.equals("!=")) {
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
        if (a.is_null() && b.is_null()) {
            if (operator.equals("==")) {
                return true;
            } else {
                return false;
            }
        } else if (a.is_null()) {
            if (operator.equals("<") || operator.equals("<=") || operator.equals("!=")) {
                return true;
            } else {
                return false;
            }
        } else if (b.is_null()) {
            if (operator.equals(">") || operator.equals(">=") || operator.equals("!=")) {
                return true;
            } else {
                return false;
            }
        }

        if (a.type == 0 && b.type == 0) {
            if (operator.equals("<")) {
                return a.intValue < b.intValue;
            } else if (operator.equals("<=")) {
                return a.intValue <= b.intValue;
            } else if (operator.equals(">")) {
                return a.intValue > b.intValue;
            } else if (operator.equals(">=")) {
                return a.intValue >= b.intValue;
            } else if (operator.equals("==")) {
                return a.intValue == b.intValue;
            } else if (operator.equals("!=")) {
                return a.intValue != b.intValue;
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
            if (operator.equals("<")) {
                return valuea < valueb;
            } else if (operator.equals("<=")) {
                return valuea <= valueb;
            } else if (operator.equals(">")) {
                return valuea > valueb;
            } else if (operator.equals(">=")) {
                return valuea >= valueb;
            } else if (operator.equals("==")) {
                return valuea == valueb;
            } else if (operator.equals("!=")) {
                return valuea != valueb;
            }
        }
        return true;
    }
}
