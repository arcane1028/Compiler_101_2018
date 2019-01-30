import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;


public class UcodeGenListener extends MiniGoBaseListener {
    private ParseTreeProperty<String> newTexts;
    private ArrayList<Map<String, Symbol>> symbol;
    private Map<String, Integer> funcNum;
    private int currentFunc;
    private int globalOffset;
    private int localOffset;
    private int labelCount;
    private String filename;


    public UcodeGenListener(String filename) {
        this.newTexts = new ParseTreeProperty<>();
        this.symbol = new ArrayList<>();
        this.symbol.add(new LinkedHashMap<>()); // global
        this.funcNum = new LinkedHashMap<>();
        funcNum.put("global", 0);
        this.currentFunc = 0;
        this.globalOffset = 0;
        this.localOffset = 0;
        this.labelCount = 0;
        this.filename = filename;
    }

    @Override
    public void enterVar_decl(MiniGoParser.Var_declContext ctx) {
        String ident = ctx.IDENT(0).toString();
        if (ctx.getChildCount() == 3) {
            symbol.get(0).put(ident, new Symbol(1, ++globalOffset, 1));

        } else if (ctx.getChild(2).getText().equals(",")) {
            for (TerminalNode t : ctx.IDENT()) {
                symbol.get(0).put(t.getText(), new Symbol(1, ++globalOffset, 1));
            }
        } else if (ctx.LITERAL() != null) {
            int size = Integer.parseInt(ctx.LITERAL().getText());
            symbol.get(0).put(ident, new Symbol(1, ++globalOffset, size));
            globalOffset += (size - 1);
        } else {
            System.out.println("var decl error");
        }
    }

    @Override
    public void enterFun_decl(MiniGoParser.Fun_declContext ctx) {
        this.symbol.add(new LinkedHashMap<>()); // local
        currentFunc++;
        localOffset = 0;
        this.funcNum.put(ctx.IDENT().getText(), currentFunc);
    }

    @Override
    public void enterParam(MiniGoParser.ParamContext ctx) {
        int count = ctx.getChildCount();
        String ident = ctx.IDENT().getText();
        for (int i = 0; i < count; i += 2) {
            symbol.get(currentFunc).put(ident, new Symbol(2, ++localOffset, 1));
        }
    }

    @Override
    public void enterLocal_decl(MiniGoParser.Local_declContext ctx) {
        String ident = ctx.IDENT().toString();
        if (ctx.getChildCount() == 3) {
            symbol.get(currentFunc).put(ident, new Symbol(2, ++localOffset, 1));
        } else if (ctx.LITERAL() != null) {
            int size = Integer.parseInt(ctx.LITERAL().getText());
            symbol.get(currentFunc).put(ident, new Symbol(2, ++localOffset, size));
            localOffset += (size - 1);
        } else {
            System.out.println("local decl error");
        }
    }

    @Override
    public void enterAssign_stmt(MiniGoParser.Assign_stmtContext ctx) {
        String ident = ctx.IDENT(0).toString();
        if (ctx.VAR() != null) {
            if (ctx.getChildCount() == 9) {
                for (int i = 0; i < 2; i++) {
                    symbol.get(currentFunc).put(ctx.IDENT(i).getText(),
                            new Symbol(2, ++localOffset, 1));
                }
            } else {
                symbol.get(currentFunc).put(ident,
                        new Symbol(2, ++localOffset, 1));
            }
        }
    }

    /*****************************************************************/
    @Override
    public void exitProgram(MiniGoParser.ProgramContext ctx) {
        Formatter formatter = null;
        try {
            formatter = new Formatter(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int varCount = 0;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.decl(i).getChild(0).getClass().equals(MiniGoParser.Fun_declContext.class)) {
                break;
            }
            varCount++;
        }
        for (int i = varCount; i < ctx.getChildCount(); i++) {
            formatter.format("%s", newTexts.get(ctx.getChild(i)));
        }
        for (int i = 0; i < varCount; i++) {
            formatter.format("%s", newTexts.get(ctx.getChild(i)));
        }
        formatter.format("           bgn %d\n", symbol.get(0).size());
        formatter.format("           ldp\n");
        formatter.format("           call main\n");
        formatter.format("           end\n");

        formatter.close();
    }

    @Override
    public void exitDecl(MiniGoParser.DeclContext ctx) {
        newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
    }

