package ca.richasf.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Predicate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ca.richasf.control.LoanItemPredicates;
import ca.richasf.control.LoanItemsController;
import ca.richasf.control.PersistenceException;
import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.VideoLoanItem;

import static ca.richasf.control.LoanItemPredicates.*;

public class LoanItemsTrackerGui {

    private final LoanItemsController controller = new LoanItemsController();

    private Predicate<LoanItem> listPredicate = LoanItemPredicates.all(),
            typePredicate = LoanItemPredicates.all();

    private final LoanItemsListView listView = new LoanItemsListView();

    public LoanItemsTrackerGui() {
    }

    private void updateLoanItems() {
        listView.updateLoanItems(controller.streamLoanItems()
                .filter(listPredicate.and(typePredicate))
                .toList());
    }

    private void updateListPredicate(Predicate<LoanItem> listPredicate) {
        this.listPredicate = listPredicate;
        updateLoanItems();
    }

    private void updateTypePredicate(Predicate<LoanItem> typePredicate) {
        this.typePredicate = typePredicate;
        updateLoanItems();
    }

    private JPanel createPageStartPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var listOptionsView = ListOptionsView.of(
                ListOptionsView.option("List All", all()),
                ListOptionsView.option("List Overdue", overdue()),
                ListOptionsView.option("List Upcoming", upcoming()));

        listOptionsView.setSelectHandler(this::updateListPredicate);

        panel.add(listOptionsView.getPanel());

        var typeOptionsView = TypeOptionsView.of(
                TypeOptionsView.option("All", all()),
                TypeOptionsView.option("Book", sameType(BookLoanItem.class)),
                TypeOptionsView.option("Audio", sameType(AudioLoanItem.class)),
                TypeOptionsView.option("Video", sameType(VideoLoanItem.class)));

        typeOptionsView.setSelectHandler(this::updateTypePredicate);

        panel.add(typeOptionsView.getPanel());

        return panel;
    }

    private JPanel createPageEndPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        panel.add(new JButton("Add an Loan Item"));

        return panel;
    }

    private JScrollPane createContentPanel() {
        var scrollPane = new JScrollPane(listView.getPanel());
        return scrollPane;
    }

    private JPanel createMainPanel() {
        var mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createPageStartPanel(), BorderLayout.PAGE_START);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        mainPanel.add(createPageEndPanel(), BorderLayout.PAGE_END);

        return mainPanel;
    }

    public void start() {
        try {
            controller.loadLoanItems();
        } catch (PersistenceException e) {
            System.err.printf("Failed to load loan items from save: %e\n", e);
        }

        var frame = new JFrame();
        frame.setTitle("Loan Items Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new MainFrameWindowListener());

        var mainPanel = createMainPanel();

        frame.add(mainPanel);

        frame.setMinimumSize(new Dimension(600, 400));

        frame.setVisible(true);

        listView.setDeleteHandler(controller::removeLoanItem);
        listView.updateLoanItems(controller.streamLoanItems().toList());
    }
}
