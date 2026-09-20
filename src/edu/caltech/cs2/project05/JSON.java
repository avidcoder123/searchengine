package edu.caltech.cs2.project05;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ParsableString {
    static final Pattern JSON_NUMBER_PATTERN = Pattern.compile(
            "-?(0|([1-9][0-9]*))(\\.[0-9]+([eE][+-]?[0-9]+)?)?");
    static final char[] SPECIAL_CHARACTERS
            = {'\"', '\\', '/', '\b','\f', '\n', '\r', '\t'};
    static final String[] SPECIAL_STRINGS
            = {"\\\"", "\\\\", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t"};
    static final char UNICODE_CHAR = 'u';
    static final  int DEL = 128;
    static final int BAN_LIST_MAX = 32;
    static final int UNICODE_LENGTH = 4;

    private final String s;
    private int idx;
    private final Matcher mNum;

    /**
     * Initializes a parsable string with idx = 0.
     * @param s the String to be parsed.
     */
    public ParsableString(String s) {
        this.s = s;
        this.idx = 0;
        mNum = JSON_NUMBER_PATTERN.matcher(s);
    }

    /**
     * Starting from the current index, moves idx forwards until it hits
     * a non-whitespace character.
     */
    public void whitespaceAdjust() {
        while (idx < s.length() && Character.isWhitespace(s.charAt(idx))) {
            idx++;
        }
    }

    public void incrementIndex() {
        incrementIndex(1);
    }

    /**
     * Moves idx forwards by i characters,
     * then skips past all continuous whitespace starting at index i.
     * @param i the number of characters.
     */
    public void incrementIndex(int i) {
        idx += i;
        whitespaceAdjust();
    }

    public int getIdx() {
        return idx;
    }

    public String getString() {
        return s;
    }

    public char getCurChar() {
        return s.charAt(idx);
    }

    public boolean parsed() {
        return idx >= s.length();
    }

    /**
     * If there's a String at the current index, parses and returns that string,
     * moving idx forwards accordingly, adjusting for whitespace.
     * Throws JSONParsingException otherwise.
     * @return the parsed String
     */
    public String parseInternalString() {
        if (s.charAt(idx) == '\"') {
            int shift = 1;
            StringBuilder b = new StringBuilder();
            while (idx + shift < s.length() && s.charAt(idx + shift) != '\"') {
                if (s.charAt(idx + shift) == '\\') {
                    shift++;
                    boolean isSpecial = false;
                    char c = s.charAt(idx + shift);
                    for (int j = 0; j < SPECIAL_STRINGS.length; j++) {
                        if (c == SPECIAL_STRINGS[j].charAt(1)) {
                            b.append(SPECIAL_CHARACTERS[j]);
                            isSpecial = true;
                            break;
                        }
                    }
                    if (!isSpecial) {
                        if (c == UNICODE_CHAR) {
                            if (idx + shift + UNICODE_LENGTH >= s.length() - 1) {
                                throw new JSONParsingException(idx + shift);
                            }
                            int unicodeValue =
                                    Integer.parseInt(s.substring(idx + shift + 1,
                                            idx + shift + UNICODE_LENGTH + 1), 16);
                            if (unicodeValue != DEL && unicodeValue >= BAN_LIST_MAX) {
                                b.append(unicodeValue);
                                shift += UNICODE_LENGTH;
                            } else {
                                throw new JSONParsingException(idx + shift);
                            }
                        }
                    }
                } else {
                    b.append(s.charAt(idx + shift));
                }
                shift++;
            }
            if (idx + shift >= s.length() && shift >= 1 && s.charAt(idx + shift - 1) != '\"') {
                throw new JSONParsingException(idx);
            }
            idx += shift + 1;
            whitespaceAdjust();
            return b.toString();
        } else {
            throw new JSONParsingException(idx);
        }
    }

    /**
     * If there's an integer or double at the current index, parses and returns that string,
     * moving idx forwards accordingly, adjusting for whitespace.
     * Throws JSONParsingException otherwise.
     * @return the number as a String
     */
    public String getNumber() {
        if (mNum.find(idx) && mNum.start() == idx) {
            idx += mNum.end() - mNum.start();
            whitespaceAdjust();
            return mNum.group();
        } else {
            throw new JSONParsingException(idx);
        }
    }
}

public interface JSON {
    int INDENT_LENGTH = 2;

    /**
     * Parses a string of JSON and returns the outermost JSON element.
     *
     * To do this, this method should call the private helper.
     * 
     **/
    public static JSON parse(String inputStr) {
        return JSON.parse(new ParsableString(inputStr));
    }

    /**
     * Parses a ParsableString of JSON and returns the next JSON element.
     * 
     * To do this, this method:
     * 1) Should discard all white space not in strings.
     * 2) detect what type of JSON element is being parsed.
     * 3) Recursively call one of many helper methods to
     *    create that specific type of element.
     */
    private static JSON parse(ParsableString inputStr) {
        JSON res = JSON.parseExpression(inputStr);

        if (inputStr.parsed()) {
            return res;
        } else {
            throw new JSONParsingException(inputStr.getIdx());
        }
    }

    private static JSON parseExpression(ParsableString inputStr) {
        inputStr.whitespaceAdjust();

        char firstChar = inputStr.getCurChar();

        if (firstChar == '[') {
            inputStr.incrementIndex();
            return JSON.parseList(inputStr);
        } else if (firstChar == '{') {
            inputStr.incrementIndex();
            return JSON.parseObj(inputStr);
        } else if (firstChar == ']' || firstChar == '}') {
            throw new JSONParsingException(inputStr.getIdx());
        } else {
            return JSON.parseValue(inputStr);
        }
    }

    /**
     * Parses a ParsableString of JSON and returns the next JSON element.
     * 
     * To do this, this method:
     * 1) Should determine what type of JSONValue is being parsed
     * 2) Parse the value
     * 3) Convert the parsed value into the corresponding JSONValue object
     * 
     * If the value is incorrectly formatted in any way then this method
     * should throw a JSONParsingException
     */
    private static JSON parseValue(ParsableString s) throws JSONParsingException {
        JSONValue<?> jsonValue;
        if (s.getCurChar() == '"') {
            String val = s.parseInternalString();
            jsonValue = new JSONStringValue(val);
        } else if (s.getString().startsWith("null", s.getIdx())) {
            jsonValue = null;
            s.incrementIndex(4);
        } else if (s.getString().startsWith("true", s.getIdx())) {
            //true
            jsonValue = new JSONBooleanValue(true);
            s.incrementIndex(4);
        } else if (s.getString().startsWith("false", s.getIdx())) {
            //false
            jsonValue = new JSONBooleanValue(false);
            s.incrementIndex(5);
            s.whitespaceAdjust();
        } else {
            String numString = s.getNumber();
            Integer intParse = JSONHelpers.tryParseInteger(numString);
            Double doubleParse = JSONHelpers.tryParseDouble(numString);

            if (intParse != null) {
                jsonValue = new JSONIntegerValue(intParse);
            } else if (doubleParse != null) {
                jsonValue = new JSONDoubleValue(doubleParse);
            } else {
                throw new JSONParsingException(s.getIdx());
            }
        }

        return jsonValue;
    }

    /**
     * Parses a ParsableString of JSON and returns the next JSON list.
     *
     * To do this, this method:
     * 1) Should create a new JSON list
     * 2) Recursively parse each item in the list
     * 3) Add each parsed value to the JSON list
     * 
     * If the list is incorrectly formatted in any way then this method
     * should throw a JSONParsingException
     */
    private static JSONList parseList(ParsableString s) {
        JSONList l = new JSONList();

        try {
            while (s.getCurChar() != ']') {
                JSON item = JSON.parseExpression(s);
                l.add(item);

                if (s.getCurChar() == ',') {
                    s.incrementIndex();
                }
                s.whitespaceAdjust();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new JSONParsingException(s.getIdx());
        }

        s.incrementIndex();
        return l;
    }
    
    /**
     * Parses a ParsableString of JSON and returns the next JSON object.
     *
     * To do this, this method:
     * 1) Shoul create a new JSON object
     * 2) Recursively parse each key and each value in the list
     * 3) Add each parsed key, value pair to the object
     * 
     * If the entry is incorrectly formatted in any way then this method
     * should throw a JSONParsingException
     */
    private static JSONObject parseObj(ParsableString s) {
        JSONObject obj = new JSONObject();

        try {
            while (s.getCurChar() != '}') {
                String key = s.parseInternalString();
                s.whitespaceAdjust();
                //Skip over the colon
                s.incrementIndex();
                JSON val = JSON.parseExpression(s);

                obj.put(key, val);

                if (s.getCurChar() == ',') {
                    s.incrementIndex();
                }
                s.whitespaceAdjust();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new JSONParsingException(s.getIdx());
        }

        s.incrementIndex();
        return obj;
    }

    /**
     * Returns a string version of the JSON file which is
     * indented by two spaces every time a list or object is
     * opened and unindenting by two spaces every time one is closed.
     **/
    default String dump() {
        return dump(0);
    }

    String dump(int numIndents);

    /**
     * Converts an XML-string representation of a JSON into a JSON.
     * Used in tests (not necessarily to implement)
     */
    static JSON fromXML(String xml) {
        Queue<HTMLTag> queue = HTMLHelpers.parseText(xml);
        return JSON.queueToJSON(queue);
    }

    /**
     * Converts a queue of XML tags, representing a JSON, into a JSON.
     * Used in tests (not necessary to implement)
     */
    static JSON queueToJSON(Queue<HTMLTag> q) {
        HTMLTag firstTag = q.dequeue();
        if (firstTag.isOpening()) {
            String type = firstTag.getElement();
            if (type.equals("object")) {
                return JSONObject.queueToJSON(q);
            } else if (type.equals("list")) {
                return JSONList.queueToJSON(q);
            } else {
                HTMLTag content = q.dequeue();
                if (type.equals("boolean")) {
                    return new JSONBooleanValue(Boolean.parseBoolean(content.toString()));
                } else if (type.equals("integer")) {
                    return new JSONIntegerValue(Integer.parseInt(content.toString()));
                } else if (type.equals("double")) {
                    return new JSONDoubleValue(Double.parseDouble(content.toString()));
                } else if (type.equals("string")) {
                    return new JSONStringValue(content.toString());
                } else {
                    throw new JSONParsingException(-1);
                }
            }
        } else if (firstTag.isSelfClosing()) {
            assert firstTag.getElement().equals("null");
            return null;
        } else {
            throw new JSONParsingException(-1);
        }
    }
}
