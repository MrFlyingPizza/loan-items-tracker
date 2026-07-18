package ca.richasf.loanitemstracker.control;

import java.time.LocalDate;
import java.util.function.Predicate;

import ca.richasf.loanitemstracker.model.LoanItem;

/**
 * Constructs predicates on loan items.
 */
public class LoanItemPredicateFactory {
    
    /**
     * Constructs a new predicate factory.
     */
    public LoanItemPredicateFactory() {
	}

	/**
     * Get a predicate that does allows all loan items.
     * @return A predicate.
     */
    public Predicate<LoanItem> all() {
        return item -> true;
    }

    /**
     * Get a predicate that selects overdue items.
     * @return A predicate.
     */
    public Predicate<LoanItem> overdue() {
        return item -> item.getDue().isBefore(LocalDate.now());
    }

    /**
     * Get a predicate that selects upcoming items.
     * @return A predicate.
     */
    public Predicate<LoanItem> upcoming() {
        return item -> !item.getDue().isBefore(LocalDate.now());
    }

    /**
     * Get a predicate that selects the same type.
     * @param type The type to select.
     * @return A predicate.
     */
    public Predicate<LoanItem> sameType(Class<? extends LoanItem> type) {
        return item -> item.getClass().equals(type);
    }
}
