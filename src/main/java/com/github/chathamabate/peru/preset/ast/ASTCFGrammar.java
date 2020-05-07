package com.github.chathamabate.peru.preset.ast;

import com.github.chathamabate.peru.charpos.EnumCharPos;
import com.github.chathamabate.peru.parser.grammar.SemanticCFGrammar;
import io.vavr.CheckedFunction2;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public class ASTCFGrammar<NT extends Enum<NT>, T extends Enum<T>>
        extends SemanticCFGrammar<NT, T, ASTProduction<NT, T>, String, EnumCharPos<T>, AST> {

    public ASTCFGrammar(NT s, Map<? extends T, ? extends CheckedFunction2<String, EnumCharPos<T>, AST>> termGens,
                        Seq<ASTProduction<NT, T>> prods) {
        super(s, termGens, prods);
    }
}
