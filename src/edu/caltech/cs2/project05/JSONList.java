package edu.caltech.cs2.project05;

import java.util.ArrayList;

public class JSONList extends ArrayList<JSON> implements JSON {

    public JSONList() {
        super();
    }

    public JSONList(ArrayList<JSON> arrList) {
        super(arrList);
    }

    public String dump(int numIndents) {
        StringBuilder rep = new StringBuilder();

        rep.append("[\n");

        for (int i = 0; i < this.size(); i++) {
            JSON item = this.get(i);
            if (item == null) {
                rep
                        .append(" ".repeat(numIndents + JSON.INDENT_LENGTH))
                        .append("null");
            } else {
                rep
                        .append(" ".repeat(numIndents + JSON.INDENT_LENGTH))
                        .append(item.dump(numIndents + JSON.INDENT_LENGTH));
            }

            //Add a comma for all but the last index
            if (i < this.size() - 1) {
                rep.append(",");
            }

            rep.append("\n");
        }

        rep
                .append(" ".repeat(numIndents))
                .append("]");

        return rep.toString();
    }

    public static JSON queueToJSON(Queue<HTMLTag> q) {
        JSONList li = new JSONList();
        int depth = 1;
        while (depth > 0) {
            HTMLTag tag = q.dequeue();
            if (tag.isClosing() && tag.getElement().equals("list")) {
                depth--;
            } else if (tag.isOpening()) {  // tag should be number
                HTMLTag itemTypeTag = q.peek();
                if (itemTypeTag.getElement().equals("object")) {
                    q.dequeue();
                    li.add(JSONObject.queueToJSON(q));
                } else if (itemTypeTag.getElement().equals("list")) {
                    depth++;
                    q.dequeue();
                    li.add(JSONList.queueToJSON(q));
                    depth--;
                } else {
                    li.add(JSON.queueToJSON(q));
                }
            }
        }
        return li;
    }
}