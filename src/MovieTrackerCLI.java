import java.util.Scanner;

/**
 * Provides the command-line interface used in the earlier Movie Tracker DMS phases.
 * The class lets a user load, add, display, update, remove, and calculate movie data
 * through menu options while the GUI and database phases are developed separately.
 */
public class MovieTrackerCLI {
    private Scanner scanner;
    private MovieManager manager;
    private MovieFileReader fileReader;

    /**
     * Creates a command-line interface connected to the supplied movie manager.
     *
     * @param manager the MovieManager object that stores and manages movie records
     */
    public MovieTrackerCLI(MovieManager manager) {
        this.manager = manager;
        scanner = new Scanner(System.in);
        fileReader = new MovieFileReader();
    }

    /**
     * Starts the command-line menu loop and continues until the user chooses to exit.
     *
     * @return true after the menu exits normally
     */
    public boolean start() {
        int choice = 0;
        System.out.println("Welcome to the Movie Tracker DMS - Phase 2");

        while (choice != 7) {
            System.out.println(buildMenu());
            choice = readIntInRange("Choose an option: ", 1, 7);
            runMenuChoice(choice);
        }

        System.out.println("Goodbye. The program ended because you chose Exit.");
        return true;
    }

    /**
     * method: buildMenu
     * parameters: none
     * return: String
     * purpose: builds the list of available CLI options for the user.
     */
    private String buildMenu() {
        return "\n===== Movie Tracker Menu =====\n"
                + "1. Load movies from text file\n"
                + "2. Add a movie manually\n"
                + "3. Display all movies\n"
                + "4. Update a movie\n"
                + "5. Remove a movie\n"
                + "6. Calculate average rating and total runtime\n"
                + "7. Exit";
    }

    /**
     * method: runMenuChoice
     * parameters: int choice
     * return: boolean
     * purpose: sends the user's valid menu choice to the correct handler method.
     */
    private boolean runMenuChoice(int choice) {
        if (choice == 1) {
            return loadMoviesFromFile();
        } else if (choice == 2) {
            return addMovieManually();
        } else if (choice == 3) {
            return displayMovies();
        } else if (choice == 4) {
            return updateMovie();
        } else if (choice == 5) {
            return removeMovie();
        } else if (choice == 6) {
            return displayCalculation();
        } else if (choice == 7) {
            return true;
        }
        return false;
    }

    /**
     * method: loadMoviesFromFile
     * parameters: none
     * return: boolean
     * purpose: asks for a text file path, loads valid movie records, and adds them to the collection.
     */
    private boolean loadMoviesFromFile() {
        String filePath = readNonBlankText("Enter the full path of the movie text file: ");
        FileLoadResult result = fileReader.loadMoviesFromFile(filePath);

        if (!result.isSuccessful()) {
            System.out.println(result.getMessage());
            return false;
        }

        int addedCount = manager.addMovies(result.getMovies());
        System.out.println(result.getMessage());
        System.out.println(addedCount + " new movie records were added to the tracker.");
        System.out.println(result.getSkippedLines() + " invalid file lines were skipped.");

        if (addedCount < result.getMovies().size()) {
            System.out.println("Some valid records were not added because their IDs were already in use.");
        }
        return true;
    }

    /**
     * method: addMovieManually
     * parameters: none
     * return: boolean
     * purpose: collects one movie record from the user and adds it when all fields are valid.
     */
    private boolean addMovieManually() {
        int movieId = readUnusedMovieId("Movie ID: ");
        String title = readNonBlankText("Title: ");
        String genre = readNonBlankText("Genre: ");
        int releaseYear = readIntInRange("Release year: ", 1888, 2100);
        int runtimeMinutes = readIntInRange("Runtime in minutes: ", 1, 1000);
        double rating = readDoubleInRange("Rating from 0.0 to 10.0: ", 0.0, 10.0);
        boolean watched = readYesOrNo("Have you watched this movie? (yes/no): ");

        Movie movie = new Movie(movieId, title, genre, releaseYear, runtimeMinutes, rating, watched);
        if (manager.addMovie(movie)) {
            System.out.println("Movie added successfully.");
            System.out.println(movie.toTableRow());
            return true;
        }

        System.out.println("Movie could not be added. Please check the data and try again.");
        return false;
    }

