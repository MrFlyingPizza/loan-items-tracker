package ca.richasf;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.MovieLoanItem;

/**
 * A factory that prompts the user to create loan items.
 */
public interface LoanItemFactory {
    Map<String, Supplier<LoanItem>> PROMPTS = Map.of(
                "book", LoanItemFactory::promptBookLoanItem,
                "audio", LoanItemFactory::promptAudioLoanItem,
                "movie", LoanItemFactory::promptMovieLoanItem);

    static Optional<LoanItem> getInstance(String type) {
        return Optional.ofNullable(PROMPTS.get(type)).map(Supplier::get);
    }

    static BookLoanItem promptBookLoanItem() {
        return null; // TODO
    }

    static AudioLoanItem promptAudioLoanItem() {
        return null; // TODO
    }

    static MovieLoanItem promptMovieLoanItem() {
        return null; // TODO
    }
}
