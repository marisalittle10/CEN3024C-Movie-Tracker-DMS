import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;

/**
 * Creates the Phase 4 graphical user interface for the Movie Tracker DMS.
 * The GUI lets a user select a SQLite database path, connect to that database,
 * and perform display, add, update, remove, validation, and calculation actions.
 */
public class MovieTrackerGUI {
    private MovieDatabase database;
    private JFrame frame;
    private JTextField databasePathField;
    private JTextField idField;
    private JTextField titleField;
    private JTextField genreField;
    private JTextField yearField;
    private JTextField runtimeField;
    private JTextField ratingField;
    private JCheckBox watchedCheckBox;
    private JTextArea outputArea;
    private JLabel messageLabel;

    private final Color backgroundColor = new Color(12, 12, 15);
    private final Color panelColor = new Color(24, 24, 31);
    private final Color royalBlue = new Color(65, 105, 225);
    private final Color textColor = new Color(245, 245, 245);
    private final Color errorColor = new Color(255, 110, 110);
    private final Color successColor = new Color(120, 255, 160);

    /**
     * Creates a GUI connected to the supplied database access object.
     *
     * @param database the MovieDatabase object used for database operations
     */
    public MovieTrackerGUI(MovieDatabase database) {
        this.database = database;
    }

    /**
     * Builds and displays the main GUI window.
     *
     * @return true after the window is created and made visible
     */
    public boolean showWindow() {
        frame = new JFrame("Movie Tracker DMS - Phase 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(buildScreen());
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return true;
    }

    /**
     * Creates the main screen layout for the GUI.
     *
     * @return the complete JPanel containing the header, form, output area, and message area
     */
    public JPanel buildScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(1000, 620));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        mainPanel.add(buildHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(buildCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(buildMessagePanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * method: buildHeaderPanel
     * parameters: none
     * return: JPanel
     * purpose: creates the title area and database connection controls for the GUI.
     */
    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(8, 8));
        headerPanel.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Movie Tracker DMS", SwingConstants.CENTER);
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel databasePanel = new JPanel(new BorderLayout(8, 8));
        databasePanel.setPreferredSize(new Dimension(950, 58));
        databasePanel.setBackground(panelColor);
        databasePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(royalBlue, 2),
                new EmptyBorder(8, 8, 8, 8)
        ));

        databasePathField = createTextField();

        JButton browseButton = createButton("Browse");
        JButton connectButton = createButton("Connect");

        JPanel databaseButtonPanel = new JPanel(new GridBagLayout());
        databaseButtonPanel.setBackground(panelColor);
        addButtonToPanel(databaseButtonPanel, browseButton, 0, 0);
        addButtonToPanel(databaseButtonPanel, connectButton, 1, 0);

        browseButton.addActionListener(event -> handleBrowseDatabase());
        connectButton.addActionListener(event -> handleConnectDatabase());

        databasePanel.add(createLabel("Database path:"), BorderLayout.WEST);
        databasePanel.add(databasePathField, BorderLayout.CENTER);
        databasePanel.add(databaseButtonPanel, BorderLayout.EAST);

