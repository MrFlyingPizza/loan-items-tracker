package ca.richasf.view.cli;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.VideoLoanItem;

public class LoanItemsListDisplayCollector
        implements Collector<LoanItem, StringBuilder, String> {

    private int counter = 0;

    @Override
    public BiConsumer<StringBuilder, LoanItem> accumulator() {
        return (sb, item) -> {
            sb.append("Loan Item Type: ").append(item.getTypeAsString()).append('\n');
            sb.append(item.getName()).append('\n');
            sb.append("- published by ").append(item.getPublisher()).append('\n');
            sb.append("- loaned to ").append(item.getLoanedTo()).append('\n');

            var due = item.getDue();
            sb.append("- ");
            sb.append("due on ").append(due);
            var daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), due);
            sb.append(" (");
            if (daysUntilDue > 0) {
                sb.append("due in ")
                        .append(daysUntilDue)
                        .append(" days(s)");
            } else if (daysUntilDue < 0) {
                sb.append("overdue by ")
                        .append(-daysUntilDue)
                        .append(" days(s)");
            } else {
                sb.append("due today");
            }
            sb.append(')').append('\n');

            switch (item) {
                case BookLoanItem book -> sb.append("- ")
                        .append(book.getPageCount())
                        .append(" pages");
                case AudioLoanItem audio -> {
                    var duration = audio.getDuration();
                    sb.append("- ")
                            .append(duration.toHours()).append(" hour(s) ")
                            .append(duration.toMinutesPart()).append(" minute(s) ")
                            .append(duration.toSecondsPart()).append(" second(s) ")
                            .append("long");
                }
                case VideoLoanItem video -> sb.append("- ")
                        .append(video.getGenre()).append(" genre");
                default -> {
                }
            }

            sb.append('\n');

            counter++;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }

    @Override
    public BinaryOperator<StringBuilder> combiner() {
        return (a, b) -> a.append(b);
    }

    @Override
    public Function<StringBuilder, String> finisher() {
        return sb -> {
            if (counter == 0) {
                sb.append("No items to show.\n");
            }

            return sb.toString();
        };
    }

    @Override
    public Supplier<StringBuilder> supplier() {
        return StringBuilder::new;
    }

}
