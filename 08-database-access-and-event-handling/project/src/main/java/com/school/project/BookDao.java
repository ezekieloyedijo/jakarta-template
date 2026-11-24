package com.school.project;

import jakarta.servlet.ServletContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * THE DAO (Data Access Object): the ONLY class that knows SQL.
 * Servlets call findAll(), insert(...), update(...) and never see a Connection or a ResultSet.
 *
 * Every method follows the same 4 steps:
 *   1. open a connection   2. prepare the SQL with ? marks   3. fill the ? marks and run it   4. close (automatic)
 */
public class BookDao {

    public static final String ATTRIBUTE_NAME = "bookDao";

    private final String url;

    public BookDao(String url) {
        this.url = url;
    }

    /** The DAO that DatabaseListener created when the app started. */
    public static BookDao from(ServletContext app) {
        return (BookDao) app.getAttribute(ATTRIBUTE_NAME);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    // ---------- set-up (called once by DatabaseListener) ----------

    public void createTableIfMissing() {
        String sql = """
                CREATE TABLE IF NOT EXISTS book (
                    id     INT AUTO_INCREMENT PRIMARY KEY,
                    title  VARCHAR(100) NOT NULL,
                    author VARCHAR(60)  NOT NULL,
                    published INT       NOT NULL,
                    copies INT          NOT NULL
                )""";
        try (Connection con = connect();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataException("Could not create the book table", e);
        }
    }

    public int count() {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM book");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataException("Could not count the books", e);
        }
    }

    // ---------- CRUD ----------

    /** CREATE: INSERT a row. Returns the id the database gave it. */
    public int insert(String title, String author, int year, int copies) {
        String sql = "INSERT INTO book (title, author, published, copies) VALUES (?, ?, ?, ?)";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);
            ps.setInt(4, copies);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {        // the new AUTO_INCREMENT id
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataException("Could not add the book", e);
        }
    }

    /** READ: every row, sorted by title. */
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM book ORDER BY title");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(toBook(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new DataException("Could not read the books", e);
        }
    }

    /** READ ONE: the row with this id, or null. */
    public Book findById(int id) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM book WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toBook(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataException("Could not read book " + id, e);
        }
    }

    /** UPDATE: change one row. False if no row has this id. */
    public boolean update(int id, String title, String author, int year, int copies) {
        String sql = "UPDATE book SET title = ?, author = ?, published = ?, copies = ? WHERE id = ?";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);
            ps.setInt(4, copies);
            ps.setInt(5, id);
            return ps.executeUpdate() == 1;                      // how many rows changed
        } catch (SQLException e) {
            throw new DataException("Could not update book " + id, e);
        }
    }

    /** DELETE: remove one row. False if no row has this id. */
    public boolean delete(int id) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement("DELETE FROM book WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataException("Could not delete book " + id, e);
        }
    }

    /** One row of the ResultSet -> one Book object. */
    private Book toBook(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                rs.getInt("published"), rs.getInt("copies"));
    }

    /**
     * Thrown when the database fails. It is unchecked, so servlets don't need try/catch:
     * Tomcat shows a 500 page and prints the cause in the terminal.
     */
    public static class DataException extends RuntimeException {
        public DataException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
