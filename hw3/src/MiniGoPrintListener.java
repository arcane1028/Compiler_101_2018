import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MiniGoPrintListener extends MiniGoBaseListener {
    ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();

    @Override
    public void exitVar_decl(MiniGoParser.Var_declContext ctx) {
        String var = "", ident = "";
        var = newTexts.get(ctx.getChild(0));
        ident = ctx.getChild(1).getText();
        switch (ctx.getChildCount()) {
            case 3:
                newTexts.put(ctx,
                        var + " " + ident + " " + newTexts.get(ctx.getChild(2)));
                break;
            case 5:
                String type = newTexts.get(ctx.getChild(2));
                String eq = newTexts.get(ctx.getChild(3));
                String literal = newTexts.get(ctx.getChild(4));
                newTexts.put(ctx,
                        var + " " + ident + " " + type + " " + eq + " " + literal);
                break;
            case 6:
                String b1 = newTexts.get(ctx.getChild(2));
                String l = newTexts.get(ctx.getChild(3));
                String b2 = newTexts.get(ctx.getChild(4));
                String t = newTexts.get(ctx.getChild(5));
                newTexts.put(ctx,
                        var + " " + ident + b1 + l + b2 + " " + t);
        }
        System.out.println(newTexts.get(ctx));
    }

    @Override
    public void exitDec_spec(MiniGoParser.Dec_specContext ctx) {
        newTexts.put(ctx, "var");
    }

    @Override
    public void exitType_spec(MiniGoParser.Type_specContext ctx) {
        if (ctx.INT() == null) {
            newTexts.put(ctx, "");
        } else {
            newTexts.put(ctx, "int");
        }

    }

    @Override
    public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {
        String func = newTexts.get(ctx.getChild(0));
        String ident = newTexts.get(ctx.getChild(1));
        String b1 = newTexts.get(ctx.getChild(2));
        String params = newTexts.get(ctx.getChild(3));
        String b2 = newTexts.get(ctx.getChild(4));
        if (ctx.getChildCount() == 7) {
            String type = newTexts.get(ctx.getChild(5));
            String compound_stmt = newTexts.get(ctx.getChild(6));
            newTexts.put(ctx,
                    func + " " + ident + b1 + params + b2 + " " + type + " " + compound_stmt);
        } else {
            String b21 = newTexts.get(ctx.getChild(5));
            String type1 = newTexts.get(ctx.getChild(6));
            String dot = newTexts.get(ctx.getChild(7));
            String type2 = newTexts.get(ctx.getChild(8));
            String b22 = newTexts.get(ctx.getChild(9));
            String compound_stmt = newTexts.get(ctx.getChild(10));
            newTexts.put(ctx,
                    func + " " + ident + b1 + params + b2 + " " + b21
                            + type1 + dot + " " + type2 + b22 + " " + compound_stmt);
        }
        System.out.println(newTexts.get(ctx));
    }

    @Override
    public void exitParams(MiniGoParser.ParamsContext ctx) {
        if (ctx.getChildCount() == 0) {
            newTexts.put(ctx, "");
        } else {
            String params = newTexts.get(ctx.getChild(0));
            for (int i = 1; (i * 2) <= ctx.getChildCount(); i++) {
                String param = newTexts.get(ctx.getChild(i * 2));
                params = params + ", " + param;
            }
            newTexts.put(ctx, params);
        }
    }

    @Override
    public void exitParam(MiniGoParser.ParamContext ctx) {
        String ident = newTexts.get(ctx.getChild(0));
        if (ctx.getChildCount() == 1) {
            newTexts.put(ctx, ident);
        } else {
            String type = newTexts.get(ctx.getChild(1));
            newTexts.put(ctx, ident + " " + type);
        }
    }

    @Override
    public void exitStmt(MiniGoParser.StmtContext ctx) {
        //super.exitStmt(ctx);
        String value = newTexts.get(ctx.getChild(0));
        newTexts.put(ctx, value);

    }

    @Override
    public void exitExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {
        // super.exitExpr_stmt(ctx);
        String value = newTexts.get(ctx.getChild(0));
        newTexts.put(ctx, value);
    }

    @Override
    public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {
        if (ctx.loop_expr() == null) {
            String f = newTexts.get(ctx.getChild(0));
            String expr = newTexts.get(ctx.getChild(1));
            String stmt = newTexts.get(ctx.getChild(2));
            newTexts.put(ctx, f + " " + expr + " " + stmt);
        } else {
            String f = newTexts.get(ctx.getChild(0));
            String loop_expr = newTexts.get(ctx.getChild(1));
            String stmt = newTexts.get(ctx.getChild(2));
            newTexts.put(ctx, f + " " + loop_expr + " " + stmt);
        }
    }

    @Override
    public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
        String stmt = newTexts.get(ctx.getChild(0)) + "\n";
        int compount_count = 0;
        ParseTree node = ctx;
        while (true) {
            if (node.getParent().getClass().equals(MiniGoParser.Compound_stmtContext.class)) {
                compount_count++;
            }
            if (node.getParent().getClass().equals(MiniGoParser.ProgramContext.class)) {
                break;
            }
            node = node.getParent();
        }
        String indent = "....";
        for (int i = 0; i < compount_count; i++) {
            indent = indent + "....";
        }
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            stmt = stmt + indent + newTexts.get(ctx.getChild(i)) + "\n";
        }
        String indent2 = "";
        for (int i = 0; i < compount_count; i++) {
            indent2 = indent2 + "....";
        }
        stmt = stmt + indent2 + newTexts.get(ctx.getChild(ctx.getChildCount() - 1));
        newTexts.put(ctx, stmt);
    }

    @Override
    public void exitLocal_decl(MiniGoParser.Local_declContext ctx) {
        String var = "", ident = "";
        var = newTexts.get(ctx.getChild(0));
        ident = ctx.getChild(1).getText();
        switch (ctx.getChildCount()) {
            case 3:
                newTexts.put(ctx,
                        var + " " + ident + " " + newTexts.get(ctx.getChild(2)));
                break;
            case 5:
                String type = newTexts.get(ctx.getChild(2));
                String eq = newTexts.get(ctx.getChild(3));
                String literal = newTexts.get(ctx.getChild(4));
                newTexts.put(ctx,
                        var + " " + ident + " " + type + " " + eq + " " + literal);
                break;
            case 6:
                String b1 = newTexts.get(ctx.getChild(2));
                String l = newTexts.get(ctx.getChild(3));
                String b2 = newTexts.get(ctx.getChild(4));
                String t = newTexts.get(ctx.getChild(5));
                newTexts.put(ctx,
                        var + " " + ident + b1 + l + b2 + " " + t);
        }
    }

    @Override
    public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {
        String ifstmt = newTexts.get(ctx.getChild(0));
        String expr = newTexts.get(ctx.getChild(1));
        String stmt = newTexts.get(ctx.getChild(2));
        String value = ifstmt + " " + expr + " " + stmt;
        if (ctx.getChildCount() == 5) {
            String else_stmt = newTexts.get(ctx.getChild(3));
            String stmt2 = newTexts.get(ctx.getChild(4));
            value = value + " " + else_stmt + " " + stmt2;
        }
        newTexts.put(ctx, value);
    }

    @Override
    public void exitReturn_stmt(MiniGoParser.Return_stmtContext ctx) {
        String r = newTexts.get(ctx.getChild(0));
        if (ctx.getChildCount() == 1) {
            newTexts.put(ctx, r);
        } else if (ctx.getChildCount() == 2) {
            String expr = newTexts.get(ctx.getChild(1));
            newTexts.put(ctx, r + " " + expr);
        } else {
            String expr1 = newTexts.get(ctx.getChild(1));
            String dot = newTexts.get(ctx.getChild(2));
            String expr2 = newTexts.get(ctx.getChild(3));
            newTexts.put(ctx, r + " " + expr1 + dot + " " + expr2);
        }
    }

    @Override
    public void exitLoop_expr(MiniGoParser.Loop_exprContext ctx) {
        String expr1 = newTexts.get(ctx.getChild(0));
        String c1 = newTexts.get(ctx.getChild(1));
        String expr2 = newTexts.get(ctx.getChild(2));
        String c2 = newTexts.get(ctx.getChild(3));
        String expr3 = newTexts.get(ctx.getChild(4));
        String pm = newTexts.get(ctx.getChild(5));
        newTexts.put(ctx, expr1 + c1 + " " + expr2 + c2 + " " + expr3 + pm);
    }

    @Override
    public void exitExpr(MiniGoParser.ExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            newTexts.put(ctx, newTexts.get(ctx.getChild(0)));
        } else if (ctx.getChild(0).getText().equals("(")) {
            String b1 = newTexts.get(ctx.getChild(0));
            String expr = newTexts.get(ctx.getChild(1));
            String b2 = newTexts.get(ctx.getChild(2));
            newTexts.put(ctx, b1 + expr + b2);
        } else if (isExpr3(ctx)) {
            String ident = newTexts.get(ctx.getChild(0));
            String b1 = newTexts.get(ctx.getChild(1));
            String expr = newTexts.get(ctx.getChild(2));
            String b2 = newTexts.get(ctx.getChild(3));
            if (ctx.getChildCount() == 6) {
                String in = newTexts.get(ctx.getChild(4));
                String expr2 = newTexts.get(ctx.getChild(5));
                newTexts.put(ctx, ident + b1 + expr + b2 + " " + in + " " + expr2);
            } else {
                newTexts.put(ctx, ident + b1 + expr + b2);
            }
        } else if (isExpr4(ctx)) {
            String ident = newTexts.get(ctx.getChild(0));
            String b1 = newTexts.get(ctx.getChild(1));
            String args = newTexts.get(ctx.getChild(2));
            String b2 = newTexts.get(ctx.getChild(3));
            newTexts.put(ctx, ident + b1 + args + b2);
        } else if (isFmt(ctx)) {
            String fmt = newTexts.get(ctx.getChild(0));
            String dot = newTexts.get(ctx.getChild(1));
            String ident = newTexts.get(ctx.getChild(2));
            String b1 = newTexts.get(ctx.getChild(3));
            String args = newTexts.get(ctx.getChild(4));
            String b2 = newTexts.get(ctx.getChild(5));
            newTexts.put(ctx, fmt + dot + ident + b1 + args + b2);
        } else if (isFrontOp(ctx)) {
            String op = newTexts.get(ctx.getChild(0));
            String expr = newTexts.get(ctx.getChild(1));
            newTexts.put(ctx, op + expr);
        } else if (isBinaryOp(ctx)) {
            String left = newTexts.get(ctx.getChild(0));
            String op = newTexts.get(ctx.getChild(1));
            String right = newTexts.get(ctx.getChild(2));
            newTexts.put(ctx, left + " " + op + " " + right);
        } else if (isInput(ctx)) {
            String ident = newTexts.get(ctx.getChild(0));
            String input = newTexts.get(ctx.getChild(1));
            String expr = newTexts.get(ctx.getChild(2));
            newTexts.put(ctx, ident + " " + input + " " + expr);
        }
    }

    private boolean isInput(MiniGoParser.ExprContext ctx) {
        if (ctx.getChild(1).getText().equals("=")) {
            return true;
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

    private boolean isFrontOp(MiniGoParser.ExprContext ctx) {
        String op[] = {"-", "+", "--", "++", "!"};
        for (int i = 0; i < op.length; i++) {
            if (ctx.getChild(0).getText().equals(op[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isFmt(MiniGoParser.ExprContext ctx) {
        if (ctx.getChild(0).getText().equals("fmt")) {
            return true;
        }
        return false;
    }


    private boolean isExpr4(MiniGoParser.ExprContext ctx) {
        if (ctx.getChild(0).equals(ctx.IDENT())
                && ctx.getChildCount() == 4
                && ctx.getChild(2).equals(ctx.args())) {
            return true;
        }
        return false;
    }

    private boolean isExpr3(MiniGoParser.ExprContext ctx) {
        if (ctx.getChild(1).getText().equals("[")
                && ((ctx.getChildCount() == 4) || (ctx.getChildCount() == 6))) {
            return true;
        }
        return false;
    }

    boolean isBinaryOperation(MiniGoParser.ExprContext ctx) {
        return ctx.getChildCount() == 3 && ctx.getChild(1) != ctx.expr();
    }

    @Override
    public void exitArgs(MiniGoParser.ArgsContext ctx) {
        if (ctx.getChild(0) == null) {
            newTexts.put(ctx, "");
        } else {
            String value = newTexts.get(ctx.getChild(0));
            for (int i = 1; i * 2 < ctx.getChildCount(); i++) {
                value = value + newTexts.get(ctx.getChild(i * 2 - 1)) + " " + newTexts.get(ctx.getChild(i * 2));
            }
            newTexts.put(ctx, value);
        }
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        newTexts.put(node, node.getText());
    }
}