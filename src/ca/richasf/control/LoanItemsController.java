package ca.richasf.control;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ca.richasf.model.LoanItem;

/**
 * Manages loan items and their persistence.
 */
public class LoanItemsController {
    private static final String SAVE_FILE_PATH = "./list.json";

    private final SaveManager saveManager;
    private final LinkedHashSet<LoanItem> loanItems;

    /**
     * Constructs a new loan items controller.
     */
    public LoanItemsController() {
        this.saveManager = new SaveManager(SAVE_FILE_PATH);
        this.loanItems = new LinkedHashSet<>();
    }

    /**
     * Add a new loan item. Existing loan items are ignored.
     * @param item The new loan item to add.
     */
    public void addLoanItem(LoanItem item) {
        loanItems.add(item);
    }

    /**
     * Remove an existing loan item. Does nothing if it doesn't exist.
     * @param item The item to remove.
     */
    public void removeLoanItem(LoanItem item) {
        loanItems.remove(item);
    }

    /**
     * Add multiple loan items, repeated instances are only added once.
     * @param newLoanItems The new loan items to add.
     */
    public void addAllLoanItems(Collection<LoanItem> newLoanItems) {
        loanItems.addAll(newLoanItems);
    }

    /**
     * Count the number of loan items.
     * @return The amount of loan items.
     */
    public int countLoanItems() {
        return loanItems.size();
    }

    /**
     * Save loan items to persistent storage.
     * @throws PersistenceException If saving loan items fail.
     */
    public void saveLoanItems() throws PersistenceException {
        try {
            saveManager.save(loanItems.stream().toList());
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Load loan items from persistent storage.
     * @throws PersistenceException If loading loan items fail.
     */
    public void loadLoanItems() throws PersistenceException {
        loanItems.clear();
        try {
            loanItems.addAll(saveManager.load());
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw new PersistenceException(e);
        }
    }

    public Stream<LoanItem> streamLoanItems() {
        return loanItems.stream();
    }

    public Iterable<LoanItem> iterateLoanItems() {
        return loanItems;
    }
}
