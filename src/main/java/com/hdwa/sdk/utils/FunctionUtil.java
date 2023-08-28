package com.hdwa.sdk.utils;

import java.lang.reflect.Method;

public class FunctionUtil {

    public static ValueObject constant(String constant) {
        ValueObject result = new ValueObject();
        result.type = 1;
        if (constant.equals("PI")) {
            result.doubleValue = Math.PI;
        } else if (constant.equals("E")) {
            result.doubleValue = Math.E;
        }
        return result;
    }

    public static ValueObject compute(String function) {
        ValueObject result = new ValueObject();
        result.type = 1;
        Class<?> mathClass = Math.class;
        try {
            Method method = mathClass.getMethod(function, new Class[]{});
            result.doubleValue = (Double) method.invoke(mathClass, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ValueObject compute(String function, ValueObject a) {
        if (a.is_null()) {
            return new ValueObject(null);
        }

        ValueObject result = new ValueObject();
        result.type = 1;
        double valuea;
        if (a.type == 0) {
            valuea = a.intValue;
        } else {
            valuea = a.doubleValue;
        }
        Class<?> mathClass = Math.class;
        try {
            Method method = mathClass.getMethod(function, new Class[]{double.class});
            result.doubleValue = (Double) method.invoke(mathClass, new Object[]{valuea});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ValueObject compute(String function, ValueObject a, ValueObject b) {
        if (a.is_null() || b.is_null()) {
            return new ValueObject(null);
        }

        ValueObject result = new ValueObject();
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
        Class<?> mathClass = Math.class;
        try {
            Method method = mathClass.getMethod(function, new Class[]{double.class, double.class});
            result.doubleValue = (Double) method.invoke(mathClass, new Object[]{valuea, valueb});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
