package ca.richasf.loanitemstracker;

import javax.swing.SwingUtilities;

import ca.richasf.loanitemstracker.view.gui.LoanItemsTrackerGui;

/**
 * Core class for managing loans.
 */
public class LoanItemsTracker {

    /**
     * Constructs a loan items tracker.
     */
    public LoanItemsTracker() {
    }

    /**
     * Entrypoint.
     * 
     * @param args CLI arguments.
     * @throws Exception If an error occurs.
     */
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            new LoanItemsTrackerGui().start();
        });
    }
}
