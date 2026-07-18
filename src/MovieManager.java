import java.util.ArrayList;

/**
 * Manages an in-memory collection of Movie objects for the Movie Tracker DMS.
 * This class contains the original CRUD logic, validation rules, display formatting,
 * and custom calculations used before the database layer was added.
 */
public class MovieManager {
    private ArrayList<Movie> movies;
    private int minimumReleaseYear;
    private int maximumReleaseYear;

    /**
     * Creates an empty movie manager with the accepted release-year range.
     */
    public MovieManager() {
        movies = new ArrayList<Movie>();
        minimumReleaseYear = 1888;
        maximumReleaseYear = 2100;
    }

    /**
     * Adds one valid movie to the collection when its ID is not already used.
     *
     * @param movie the movie record to add
     * @return true if the movie was added, false if validation failed or the ID already exists
     */
    public boolean addMovie(Movie movie) {
        if (!validateMovie(movie) || findMovieById(movie.getMovieId()) != null) {
            return false;
        }
        movies.add(movie);
        return true;
    }

    /**
     * Adds multiple valid movies to the collection.
     *
     * @param movieList the movies to add
     * @return the number of movies successfully added
     */
    public int addMovies(ArrayList<Movie> movieList) {
        int addedCount = 0;
        if (movieList == null) {
            return addedCount;
        }

        for (Movie movie : movieList) {
            if (addMovie(movie)) {
                addedCount++;
            }
        }
        return addedCount;
    }

