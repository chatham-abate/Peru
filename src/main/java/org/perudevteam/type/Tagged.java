package org.perudevteam.type;

import java.util.Objects;
import org.perudevteam.fa.*;

/**
 * Here is a Tagged abstract class... right now can apply to both
 * data tags (think "INT", "DOUBLE" ... etc)
 * and also operation tags (think "ADD", "SUBTRACT" ... etc)
 *
 * @param <TG> Tag enum.
 */
public abstract class Tagged<TG extends Enum<TG>> {
    private final TG tag;

    public Tagged(TG tt) {
        Objects.requireNonNull(tt);
        tag = tt;
    }

    public TG getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tagged<?> tagged = (Tagged<?>) o;
        return tag.equals(tagged.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}
