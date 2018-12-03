import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class UcodeGenListener extends MiniGoBaseListener {
    ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();

    public UcodeGenListener() {

    }

    @Override
    public void exitProgram(MiniGoParser.ProgramContext ctx) {


    }
}