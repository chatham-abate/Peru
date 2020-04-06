package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;

public class CharSimpleContext {

    public static final CharSimpleContext INIT_SIMPLE_CONTEXT = new CharSimpleContext(
            PositionData.INIT_POSITION,
            PositionData.INIT_POSITION
    );

   private PositionData line;
   private PositionData linePosition;

   public CharSimpleContext(PositionData l, PositionData lp) {
       line = l;
       linePosition = lp;
   }

    public PositionData getLine() {
        return line;
    }

    public PositionData getLinePosition() {
        return linePosition;
    }

    public CharSimpleContext withLine(PositionData l) {
       return new CharSimpleContext(l, linePosition);
    }

    public CharSimpleContext mapLine(Function1<? super PositionData, ? extends  PositionData> m) {
       return new CharSimpleContext(m.apply(line), linePosition);
    }

    public CharSimpleContext withLinePosition(PositionData lp) {
       return new CharSimpleContext(line, lp);
    }

    public CharSimpleContext mapLinePosition(Function1<? super PositionData, ? extends PositionData> m) {
       return new CharSimpleContext(line, m.apply(linePosition));
    }

    public CharSimpleContext map(Function1<? super PositionData, ? extends  PositionData> lm,
                                 Function1<? super PositionData, ? extends  PositionData> lpm) {
       return new CharSimpleContext(lm.apply(line), lpm.apply(linePosition));
    }
}
