import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Loader {

	public static void main(String[] args) {

		Grammar g = new Grammar();

		try{
			BufferedReader fileReader = new BufferedReader(new FileReader(new File("grammar.txt")));
			String line = "";
			while((line = fileReader.readLine()) != null){
				String[] grammar = line.split("->");
				String NonTerminal = grammar[0].replaceAll(" ", "");
				String[] RuleSet = grammar[1].replaceAll(" ", "").split("\\|");
				for(int i = 0; i < RuleSet.length; i++){
					g.AddRule(NonTerminal, RuleSet[i]);
				}
			}
			fileReader.close();
		} catch (IOException e){
			e.printStackTrace();
		}

		g.viewGrammar();
		g.findFirst();
		g.findFollow();

		RecursiveDescentParser parser = new RecursiveDescentParser(g, "( id * id )");
	}
}
