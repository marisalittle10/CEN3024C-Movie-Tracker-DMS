import java.util.ArrayList;

/**
 * Stores the outcome of loading movie data from an external text source.
 * This result object allows the Movie Tracker DMS to return the valid movies,
 * skipped line count, success status, and user-facing message from one file operation.
 */
public class FileLoadResult {
    private ArrayList<Movie> movies;
    private int skippedLines;
    private boolean successful;
    private String message;

    /**
     * Creates a file-load result with the loaded movies and status details.
     *
     * @param movies the list of valid movies read from the source
     * @param skippedLines the number of invalid lines skipped while reading
     * @param successful true if the file operation completed successfully, false otherwise
     * @param message the message describing the file-load result
     */
    public FileLoadResult(ArrayList<Movie> movies, int skippedLines, boolean successful, String message) {
        this.movies = movies;
        this.skippedLines = skippedLines;
        this.successful = successful;
        this.message = message;
    }

    /**
     * Returns a copy of the movies loaded from the source.
     *
     * @return a new list containing the loaded movie records
     */
    public ArrayList<Movie> getMovies() {
        return new ArrayList<Movie>(movies);
    }

    /**
     * Returns the number of invalid lines skipped during file loading.
     *
     * @return the skipped line count
     */
    public int getSkippedLines() {
        return skippedLines;
    }

    /**
     * Returns whether the file operation completed successfully.
     *
     * @return true if the operation was successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Returns the result message for the file operation.
     *
     * @return a message describing the file-load result
     */
    public String getMessage() {
        return message;
    }
}
