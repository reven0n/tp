package nusemp.logic.parser;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Arrays;
import java.util.List;

/**
 * A prefix that marks the beginning of an argument in an arguments string.
 * E.g. '--tag ' in 'contact add James --tag friend'.
 */
public class Prefix {
    private final String[] prefixes;

    /**
     * Constructs a prefix.
     * Multiple values can be defined, which all act as synonyms for the same prefix.
     * Note that the first value would be the primary value, used for display in toString().
     *
     * @param prefixes The prefix values. Must contain at least one value, and none of the values can be null.
     */
    public Prefix(String... prefixes) {
        assert prefixes.length > 0 : "There should be at least one prefix string!";
        requireAllNonNull((Object[]) prefixes);

        this.prefixes = prefixes;
    }

    public List<String> getPrefixes() {
        return List.of(prefixes);
    }

    /**
     * @return The primary prefix string.
     */
    @Override
    public String toString() {
        return prefixes[0];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(prefixes);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Prefix)) {
            return false;
        }

        Prefix otherPrefix = (Prefix) other;
        return Arrays.equals(prefixes, otherPrefix.prefixes);
    }
}
