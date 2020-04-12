package org.perudevteam.type;

import java.util.Objects;

/**
 * Here is a Tagged abstract class... right now can apply to both
 * data tags (think "INT", "DOUBLE" ... etc)
 * and also operation tags (think "ADD", "SUBTRACT" ... etc)
 *
 * @param <TG> Tag enum.
 */
public abstract class Tagged<TG extends Enum<TG>> {
    private TG tag;

    public Tagged(TG tt) {
        Objects.requireNonNull(tt);
        tag = tt;
    }

    public TG getTag() {
        return tag;
    }
}
