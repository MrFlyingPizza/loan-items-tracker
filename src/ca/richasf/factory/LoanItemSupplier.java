package ca.richasf.factory;

import ca.richasf.model.LoanItem;

/**
 * Supplies a loan item or indicates error.
 */
@FunctionalInterface
public interface LoanItemSupplier {
    LoanItem get() throws LoanItemSupplyException;
}
