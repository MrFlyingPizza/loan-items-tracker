package ca.richasf.view.cli;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.BiConsumer;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.VideoLoanItem;

class LoanItemsAccumulator implements BiConsumer<StringBuilder, LoanItem> {
    
    @Override
    public void accept(StringBuilder sb, LoanItem item) {
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
    }

}