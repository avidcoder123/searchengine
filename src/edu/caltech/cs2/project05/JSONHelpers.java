package edu.caltech.cs2.project05;

public interface JSONHelpers {

    char[] SPECIAL_CHARACTERS = {'\"', '\\', '/', '\b', '\f', '\n', '\r', '\t'};
    String[] SPECIAL_STRINGS = {"\\\"", "\\\\", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t"};
    char UNICODE_CHAR = 'u';
    int DEL = 128;
    int BAN_LIST_MAX = 32;
    int UNICODE_LENGTH = 4;

    static String toFormattedString(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            boolean isSpecial = false;
            char c = s.charAt(i);
            for (int j = 0; j < SPECIAL_STRINGS.length; j++) {
                if (c == SPECIAL_CHARACTERS[j]) {
                    b.append(SPECIAL_STRINGS[j]);
                    isSpecial = true;
                    break;
                }
            }
            if (!isSpecial) {
                if (c == DEL || c < BAN_LIST_MAX) {
                    throw new IllegalArgumentException(
                            "Disallowed control character at index " + i);
                } else if (c < DEL) {
                    b.append(c);
                } else {
                    b.append("\\" + UNICODE_CHAR);
                    StringBuilder unicode = new StringBuilder(Integer.toString(c, 16));
                    while (unicode.length() < UNICODE_LENGTH) {
                        unicode.insert(0, "0");
                    }
                    b.append(unicode.toString().toUpperCase());
                }
            }
        }
        b.append("\"");
        return b.toString();
    }

    static Integer tryParseInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception E) {
            return null;
        }
    }

    static Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception E) {
            return null;
        }
    }
}