    /**
     * method: displayMovies
     * parameters: none
     * return: boolean
     * purpose: prints all stored movie records to the command line.
     */
    private boolean displayMovies() {
        System.out.println(manager.formatMovieTable());
        return true;
    }

    /**
     * method: updateMovie
     * parameters: none
     * return: boolean
     * purpose: lets the user choose an existing movie and update any one of its fields.
     */
    private boolean updateMovie() {
        if (manager.getMovieCount() == 0) {
            System.out.println("There are no movie records to update.");
            return false;
        }

        System.out.println(manager.formatMovieTable());
        int movieId = readExistingMovieId("Enter the ID of the movie to update: ");
        System.out.println(buildUpdateMenu());
        int choice = readIntInRange("Choose the field to update: ", 1, 7);
        boolean updated = runUpdateChoice(movieId, choice);

        if (updated) {
            System.out.println("Movie updated successfully.");
            System.out.println(manager.findMovieById(choice == 1 ? readLastUpdatedId(movieId) : movieId).toTableRow());
            return true;
        }

        System.out.println("The movie was not updated. Please check the value and try again.");
        return false;
    }

    /**
     * method: buildUpdateMenu
     * parameters: none
     * return: String
     * purpose: builds the list of movie fields that can be changed by the user.
     */
    private String buildUpdateMenu() {
        return "\nUpdate Field\n"
                + "1. Movie ID\n"
                + "2. Title\n"
                + "3. Genre\n"
                + "4. Release year\n"
                + "5. Runtime minutes\n"
                + "6. Rating\n"
                + "7. Watched status";
    }

    /**
     * method: runUpdateChoice
     * parameters: int movieId, int choice
     * return: boolean
     * purpose: updates the selected movie field using validated input from the user.
     */
    private boolean runUpdateChoice(int movieId, int choice) {
        if (choice == 1) {
            int newId = readUnusedMovieId("New movie ID: ");
            boolean changed = manager.updateMovieId(movieId, newId);
            if (changed) {
                setLastUpdatedId(newId);
            }
            return changed;
        } else if (choice == 2) {
            return manager.updateTitle(movieId, readNonBlankText("New title: "));
        } else if (choice == 3) {
            return manager.updateGenre(movieId, readNonBlankText("New genre: "));
        } else if (choice == 4) {
            return manager.updateReleaseYear(movieId, readIntInRange("New release year: ", 1888, 2100));
        } else if (choice == 5) {
            return manager.updateRuntimeMinutes(movieId, readIntInRange("New runtime in minutes: ", 1, 1000));
        } else if (choice == 6) {
            return manager.updateRating(movieId, readDoubleInRange("New rating from 0.0 to 10.0: ", 0.0, 10.0));
        } else if (choice == 7) {
            return manager.updateWatched(movieId, readYesOrNo("Watched? (yes/no): "));
        }
        return false;
    }

    private int lastUpdatedId;

    /**
     * method: setLastUpdatedId
     * parameters: int movieId
     * return: boolean
     * purpose: stores the new ID after an ID update so the changed record can be displayed.
     */
    private boolean setLastUpdatedId(int movieId) {
        lastUpdatedId = movieId;
        return true;
    }

    /**
     * method: readLastUpdatedId
     * parameters: int originalId
     * return: int
     * purpose: returns the updated ID when an ID field changed, or the original ID otherwise.
     */
    private int readLastUpdatedId(int originalId) {
        if (lastUpdatedId > 0) {
            return lastUpdatedId;
        }
        return originalId;
    }

