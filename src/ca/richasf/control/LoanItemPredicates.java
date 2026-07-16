package ca.richasf.control;

import java.time.LocalDate;
import java.util.function.Predicate;

import ca.richasf.model.LoanItem;

public class LoanItemPredicates {
    public static Predicate<LoanItem> all() {
        return item -> true;
    }

    public static Predicate<LoanItem> overdue() {
        return item -> item.getDue().isBefore(LocalDate.now());
    }

    public static Predicate<LoanItem> upcoming() {
        return item -> !item.getDue().isBefore(LocalDate.now());
    }

    public static Predicate<LoanItem> sameType(Class<? extends LoanItem> type) {
        return item -> item.getClass().equals(type);
    }
}
