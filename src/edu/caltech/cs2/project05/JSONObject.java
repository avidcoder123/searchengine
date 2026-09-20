package edu.caltech.cs2.project05;

import java.util.Arrays;
import java.util.HashMap;

public class JSONObject extends HashMap<String, JSON> implements JSON {
    public JSONObject() {
        super();
    }

    public JSONObject(HashMap<String, JSON> hMap) {
        super(hMap);
    }


    public String dump(int numIndents) {

        StringBuilder rep = new StringBuilder();

        rep.append("{\n");

        int i = 0;
        String[] keys = this.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        for (String key : keys) {
            JSON item = this.get(key);
            rep
                    .append(" ".repeat(numIndents + JSON.INDENT_LENGTH))
                    .append((new JSONStringValue(key)).dump())
                    .append(": ")
                    .append(item.dump(numIndents + JSON.INDENT_LENGTH));

            //Add a comma for all but the last index
            if (i < this.size() - 1) {
                rep.append(",");
            }

            rep.append("\n");
            i++;
        }

        rep.append(" ".repeat(numIndents));
        rep.append("}");

        return rep.toString();
    }

    public static JSON queueToJSON(Queue<HTMLTag> q) {
        JSONObject obj = new JSONObject();
        int depth = 1;
        while (depth > 0) {
            HTMLTag tag = q.dequeue();
            if (tag.isClosing() && tag.getElement().equals("object")) {
                depth--;
            } else if (tag.isOpening() && tag.getElement().equals("entry")) {
                q.dequeue();  // eat key
                q.dequeue();  // eat string
                String key = q.dequeue().toString();
                q.dequeue(); // eat closing string
                q.dequeue(); // eat closing key
                q.dequeue(); // eat value
                HTMLTag valueTypeTag = q.peek();
                if (valueTypeTag.getElement().equals("object")) {
                    depth++;
                    q.dequeue();
                    obj.put(key, JSONObject.queueToJSON(q));
                    depth--;
                } else if (valueTypeTag.getElement().equals("list")) {
                    q.dequeue();
                    obj.put(key, JSONList.queueToJSON(q));
                } else {
                    obj.put(key, JSON.queueToJSON(q));
                }
            }
        }
        return obj;
    }
}