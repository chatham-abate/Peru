package org.perudevteam.peru.ast;

import io.vavr.CheckedFunction2;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.perudevteam.charpos.CharPosEnum;
import org.perudevteam.parser.grammar.SemanticCFGrammar;

public class ASTCFGrammar<NT extends Enum<NT>, T extends Enum<T>>
        extends SemanticCFGrammar<NT, T, ASTProduction<NT, T>, String, CharPosEnum<T>, AST> {

    public ASTCFGrammar(NT s, Map<? extends T, ? extends CheckedFunction2<String, CharPosEnum<T>, AST>> termGens,
                        Seq<ASTProduction<NT, T>> prods) {
        super(s, termGens, prods);
    }
}
