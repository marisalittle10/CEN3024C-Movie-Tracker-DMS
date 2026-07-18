CREATE TABLE IF NOT EXISTS movies (
    movieId INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    genre TEXT NOT NULL,
    releaseYear INTEGER NOT NULL,
    runtimeMinutes INTEGER NOT NULL,
    rating REAL NOT NULL,
    watched INTEGER NOT NULL
);

DELETE FROM movies;

INSERT INTO movies(movieId, title, genre, releaseYear, runtimeMinutes, rating, watched) VALUES
(101, 'Inception', 'Sci-Fi', 2010, 148, 9.2, 1),
(102, 'Coraline', 'Fantasy', 2009, 100, 8.4, 1),
(103, 'Top Gun', 'Action', 1986, 110, 7.8, 1),
(104, 'The Hunger Games', 'Action', 2012, 142, 8.5, 1),
(105, 'The Hunger Games: Catching Fire', 'Action', 2013, 146, 9.0, 1),
(106, 'The Hunger Games: Mockingjay - Part 1', 'Action', 2014, 123, 8.0, 1),
(107, 'The Backrooms', 'Horror', 2026, 90, 7.5, 0),
(108, 'The Lord of the Rings', 'Fantasy', 2001, 178, 9.5, 1),
(109, 'The Hobbit', 'Fantasy', 2012, 169, 8.2, 1),
(110, 'Interstellar', 'Sci-Fi', 2014, 169, 9.3, 1),
(111, 'Spider-Man', 'Action', 2002, 121, 8.0, 1),
(112, 'The Batman', 'Action', 2022, 176, 8.7, 1),
(113, 'Frozen', 'Animation', 2013, 102, 7.4, 1),
(114, 'Moana', 'Animation', 2016, 107, 8.1, 1),
(115, 'Scream', 'Horror', 1996, 111, 8.0, 1),
(116, 'Get Out', 'Horror', 2017, 104, 8.6, 1),
(117, 'Barbie', 'Comedy', 2023, 114, 7.2, 1),
(118, 'Mean Girls', 'Comedy', 2004, 97, 8.3, 1),
(119, 'Avatar', 'Sci-Fi', 2009, 162, 8.1, 1),
(120, 'The Princess Bride', 'Adventure', 1987, 98, 8.5, 1);
