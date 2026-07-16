package ca.richasf.control;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ca.richasf.model.LoanItem;

public class LoanItemsController {
    private static final String SAVE_FILE_PATH = "./list.json";

    private final SaveManager saveManager;
    private final LinkedHashSet<LoanItem> loanItems;

    public LoanItemsController() {
        this.saveManager = new SaveManager(SAVE_FILE_PATH);
        this.loanItems = new LinkedHashSet<>();
    }

    public void addLoanItem(LoanItem item) {
        loanItems.add(item);
    }

    public void removeLoanItem(LoanItem item) {
        loanItems.remove(item);
    }

    public void addAllLoanItems(Collection<LoanItem> newLoanItems) {
        loanItems.addAll(newLoanItems);
    }

    public int countLoanItems() {
        return loanItems.size();
    }

    public void saveLoanItems() throws IOException {
        saveManager.save(loanItems.stream().toList());
    }

    public void loadLoanItems() throws JsonIOException, JsonSyntaxException, IOException {
        loanItems.clear();
        loanItems.addAll(saveManager.load());
    }

    public Stream<LoanItem> streamLoanItems() {
        return loanItems.stream();
    }

    public Iterable<LoanItem> iterateLoanItems() {
        return loanItems;
    }
}
