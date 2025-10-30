package nusemp.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

/**
 * Custom {@code TransformationList} that adds an extra value at the start of the source list.
 * Used in contact and event listviews to provide an additional heading item.
 * Should not be modified.
 */
public class DisplayedList<E, V> extends TransformationList<E, E> {

    private V extraValue;

    /** Creates an {@code DisplayedList} that adds {@code extraValue} at the start of {@code source}. */
    public DisplayedList(ObservableList<? extends E> source, V extraValue) {
        super(source);
        this.extraValue = extraValue;
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends E> c) {
        beginChange();

        while (c.next()) {
            if (c.wasPermutated()) {
                int[] permutation = new int[c.getTo() - c.getFrom()];
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    permutation[i - c.getFrom()] = c.getPermutation(i) + 1;
                }
                nextPermutation(c.getFrom() + 1, c.getTo() + 1, permutation);
            } else if (c.wasUpdated()) {
                nextUpdate(c.getFrom() + 1);
            } else {
                if (c.wasRemoved()) {
                    nextRemove(c.getFrom() + 1, c.getRemoved());
                }
                if (c.wasAdded()) {
                    nextAdd(c.getFrom() + 1, c.getTo() + 1);
                }
            }
        }

        endChange();
    }

    @Override
    public int getSourceIndex(int i) {
        if (i <= 0 || i >= size()) {
            return -1;
        }
        return i - 1;
    }

    @Override
    public int getViewIndex(int i) {
        if (i < 0 || i >= getSource().size()) {
            return -1;
        }
        return i + 1;
    }

    /**
     * {@inheritDoc}
     *
     * At index 0, null is returned instead. You should use {@link #getExtraValue()} to get the extra value.
     */
    @Override
    public E get(int index) {
        if (index == 0) {
            return null;
        }
        int sourceIndex = getSourceIndex(index);
        if (sourceIndex == -1) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        return getSource().get(sourceIndex);
    }

    @Override
    public int size() {
        return getSource().size() + 1;
    }

    public V getExtraValue() {
        return extraValue;
    }

    /**
     * Updates the extra value with the new value.
     */
    public void updateExtraValue(V newValue) {
        beginChange();
        extraValue = newValue;
        nextUpdate(0);
        endChange();
    }
}
