public class Utils {
    public static String getIndentation(int depth){
        StringBuilder indent = new StringBuilder();
        while (depth-- > 0)
            indent.append("\t");
        return indent.toString();
    }
}
