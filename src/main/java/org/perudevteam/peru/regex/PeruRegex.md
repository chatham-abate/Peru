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
  , `(`, `)`, `,`, `|`, `.`, `-`, `^`, `\ `.
  * `\ ` followed by any non-special character may refer to a specific set of characters.
  (Ex `\s` matches all white space characters)  
    
#### Character Classes
  * `[` starts a Character Class.
  * Within a Character Class, `]`, `-`, `^`, and `\ ` all have special meanings. 
  All other characters are treated as literals.
  * `]` ends a Character Class.
  * All backslash rules mentioned above work inside Character Classes.
  * `^` at the start of a Character Class refers to a complement. 
  (Ex `[^a]` matches all characters except `a`)
  * `-` marks a range between two literals. (Ex `[a-z]` matches all lowercase letters)
  
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
<CharClassCMD>     =:: "]" | "-" | "^" | "\"
<OtherCMD>         =:: "*" | "+" | "?" | "{" | "}" 
                     | "(" | ")" | "[" | "," | "."
                     | "|"
<DigitLiteral>     =:: "0" | "1" | "2" | "3" | "4"
                     | "5" | "6" | "7" | "8" | "9"
<OtherLiteral>     =:: Every ASCII Character which is not 
                       a <CharClassCMD>, <OtherCMD>, or <DigitLiteral>.

<CMD>              =:: <CharClassCMD> | <OtherCMD>
<Escape>           =:: "\" <CMD>

<Literal>          =:: <DigitLiteral> | <OtherLiteral>
<SpecialSet>       =:: "\" <Literal>

<CharClassLiteral> =:: <Literal> | <OtherCMD>
<CharClassChar>    =:: <CharClassLiteral> | <Escape>
<CharClassRange>   =:: <CharClassChar> "-" <CharClassChar>
<CharClassToken>   =:: <CharClassChar> | <CharClassRange> 
                     | <SpecialSet> 
<CharClassInner>   =:: <CharClassInner> <CharClassToken>
                     | <CharClassToken>
<CharClass>        =:: "[" <CharClassInner> "]"
                     | "[" "^" <CharClassInner> "]"   



<Group>            =::   
 
```