    @Override
    public void exitVar_decl(MiniGoParser.Var_declContext ctx) {
        Formatter formatter = new Formatter();
        String ident = ctx.IDENT(0).toString();
        if (ctx.getChildCount() == 3) {
            formatter.format("           sym %d %d %d\n",
                    symbol.get(0).get(ident).base,
                    symbol.get(0).get(ident).offset,
                    1);
        } else if (ctx.getChild(2).getText().equals(",")) {
            for (TerminalNode t : ctx.IDENT()) {
                formatter.format("           sym %d %d %d\n",
                        symbol.get(0).get(t.getText()).base,
                        symbol.get(0).get(t.getText()).offset,
                        1);
            }
        } else if (ctx.LITERAL() != null) {
            formatter.format("           sym %d %d %d\n",
                    symbol.get(0).get(ident).base,
                    symbol.get(0).get(ident).offset,
                    symbol.get(0).get(ident).size);
        } else {
            System.out.println("var decl error");
        }
        newTexts.put(ctx, formatter.toString());
    }

    @Override
    public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {
        Formatter formatter = new Formatter();
        if (ctx.getChildCount() == 7) {
            String compound = newTexts.get(ctx.getChild(6));
            formatter.format("%-10s proc %d %d %d\n", ctx.IDENT(), localOffset, 2, 2);
            formatter.format("%s", compound);
            formatter.format("           end\n");
        } else {
            String compound = newTexts.get(ctx.getChild(10));
            formatter.format("%-10s proc %d %d %d\n", ctx.IDENT(), localOffset, 2, 2);
            formatter.format("%s", compound);
            formatter.format("           end\n");
        }
        newTexts.put(ctx, formatter.toString());
    }

    @Override
    public void exitStmt(MiniGoParser.StmtContext ctx) {
        newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
    }

    @Override
    public void exitExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {
        newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
    }

    @Override
    public void exitAssign_stmt(MiniGoParser.Assign_stmtContext ctx) {
        Formatter formatter = new Formatter();
        int count = ctx.getChildCount();
        switch (count) {
            case 9:
                for (int i = 0; i < 2; i++) {
                    Symbol lookupSymbol = lookup(ctx.IDENT(i).getText());
                    String literal = ctx.LITERAL(i).getText();
                    formatter.format("           ldc %s\n", literal);
                    formatter.format("           str %d %d\n", lookupSymbol.base, lookupSymbol.offset);
                }
                break;
            case 5: {
                Symbol lookupSymbol = lookup(ctx.IDENT(0).getText());
                String expr = newTexts.get(ctx.expr(0));
                formatter.format("%s", expr);
                formatter.format("           str %d %d\n", lookupSymbol.base, lookupSymbol.offset);
                break;
            }

            case 4: {
                Symbol lookupSymbol = lookup(ctx.IDENT(0).getText());
                String expr = newTexts.get(ctx.expr(0));
                formatter.format("%s", expr);
                formatter.format("           str %d %d\n", lookupSymbol.base, lookupSymbol.offset);
                break;
            }
            case 6: {
                Symbol lookupSymbol = lookup(ctx.IDENT(0).getText());
                String index = newTexts.get(ctx.expr(0));
                String expr = newTexts.get(ctx.expr(1));
                formatter.format("%s", index);
                formatter.format("           lda %d %d\n", lookupSymbol.base, lookupSymbol.offset);
                formatter.format("           add\n");
                formatter.format("%s", expr);
                formatter.format("           sti\n");
                break;
            }
        }
        newTexts.put(ctx, formatter.toString());
    }

    @Override
    public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
        Formatter formatter = new Formatter();

