package ca.richasf.view.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

public class LoanItemsTrackerGui {

    public LoanItemsTrackerGui() {
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

    private JPanel createMainPanel() {
        var mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createPageStartPanel(), BorderLayout.PAGE_START);

        return mainPanel;
    }

    public void start() {
        var frame = new JFrame();
        frame.setTitle("Loan Items Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 300));

        frame.addWindowListener(new MainFrameWindowListener());

        var mainPanel = createMainPanel();

        frame.add(mainPanel);

        frame.pack();
        frame.setMinimumSize(frame.getSize());

        frame.setVisible(true);
    }

}
