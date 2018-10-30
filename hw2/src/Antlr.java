
import java.io.IOException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

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

    }

}
