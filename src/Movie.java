/**
 * Represents one movie record in the Movie Tracker DMS.
 * A Movie stores the identifying, descriptive, and numeric data used by the
 * application for CRUD operations, display output, validation, and calculations.
 */
public class Movie {
    private int movieId;
    private String title;
    private String genre;
    private int releaseYear;
    private int runtimeMinutes;
    private double rating;
    private boolean watched;

    /**
     * Creates a movie record with all values needed by the DMS.
     *
     * @param movieId the unique ID for the movie
     * @param title the movie title
     * @param genre the movie genre
     * @param releaseYear the year the movie was released
     * @param runtimeMinutes the movie runtime in minutes
     * @param rating the user's rating from 0.0 to 10.0
     * @param watched true if the movie has been watched, false otherwise
     */
    public Movie(int movieId, String title, String genre, int releaseYear, int runtimeMinutes, double rating, boolean watched) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.runtimeMinutes = runtimeMinutes;
        this.rating = rating;
        this.watched = watched;
    }

    /**
     * Returns the movie's unique ID.
     *
     * @return the movie ID
     */
    public int getMovieId() {
        return movieId;
    }

    /**
     * Updates the movie ID when the supplied value is positive.
     *
     * @param movieId the new movie ID
     * @return true if the ID was updated, false if the value was invalid
     */
    public boolean setMovieId(int movieId) {
        if (movieId <= 0) {
            return false;
        }
        this.movieId = movieId;
        return true;
    }

    /**
     * Returns the movie title.
     *
     * @return the title of the movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * Updates the movie title when the supplied value is not blank.
     *
     * @param title the new title value
     * @return true if the title was updated, false if the value was blank
     */
    public boolean setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        this.title = title.trim();
        return true;
    }

    /**
     * Returns the movie genre.
     *
     * @return the genre of the movie
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Updates the movie genre when the supplied value is not blank.
     *
     * @param genre the new genre value
     * @return true if the genre was updated, false if the value was blank
     */
    public boolean setGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return false;
        }
        this.genre = genre.trim();
        return true;
    }

    /**
     * Returns the movie release year.
     *
     * @return the release year
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Updates the release year when it falls within the accepted range.
     *
     * @param releaseYear the new release year
     * @return true if the year was updated, false if the value was invalid
     */
    public boolean setReleaseYear(int releaseYear) {
        if (releaseYear < 1888 || releaseYear > 2100) {
            return false;
        }
        this.releaseYear = releaseYear;
        return true;
    }

    /**
     * Returns the movie runtime in minutes.
     *
     * @return the runtime in minutes
     */
    public int getRuntimeMinutes() {
        return runtimeMinutes;
    }

    /**
     * Updates the runtime when the supplied value is positive.
     *
     * @param runtimeMinutes the new runtime in minutes
     * @return true if the runtime was updated, false if the value was invalid
     */
    public boolean setRuntimeMinutes(int runtimeMinutes) {
        if (runtimeMinutes <= 0) {
            return false;
        }
        this.runtimeMinutes = runtimeMinutes;
        return true;
    }

    /**
     * Returns the user rating for the movie.
     *
     * @return the movie rating from 0.0 to 10.0
     */
    public double getRating() {
        return rating;
    }

    /**
     * Updates the rating when the supplied value is between 0.0 and 10.0.
     *
     * @param rating the new movie rating
     * @return true if the rating was updated, false if the value was invalid
     */
    public boolean setRating(double rating) {
        if (rating < 0.0 || rating > 10.0) {
            return false;
        }
        this.rating = rating;
        return true;
    }

    /**
     * Returns whether the movie has been watched.
     *
     * @return true if watched, false otherwise
     */
    public boolean isWatched() {
        return watched;
    }

    /**
     * Updates the watched status for the movie.
     *
     * @param watched the new watched status
     * @return true after the watched status is updated
     */
    public boolean setWatched(boolean watched) {
        this.watched = watched;
        return true;
    }

    /**
     * Creates a separate Movie object with the same field values.
     * This supports safe sharing of movie records without exposing the original object.
     *
     * @return a copy of this movie
     */
    public Movie copy() {
        return new Movie(movieId, title, genre, releaseYear, runtimeMinutes, rating, watched);
    }

    /**
     * Formats this movie as one row for text-based table output.
     *
     * @return a formatted table row containing this movie's values
     */
    public String toTableRow() {
        return String.format("%-6d %-28s %-14s %-6d %-8d %-7.1f %-8s",
                movieId, title, genre, releaseYear, runtimeMinutes, rating, watched ? "Yes" : "No");
    }

    /**
     * Converts this movie to the pipe-delimited format used by text storage.
     *
     * @return a pipe-delimited line representing this movie
     */
    public String toFileLine() {
        return movieId + "|" + title + "|" + genre + "|" + releaseYear + "|" + runtimeMinutes + "|" + rating + "|" + watched;
    }

    /**
     * Builds a readable string representation of this movie.
     *
     * @return a string containing the movie field values
     */
    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                ", runtimeMinutes=" + runtimeMinutes +
                ", rating=" + rating +
                ", watched=" + watched +
                '}';
    }
}
