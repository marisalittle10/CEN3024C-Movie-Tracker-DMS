import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Marisa Littleton
 * CEN 3024C - Software Development 1
 * July 5, 2026
 * MovieManagerTest.java
 * This class contains JUnit unit tests for Phase 2 of the Movie Tracker DMS.
 * The tests verify the main program logic from Phase 1, including file loading,
 * CRUD operations, validation, and the custom average rating and total runtime
 * calculations.
 */
public class MovieManagerTest {

    /**
     * method: createSampleMovie
     * parameters: int movieId
     * return: Movie
     * purpose: creates a valid Movie object for unit tests that need sample data.
     */
    private Movie createSampleMovie(int movieId) {
        return new Movie(movieId, "Inception", "Sci-Fi", 2010, 148, 9.2, true);
    }

    /**
     * method: fileCanBeOpenedAndLoaded
     * parameters: none
     * return: void
     * purpose: verifies that the file reader can open a valid text file and load movie records.
     */
    @Test
    public void fileCanBeOpenedAndLoaded() throws IOException {
        Path filePath = Files.createTempFile("movie-test", ".txt");
        Files.writeString(filePath,
                "101|Inception|Sci-Fi|2010|148|9.2|true\n"
                        + "102|Coraline|Fantasy|2009|100|8.4|yes\n"
                        + "103|Top Gun|Action|1986|110|7.8|false\n");

        MovieFileReader reader = new MovieFileReader();
        FileLoadResult result = reader.loadMoviesFromFile(filePath.toString());

        assertTrue(result.isSuccessful());
        assertEquals(3, result.getMovies().size());
        assertEquals(0, result.getSkippedLines());
        assertEquals("Inception", result.getMovies().get(0).getTitle());
    }

    /**
     * method: blankFilePathIsHandledSafely
     * parameters: none
     * return: void
     * purpose: verifies that a blank file path is rejected without crashing the program.
     */
    @Test
    public void blankFilePathIsHandledSafely() {
        MovieFileReader reader = new MovieFileReader();
        FileLoadResult result = reader.loadMoviesFromFile("   ");

        assertFalse(result.isSuccessful());
        assertEquals(0, result.getMovies().size());
        assertTrue(result.getMessage().contains("cannot be blank"));
    }

    /**
     * method: missingFileIsHandledSafely
     * parameters: none
     * return: void
     * purpose: verifies that a missing file path returns a failed result instead of crashing.
     */
    @Test
    public void missingFileIsHandledSafely() {
        MovieFileReader reader = new MovieFileReader();
        FileLoadResult result = reader.loadMoviesFromFile("missing-file-that-does-not-exist.txt");

        assertFalse(result.isSuccessful());
        assertEquals(0, result.getMovies().size());
        assertTrue(result.getMessage().contains("could not be found or opened"));
    }

    /**
     * method: invalidFileLinesAreSkipped
     * parameters: none
     * return: void
     * purpose: verifies that invalid text file records are skipped while valid records are loaded.
     */
    @Test
    public void invalidFileLinesAreSkipped() throws IOException {
        Path filePath = Files.createTempFile("movie-invalid-lines", ".txt");
        Files.writeString(filePath,
                "101|Inception|Sci-Fi|2010|148|9.2|true\n"
                        + "102||Fantasy|2009|100|8.4|true\n"
                        + "103|Bad Rating|Action|1986|110|11.8|false\n"
                        + "104|Bad Runtime|Action|2001|-4|7.0|false\n");

        MovieFileReader reader = new MovieFileReader();
        FileLoadResult result = reader.loadMoviesFromFile(filePath.toString());

        assertTrue(result.isSuccessful());
        assertEquals(1, result.getMovies().size());
        assertEquals(3, result.getSkippedLines());
    }

    /**
     * method: addMovieStoresValidMovie
     * parameters: none
     * return: void
     * purpose: verifies that a valid movie can be added to the MovieManager collection.
     */
    @Test
    public void addMovieStoresValidMovie() {
        MovieManager manager = new MovieManager();
        Movie movie = createSampleMovie(101);

        boolean added = manager.addMovie(movie);

        assertTrue(added);
        assertEquals(1, manager.getMovieCount());
        assertNotNull(manager.findMovieById(101));
        assertEquals("Inception", manager.findMovieById(101).getTitle());
    }

