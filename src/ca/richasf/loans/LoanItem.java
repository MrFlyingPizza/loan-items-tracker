package ca.richasf.loans;

import java.time.LocalDate;

public interface LoanItem {
    String getName();
    String getPublisher();
    String getLoanedTo();
    LocalDate getDue();
}
