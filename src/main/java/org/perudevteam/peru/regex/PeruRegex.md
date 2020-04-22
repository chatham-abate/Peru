## Peru Regex Grammar and Rules

As of now, Peru does not use all the popular Regex standards. 

Here is a list of the Regex rules supported by Peru.

#### Rules
  * __Peru Regex__ will only match Strings of ASCII Characters (0 - 127).
  * `A*` matches `A` 0 or more times.
  * `A+` matches `A` 1 or more times.
  * `A?` matches `A` 1 or 0 times.
  * `A{n}` matches `A` exactly n times.
  * `A{min,}` matches `A` at least `min` times.
  * `A{min,max}` matches `A` at least `min` times and at most `max` times. 
  * `A|B` matches `A` or `B`.
  * `AB` matched `A` followed by `B`.
  * `.` matches any character.
  * `(A)` matches`A` and refers to a group.
  * `[...]` marks a Character Class. The rules for what `...` can be are listed below. 
  A Character Class represents a set of characters and matches any single character inside
  that set. 
  * `\ ` followed by any special character escapes that special character's meaning. 
  Here are all __Peru Regex__ special characters... `*`, `+`, `?`, `{`, `}`, `[`, `]` 
  , `(`, `)`, `,`, `|`, `.`, `-`, `^`, `\ `. (__NOTE :__ These special characters always
  require a backslash to be treated as a literal)
  * `\ ` followed by any non-special character may refer to a specific set of characters.
  (Ex `\s` matches all white space characters)  
    
#### Character Classes
  * `[` starts a Character Class.
  * `]` ends a Character Class.
  * `^` at the start of a Character Class refers to a complement. 
  (Ex `[^a]` matches all characters except `a`)
  * `-` marks a range between two literals. (Ex `[a-z]` matches all lowercase letters)
  * Character Classes cannot be nested.
  * Since Character Classes cannot be nested, no preset Character Classes are allowed
    inside Character Classes. `\ ` can only appear if it is escaping a special character.
  
#### Precedence

Here the order for which to interpret the Peru Regex rules listed above.

  1. `\ ` followed by anything.
  2. `[...]` Character Classes. 
  3. `(A)` Grouping.
  4. `+`, `*`, `?`, `{n}`, `{min,}`, `{min,max}` Quantification.
  5. `AB` Concatenation.
  6. `|` Alternation.
#### Grammar
```
<Escape>        =:: "\" "*" | "\" "+" | "\" "?" 
                  | "\" "{" | "\" "}" | "\" "[" 
                  | "\" "]" | "\" "(" | "\" ")" 
                  | "\" "," | "\" "|" | "\" "." 
                  | "\" "-" | "\" "^" | "\" "\"

<Digit>         =:: "0" | "1" | "2" | "3" | "4"
                  | "5" | "6" | "7" | "8" | "9"

<NonSpecial>    =:: All ASCII Characters which do not need to be escaped
                    and are not digits.

<Literal>       =:: <Escape> | <Digit> | <NonSpecial> 

<ClassPreset>   =:: "\" <NonSpecial> | "\" <Digit>

<ClassAtom>     =:: <Literal> | <Literal> "-" <Literal>
<ClassInner>    =:: <ClassInner> <ClassAtom> | <ClassAtom>
<Class>         =:: "[" <ClassInner> "]" | "[" "^" <ClassInner> "]"

<Value>         =:: <Literal> | <Class> | <ClassPreset> 
                  | "(" <Expression> ")"

<Number>        =:: <Number> <Digit> | <Digit>
<Quantifier>    =:: <Value> "+" 
                  | <Value> "*"
                  | <Value> "?"
                  | <Value> "{" <Number> "}"
                  | <Value> "{" <Number> ","  "}"
                  | <Value> "{" <Number> "," <Number> "}"

<Concat>        =:: <Concat> <Quantifier> | <Quantifier>
<Expression>    =:: <Expression> "|" <Concat> | <Concat> 
```