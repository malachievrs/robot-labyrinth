package com.lab.robot.interpreter.lexer;

import java_cup.runtime.*;
import static com.lab.robot.interpreter.parser.RobotSymbols.*;

%%

%class Lexer
%unicode
%line
%column
%cupsym RobotSymbols
%cup
%public
%type java_cup.runtime.Symbol

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

%eofval{
    return symbol(EOF);
%eofval}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

// Комментарии
Comment = "//" [^\r\n]* | "/*" ~"*/"

// Идентификатор
Identifier = [a-zA-Z][a-zA-Z0-9_]*

// Десятичные целые
DecInteger = 0 | [1-9][0-9]*

// Шестнадцатеричные: начинаются с x, затем цифры 0-9, A-F, a-f
HexInteger = x[0-9A-Fa-f]+

%%

// ========== Многосимвольные действия робота ==========
"^_^"           { return symbol(MOVE_UP); }
"v_v"           { return symbol(MOVE_DOWN); }
"<_<"           { return symbol(MOVE_LEFT); }
">_>"           { return symbol(MOVE_RIGHT); }
"o_o"           { return symbol(MOVE_FORWARD); }
"~_~"           { return symbol(MOVE_BACK); }

"^_0"           { return symbol(MEASURE_UP); }
"v_0"           { return symbol(MEASURE_DOWN); }
"<_0"           { return symbol(MEASURE_LEFT); }
">_0"           { return symbol(MEASURE_RIGHT); }
"o_0"           { return symbol(MEASURE_FORWARD); }
"~_0"           { return symbol(MEASURE_BACK); }

"*_*"           { return symbol(GETPOSITION); }
">_<"           { return symbol(BREAK); }

// ========== Ключевые слова ==========
"seisu"         { return symbol(SEISU); }
"ronri"         { return symbol(RONRI); }
"rippotai"      { return symbol(RIPPTAI); }
"hairetsu"      { return symbol(HAIRETSU); }

"shinri"        { return symbol(SHINRI); }
"uso"           { return symbol(USO); }

"shuki"         { return symbol(SHUKI); }
"kido"          { return symbol(KIDO); }
"shushi"        { return symbol(SHUSHI); }
"sorenara"      { return symbol(SORENARA); }
"kansu"         { return symbol(KANSU); }
"return"        { return symbol(RETURN); }
"ruikei"        { return symbol(RUIKEI); }
"jigen"         { return symbol(JIGEN); }

// ========== Одиночные символы и операторы ==========
";"             { return symbol(SEMICOLON); }
","             { return symbol(COMMA); }
":"             { return symbol(COLON); }
"("             { return symbol(LPAREN); }
")"             { return symbol(RPAREN); }
"{"             { return symbol(LBRACE); }
"}"             { return symbol(RBRACE); }
"["             { return symbol(LBRACKET); }
"]"             { return symbol(RBRACKET); }
"="             { return symbol(EQ); }
"+"             { return symbol(ADD); }
"-"             { return symbol(SUB); }
"~"             { return symbol(NOT); }
"^"             { return symbol(CONJUNCT); }
"<"             { return symbol(LESS); }
">"             { return symbol(GREATER); }
"=>"            { return symbol(DOT_ACCESS); }
"v"             { return symbol(DISJUNCT); }

// ========== Литералы и идентификаторы ==========
{DecInteger}    { return symbol(INT_LITERAL, Integer.parseInt(yytext())); }
{HexInteger}    { return symbol(HEX_LITERAL, yytext()); }
{Identifier}    { return symbol(IDENTIFIER, yytext()); }

{WhiteSpace}    { /* ignore */ }
{Comment}       { /* ignore */ }

[^]             { throw new RuntimeException("Illegal character '" + yytext() + "' at line " + yyline); }