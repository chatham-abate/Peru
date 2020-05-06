package org.perudevteam.ide.editor;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

import java.util.Objects;

public class Stylist {

    public static Stylist stylist(
            TokenStyle ds, TokenStyle es,
            Function1<? super String,
                    ? extends Seq<? extends Tuple2<? extends String, ? extends Try<? extends TokenStyle>>>> sf) {
        Objects.requireNonNull(ds);
        Objects.requireNonNull(es);
        Objects.requireNonNull(sf);

        return new Stylist(ds, es, narrowStyleFunc(sf));
    }

    static Function1<String, Seq<Tuple2<String, Try<TokenStyle>>>> narrowStyleFunc(
            Function1<? super String,
                    ? extends Seq<? extends Tuple2<? extends String, ? extends Try<? extends TokenStyle>>>> sf
    ) {
        Function1<? super String, ? extends Seq<Tuple2<String, Try<TokenStyle>>>> fInnerNarrow =
                s -> sf.apply(s).map(tuple -> Tuple.narrow(tuple.map2(Try::narrow)));
        return Function1.narrow(fInnerNarrow);
    }

    private final TokenStyle defaultStyle;
    private final TokenStyle errorStyle;
    private final Function1<String, Seq<Tuple2<String, Try<TokenStyle>>>> styleFunc;


    protected Stylist(TokenStyle ds, TokenStyle es,
                      Function1<String, Seq<Tuple2<String, Try<TokenStyle>>>> sf) {
        defaultStyle = ds;
        errorStyle = es;
        styleFunc = sf;
    }

    public Seq<Tuple2<String, TokenStyle>> style(String s) {
        return styleFunc.apply(s).map(tuple -> tuple.map2(ty -> ty.getOrElse(errorStyle)));
    }

    public TokenStyle getDefaultStyle() {
        return defaultStyle;
    }

    public TokenStyle getErrorStyle() {
        return errorStyle;
    }

    public Function1<String, Seq<Tuple2<String, Try<TokenStyle>>>> getStyleFunc() {
        return styleFunc;
    }

    public Stylist withDefaultStyle(TokenStyle ds) {
        Objects.requireNonNull(ds);
        return new Stylist(ds, errorStyle, styleFunc);
    }

    public Stylist mapDefaultStyle(Function1<? super TokenStyle, ? extends TokenStyle> f) {
        Objects.requireNonNull(f);
        return new Stylist(f.apply(defaultStyle), errorStyle, styleFunc);
    }

    public Stylist withErrorStyle(TokenStyle es) {
        Objects.requireNonNull(es);
        return new Stylist(defaultStyle, es, styleFunc);
    }

    public Stylist mapErrorStyle(Function1<? super TokenStyle, ? extends TokenStyle> f) {
        Objects.requireNonNull(f);
        return new Stylist(defaultStyle, f.apply(errorStyle), styleFunc);
    }

    public Stylist withStyleFunc(
            Function1<? super String,
                    ? extends Seq<? extends Tuple2<? extends String, ? extends Try<? extends TokenStyle>>>> sf
    ) {
        Objects.requireNonNull(sf);
        return new Stylist(defaultStyle, errorStyle, narrowStyleFunc(sf));
    }
}