        if (ctx.parent.getClass().equals(MiniGoParser.Fun_declContext.class)) {
            int funcN = funcNum.get(ctx.parent.getChild(1).getText());
            for (String key : symbol.get(funcN).keySet()) {
                formatter.format("           sym %d %d %d\n",
                        symbol.get(funcN).get(key).base,
                        symbol.get(funcN).get(key).offset,
                        1);
            }
        }
        for (int i = 1 + ctx.local_decl().size(); i < ctx.getChildCount() - 1; i++) {
            formatter.format("%s", newTexts.get(ctx.getChild(i)));
        }
        newTexts.put(ctx, formatter.toString());
    }

    @Override
    public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {
        Formatter formatter = new Formatter();
        String expr = newTexts.get(ctx.getChild(1));
        String compound = newTexts.get(ctx.getChild(2));
        if (ctx.ELSE() == null) {
            int jump = labelCount;
            formatter.format("%s", expr);
            formatter.format("           fjp %s\n", ("$$" + jump));
            formatter.format("%s", compound);
            formatter.format("%-10s nop\n", ("$$" + jump));
            labelCount++;
            newTexts.put(ctx, formatter.toString());
        } else {
            String compound2 = newTexts.get(ctx.getChild(4));
            int jump1 = labelCount;
            int jump2 = labelCount + 1;
            formatter.format("%s", expr);
            formatter.format("           fjp %s\n", ("$$" + jump1));
            formatter.format("%s", compound);
            formatter.format("           ujp %s\n", ("$$" + jump2));
            formatter.format("%-10s nop\n", ("$$" + jump1));
            formatter.format("%s", compound2);
            formatter.format("%-10s nop\n", ("$$" + jump2));
            labelCount += 2;

            newTexts.put(ctx, formatter.toString());
        }
    }

    @Override
    public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {
        Formatter formatter = new Formatter();
        String expr = newTexts.get(ctx.getChild(1));
        String compound = newTexts.get(ctx.getChild(2));
        int jump1 = labelCount;
        int jump2 = labelCount + 1;

        formatter.format("%-10s nop\n", ("$$" + jump1));
        formatter.format("%s", expr);
        formatter.format("           fjp %s\n", ("$$" + jump2));
        formatter.format("%s", compound);
        formatter.format("           ujp %s\n", ("$$" + jump1));
        formatter.format("%-10s nop\n", ("$$" + jump2));

        labelCount += 2;

        newTexts.put(ctx, formatter.toString());
    }

    @Override
    public void exitReturn_stmt(MiniGoParser.Return_stmtContext ctx) {
        Formatter formatter = new Formatter();
        int count = ctx.getChildCount();
        switch (count) {
            case 4:
                String expr1 = newTexts.get(ctx.getChild(1));
                String expr2 = newTexts.get(ctx.getChild(3));
                formatter.format("%s", expr1);
                formatter.format("%s", expr2);
                formatter.format("           retv\n");
                break;
            case 2:
                String expr = newTexts.get(ctx.getChild(1));
                formatter.format("%s", expr);
                formatter.format("           retv\n");
                break;
            case 1:
                formatter.format("           ret\n");
        }
        newTexts.put(ctx, formatter.toString());
    }

    @Override
    public void exitExpr(MiniGoParser.ExprContext ctx) {
        Formatter formatter = new Formatter();

        if (ctx.getChildCount() == 1) {
            if (ctx.IDENT() != null) {
                Symbol lookupSymbol = lookup(ctx.IDENT().getText());
                formatter.format("           lod %d %d\n", lookupSymbol.base, lookupSymbol.offset);
            } else {
                formatter.format("           ldc %d\n", Integer.parseInt(ctx.LITERAL(0).getText()));
            }
            newTexts.put(ctx, formatter.toString());
        } else if (ctx.getChild(0).getText().equals("(")) {
            String temp = newTexts.get(ctx.getChild(1));
            newTexts.put(ctx, temp);
        } else if (ctx.getChild(1).getText().equals("[") && ctx.getChildCount() == 4) {
            String expr = newTexts.get(ctx.getChild(2));
            Symbol lookupSymbol = lookup(ctx.IDENT().getText());
            formatter.format("%s", expr);
            formatter.format("           lda %d %d\n", lookupSymbol.base, lookupSymbol.offset);
            formatter.format("           add\n");
            formatter.format("           ldi\n");
            newTexts.put(ctx, formatter.toString());
        } else if (ctx.getChild(1).getText().equals("(") && ctx.getChildCount() == 4) {
            String args = newTexts.get(ctx.getChild(2));
            formatter.format("           ldp\n");
            formatter.format("%s", args);
            formatter.format("           call %s\n", ctx.IDENT().getText());
            newTexts.put(ctx, formatter.toString());
        } else if (ctx.FMT() != null) {
            String args = newTexts.get(ctx.getChild(4));
            formatter.format("           ldp\n");
            formatter.format("%s", args);
            formatter.format("           call %s\n", ctx.IDENT().getText());
            newTexts.put(ctx, formatter.toString());
        } else if (isFrontOp(ctx)) {
            String expr = newTexts.get(ctx.getChild(1));
            String op = ctx.getChild(0).getText();
            formatter.format("%s", expr);
            switch (op) {
                case "-":
                    formatter.format("           neg\n");
                    break;
                case "+":
                    formatter.format("           ???\n");
                    break;
                case "--":
                    formatter.format("           dec\n");
                    break;
                case "++":
                    formatter.format("           inc\n");
                    break;
                case "!":
                    formatter.format("           notop\n");
                    break;

            }
            newTexts.put(ctx, formatter.toString());

        } else if (isBinaryOp(ctx)) {
            String left = newTexts.get(ctx.getChild(0));
            String op = ctx.getChild(1).getText();
            String right = newTexts.get(ctx.getChild(2));
            formatter.format("%s", left);
            formatter.format("%s", right);
            switch (op) {
                case "*":
                    formatter.format("           mult\n");
                    break;
                case "/":
                    formatter.format("           div\n");
                    break;
                case "%":
                    formatter.format("           mod\n");
                    break;
                case "+":
                    formatter.format("           add\n");
                    break;
                case "-":
                    formatter.format("           sub\n");
                    break;
                case "==":
                    formatter.format("           eq\n");
                    break;
                case "!=":
                    formatter.format("           ne\n");
                    break;
                case "<=":
                    formatter.format("           le\n");
                    break;
                case "<":
                    formatter.format("           lt\n");
                    break;
                case ">=":
                    formatter.format("           ge\n");
                    break;
                case ">":
                    formatter.format("           gt\n");
                    break;
                case "and":
                    formatter.format("           and\n");
                    break;
                case "or":
                    formatter.format("           or\n");
                    break;
            }
            newTexts.put(ctx, formatter.toString());
        } else if (ctx.getChildCount() == 3 && ctx.getChild(1).getText().equals(",")) {
            String l1 = ctx.getChild(0).getText();
            String l2 = ctx.getChild(2).getText();
            formatter.format("           ldc %s\n", l1);
            formatter.format("           ldc %s\n", l2);
            newTexts.put(ctx, formatter.toString());
        } else if (ctx.IDENT() != null && ctx.getChildCount() == 3) {
            String expr = newTexts.get(ctx.getChild(2));
            Symbol lookupSymbol = lookup(ctx.IDENT().getText());
            formatter.format("%s", expr);
            formatter.format("           str %d %d\n", lookupSymbol.base, lookupSymbol.offset);
            newTexts.put(ctx, formatter.toString());
        } else if (ctx.IDENT() != null && ctx.getChildCount() == 6) {
            String index = newTexts.get(ctx.getChild(2));
            String expr = newTexts.get(ctx.getChild(5));
            Symbol lookupSymbol = lookup(ctx.IDENT().getText());

            formatter.format("%s", index);
            formatter.format("           lda %d %d\n", lookupSymbol.base, lookupSymbol.offset);
            formatter.format("           add\n");
            formatter.format("%s", expr);
            formatter.format("           sti\n");
            newTexts.put(ctx, formatter.toString());
        }
    }

    @Override
    public void exitArgs(MiniGoParser.ArgsContext ctx) {
        Formatter formatter = new Formatter();
        for (int i = 0; i < ctx.getChildCount(); i += 2) {
            formatter.format("%s", newTexts.get(ctx.getChild(i)));
        }
        newTexts.put(ctx, formatter.toString());
    }

    private Symbol lookup(String ident) {
        if (symbol.get(currentFunc).containsKey(ident)) {
            return symbol.get(currentFunc).get(ident);
        }
        if (symbol.get(0).containsKey(ident)) {
            return symbol.get(0).get(ident);
        }
        return new Symbol(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    private boolean isFrontOp(MiniGoParser.ExprContext ctx) {
        String op[] = {"-", "+", "--", "++", "!"};
        for (int i = 0; i < op.length; i++) {
            if (ctx.getChild(0).getText().equals(op[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isBinaryOp(MiniGoParser.ExprContext ctx) {
        String op[] = {"*", "/", "%", "+", "-", "==", "!=", "<=", "<", ">=", ">", "and", "or"};
        for (int i = 0; i < op.length; i++) {
            if (ctx.getChild(1).getText().equals(op[i])) {
                return true;
            }
        }
        return false;
    }
}