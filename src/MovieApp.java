import javax.swing.SwingUtilities;

/**
 * Launches Phase 4 of the Movie Tracker Data Management System.
 * The application starts the GUI front-end and connects it to the SQLite database
 * layer so movie records can be displayed, added, updated, removed, and summarized.
 */
public class MovieApp {

    /**
     * Creates a MovieApp object.
     * This constructor is included so Javadoc can document the public application class.
     */
    public MovieApp() {
    }

    /**
     * Starts the Movie Tracker GUI on the Swing event dispatch thread.
     *
     * @param args command-line arguments that are not used by this application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MovieDatabase database = new MovieDatabase();
                MovieTrackerGUI gui = new MovieTrackerGUI(database);
                gui.showWindow();
            }
        });
    }
}