    /**
     * method: duplicateMovieIdIsRejected
     * parameters: none
     * return: void
     * purpose: verifies that the program does not allow two movie records with the same ID.
     */
    @Test
    public void duplicateMovieIdIsRejected() {
        MovieManager manager = new MovieManager();
        Movie firstMovie = createSampleMovie(101);
        Movie duplicateMovie = new Movie(101, "Coraline", "Fantasy", 2009, 100, 8.4, true);

        assertTrue(manager.addMovie(firstMovie));
        assertFalse(manager.addMovie(duplicateMovie));
        assertEquals(1, manager.getMovieCount());
    }

    /**
     * method: invalidMovieIsRejected
     * parameters: none
     * return: void
     * purpose: verifies that bad movie data is rejected before it can be stored.
     */
    @Test
    public void invalidMovieIsRejected() {
        MovieManager manager = new MovieManager();
        Movie blankTitle = new Movie(104, "", "Action", 2012, 142, 8.5, true);
        Movie badRating = new Movie(105, "Invalid Rating", "Action", 2014, 123, 11.5, true);
        Movie badRuntime = new Movie(106, "Invalid Runtime", "Action", 2015, -90, 7.2, false);

        assertFalse(manager.addMovie(blankTitle));
        assertFalse(manager.addMovie(badRating));
        assertFalse(manager.addMovie(badRuntime));
        assertEquals(0, manager.getMovieCount());
    }

