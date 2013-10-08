package org.bigraph.model.interfaces;

/**
 * The abstract interface to {@link SortSet}s.
 */
public interface ISortSet {
	Iterable<? extends ISort> getPlaceSorts();
	Iterable<? extends ISort> getLinkSorts();
}
