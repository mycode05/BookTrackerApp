// BookTrackerApp: A simple Android app to track books with SQLite database

package com.example.booktrackerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editBookTitle, editAuthorName, editReview, editRating;
    private Button buttonAdd, buttonView, buttonViewReviews;
    private TextView textViewBooks;
    private BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editBookTitle = findViewById(R.id.editBookTitle);
        editAuthorName = findViewById(R.id.editAuthorName);
        editReview = findViewById(R.id.editReview);
        editRating = findViewById(R.id.editRating);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonView = findViewById(R.id.buttonView);
        buttonViewReviews = findViewById(R.id.buttonViewReviews);
        textViewBooks = findViewById(R.id.textViewBooks);

        // Initialize database helper
        dbHelper = new BookDatabaseHelper(this);

        // Add book to database
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = editBookTitle.getText().toString().trim();
                String authorName = editAuthorName.getText().toString().trim();
                String review = editReview.getText().toString().trim();
                String rating = editRating.getText().toString().trim();

                if (!bookTitle.isEmpty() && !authorName.isEmpty() && !review.isEmpty() && !rating.isEmpty()) {
                    addBook(bookTitle, authorName, review, rating);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // View books in database
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBooks();
            }
        });

        // View reviews and ratings in database
        buttonViewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewReviews();
            }
        });
    }

    private void addBook(String bookTitle, String authorName, String review, String rating) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("book_title", bookTitle);
        values.put("author_name", authorName);
        values.put("review", review);
        values.put("rating", rating);

        long result = db.insert("books", null, values);
        if (result != -1) {
            Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show();
            editBookTitle.setText("");
            editAuthorName.setText("");
            editReview.setText("");
            editRating.setText("");
        } else {
            Toast.makeText(this, "Error adding book", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewBooks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("books", null, null, null, null, null, null);

        StringBuilder books = new StringBuilder();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("book_title"));
            String author = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
            books.append("Title: ").append(title).append("\nAuthor: ").append(author).append("\n\n");
        }
        cursor.close();

        textViewBooks.setText(books.toString());
    }

    private void viewReviews() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("books", null, null, null, null, null, null);

        StringBuilder reviews = new StringBuilder();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("book_title"));
            String review = cursor.getString(cursor.getColumnIndexOrThrow("review"));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"));
            reviews.append("Title: ").append(title).append("\nReview: ").append(review).append("\nRating: ").append(rating).append("\n\n");
        }
        cursor.close();

        textViewBooks.setText(reviews.toString());
    }

    static class BookDatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "booktracker.db";
        private static final int DATABASE_VERSION = 2;

        public BookDatabaseHelper(MainActivity context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT, book_title TEXT, author_name TEXT, review TEXT, rating TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS books");
            onCreate(db);
        }
    }
}
