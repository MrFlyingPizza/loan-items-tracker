package ca.richasf;

/**
 * Core class for managing loans.
 */
public abstract class LoanItemsTracker {

    abstract protected void handleListAllLoanItems();

    abstract protected void handleAddLoanItems();

    abstract protected void handleRemoveLoanItems();

    abstract protected void handleListOverdueLoanItems();

    abstract protected void handleListUpcomingLoanItems();

    abstract protected void handleListSameTypeLoanItems();

    abstract protected void handleExit();

    /**
     * Starts the loan items tracker.
     */
    abstract public void run();

    /**
     * Entrypoint.
     * 
     * @param args CLI arguments.
     * @throws Exception If an error occurs.
     */
    public static void main(String[] args) throws Exception {
        new CommandLineLoanItemsTracker().run();
    }
}
