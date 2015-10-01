//
// Grammar build build on the grammar on http://json.org and
// the grammar at https://github.com/antlr/grammars-v4/tree/master/json
//

grammar JSON;

@header {
    package org.jqassistant.plugin.json.parser;
}


jsonDocument
    :   jsonObject
    |   jsonArray
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

jsonArray
    :   '[' ']'
    |   '[' arrayElements ']'
    |
    ;

value
    :   jsonArray
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

// Some JSON parsers support line comments
LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

// Some JSON parsers support block comments
BLOCK_COMMENT
    :   '/*' .*? '*/' -> skip
    ;






