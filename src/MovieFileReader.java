import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads movie records from a pipe-delimited text file.
 * This class supports earlier phases of the Movie Tracker DMS by converting file
 * data into Movie objects and reporting invalid lines through FileLoadResult.
 */
public class MovieFileReader {

    /**
     * Creates a file reader for loading movie records from text files.
     */
    public MovieFileReader() {
    }

    /**
     * Loads movie records from the supplied text file path.
     *
     * @param filePath the path to the text file containing movie records
     * @return a FileLoadResult containing valid movies, skipped lines, and a status message
     */
    public FileLoadResult loadMoviesFromFile(String filePath) {
        ArrayList<Movie> loadedMovies = new ArrayList<Movie>();
        int skippedLines = 0;

        if (filePath == null || filePath.trim().isEmpty()) {
            return new FileLoadResult(loadedMovies, 0, false, "File path cannot be blank.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.trim()))) {
            String line = reader.readLine();

            while (line != null) {
                Movie movie = parseMovieLine(line);
                if (movie == null) {
                    skippedLines++;
                } else {
                    loadedMovies.add(movie);
                }
                line = reader.readLine();
            }
        } catch (IOException exception) {
            return new FileLoadResult(loadedMovies, skippedLines, false,
                    "The file could not be found or opened. Please check the path and try again.");
        }

        return new FileLoadResult(loadedMovies, skippedLines, true,
                loadedMovies.size() + " valid movie records were read from the file.");
    }

    /**
     * method: parseMovieLine
     * parameters: String line
     * return: Movie
     * purpose: converts one text file line into a Movie object when the line has
     * the correct number of fields and valid data types.
     */
    private Movie parseMovieLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|");
        if (parts.length != 7) {
            return null;
        }

        try {
            int movieId = Integer.parseInt(parts[0].trim());
            String title = parts[1].trim();
            String genre = parts[2].trim();
            int releaseYear = Integer.parseInt(parts[3].trim());
            int runtimeMinutes = Integer.parseInt(parts[4].trim());
            double rating = Double.parseDouble(parts[5].trim());
            Boolean watched = parseWatched(parts[6].trim());
            if (watched == null) {
                return null;
            }

            Movie movie = new Movie(movieId, title, genre, releaseYear, runtimeMinutes, rating, watched);
            if (movieId <= 0 || title.isEmpty() || genre.isEmpty() || releaseYear < 1888
                    || releaseYear > 2100 || runtimeMinutes <= 0 || rating < 0.0 || rating > 10.0) {
                return null;
            }
            return movie;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * method: parseWatched
     * parameters: String value
     * return: Boolean
     * purpose: converts text from the file into a true or false watched value, or null when the value is invalid.
     */
    private Boolean parseWatched(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y")) {
            return true;
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("n")) {
            return false;
        }
        return null;
    }
}