    /**
     * Removes a movie from the collection using its ID.
     *
     * @param movieId the ID of the movie to remove
     * @return true if a movie was removed, false if no matching ID exists
     */
    public boolean removeMovieById(int movieId) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        movies.remove(movie);
        return true;
    }

    /**
     * Removes the first movie with a matching title.
     *
     * @param title the title of the movie to remove
     * @return true if a movie was removed, false if no matching title exists
     */
    public boolean removeMovieByTitle(String title) {
        Movie movie = findMovieByTitle(title);
        if (movie == null) {
            return false;
        }
        movies.remove(movie);
        return true;
    }

    /**
     * Changes a movie's ID when the current ID exists and the new ID is valid and unused.
     *
     * @param currentId the movie's current ID
     * @param newId the replacement ID
     * @return true if the ID was updated, false otherwise
     */
    public boolean updateMovieId(int currentId, int newId) {
        Movie movie = findMovieById(currentId);
        if (movie == null || newId <= 0 || findMovieById(newId) != null) {
            return false;
        }
        return movie.setMovieId(newId);
    }

    /**
     * Updates the title of an existing movie.
     *
     * @param movieId the ID of the movie to update
     * @param title the replacement title
     * @return true if the title was updated, false otherwise
     */
    public boolean updateTitle(int movieId, String title) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        return movie.setTitle(title);
    }

    /**
     * Updates the genre of an existing movie.
     *
     * @param movieId the ID of the movie to update
     * @param genre the replacement genre
     * @return true if the genre was updated, false otherwise
     */
    public boolean updateGenre(int movieId, String genre) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        return movie.setGenre(genre);
    }

    /**
     * Updates the release year of an existing movie.
     *
     * @param movieId the ID of the movie to update
     * @param releaseYear the replacement release year
     * @return true if the year was updated, false otherwise
     */
    public boolean updateReleaseYear(int movieId, int releaseYear) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        return movie.setReleaseYear(releaseYear);
    }

    /**
     * Updates the runtime of an existing movie.
     *
     * @param movieId the ID of the movie to update
     * @param runtimeMinutes the replacement runtime in minutes
     * @return true if the runtime was updated, false otherwise
     */
    public boolean updateRuntimeMinutes(int movieId, int runtimeMinutes) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        return movie.setRuntimeMinutes(runtimeMinutes);
    }

    /**
     * Updates the rating of an existing movie.
     *
     * @param movieId the ID of the movie to update
     * @param rating the replacement rating from 0.0 to 10.0
     * @return true if the rating was updated, false otherwise
     */
    public boolean updateRating(int movieId, double rating) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        return movie.setRating(rating);
    }

    /**
     * Updates the watched status of an existing movie.
     *
     * @param movieId the ID of the movie to update
     * @param watched the replacement watched status
     * @return true if the watched status was updated, false otherwise
     */
    public boolean updateWatched(int movieId, boolean watched) {
        Movie movie = findMovieById(movieId);
        if (movie == null) {
            return false;
        }
        return movie.setWatched(watched);
    }

    /**
     * Finds a movie by its ID.
     *
     * @param movieId the ID to search for
     * @return the matching movie, or null if no match exists
     */
    public Movie findMovieById(int movieId) {
        for (Movie movie : movies) {
            if (movie.getMovieId() == movieId) {
                return movie;
            }
        }
        return null;
    }

    /**
     * Finds the first movie with a matching title.
     *
     * @param title the title to search for
     * @return the matching movie, or null if no match exists
     */
    public Movie findMovieByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(title.trim())) {
                return movie;
            }
        }
        return null;
    }

    /**
     * Returns copies of all stored movies for safe display.
     *
     * @return a list of copied movie records
     */
    public ArrayList<Movie> displayMovies() {
        ArrayList<Movie> copies = new ArrayList<Movie>();
        for (Movie movie : movies) {
            copies.add(movie.copy());
        }
        return copies;
    }

    /**
     * Builds a formatted table of all stored movies.
     *
     * @return a formatted movie table, or a message if no records exist
     */
    public String formatMovieTable() {
        if (movies.isEmpty()) {
            return "No movie records found.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-6s %-28s %-14s %-6s %-8s %-7s %-8s%n",
                "ID", "Title", "Genre", "Year", "Runtime", "Rating", "Watched"));
        builder.append("--------------------------------------------------------------------------------\n");
        for (Movie movie : movies) {
            builder.append(movie.toTableRow()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Calculates the average rating of all stored movies.
     *
     * @return the average rating, or 0.0 when no movies are stored
     */
    public double calculateAverageRating() {
        if (movies.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0.0;
        for (Movie movie : movies) {
            totalRating += movie.getRating();
        }
        return totalRating / movies.size();
    }

    /**
     * Calculates the combined runtime of all stored movies.
     *
     * @return the total runtime in minutes
     */
    public int calculateTotalRuntime() {
        int totalRuntime = 0;
        for (Movie movie : movies) {
            totalRuntime += movie.getRuntimeMinutes();
        }
        return totalRuntime;
    }

    /**
     * Builds the custom calculation summary shown to the user.
     *
     * @return the average rating and total runtime summary, or a message if no movies exist
     */
    public String getCalculationSummary() {
        if (movies.isEmpty()) {
            return "No movies are stored yet, so average rating and total runtime cannot be calculated.";
        }

        return String.format("Average rating: %.2f%nTotal runtime: %d minutes", calculateAverageRating(), calculateTotalRuntime());
    }

    /**
     * Checks whether a movie record contains valid values.
     *
     * @param movie the movie to validate
     * @return true if the movie is valid, false otherwise
     */
    public boolean validateMovie(Movie movie) {
        if (movie == null) {
            return false;
        }

        return movie.getMovieId() > 0
                && movie.getTitle() != null
                && !movie.getTitle().trim().isEmpty()
                && movie.getGenre() != null
                && !movie.getGenre().trim().isEmpty()
                && movie.getReleaseYear() >= minimumReleaseYear
                && movie.getReleaseYear() <= maximumReleaseYear
                && movie.getRuntimeMinutes() > 0
                && movie.getRating() >= 0.0
                && movie.getRating() <= 10.0;
    }

    /**
     * Returns the number of movies stored in the manager.
     *
     * @return the movie count
     */
    public int getMovieCount() {
        return movies.size();
    }
}
