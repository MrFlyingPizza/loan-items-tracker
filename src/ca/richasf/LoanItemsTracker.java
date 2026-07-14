package ca.richasf;

/**
 * Core class for managing loans.
 */
public class LoanItemsTracker {

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
