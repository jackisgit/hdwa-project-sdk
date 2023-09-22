package com.hdwa.sdk.entity.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MatchUtil {
    public static boolean match(String fuhao, Object objValue1, Object objValue2) {
        if (fuhao.equals("ne") || fuhao.equals("e")) {
            if (objValue1 == null || objValue2 == null) {
                if (objValue1 != null || objValue2 != null) {
                    return fuhao.equals("ne");
                } else {
                    return fuhao.equals("e");
                }
            }
        }

        int compareRsult;
        if (objValue1 instanceof String && objValue2 instanceof String) {
            String value1 = (String) objValue1;
            String value2 = (String) objValue2;
            compareRsult = value1.compareTo(value2);
        } else if ((objValue1 instanceof Boolean || objValue1 instanceof String) && (objValue2 instanceof Boolean || objValue2 instanceof String)) {
            Boolean value1 = objValue1 instanceof Boolean ? (Boolean) objValue1 : Boolean.parseBoolean((String) objValue1);
            Boolean value2 = objValue2 instanceof Boolean ? (Boolean) objValue2 : Boolean.parseBoolean((String) objValue2);
            compareRsult = value1.compareTo(value2);
            compareRsult = value1.compareTo(value2);
        } else if ((objValue1 instanceof Integer || objValue1 instanceof Long || objValue1 instanceof BigInteger || objValue1 instanceof Float
                || objValue1 instanceof Double || objValue1 instanceof BigDecimal || objValue1 instanceof String)
                && (objValue2 instanceof Integer || objValue2 instanceof Long || objValue2 instanceof BigInteger || objValue2 instanceof Float
                || objValue2 instanceof Double || objValue2 instanceof BigDecimal || objValue2 instanceof String)) {
            Object noString1 = objValue1;
            if (objValue1 instanceof String) {
                try {
                    noString1 = Long.parseLong((String) objValue1);
                } catch (NumberFormatException e) {
                    try {
                        noString1 = Double.parseDouble((String) objValue1);
                    } catch (NumberFormatException e1) {
                        // throw e1;
                        return false;
                    }
                }
            }
            Object noString2 = objValue2;
            if (objValue2 instanceof String) {
                try {
                    noString2 = Long.parseLong((String) objValue2);
                } catch (NumberFormatException e) {
                    try {
                        noString2 = Double.parseDouble((String) objValue2);
                    } catch (NumberFormatException e2) {
                        // throw e2;
                        return false;
                    }
                }
            }
            if ((noString1 instanceof Float || noString1 instanceof Double || noString1 instanceof BigDecimal)
                    || (noString2 instanceof Float || noString2 instanceof Double || noString2 instanceof BigDecimal)) {
                if (noString1 instanceof Float || noString1 instanceof Double || noString1 instanceof BigDecimal) {
                    if (noString2 instanceof Float || noString2 instanceof Double || noString2 instanceof BigDecimal) {
                        Double value1 = noString1 instanceof Float ? (Float) noString1
                                : (noString1 instanceof Double ? (Double) noString1 : ((BigDecimal) noString1).doubleValue());
                        Double value2 = noString2 instanceof Float ? (Float) noString2
                                : (noString2 instanceof Double ? (Double) noString2 : ((BigDecimal) noString2).doubleValue());
                        compareRsult = value1.compareTo(value2);
                    } else {
                        Double value1 = noString1 instanceof Float ? (Float) noString1
                                : (noString1 instanceof Double ? (Double) noString1 : ((BigDecimal) noString1).doubleValue());
                        long value2 = noString2 instanceof Integer ? (Integer) noString2
                                : (noString2 instanceof Long ? (Long) noString2 : ((BigInteger) noString2).longValue());
                        compareRsult = value1.compareTo((double) value2);
                    }
                } else {
                    long value1 = noString1 instanceof Integer ? (Integer) noString1
                            : (noString1 instanceof Long ? (Long) noString1 : ((BigInteger) noString1).longValue());
                    Double value2 = noString2 instanceof Float ? (Float) noString2
                            : (noString2 instanceof Double ? (Double) noString2 : ((BigDecimal) noString2).doubleValue());
                    compareRsult = -value2.compareTo((double) value1);
                }
            } else {
                Long value1 = noString1 instanceof Integer ? (Integer) noString1
                        : (noString1 instanceof Long ? (Long) noString1 : ((BigInteger) noString1).longValue());
                Long value2 = noString2 instanceof Integer ? (Integer) noString2
                        : (noString2 instanceof Long ? (Long) noString2 : ((BigInteger) noString2).longValue());
                compareRsult = value1.compareTo(value2);
            }
        } else {
            return false;
        }

        switch (fuhao) {
            case "gt":
                return compareRsult >= 1;
            case "gte":
                return compareRsult >= 0;
            case "lt":
                return compareRsult < 0;
            case "lte":
                return compareRsult <= 0;
            case "e":
                return compareRsult == 0;
            case "ne":
                return compareRsult != 0;
            default:
                return false;
        }
    }
}
