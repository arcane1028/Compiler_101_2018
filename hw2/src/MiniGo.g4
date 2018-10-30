grammar MiniGo;
program		: decl+	{System.out.println("201302480 Rule 0"); };

decl		: var_decl {System.out.println("201302480 Rule 1-0\n"); }
			  | fun_decl {System.out.println("201302480 Rule 1-1\n"); };

var_decl	: dec_spec IDENT type_spec {System.out.println("201302480 Rule 2-0"); }
          | dec_spec IDENT type_spec '=' LITERAL  {System.out.println("201302480 Rule 2-1"); }
          | dec_spec IDENT '[' LITERAL ']' type_spec{System.out.println("201302480 Rule 2-2"); } ;

dec_spec	: VAR {System.out.println("201302480 Rule 3"); };

type_spec	: INT {System.out.println("201302480 Rule 4-0"); }
			    | {System.out.println("201302480 Rule 4-1"); };

fun_decl	: FUNC IDENT '(' params ')' type_spec compound_stmt {System.out.println("201302480 Rule 5-0"); }
			    | FUNC IDENT '(' params ')' '(' type_spec ',' type_spec ')' compound_stmt {System.out.println("201302480 Rule 5-1"); };

params		: param(',' param)* {System.out.println("201302480 Rule 6-0"); }
			    | {System.out.println("201302480 Rule 6-1"); };

param 	: IDENT {System.out.println("201302480 Rule 7-0"); }
			  | IDENT type_spec	{System.out.println("201302480 Rule 7-1"); };

stmt		: expr_stmt {System.out.println("201302480 Rule 8-0"); }
        | compound_stmt {System.out.println("201302480 Rule 8-1"); }
        | if_stmt {System.out.println("201302480 Rule 8-2"); }
        | for_stmt {System.out.println("201302480 Rule 8-3"); }
        | return_stmt {System.out.println("201302480 Rule 8-4"); };

expr_stmt	: expr	{System.out.println("201302480 Rule 9"); };

for_stmt	: FOR loop_expr stmt	{System.out.println("201302480 Rule 10-0"); }
			    | FOR expr stmt 	{System.out.println("201302480 Rule 10-1"); };

compound_stmt: '{' local_decl* stmt* '}' {System.out.println("201302480 Rule 11"); } ;

local_decl	: dec_spec IDENT type_spec  {System.out.println("201302480 Rule 12-0\n"); }
            | dec_spec IDENT type_spec '=' LITERAL  {System.out.println("201302480 Rule 12-1\n"); }
            | dec_spec IDENT '[' LITERAL ']' type_spec {System.out.println("201302480 Rule 12-2\n"); };

if_stmt		: IF expr stmt {System.out.println("201302480 Rule 13-0"); }
			    | IF expr stmt ELSE stmt {System.out.println("201302480 Rule 13-1"); };

return_stmt	:  RETURN expr {System.out.println("201302480 Rule 14-0"); }
            | RETURN expr ',' expr{System.out.println("201302480 Rule 14-1"); }
            | RETURN {System.out.println("201302480 Rule 14-2"); };


loop_expr   : expr ';' expr ';' expr ('++'|'--') {System.out.println("201302480 Rule 15"); };

expr		: (LITERAL|IDENT) {System.out.println("201302480 Rule 16-0"); }
        | '(' expr ')' {System.out.println("201302480 Rule 16-1"); }
        | IDENT '[' expr ']' {System.out.println("201302480 Rule 16-2"); }
        | IDENT '(' args ')' {System.out.println("201302480 Rule 16-3"); }
        | FMT '.' IDENT '(' args ')' {System.out.println("201302480 Rule 16-4"); }
        | op=('-'|'+'|'--'|'++'|'!') expr {System.out.println("201302480 Rule 16-5"); }
        | left=expr op=('*'|'/') right=expr {System.out.println("201302480 Rule 16-6"); }
        | left=expr op=('%'|'+'|'-') right=expr {System.out.println("201302480 Rule 16-7"); }
        | left=expr op=(EQ|NE|LE|'<'|GE|'>'|AND|OR) right=expr {System.out.println("201302480 Rule 16-8"); }
        | IDENT '=' expr {System.out.println("201302480 Rule 16-9"); }
        | IDENT '[' expr ']' '=' expr {System.out.println("201302480 Rule 16-10"); };

args		: expr (',' expr) * {System.out.println("201302480 Rule 17-0"); }
		  	| {System.out.println("201302480 Rule 17-1"); };

VAR			: 'var'   ;
FUNC		: 'func'  ;
FMT			: 'fmt'	  ;
INT			: 'int'   ;
FOR			: 'for'   ;
IF			: 'if'    ;
ELSE		: 'else'  ;
RETURN		: 'return';
OR			: 'or'    ;
AND			: 'and'   ;
LE			: '<='    ;
GE			: '>='    ;
EQ			: '=='    ;
NE			: '!='    ;

IDENT		: [a-zA-Z_] 
			( [a-zA-Z_]
			| [0-9]
			)*;
			
LITERAL		: DecimalConstant | OctalConstant | HexadecimalConstant ;

DecimalConstant	: '0' | [1-9] [0-9]* ;
OctalConstant	: '0' [0-7]* ;
HexadecimalConstant	: '0' [xX] [0-9a-fA-F]+ ;
WS			: (' '
			| '\t'
			| '\r'
			| '\n'        
			)+
	-> channel(HIDDEN)	 
    ;
