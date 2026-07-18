MovieTrackerDMS Phase 4 

This version adds the Phase 4 backend database requirement to the Movie Tracker DMS.
The GUI now asks the user to supply a SQLite database file path instead of a text file path.

Main files:
- src/MovieApp.java launches the GUI.
- src/MovieTrackerGUI.java displays the black/royal-blue GUI.
- src/MovieDatabase.java connects to SQLite and handles database CRUD.
- movie_tracker_sample.db is the sample SQLite database with 20 movie records.
- create_sample_database.sql is a script that can recreate the sample database.

How to build a runnable JAR with the SQLite dependency included:
1. Open the Maven panel in IntelliJ.
2. Expand Lifecycle.
3. Double-click package.
4. The runnable JAR should be created at target/MovieTrackerDMS-Phase4.jar.

PowerShell Maven build option:
mvn clean package

Required database table:
movies(movieId, title, genre, releaseYear, runtimeMinutes, rating, watched)
