//
// Grammar build build on the grammar on http://json.org and
// the grammar at https://github.com/antlr/grammars-v4/tree/master/json
//

grammar JSON;

@header {
    package org.jqassistant.plugin.json.parser;
}

//---
// Fragments
//---

json
    :   jsonObject
    |   array
    ;


jsonObject
    :   '{' keyValuePair (',' keyValuePair)* '}'
    |   '{' '}'
    |
    ;

keyValuePair
    :   STRING ':' value
    ;

arrayElements
    :   value (',' value)*
    ;

array
    :   '[' ']'
    |   '[' arrayElements ']'
    |
    ;

value
    :   array
    |   jsonObject
    |   STRING
    |   NUMBER
    |   'true'
    |   'false'
    |   'null'
    ;

fragment E
    :   [Ee] [+\-]? INT
    ;

fragment INT
    :   '0' | [1-9] [0-9]*
    ;

fragment HEX
    :   [0-9a-fA-F]
    ;

fragment UNICODE_ESCAPE_SEQ
    :   'u' HEX HEX HEX HEX
    ;

fragment ESC
    :   '\\' (["\\/bfnrt] | UNICODE_ESCAPE_SEQ)
    ;

STRING
    :   '"' (ESC | ~["\\])* '"'
    ;

WHITESPACE
    :   [ \t\n\r]+ -> skip
    ;

NUMBER
    :   '-'? INT '.' [0-9]+ E?
    |   '-'? INT E
    |   '-'? INT
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    :   '/*' .*? '*/' -> skip
    ;






