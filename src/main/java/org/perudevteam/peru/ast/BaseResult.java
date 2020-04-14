package org.perudevteam.peru.ast;

import org.perudevteam.peru.base.BaseValue;

public class BaseResult {
    private BaseValue result;
    private int line;
    private int linePosition;

    public BaseResult(int l, int lp, BaseValue r) {
        result = r;
        line = l;
        linePosition = lp;
    }

    public BaseValue getResult() {
        return result;
    }

    public int getLine() {
        return line;
    }

    public int getLinePosition() {
        return linePosition;
    }

    public BaseResult withResult(BaseValue r) {
        return new BaseResult(line, linePosition, r);
    }

    public BaseResult withLine(int l) {
        return new BaseResult(l, linePosition, result);
    }

    public BaseResult withLinePosition(int lp) {
        return new BaseResult(line, lp, result);
    }
}
