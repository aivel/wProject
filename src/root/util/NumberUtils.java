package root.util;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class NumberUtils {

    public static Double objectToDouble(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Double) {
            return (Double) object;
        } else if (object instanceof String) {
            try {
                Double d = Double.parseDouble((String) object);
                return d;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return null;
    }

    public static Integer objectToInteger(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof String) {
            try {
                Integer d = Integer.parseInt((String) object);
                return d;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        return null;
    }

}