    /**
     * method: removeMovie
     * parameters: none
     * return: boolean
     * purpose: removes a movie by either ID or title and handles missing records safely.
     */
    private boolean removeMovie() {
        if (manager.getMovieCount() == 0) {
            System.out.println("There are no movie records to remove.");
            return false;
        }

        System.out.println(manager.formatMovieTable());
        System.out.println("Remove by: 1. ID  2. Title");
        int choice = readIntInRange("Choose an option: ", 1, 2);
        boolean removed;

        if (choice == 1) {
            int movieId = readIntInRange("Enter the movie ID to remove: ", 1, Integer.MAX_VALUE);
            removed = manager.removeMovieById(movieId);
        } else {
            String title = readNonBlankText("Enter the movie title to remove: ");
            removed = manager.removeMovieByTitle(title);
        }

        if (removed) {
            System.out.println("Movie removed successfully.");
            return true;
        }

        System.out.println("No matching movie was found. Nothing was removed.");
        return false;
    }

    /**
     * method: displayCalculation
     * parameters: none
     * return: boolean
     * purpose: prints the custom calculation results for average rating and total runtime.
     */
    private boolean displayCalculation() {
        System.out.println(manager.getCalculationSummary());
        return true;
    }

    /**
     * method: readIntInRange
     * parameters: String prompt, int minimum, int maximum
     * return: int
     * purpose: keeps asking the user for an integer until the value is within the required range.
     */
    private int readIntInRange(String prompt, int minimum, int maximum) {
        boolean valid = false;
        int value = minimum;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                value = Integer.parseInt(input);
                if (value >= minimum && value <= maximum) {
                    valid = true;
                } else {
                    System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
                }
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
        return value;
    }

    /**
     * method: readDoubleInRange
     * parameters: String prompt, double minimum, double maximum
     * return: double
     * purpose: keeps asking the user for a decimal number until the value is within the required range.
     */
    private double readDoubleInRange(String prompt, double minimum, double maximum) {
        boolean valid = false;
        double value = minimum;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                value = Double.parseDouble(input);
                if (value >= minimum && value <= maximum) {
                    valid = true;
                } else {
                    System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
                }
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
        return value;
    }

    /**
     * method: readNonBlankText
     * parameters: String prompt
     * return: String
     * purpose: keeps asking the user for text until the input is not blank.
     */
    private String readNonBlankText(String prompt) {
        String value = "";

        while (value.isEmpty()) {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("This value cannot be blank.");
            }
        }
        return value;
    }

    /**
     * method: readYesOrNo
     * parameters: String prompt
     * return: boolean
     * purpose: keeps asking the user for yes or no and returns the matching boolean value.
     */
    private boolean readYesOrNo(String prompt) {
        boolean answered = false;
        boolean value = false;

        while (!answered) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y") || input.equalsIgnoreCase("true")) {
                value = true;
                answered = true;
            } else if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("false")) {
                value = false;
                answered = true;
            } else {
                System.out.println("Please enter yes or no.");
            }
        }
        return value;
    }

    /**
     * method: readUnusedMovieId
     * parameters: String prompt
     * return: int
     * purpose: keeps asking the user for a positive movie ID until the ID is not already used.
     */
    private int readUnusedMovieId(String prompt) {
        boolean unused = false;
        int movieId = 0;

        while (!unused) {
            movieId = readIntInRange(prompt, 1, Integer.MAX_VALUE);
            if (manager.findMovieById(movieId) == null) {
                unused = true;
            } else {
                System.out.println("That ID is already being used. Please choose a different ID.");
            }
        }
        return movieId;
    }

    /**
     * method: readExistingMovieId
     * parameters: String prompt
     * return: int
     * purpose: keeps asking the user for a movie ID until the ID matches an existing record.
     */
    private int readExistingMovieId(String prompt) {
        boolean exists = false;
        int movieId = 0;

        while (!exists) {
            movieId = readIntInRange(prompt, 1, Integer.MAX_VALUE);
            if (manager.findMovieById(movieId) != null) {
                exists = true;
            } else {
                System.out.println("No movie with that ID exists. Please try again.");
            }
        }
        return movieId;
    }
}
