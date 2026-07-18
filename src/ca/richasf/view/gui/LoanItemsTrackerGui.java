package ca.richasf.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Predicate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import ca.richasf.control.LoanItemPredicateFactory;
import ca.richasf.control.LoanItemsController;
import ca.richasf.control.PersistenceException;
import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.VideoLoanItem;

/**
 * Core class for running the loan items tracker GUI app.
 */
public class LoanItemsTrackerGui {

    private final LoanItemPredicateFactory predicateFactory = new LoanItemPredicateFactory();

    private final LoanItemsController controller = new LoanItemsController();

    private final JFrame frame = new JFrame();

    private Predicate<LoanItem> duePredicate = predicateFactory.all(),
            typePredicate = predicateFactory.all();

    private final LoanItemsListView listView = new LoanItemsListView();

    /**
     * Construct a new loan items tracker GUI app instance.
     */
    public LoanItemsTrackerGui() {
    }

    /**
     * Update the loan items list view.
     */
    private void updateLoanItemsListView() {
        listView.updateLoanItems(controller.streamLoanItems()
                .filter(duePredicate.and(typePredicate))
                .toList());
    }

    /**
     * Update the predicate used to filter loan items by due date.
     * 
     * @param duePredicate The new predicate.
     */
    private void updateDuePredicate(Predicate<LoanItem> duePredicate) {
        this.duePredicate = duePredicate;
        updateLoanItemsListView();
    }

    /**
     * Update the predicate used to filter loan items by type.
     * 
     * @param typePredicate The new predicate.
     */
    private void updateTypePredicate(Predicate<LoanItem> typePredicate) {
        this.typePredicate = typePredicate;
        updateLoanItemsListView();
    }

    /**
     * Create the panel at the top of the page.
     * 
     * @return The page start panel.
     */
    private JPanel createPageStartPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var listOptionsView = new ListOptionsView<>(
                new ListOptionsView.Option<>("List All", predicateFactory.all()),
                new ListOptionsView.Option<>("List Overdue", predicateFactory.overdue()),
                new ListOptionsView.Option<>("List Upcoming", predicateFactory.upcoming()));

        listOptionsView.setSelectHandler(this::updateDuePredicate);

        panel.add(listOptionsView.getPanel());

        var typeOptionsView = TypeOptionsView.of(
                TypeOptionsView.option("All", predicateFactory.all()),
                TypeOptionsView.option("Book", predicateFactory.sameType(BookLoanItem.class)),
                TypeOptionsView.option("Audio", predicateFactory.sameType(AudioLoanItem.class)),
                TypeOptionsView.option("Video", predicateFactory.sameType(VideoLoanItem.class)));

        typeOptionsView.setSelectHandler(this::updateTypePredicate);

        panel.add(typeOptionsView.getPanel());

        return panel;
    }

    /**
     * Create the panel at the bottom of the page.
     * 
     * @return The page end panel.
     */
    private JPanel createPageEndPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        var addButton = new JButton("Add an Loan Item");
        panel.add(addButton);

        addButton.addActionListener(e -> {
            var addModal = new LoanItemAddModal(frame);
            addModal.setCreateHandler(item -> {
                controller.addLoanItem(item);
                updateLoanItemsListView();
            });
            addModal.setVisible(true);
        });

        return panel;
    }

    /**
     * Create the center content panel.
     * 
     * @return The content panel.
     */
    private JScrollPane createContentPanel() {
        var scrollPane = new JScrollPane(listView.getPanel());
        return scrollPane;
    }

    /**
     * Create the main panel.
     * 
     * @return The main panel.
     */
    private JPanel createMainPanel() {
        var mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createPageStartPanel(), BorderLayout.PAGE_START);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        mainPanel.add(createPageEndPanel(), BorderLayout.PAGE_END);

        return mainPanel;
    }

    /**
     * Starts the loan items GUI application.
     */
    public void start() {
        try {
            controller.loadLoanItems();
        } catch (PersistenceException e) {
            System.err.printf("Failed to load loan items from save: %e\n", e);
        }

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    System.out.println("Saving loan items.");
                    controller.saveLoanItems();
                } catch (PersistenceException exception) {
                    System.err.printf("Failed to save loan items: %e", exception);
                }
            }
        });

        frame.setTitle("Loan Items Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        listView.setDeleteHandler(toDelete -> {
            controller.removeLoanItem(toDelete);
            updateLoanItemsListView();
        });
        listView.updateLoanItems(controller.streamLoanItems().toList());

        frame.add(createMainPanel());

        frame.setMinimumSize(new Dimension(600, 400));

        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
