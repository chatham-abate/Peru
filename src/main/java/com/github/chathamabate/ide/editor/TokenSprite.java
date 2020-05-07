package com.github.chathamabate.ide.editor;

import com.github.chathamabate.ide.editor.sprite.Sprite;
import io.vavr.Function1;

import java.awt.*;
import java.util.Objects;

public class TokenSprite extends Sprite<TokenSprite> {

    public static TokenSprite tokenSprite(int x, int y, String l, TokenStyle s) {
        Objects.requireNonNull(l);
        Objects.requireNonNull(s);
        return new TokenSprite(x, y, l, s);
    }

    private final String lexeme;
    private final TokenStyle style;

    protected TokenSprite(int x, int y, String l, TokenStyle s) {
        super(x, y);
        lexeme = l;
        style = s;
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenSprite withLexeme(String lex) {
        Objects.requireNonNull(lex);
        return new TokenSprite(getXPosition(), getYPosition(), lex, style);
    }

    public TokenSprite mapLexeme(Function1<? super String, ? extends String> f) {
        Objects.requireNonNull(f);
        return withLexeme(f.apply(lexeme));
    }

    public TokenStyle getStyle() {
        return style;
    }

    public TokenSprite withStyle(TokenStyle s) {
        Objects.requireNonNull(s);
        return new TokenSprite(getXPosition(), getYPosition(), lexeme, s);
    }

    public TokenSprite mapStyle(Function1<? super TokenStyle, ? extends TokenStyle> f) {
        Objects.requireNonNull(f);
        return withStyle(f.apply(style));
    }

    @Override
    public TokenSprite withPosition(int x, int y) {
        return new TokenSprite(x, y, lexeme, style);
    }

    @Override
    public void render(Graphics g) {
        // TODO Write Render Function...
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TokenSprite that = (TokenSprite) o;
        return lexeme.equals(that.lexeme) &&
                style.equals(that.style);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lexeme, style);
    }
}
