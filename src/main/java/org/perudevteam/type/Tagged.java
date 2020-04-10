package org.perudevteam.type;

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
        tag = tt;
    }

    public TG getTag() {
        return tag;
    }
}
