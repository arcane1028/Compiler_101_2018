
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Antlr {

    public static void main(String[] args) {
        MiniGoLexer lexer = null;
        try {
            lexer = new MiniGoLexer(CharStreams.fromFileName("test.go"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniGoParser parser = new MiniGoParser(tokens);
        ParseTree tree = parser.program();

        //System.out.println(tree.toStringTree());
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new MiniGoPrintListener(), tree);
    }
}


