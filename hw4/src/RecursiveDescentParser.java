import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class RecursiveDescentParser {
    private HashMap<String, ArrayList<String>> first = new HashMap<>();
    private HashMap<String, ArrayList<String>> follow = new HashMap<>();
    private ArrayList<String> inputList = new ArrayList<>();

    public RecursiveDescentParser(Grammar g, String input){
        StringTokenizer tokenizer = new StringTokenizer(input, " ");
        while (tokenizer.hasMoreTokens()) {
            inputList.add(tokenizer.nextToken());
        }
        pE();
    }
    void pE(){

    }
    void pEdot(){

    }
    void pT(){

    }
    void pTdot(){

    }
    void pF(){

    }
    void pTerminal(){

    }


}
