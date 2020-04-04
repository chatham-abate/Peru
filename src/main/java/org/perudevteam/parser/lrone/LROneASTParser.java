package org.perudevteam.parser.lrone;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import org.perudevteam.dynamic.Dynamic;
import org.perudevteam.misc.Typed;
import org.perudevteam.parser.ASTParser;

public class LROneASTParser<NT extends Enum<NT>, T extends Enum<T>, L, D extends Typed<T>>
        implements ASTParser<L, D> {

    @Override
    public Function1<Dynamic, Dynamic> buildASTUnsafe(Seq<Tuple2<L, D>> tokens) throws Throwable {
        return null;
    }
}
