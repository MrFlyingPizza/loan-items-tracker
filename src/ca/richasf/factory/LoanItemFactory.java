package ca.richasf.factory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.richasf.model.LoanItem;

/**
 * A factory that prompts the user to create loan items.
 */
public final class LoanItemFactory {
    /**
     * An entry of a loan item supplier and its metadata.
     * 
     * @param type     String-representation of the type, must be unique.
     * @param text     The display text for this supplier.
     * @param supplier The supplier that creates a loan item.
     */
    public static record Entry(String type, String text, LoanItemSupplier supplier) {
    }

    private final Map<String, Entry> suppliers;

    /**
     * Constructs the loan factory which uses the provided suppliers.
     * 
     * @param suppliers The suppliers to create loan items with.
     */
    public LoanItemFactory(Entry... suppliers) {
        this.suppliers = Stream.of(suppliers)
                .collect(Collectors.toMap(Entry::type, Function.identity()));
    }

    /**
     * Constructs a loan item by prompting the user given the type.
     * 
     * @param code The type of the loan item to prompt for.
     * @return The result loan item.
     * @throws LoanItemSupplyException If getting a loan item fails.
     */
    public LoanItem getInstance(String code) throws LoanItemSupplyException {
        var entry = suppliers.get(code);
        if (entry == null) {
            throw new LoanItemSupplyException(code);
        }
        return entry.supplier.get();
    }

    /**
     * Get available types to create.
     * 
     * @return A set of strings representing available types.
     */
    public Set<String> getTypes() {
        return suppliers.keySet();
    }
}
