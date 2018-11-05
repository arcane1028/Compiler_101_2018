import java.util.*;

public class Grammar {
    private HashMap<String, ArrayList<String>> table = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> first = new HashMap<>();
    private HashMap<String, ArrayList<String>> follow = new HashMap<>();
    private ArrayList<String> nonTerminal = new ArrayList<>();
    private ArrayList<String> terminal = new ArrayList<>();

    public HashMap<String, ArrayList<String>> getTable() {
        return table;
    }

    public void AddRule(String nonterminal, String rule) {
        if (table.get(nonterminal) == null) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(rule);
            table.put(nonterminal, list);
        } else {
            table.get(nonterminal).add(rule);
        }
    }

    public String getRule(String nonTerminal, int index) {
        return table.get(nonTerminal).get(index);
    }

    public ArrayList<String> getRules(String nonTerminal) {
        return table.get(nonTerminal);
    }

    public void findFirst() {
        if (nonTerminal.isEmpty() && terminal.isEmpty()) {
            makeList();
        }
        // terminal 시작 경우
        for (String lhs : table.keySet()) {
            for (String rhs : table.get(lhs)) {
                ArrayList<String> list;
                if (first.get(lhs) == null) {
                    list = new ArrayList<String>();
                } else {
                    list = first.get(lhs);
                }
                for (String term : terminal) {
                    if (rhs.startsWith(term)) {
                        list.add(term);
                    }
                }
                first.put(lhs, list);
            }
        }
        ArrayList<String> terms = new ArrayList<>();
        terms.addAll(nonTerminal);
        terms.addAll(terminal);
        // nonTerminal
        HashMap<String, ArrayList<String>> tempfirst = new HashMap<>();
        do {
            for (String key : first.keySet()) {
                ArrayList<String> t = new ArrayList<>(first.get(key));
                tempfirst.put(key, t);
            }

            for (String lhs : table.keySet()) {
                for (String rhs : table.get(lhs)) {
                    if (startTerminal(rhs)) {
                        continue;
                    }
                    ArrayList<String> list;
                    if (first.get(lhs) == null) {
                        list = new ArrayList<String>();
                    } else {
                        list = first.get(lhs);
                    }
                    // 규칙 토큰으로 나눔
                    ArrayList<String> token = new ArrayList<>();
                    String temp = rhs;
                    while (temp.length() != 0) {
                        for (String term : terms) {
                            if (temp.startsWith(term)) {
                                int index = temp.indexOf(term);
                                temp = temp.substring(index + term.length());
                                token.add(term);
                                break;
                            }
                        }
                    }
                    for (String s : token) {
                        ArrayList<String> fis = new ArrayList<>(first.get(s));
                        if (fis.contains("e")) {
                            fis.remove("e");
                            for (String string : fis) {
                                if (!list.contains(string)) {
                                    list.add(string);
                                }
                            }
                        } else {
                            for (String string : fis) {
                                if (!list.contains(string)) {
                                    list.add(string);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } while (!tempfirst.equals(first));

        System.out.println("------------------------");
        StringBuilder builder = new StringBuilder();
        for (String key : first.keySet()) {
            builder.append("first(").append(key).append(") : {");
            for (String s : first.get(key)) {
                builder.append(s).append(", ");
            }
            builder.deleteCharAt(builder.length() - 1); // , 삭제
            builder.append("}\n");
        }
        builder.deleteCharAt(builder.length() - 1); // 줄바꿈 삭제
        System.out.println(builder);

    }

    private boolean startTerminal(String s) {
        for (String term : terminal) {
            if (s.startsWith(term)) {
                return true;
            }
        }
        return false;
    }

    public void findFollow() {
        // 1
        follow.put("E", new ArrayList<>(Collections.singletonList("$")));

        // 2
        for (String lhs : table.keySet()) {
            for (String rhs : table.get(lhs)) {
                for (String term : terminal) {
                    if (rhs.endsWith(term) && (!term.equals("e"))) {
                        int index = rhs.lastIndexOf(term);
                        String temp = rhs.substring(0, index);
                        for (String nonTerm : nonTerminal) {
                            if (temp.endsWith(nonTerm)) {
                                ArrayList<String> list;
                                if (follow.get(nonTerm) == null) {
                                    list = new ArrayList<String>();
                                } else {
                                    list = follow.get(nonTerm);
                                }
                                if (!list.contains(term)) {
                                    list.add(term);
                                }
                                follow.put(nonTerm, list);
                            }
                        }

                    }
                }
                for (String nonTerm : nonTerminal) {
                    if (rhs.endsWith(nonTerm)) {
                        int index = rhs.lastIndexOf(nonTerm);
                        String temp = rhs.substring(0, index);
                        for (String nonTerm2 : nonTerminal) {
                            if (temp.endsWith(nonTerm2)) {
                                ArrayList<String> list;
                                if (follow.get(nonTerm2) == null) {
                                    list = new ArrayList<String>();
                                } else {
                                    list = follow.get(nonTerm2);
                                }
                                ArrayList<String> templist = new ArrayList<>(first.get(nonTerm));
                                templist.remove("e");
                                for (String string : templist) {
                                    if (!list.contains(string)) {
                                        list.add(string);
                                    }
                                }
                                follow.put(nonTerm2, list);
                            }
                        }
                    }
                }
            }
        }

        HashMap<String, ArrayList<String>> tempFollow = new HashMap<>();
        do {
            for (String key : follow.keySet()) {
                ArrayList<String> t = new ArrayList<>(follow.get(key));
                tempFollow.put(key, t);
            }
            // 3 - 1
            for (String lhs : table.keySet()) {
                for (String rhs : table.get(lhs)) {
                    for (String nonTerm : nonTerminal) {
                        if (rhs.endsWith(nonTerm) ) {
                            ArrayList<String> list;
                            if (follow.get(nonTerm) == null) {
                                list = new ArrayList<String>();
                            } else {
                                list = follow.get(nonTerm);
                            }
                            ArrayList<String> temp ;
                            if (follow.get(lhs) == null) {
                                temp = new ArrayList<String>();
                            } else {
                                temp = follow.get(lhs);
                            }
                            for (String string : temp) {
                                if (!list.contains(string)) {
                                    list.add(string);
                                }
                            }
                            follow.put(nonTerm, list);
                        }
                    }
                }
            }
            // 3-2
            for (String lhs : table.keySet()) {
                for (String rhs : table.get(lhs)) {
                    for (String nonTerm : nonTerminal) {
                        if (rhs.endsWith(nonTerm) && first.get(nonTerm).contains("e")){
                            int index = rhs.lastIndexOf(nonTerm);
                            String temp = rhs.substring(0, index);
                            for (String nonTerm2 : nonTerminal) {
                                if (temp.endsWith(nonTerm2)) {
                                    ArrayList<String> list;
                                    if (follow.get(nonTerm2) == null) {
                                        list = new ArrayList<String>();
                                    } else {
                                        list = follow.get(nonTerm2);
                                    }
                                    ArrayList<String> tempList ;
                                    if (follow.get(lhs) == null) {
                                        tempList = new ArrayList<String>();
                                    } else {
                                        tempList = follow.get(lhs);
                                    }
                                    for (String string : tempList) {
                                        if (!list.contains(string)) {
                                            list.add(string);
                                        }
                                    }
                                    follow.put(nonTerm2, list);
                                }
                            }
                        }
                    }
                }
            }
        } while (!tempFollow.equals(follow));
        System.out.println("------------------------");
        StringBuilder builder = new StringBuilder();
        for (String key : follow.keySet()) {
            builder.append("follow(").append(key).append(") : {");
            for (String s : follow.get(key)) {
                builder.append(s).append(", ");
            }
            builder.deleteCharAt(builder.length() - 1); // , 삭제
            builder.append("}\n");
        }
        builder.deleteCharAt(builder.length() - 1); // 줄바꿈 삭제
        System.out.println(builder);

    }

    private void makeList() {
        String delim = "";
        for (String nonTerm : table.keySet()) {
            if (!nonTerminal.contains(nonTerm)) {
                nonTerminal.add(nonTerm);
                delim += nonTerm;
            }
        }
        //delim += "e";
        for (String lhs : table.keySet()) {
            for (String rhs : table.get(lhs)) {
                StringTokenizer tokenizer = new StringTokenizer(rhs, delim);
                while (tokenizer.hasMoreTokens()) {
                    String term = tokenizer.nextToken();
                    if (!terminal.contains(term)) {
                        terminal.add(term);
                    }
                }

            }
        }
    }

    public boolean isTerminal(String token) {
        if (terminal.contains(token)){
            return true;
        }
        return false;
    }

    public boolean isNonTerminal(String token) {
        if (nonTerminal.contains(token)){
            return true;
        }
        return false;
    }


    public void viewGrammar() {
        int index = 1;
        for (String lhs : table.keySet()) {
            for (String rhs : table.get(lhs)) {
                System.out.println((index++) + "." + lhs + " ->  " + rhs);
            }
        }
    }
}
