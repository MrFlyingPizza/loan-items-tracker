package ca.richasf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import ca.richasf.model.LoanItem;

/**
 * A factory that prompts the user to create loan items.
 */
public final class LoanItemFactory {
    private final Map<String, Supplier<LoanItem>> suppliers = new HashMap<>();

    public Optional<LoanItem> getInstance(String type) {
        return Optional.ofNullable(suppliers.get(type)).map(Supplier::get);
    }

    public void addSupplier(String type, Supplier<LoanItem> supplier) {
        suppliers.put(type, supplier);
    }
}
