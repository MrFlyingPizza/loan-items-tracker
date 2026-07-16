package ca.richasf.control;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ca.richasf.model.LoanItem;

public class LoanItemsController {
    private static final String SAVE_FILE_PATH = "./list.json";

    private final SaveManager saveManager;
    private final List<LoanItem> loanItems;

    public LoanItemsController() {
        this.saveManager = new SaveManager(SAVE_FILE_PATH);
        this.loanItems = new ArrayList<>();
    }

    public LoanItem getLoanItem(int index) {
        return loanItems.get(index);
    }

    public void addLoanItem(LoanItem item) {
        loanItems.add(item);
    }

    public void removeLoanItem(int index) {
        loanItems.remove(index);
    }

    public void addAllLoanItems(Collection<LoanItem> newLoanItems) {
        loanItems.addAll(newLoanItems);
    }

    public int countLoanItems() {
        return loanItems.size();
    }

    public void saveLoanItems() throws IOException {
        saveManager.save(loanItems);
    }

    public void loadLoanItems() throws JsonIOException, JsonSyntaxException, IOException {
        loanItems.clear();
        loanItems.addAll(saveManager.load());
    }

    public Stream<LoanItem> streamLoanItems() {
        return loanItems.stream();
    }

    public Stream<LoanItem> streamOverdueLoanItems() {
        return loanItems.stream().filter(loan -> loan.getDue().isBefore(LocalDate.now()));
    }

    public Stream<LoanItem> streamUpcomingLoanItems() {
        return loanItems.stream().filter(loan -> !loan.getDue().isBefore(LocalDate.now()));
    }

    public Stream<LoanItem> streamSameTypeLoanItems(Class<? extends LoanItem> loanItemType) {
        return loanItems.stream().filter(loan -> loan.getClass().equals(loanItemType));
    }

    public Iterable<LoanItem> iterateLoanItems() {
        return loanItems;
    }
}