        headerPanel.add(databasePanel, BorderLayout.SOUTH);
        return headerPanel;
    }

    /**
     * method: buildCenterPanel
     * parameters: none
     * return: JPanel
     * purpose: creates the center section containing the movie form and movie output area.
     */
    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(buildFormPanel(), BorderLayout.WEST);
        centerPanel.add(buildOutputPanel(), BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * method: buildFormPanel
     * parameters: none
     * return: JPanel
     * purpose: creates the input form and action buttons used to manage database movie records.
     */
    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(300, 455));
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(royalBlue, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        idField = createTextField();
        titleField = createTextField();
        genreField = createTextField();
        yearField = createTextField();
        runtimeField = createTextField();
        ratingField = createTextField();

        watchedCheckBox = new JCheckBox("Watched");
        watchedCheckBox.setBackground(panelColor);
        watchedCheckBox.setForeground(textColor);
        watchedCheckBox.setFocusPainted(false);

        int row = 0;
        addFormRow(formPanel, row++, "Movie ID:", idField);
        addFormRow(formPanel, row++, "Title:", titleField);
        addFormRow(formPanel, row++, "Genre:", genreField);
        addFormRow(formPanel, row++, "Year:", yearField);
        addFormRow(formPanel, row++, "Runtime:", runtimeField);
        addFormRow(formPanel, row++, "Rating:", ratingField);
        addFormRow(formPanel, row++, "Watched:", watchedCheckBox);
        addButtonRows(formPanel, row);

        return formPanel;
    }

    /**
     * method: buildOutputPanel
     * parameters: none
     * return: JPanel
     * purpose: creates the text area where movie records and calculation results are displayed.
     */
    private JPanel buildOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout(8, 8));
        outputPanel.setPreferredSize(new Dimension(670, 455));
        outputPanel.setBackground(panelColor);
        outputPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(royalBlue, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel outputLabel = createLabel("Movie Output");

        outputArea = new JTextArea();
        outputArea.setRows(20);
        outputArea.setColumns(60);
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(textColor);
        outputArea.setCaretColor(textColor);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setText("Connect to a SQLite database to display, add, update, remove, or calculate movie records.");

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new LineBorder(royalBlue, 1));

        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        return outputPanel;
    }

    /**
     * method: buildMessagePanel
     * parameters: none
     * return: JPanel
     * purpose: creates the bottom message area used for success and error messages.
     */
    private JPanel buildMessagePanel() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setPreferredSize(new Dimension(950, 48));
        messagePanel.setBackground(backgroundColor);

        messageLabel = new JLabel("Ready.");
        messageLabel.setOpaque(true);
        messageLabel.setBackground(panelColor);
        messageLabel.setForeground(textColor);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 13));
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(royalBlue, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));

        messagePanel.add(messageLabel, BorderLayout.CENTER);
        return messagePanel;
    }

    /**
     * method: addFormRow
     * parameters: JPanel formPanel, int row, String labelText, java.awt.Component inputComponent
     * return: boolean
     * purpose: places one label and one input component into the form panel.
     */
    private boolean addFormRow(JPanel formPanel, int row, String labelText, java.awt.Component inputComponent) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(4, 4, 4, 8);
        formPanel.add(createLabel(labelText), labelConstraints);

        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.gridx = 1;
        inputConstraints.gridy = row;
        inputConstraints.weightx = 1.0;
        inputConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputConstraints.insets = new Insets(4, 4, 4, 4);
        formPanel.add(inputComponent, inputConstraints);

        return true;
    }

    /**
     * method: addButtonRows
     * parameters: JPanel formPanel, int startRow
     * return: boolean
     * purpose: adds the database CRUD, calculation, display, clear, and exit buttons to the GUI form.
     */
    private boolean addButtonRows(JPanel formPanel, int startRow) {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(panelColor);

        JButton addButton = createButton("Add Movie");
        JButton updateButton = createButton("Update");
        JButton removeButton = createButton("Remove");
        JButton displayButton = createButton("Display All");
        JButton calculateButton = createButton("Calculate");
        JButton clearButton = createButton("Clear");
        JButton exitButton = createButton("Exit");

        addButton.addActionListener(event -> handleAddMovie());
        updateButton.addActionListener(event -> handleUpdateMovie());
        removeButton.addActionListener(event -> handleRemoveMovie());
        displayButton.addActionListener(event -> refreshDisplay());
        calculateButton.addActionListener(event -> handleCalculation());
        clearButton.addActionListener(event -> clearFields());
        exitButton.addActionListener(event -> handleExit());

        addButtonToPanel(buttonPanel, addButton, 0, 0);
        addButtonToPanel(buttonPanel, updateButton, 1, 0);
        addButtonToPanel(buttonPanel, removeButton, 0, 1);
        addButtonToPanel(buttonPanel, displayButton, 1, 1);
        addButtonToPanel(buttonPanel, calculateButton, 0, 2);
        addButtonToPanel(buttonPanel, clearButton, 1, 2);
        addButtonToPanel(buttonPanel, exitButton, 0, 3);

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = startRow;
        buttonConstraints.gridwidth = 2;
        buttonConstraints.weightx = 1.0;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.insets = new Insets(12, 4, 4, 4);
        formPanel.add(buttonPanel, buttonConstraints);

        return true;
    }

    /**
     * method: addButtonToPanel
     * parameters: JPanel panel, JButton button, int column, int row
     * return: boolean
     * purpose: places one button into the button panel at the requested grid location.
     */
    private boolean addButtonToPanel(JPanel panel, JButton button, int column, int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 4, 4, 4);
        panel.add(button, constraints);
        return true;
    }

    /**
     * method: createLabel
     * parameters: String text
     * return: JLabel
     * purpose: creates a styled label for the GUI's dark theme.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    /**
     * method: createTextField
     * parameters: none
     * return: JTextField
     * purpose: creates a styled text field for the GUI's dark theme.
     */
    private JTextField createTextField() {
        JTextField textField = new JTextField(13);
        textField.setPreferredSize(new Dimension(140, 30));
        textField.setBackground(Color.BLACK);
        textField.setForeground(textColor);
        textField.setCaretColor(textColor);
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(royalBlue, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        return textField;
    }

    /**
     * method: createButton
     * parameters: String text
     * return: JButton
     * purpose: creates a rounded royal blue button for the GUI.
     */
    private JButton createButton(String text) {
        JButton button = new RoundedButton(text);
        button.setBackground(royalBlue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 32));
        return button;
    }

    /**
     * method: handleBrowseDatabase
     * parameters: none
     * return: boolean
     * purpose: lets the user choose a SQLite database file and places its path into the database path field.
     */
    private boolean handleBrowseDatabase() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(frame);

        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File selectedFile = chooser.getSelectedFile();
        databasePathField.setText(selectedFile.getAbsolutePath());
        showMessage("Selected database: " + selectedFile.getName(), true);
        return true;
    }

    /**
     * method: handleConnectDatabase
     * parameters: none
     * return: boolean
     * purpose: connects the GUI to the SQLite database path supplied by the user.
     */
    private boolean handleConnectDatabase() {
        boolean connected = database.connect(databasePathField.getText());

        if (!connected) {
            showMessage("Unable to connect to database.", false);
            return false;
        }

        refreshDisplay();
        showMessage("Database connected successfully.", true);
        return true;
    }

    /**
     * method: handleAddMovie
     * parameters: none
     * return: boolean
     * purpose: creates a movie from the form fields and stores it in the database when the data is valid.
     */
    private boolean handleAddMovie() {
        if (!checkDatabaseConnection()) {
            return false;
        }

        Integer movieId = readRequiredInt(idField, "Movie ID");

        if (movieId == null) {
            return false;
        }

        Movie movie = buildMovieFromFields(movieId);

        if (movie == null) {
            return false;
        }

        boolean added = database.addMovie(movie);

        if (!added) {
            showMessage("Movie could not be added. Check for duplicate ID or invalid data.", false);
            return false;
        }

        refreshDisplay();
        showMessage("Movie added successfully.", true);
        return true;
    }

    /**
     * method: handleUpdateMovie
     * parameters: none
     * return: boolean
     * purpose: updates an existing database movie record using the ID and replacement field values entered by the user.
     */
    private boolean handleUpdateMovie() {
        if (!checkDatabaseConnection()) {
            return false;
        }

        Integer movieId = readRequiredInt(idField, "Movie ID");

        if (movieId == null) {
            return false;
        }

        if (database.findMovieById(movieId) == null) {
            showMessage("No movie exists with ID " + movieId + ".", false);
            return false;
        }

        Movie updatedMovie = buildMovieFromFields(movieId);

        if (updatedMovie == null) {
            return false;
        }

        boolean updated = database.updateMovie(movieId, updatedMovie);

        if (!updated) {
            showMessage("Movie could not be updated. Check the replacement values.", false);
            return false;
        }

        refreshDisplay();
        showMessage("Movie updated successfully.", true);
        return true;
    }

    /**
     * method: handleRemoveMovie
     * parameters: none
     * return: boolean
     * purpose: removes a movie from the database using the ID entered by the user.
     */
    private boolean handleRemoveMovie() {
        if (!checkDatabaseConnection()) {
            return false;
        }

        Integer movieId = readRequiredInt(idField, "Movie ID");

        if (movieId == null) {
            return false;
        }

        boolean removed = database.deleteMovie(movieId);

        if (!removed) {
            showMessage("No movie exists with ID " + movieId + ". Nothing was removed.", false);
            return false;
        }

        refreshDisplay();
        showMessage("Movie removed successfully.", true);
        return true;
    }

    /**
     * method: handleCalculation
     * parameters: none
     * return: boolean
     * purpose: displays the custom average rating and total runtime calculation from the database.
     */
    private boolean handleCalculation() {
        if (!checkDatabaseConnection()) {
            return false;
        }

        outputArea.setText(database.getCalculationSummary());
        showMessage("Custom calculation completed.", true);
        return true;
    }

    /**
     * method: refreshDisplay
     * parameters: none
     * return: boolean
     * purpose: updates the output area with all movie records currently stored in the database.
     */
    private boolean refreshDisplay() {
        if (!checkDatabaseConnection()) {
            return false;
        }

        outputArea.setText(database.formatMovieTable());
        showMessage("Displayed " + database.getMovieCount() + " movie records.", true);
        return true;
    }

    /**
     * method: clearFields
     * parameters: none
     * return: boolean
     * purpose: clears the movie input fields so the user can enter a new record.
     */
    private boolean clearFields() {
        idField.setText("");
        titleField.setText("");
        genreField.setText("");
        yearField.setText("");
        runtimeField.setText("");
        ratingField.setText("");
        watchedCheckBox.setSelected(false);
        showMessage("Input fields cleared.", true);
        return true;
    }

    /**
     * method: buildMovieFromFields
     * parameters: int movieId
     * return: Movie
     * purpose: creates a Movie object from the form fields when the user's input is valid.
     */
    private Movie buildMovieFromFields(int movieId) {
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        Integer releaseYear = readRequiredInt(yearField, "Release Year");
        Integer runtime = readRequiredInt(runtimeField, "Runtime");
        Double rating = readRequiredDouble(ratingField, "Rating");

        if (releaseYear == null || runtime == null || rating == null) {
            return null;
        }

        Movie movie = new Movie(movieId, title, genre, releaseYear, runtime, rating, watchedCheckBox.isSelected());

        if (!database.validateMovie(movie)) {
            showMessage("Invalid movie data. Title and genre cannot be blank. Year must be 1888-2100, runtime must be positive, and rating must be 0-10.", false);
            return null;
        }

        return movie;
    }

    /**
     * method: readRequiredInt
     * parameters: JTextField field, String fieldName
     * return: Integer
     * purpose: converts a required text field value into an integer or shows an error message when invalid.
     */
    private Integer readRequiredInt(JTextField field, String fieldName) {
        try {
            if (field.getText().trim().isEmpty()) {
                showMessage(fieldName + " cannot be blank.", false);
                return null;
            }

            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException exception) {
            showMessage(fieldName + " must be a whole number.", false);
            return null;
        }
    }

    /**
     * method: readRequiredDouble
     * parameters: JTextField field, String fieldName
     * return: Double
     * purpose: converts a required text field value into a decimal number or shows an error message when invalid.
     */
    private Double readRequiredDouble(JTextField field, String fieldName) {
        try {
            if (field.getText().trim().isEmpty()) {
                showMessage(fieldName + " cannot be blank.", false);
                return null;
            }

            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException exception) {
            showMessage(fieldName + " must be a number.", false);
            return null;
        }
    }

    /**
     * method: checkDatabaseConnection
     * parameters: none
     * return: boolean
     * purpose: checks whether a database is connected before a database action is attempted.
     */
    private boolean checkDatabaseConnection() {
        if (!database.isConnected()) {
            showMessage("Unable to connect to database.", false);
            return false;
        }

        return true;
    }

    /**
     * method: showMessage
     * parameters: String message, boolean success
     * return: boolean
     * purpose: displays a success or error message at the bottom of the GUI.
     */
    private boolean showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? successColor : errorColor);
        return true;
    }

    /**
     * method: handleExit
     * parameters: none
     * return: boolean
     * purpose: closes the database connection and exits the GUI.
     */
    private boolean handleExit() {
        database.close();
        frame.dispose();
        return true;
    }

    /**
     * Marisa Littleton
     * CEN 3024C - Software Development 1
     * July 2026
     * RoundedButton.java
     * This inner class creates rounded royal blue buttons for the Movie Tracker GUI.
     */
    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        /**
         * method: paintComponent
         * parameters: Graphics graphics
         * return: void
         * purpose: draws the rounded button background and text.
         */
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color buttonColor = getBackground();

            if (getModel().isPressed()) {
                buttonColor = buttonColor.darker();
            } else if (getModel().isRollover()) {
                buttonColor = buttonColor.brighter();
            }

            graphics2D.setColor(buttonColor);
            graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            graphics2D.setColor(new Color(100, 140, 255));
            graphics2D.setStroke(new BasicStroke(1.5f));
            graphics2D.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);

            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }
}
