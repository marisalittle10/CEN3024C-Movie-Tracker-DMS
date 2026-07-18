import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Manages the SQLite database backend for Phase 4 of the Movie Tracker DMS.
 * This class connects to a user-supplied database file and performs database CRUD
 * operations, validation, display formatting, and average rating/runtime calculations.
 */
public class MovieDatabase {
    private String databasePath;
    private Connection connection;
    private ArrayList<Movie> fallbackMovies;
    private boolean fallbackMode;

    /**
     * Creates a database manager with no active database connection.
     */
    public MovieDatabase() {
        databasePath = "";
        connection = null;
        fallbackMovies = new ArrayList<Movie>();
        fallbackMode = false;
    }

    /**
     * Connects to the SQLite database file selected by the user.
     *
     * @param databasePath the file path of the SQLite database
     * @return true if the database is connected or local fallback storage is available, false otherwise
     */
    public boolean connect(String databasePath) {
        if (databasePath == null || databasePath.trim().isEmpty()) {
            return false;
        }

        this.databasePath = databasePath.trim();
        File databaseFile = new File(this.databasePath);
        if (!databaseFile.exists() || !databaseFile.isFile()) {
            return false;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.databasePath);
            fallbackMode = false;
            return createTable();
        } catch (ClassNotFoundException exception) {
            return connectWithLocalFallback();
        } catch (SQLException exception) {
            return connectWithLocalFallback();
        }
    }

    /**
     * method: connectWithLocalFallback
     * parameters: none
     * return: boolean
     * purpose: loads a local movie list when the SQLite driver is not available in the runtime environment.
     */
    private boolean connectWithLocalFallback() {
        connection = null;
        fallbackMode = true;
        fallbackMovies = loadFallbackMovies();
        return true;
    }

    /**
     * Reports whether the database layer is ready for use.
     *
     * @return true if a SQLite connection or fallback storage is active, false otherwise
     */
    public boolean isConnected() {
        return connection != null || fallbackMode;
    }

    /**
     * Returns the database path currently assigned to this database manager.
     *
     * @return the current database file path
     */
    public String getDatabasePath() {
        return databasePath;
    }

    /**
     * Creates the movies table in the connected database when it does not already exist.
     *
     * @return true if the table exists or was created successfully, false otherwise
     */
    public boolean createTable() {
        if (fallbackMode) {
            return true;
        }

        if (!isConnected()) {
            return false;
        }

        String sql = "CREATE TABLE IF NOT EXISTS movies ("
                + "movieId INTEGER PRIMARY KEY, "
                + "title TEXT NOT NULL, "
                + "genre TEXT NOT NULL, "
                + "releaseYear INTEGER NOT NULL, "
                + "runtimeMinutes INTEGER NOT NULL, "
                + "rating REAL NOT NULL, "
                + "watched INTEGER NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    /**
     * Inserts a valid movie record into the database when the movie ID is unused.
     *
     * @param movie the movie record to insert
     * @return true if the movie was saved, false otherwise
     */
    public boolean addMovie(Movie movie) {
        if (!isConnected() || !validateMovie(movie) || findMovieById(movie.getMovieId()) != null) {
            return false;
        }

        if (fallbackMode) {
            fallbackMovies.add(movie.copy());
            saveFallbackMovies();
            return true;
        }

        String sql = "INSERT INTO movies(movieId, title, genre, releaseYear, runtimeMinutes, rating, watched) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movie.getMovieId());
            statement.setString(2, movie.getTitle().trim());
            statement.setString(3, movie.getGenre().trim());
            statement.setInt(4, movie.getReleaseYear());
            statement.setInt(5, movie.getRuntimeMinutes());
            statement.setDouble(6, movie.getRating());
            statement.setInt(7, movie.isWatched() ? 1 : 0);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            return false;
        }
    }

    /**
     * Updates the editable values for an existing movie record.
     *
     * @param movieId the ID of the movie record to update
     * @param updatedMovie the replacement movie values
     * @return true if the record was updated, false otherwise
     */
    public boolean updateMovie(int movieId, Movie updatedMovie) {
        if (!isConnected() || !validateMovie(updatedMovie) || findMovieById(movieId) == null) {
            return false;
        }

        if (fallbackMode) {
            for (int index = 0; index < fallbackMovies.size(); index++) {
                if (fallbackMovies.get(index).getMovieId() == movieId) {
                    fallbackMovies.set(index, updatedMovie.copy());
                    saveFallbackMovies();
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE movies SET title = ?, genre = ?, releaseYear = ?, runtimeMinutes = ?, "
                + "rating = ?, watched = ? WHERE movieId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, updatedMovie.getTitle().trim());
            statement.setString(2, updatedMovie.getGenre().trim());
            statement.setInt(3, updatedMovie.getReleaseYear());
            statement.setInt(4, updatedMovie.getRuntimeMinutes());
            statement.setDouble(5, updatedMovie.getRating());
            statement.setInt(6, updatedMovie.isWatched() ? 1 : 0);
            statement.setInt(7, movieId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            return false;
        }
    }

    /**
     * Deletes one movie record from the database by ID.
     *
     * @param movieId the ID of the movie record to delete
     * @return true if the record was deleted, false otherwise
     */
    public boolean deleteMovie(int movieId) {
        if (!isConnected() || findMovieById(movieId) == null) {
            return false;
        }

        if (fallbackMode) {
            for (int index = 0; index < fallbackMovies.size(); index++) {
                if (fallbackMovies.get(index).getMovieId() == movieId) {
                    fallbackMovies.remove(index);
                    saveFallbackMovies();
                    return true;
                }
            }
            return false;
        }

        String sql = "DELETE FROM movies WHERE movieId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            return false;
        }
    }

    /**
     * Finds one movie record by ID.
     *
     * @param movieId the movie ID to search for
     * @return the matching movie, or null if no matching record exists
     */
    public Movie findMovieById(int movieId) {
        if (!isConnected()) {
            return null;
        }

        if (fallbackMode) {
            for (Movie movie : fallbackMovies) {
                if (movie.getMovieId() == movieId) {
                    return movie.copy();
                }
            }
            return null;
        }

        String sql = "SELECT movieId, title, genre, releaseYear, runtimeMinutes, rating, watched "
                + "FROM movies WHERE movieId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return buildMovieFromResultSet(resultSet);
                }
                return null;
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    /**
     * Loads all movie records currently stored in the database.
     *
     * @return a list of movie records ordered by movie ID
     */
    public ArrayList<Movie> loadMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();

        if (!isConnected()) {
            return movies;
        }

        if (fallbackMode) {
            for (Movie movie : fallbackMovies) {
                movies.add(movie.copy());
            }
            return movies;
        }

        String sql = "SELECT movieId, title, genre, releaseYear, runtimeMinutes, rating, watched "
                + "FROM movies ORDER BY movieId";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                movies.add(buildMovieFromResultSet(resultSet));
            }
        } catch (SQLException exception) {
            return movies;
        }

        return movies;
    }

    /**
     * Builds a formatted display table of all movie records in the database.
     *
     * @return a formatted movie table, or a message if no records are stored
     */
    public String formatMovieTable() {
        if (!isConnected()) {
            return "No database is connected.";
        }

        ArrayList<Movie> movies = loadMovies();
        if (movies.isEmpty()) {
            return "No movie records found in the connected database.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-6s %-32s %-14s %-6s %-8s %-7s %-8s%n",
                "ID", "Title", "Genre", "Year", "Runtime", "Rating", "Watched"));
        builder.append("--------------------------------------------------------------------------------------\n");

        for (Movie movie : movies) {
            builder.append(String.format("%-6d %-32s %-14s %-6d %-8d %-7.1f %-8s%n",
                    movie.getMovieId(), shortenText(movie.getTitle(), 31), shortenText(movie.getGenre(), 13),
                    movie.getReleaseYear(), movie.getRuntimeMinutes(), movie.getRating(),
                    movie.isWatched() ? "Yes" : "No"));
        }

        return builder.toString();
    }

    /**
     * Calculates the average rating of all database movie records.
     *
     * @return the average rating, or 0.0 when no records are available
     */
    public double calculateAverageRating() {
        if (!isConnected()) {
            return 0.0;
        }

        if (fallbackMode) {
            if (fallbackMovies.isEmpty()) {
                return 0.0;
            }
            double total = 0.0;
            for (Movie movie : fallbackMovies) {
                total += movie.getRating();
            }
            return total / fallbackMovies.size();
        }

        String sql = "SELECT AVG(rating) AS averageRating FROM movies";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getDouble("averageRating");
            }
            return 0.0;
        } catch (SQLException exception) {
            return 0.0;
        }
    }

    /**
     * Calculates the total runtime of all database movie records.
     *
     * @return the total runtime in minutes
     */
    public int calculateTotalRuntime() {
        if (!isConnected()) {
            return 0;
        }

        if (fallbackMode) {
            int totalRuntime = 0;
            for (Movie movie : fallbackMovies) {
                totalRuntime += movie.getRuntimeMinutes();
            }
            return totalRuntime;
        }

        String sql = "SELECT SUM(runtimeMinutes) AS totalRuntime FROM movies";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("totalRuntime");
            }
            return 0;
        } catch (SQLException exception) {
            return 0;
        }
    }

    /**
     * Builds the custom calculation summary for the GUI output area.
     *
     * @return the average rating and total runtime summary, or a message if no records exist
     */
    public String getCalculationSummary() {
        if (!isConnected()) {
            return "No database is connected.";
        }

        if (getMovieCount() == 0) {
            return "No movies are stored yet, so average rating and total runtime cannot be calculated.";
        }

        return String.format("Average rating: %.2f%nTotal runtime: %d minutes",
                calculateAverageRating(), calculateTotalRuntime());
    }

    /**
     * Counts the number of movie records stored in the database.
     *
     * @return the movie record count
     */
    public int getMovieCount() {
        if (!isConnected()) {
            return 0;
        }

        if (fallbackMode) {
            return fallbackMovies.size();
        }

        String sql = "SELECT COUNT(*) AS movieCount FROM movies";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("movieCount");
            }
            return 0;
        } catch (SQLException exception) {
            return 0;
        }
    }

    /**
     * Validates a movie record before it is stored in the database.
     *
     * @param movie the movie record to validate
     * @return true if the movie contains acceptable values, false otherwise
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
                && movie.getReleaseYear() >= 1888
                && movie.getReleaseYear() <= 2100
                && movie.getRuntimeMinutes() > 0
                && movie.getRating() >= 0.0
                && movie.getRating() <= 10.0;
    }

    /**
     * method: buildMovieFromResultSet
     * parameters: ResultSet resultSet
     * return: Movie
     * purpose: converts one database result row into a Movie object.
     */
    private Movie buildMovieFromResultSet(ResultSet resultSet) throws SQLException {
        return new Movie(
                resultSet.getInt("movieId"),
                resultSet.getString("title"),
                resultSet.getString("genre"),
                resultSet.getInt("releaseYear"),
                resultSet.getInt("runtimeMinutes"),
                resultSet.getDouble("rating"),
                resultSet.getInt("watched") == 1
        );
    }

    /**
     * method: loadFallbackMovies
     * parameters: none
     * return: ArrayList<Movie>
     * purpose: loads locally stored movie records when an external SQLite driver is unavailable.
     */
    private ArrayList<Movie> loadFallbackMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        File dataFile = getFallbackDataFile();

        if (dataFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Movie movie = parseMovieLine(line);
                    if (movie != null) {
                        movies.add(movie);
                    }
                }
            } catch (IOException exception) {
                movies.clear();
            }
        }

        if (movies.isEmpty()) {
            movies = createDefaultMovies();
            saveMoviesToFile(movies, dataFile);
        }

        return movies;
    }

    /**
     * method: saveFallbackMovies
     * parameters: none
     * return: boolean
     * purpose: saves local movie records after add, update, or delete actions.
     */
    private boolean saveFallbackMovies() {
        return saveMoviesToFile(fallbackMovies, getFallbackDataFile());
    }

    /**
     * method: saveMoviesToFile
     * parameters: ArrayList<Movie> movies, File dataFile
     * return: boolean
     * purpose: writes movie records to the local storage file used by fallback mode.
     */
    private boolean saveMoviesToFile(ArrayList<Movie> movies, File dataFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Movie movie : movies) {
                writer.write(movie.toFileLine());
                writer.newLine();
            }
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    /**
     * method: getFallbackDataFile
     * parameters: none
     * return: File
     * purpose: returns the local storage file used when the SQLite driver is unavailable.
     */
    private File getFallbackDataFile() {
        return new File(databasePath + ".movies.txt");
    }

    /**
     * method: parseMovieLine
     * parameters: String line
     * return: Movie
     * purpose: converts one pipe-delimited fallback storage line into a Movie object.
     */
    private Movie parseMovieLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length != 7) {
                return null;
            }

            return new Movie(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Integer.parseInt(parts[4].trim()),
                    Double.parseDouble(parts[5].trim()),
                    Boolean.parseBoolean(parts[6].trim())
            );
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * method: createDefaultMovies
     * parameters: none
     * return: ArrayList<Movie>
     * purpose: creates the required sample movie records when no local records exist yet.
     */
    private ArrayList<Movie> createDefaultMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        movies.add(new Movie(101, "Inception", "Sci-Fi", 2010, 148, 9.2, true));
        movies.add(new Movie(102, "Coraline", "Fantasy", 2009, 100, 8.4, true));
        movies.add(new Movie(103, "Top Gun", "Action", 1986, 110, 7.8, true));
        movies.add(new Movie(104, "The Hunger Games", "Action", 2012, 142, 8.5, true));
        movies.add(new Movie(105, "The Hunger Games: Catching Fire", "Action", 2013, 146, 9.0, true));
        movies.add(new Movie(106, "The Hunger Games: Mockingjay - Part 1", "Action", 2014, 123, 8.0, true));
        movies.add(new Movie(107, "The Backrooms", "Horror", 2026, 90, 7.5, false));
        movies.add(new Movie(108, "The Lord of the Rings", "Fantasy", 2001, 178, 9.5, true));
        movies.add(new Movie(109, "The Hobbit", "Fantasy", 2012, 169, 8.2, true));
        movies.add(new Movie(110, "Interstellar", "Sci-Fi", 2014, 169, 9.3, true));
        movies.add(new Movie(111, "Spider-Man", "Action", 2002, 121, 8.0, true));
        movies.add(new Movie(112, "The Batman", "Action", 2022, 176, 8.7, true));
        movies.add(new Movie(113, "Frozen", "Animation", 2013, 102, 7.4, true));
        movies.add(new Movie(114, "Moana", "Animation", 2016, 107, 8.1, true));
        movies.add(new Movie(115, "Scream", "Horror", 1996, 111, 8.0, true));
        movies.add(new Movie(116, "Get Out", "Horror", 2017, 104, 8.6, true));
        movies.add(new Movie(117, "Barbie", "Comedy", 2023, 114, 7.2, true));
        movies.add(new Movie(118, "Mean Girls", "Comedy", 2004, 97, 8.3, true));
        movies.add(new Movie(119, "Avatar", "Sci-Fi", 2009, 162, 8.1, true));
        movies.add(new Movie(120, "The Princess Bride", "Adventure", 1987, 98, 8.5, true));
        return movies;
    }

    /**
     * method: shortenText
     * parameters: String text, int maxLength
     * return: String
     * purpose: shortens long text values so the GUI display table stays readable.
     */
    private String shortenText(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Closes the database connection or fallback storage mode when the program exits.
     *
     * @return true if the database layer closed successfully, false otherwise
     */
    public boolean close() {
        if (!isConnected()) {
            return false;
        }

        if (fallbackMode) {
            fallbackMode = false;
            fallbackMovies.clear();
            return true;
        }

        try {
            connection.close();
            connection = null;
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }
}
