//
// Grammar build build on the grammar on http://json.org and
// the grammar at https://github.com/antlr/grammars-v4/tree/master/json
//

grammar JSON;

//---
// Fragments
//---

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

json:   jsonObject
    ;


jsonObject
    :   '{' keyValuePair (',' keyValuePair)* '}'
    ;

keyValuePair
    :   STRING ':' value
    ;

value
    :   STRING
    ;