    /**
     * method: displayMoviesReturnsStoredMovies
     * parameters: none
     * return: void
     * purpose: verifies that the display method returns the movie records stored in the collection.
     */
    @Test
    public void displayMoviesReturnsStoredMovies() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));
        manager.addMovie(new Movie(102, "Coraline", "Fantasy", 2009, 100, 8.4, true));

        ArrayList<Movie> movies = manager.displayMovies();

        assertEquals(2, movies.size());
        assertEquals("Inception", movies.get(0).getTitle());
        assertEquals("Coraline", movies.get(1).getTitle());
    }

    /**
     * method: displayMoviesReturnsCopies
     * parameters: none
     * return: void
     * purpose: verifies that displayed movies are copies so outside code cannot directly change stored data.
     */
    @Test
    public void displayMoviesReturnsCopies() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        ArrayList<Movie> displayedMovies = manager.displayMovies();
        displayedMovies.get(0).setTitle("Changed Outside Manager");

        assertEquals("Inception", manager.findMovieById(101).getTitle());
    }

    /**
     * method: formatMovieTableHandlesEmptyCollection
     * parameters: none
     * return: void
     * purpose: verifies that displaying an empty collection returns a clear message.
     */
    @Test
    public void formatMovieTableHandlesEmptyCollection() {
        MovieManager manager = new MovieManager();

        String output = manager.formatMovieTable();

        assertEquals("No movie records found.", output);
    }

    /**
     * method: removeMovieByIdDeletesExistingMovie
     * parameters: none
     * return: void
     * purpose: verifies that an existing movie can be removed by its ID.
     */
    @Test
    public void removeMovieByIdDeletesExistingMovie() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        boolean removed = manager.removeMovieById(101);

        assertTrue(removed);
        assertEquals(0, manager.getMovieCount());
        assertNull(manager.findMovieById(101));
    }

    /**
     * method: removeMissingMovieByIdFailsSafely
     * parameters: none
     * return: void
     * purpose: verifies that removing a missing ID fails safely without changing the collection.
     */
    @Test
    public void removeMissingMovieByIdFailsSafely() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        boolean removed = manager.removeMovieById(999);

        assertFalse(removed);
        assertEquals(1, manager.getMovieCount());
        assertNotNull(manager.findMovieById(101));
    }

    /**
     * method: removeMovieByTitleDeletesExistingMovie
     * parameters: none
     * return: void
     * purpose: verifies that an existing movie can be removed by title.
     */
    @Test
    public void removeMovieByTitleDeletesExistingMovie() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        boolean removed = manager.removeMovieByTitle("inception");

        assertTrue(removed);
        assertEquals(0, manager.getMovieCount());
    }

    /**
     * method: updateMovieFieldsChangeStoredData
     * parameters: none
     * return: void
     * purpose: verifies that each editable movie field can be updated successfully.
     */
    @Test
    public void updateMovieFieldsChangeStoredData() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        assertTrue(manager.updateTitle(101, "The Hunger Games"));
        assertTrue(manager.updateGenre(101, "Action"));
        assertTrue(manager.updateReleaseYear(101, 2012));
        assertTrue(manager.updateRuntimeMinutes(101, 142));
        assertTrue(manager.updateRating(101, 8.5));
        assertTrue(manager.updateWatched(101, false));

        Movie updatedMovie = manager.findMovieById(101);
        assertEquals("The Hunger Games", updatedMovie.getTitle());
        assertEquals("Action", updatedMovie.getGenre());
        assertEquals(2012, updatedMovie.getReleaseYear());
        assertEquals(142, updatedMovie.getRuntimeMinutes());
        assertEquals(8.5, updatedMovie.getRating(), 0.001);
        assertFalse(updatedMovie.isWatched());
    }

    /**
     * method: updateMovieIdChangesStoredId
     * parameters: none
     * return: void
     * purpose: verifies that a movie ID can be updated when the new ID is valid and unused.
     */
    @Test
    public void updateMovieIdChangesStoredId() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        boolean updated = manager.updateMovieId(101, 201);

        assertTrue(updated);
        assertNull(manager.findMovieById(101));
        assertNotNull(manager.findMovieById(201));
    }

    /**
     * method: invalidUpdatesDoNotChangeMovie
     * parameters: none
     * return: void
     * purpose: verifies that invalid update values are rejected and the original record stays unchanged.
     */
    @Test
    public void invalidUpdatesDoNotChangeMovie() {
        MovieManager manager = new MovieManager();
        manager.addMovie(createSampleMovie(101));

        assertFalse(manager.updateTitle(101, ""));
        assertFalse(manager.updateGenre(101, ""));
        assertFalse(manager.updateReleaseYear(101, 1500));
        assertFalse(manager.updateRuntimeMinutes(101, -20));
        assertFalse(manager.updateRating(101, 15.0));
        assertFalse(manager.updateRating(999, 8.0));

        Movie movie = manager.findMovieById(101);
        assertEquals("Inception", movie.getTitle());
        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals(2010, movie.getReleaseYear());
        assertEquals(148, movie.getRuntimeMinutes());
        assertEquals(9.2, movie.getRating(), 0.001);
    }

    /**
     * method: customCalculationsReturnExpectedResults
     * parameters: none
     * return: void
     * purpose: verifies the average rating and total runtime custom calculations.
     */
    @Test
    public void customCalculationsReturnExpectedResults() {
        MovieManager manager = new MovieManager();
        manager.addMovie(new Movie(101, "Inception", "Sci-Fi", 2010, 148, 9.2, true));
        manager.addMovie(new Movie(102, "Coraline", "Fantasy", 2009, 100, 8.4, true));
        manager.addMovie(new Movie(103, "Top Gun", "Action", 1986, 110, 7.8, false));

        assertEquals(8.466, manager.calculateAverageRating(), 0.01);
        assertEquals(358, manager.calculateTotalRuntime());
    }

    /**
     * method: customCalculationHandlesEmptyCollection
     * parameters: none
     * return: void
     * purpose: verifies that the custom calculation does not divide by zero when no movies are stored.
     */
    @Test
    public void customCalculationHandlesEmptyCollection() {
        MovieManager manager = new MovieManager();

        assertEquals(0.0, manager.calculateAverageRating(), 0.001);
        assertEquals(0, manager.calculateTotalRuntime());
        assertTrue(manager.getCalculationSummary().contains("No movies are stored yet"));
    }
}
