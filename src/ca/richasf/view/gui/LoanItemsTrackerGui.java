package ca.richasf.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ca.richasf.control.LoanItemsController;
import ca.richasf.model.LoanItem;

public class LoanItemsTrackerGui {

    private final LoanItemsController controller;

    public LoanItemsTrackerGui() {
        controller = new LoanItemsController();
    }

    private JPanel createLoanItemTypeButtonPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        var allButton = new JRadioButton("All", true);
        var bookButton = new JRadioButton("Book");
        var audioButton = new JRadioButton("Audio");
        var videoButton = new JRadioButton("Video");

        var buttons = new ButtonGroup();
        buttons.add(allButton);
        buttons.add(bookButton);
        buttons.add(audioButton);
        buttons.add(videoButton);

        panel.add(allButton);
        panel.add(bookButton);
        panel.add(audioButton);
        panel.add(videoButton);

        allButton.setSelected(true);

        return panel;
    }

    private JPanel createListButtonPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        var allButton = new JToggleButton("List All");
        var overdueButton = new JToggleButton("List Overdue");
        var upcomingButton = new JToggleButton("List Upcoming");
        var sameTypeButton = new JToggleButton("List Same Type");

        var buttonGroup = new ButtonGroup();
        buttonGroup.add(allButton);
        buttonGroup.add(overdueButton);
        buttonGroup.add(upcomingButton);
        buttonGroup.add(sameTypeButton);

        allButton.setSelected(true);

        panel.add(allButton);
        panel.add(overdueButton);
        panel.add(upcomingButton);
        panel.add(sameTypeButton);

        return panel;
    }

    private JPanel createPageStartPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(createListButtonPanel());
        panel.add(createLoanItemTypeButtonPanel());

        return panel;
    }

    private JPanel createPageEndPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        panel.add(new JButton("Add an Loan Item"));

        return panel;
    }

    private JPanel createLoanItemPanel(LoanItem item) {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var type = "Type: " + item.getTypeAsString();
        var name = item.getName();
        var publishedBy = "Published by " + item.getPublisher();
        var loanedTo = "Loaned to " + item.getLoanedTo();
        var due = new StringBuilder("Due on ").append(item.getDue()).append(" (");
        var now = LocalDate.now();
        var dueCompare = item.getDue().compareTo(now);
        if (dueCompare > 0) {
            due.append("due in ")
                    .append(now.until(item.getDue(), ChronoUnit.DAYS))
                    .append(" day(s)");
        } else if (dueCompare < 0) {
            due.append("overdue by ")
                    .append(item.getDue().until(now, ChronoUnit.DAYS))
                    .append(" day(s)");
        } else {
            due.append("due today");
        }
        due.append(")");

        panel.add(new JLabel(type));
        panel.add(new JLabel(name));
        panel.add(new JLabel(publishedBy));
        panel.add(new JLabel(loanedTo));
        panel.add(new JLabel(due.toString()));
        panel.add(new JButton("Remove"));

        return panel;
    }

    private JPanel createListPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        for (var loanItem : controller.iterateLoanItems()) {
            var element = createLoanItemPanel(loanItem);
            element.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            panel.add(element);
        }

        return panel;
    }

    private JScrollPane createContentPanel() {
        var scrollPane = new JScrollPane(createListPanel());
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
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
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
    }

}
