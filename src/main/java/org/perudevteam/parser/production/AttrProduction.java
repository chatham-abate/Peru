package org.perudevteam.parser.production;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.perudevteam.dynamic.Dynamic;

public abstract class AttrProduction<NT extends Enum<NT>, T extends Enum<T>> extends Production<NT, T> {

    public AttrProduction(NT s, Seq<Either<NT, T>> r) {
        super(s, r);
    }

    // An abstract syntax tree will simply be a function which takes an environment Dynamic and returns
    // some value dynamic.
    // The environment represents the attributes passed down from the parent.
    // The Value returned the attributes passed up from the children.

    // This production will state the rules for how to build ASTs.
    public abstract Function1<Dynamic, Dynamic> buildAST(
            Seq<? extends Function1<? super Dynamic, ? extends Dynamic>> children);
}
