package com.example.simpledictionary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

/**
 * Screen used both for adding a brand-new word and for editing an
 * existing one. Which mode it's in is decided by whether EXTRA_POSITION
 * was supplied in the launching Intent (-1 means "add new").
 *
 * Note: etWord / etMeaning already have their own onSaveInstanceState
 * handling built into Android's View class (because they have IDs),
 * so their typed text also survives rotation automatically.
 */
public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";
    public static final String EXTRA_MEANING = "extra_meaning";
    public static final String EXTRA_POSITION = "extra_position";

    private TextInputEditText etWord;
    private TextInputEditText etMeaning;
    private int position = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        Toolbar toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etWord = findViewById(R.id.etWord);
        etMeaning = findViewById(R.id.etMeaning);
        Button btnSave = findViewById(R.id.btnSave);

        Intent incomingIntent = getIntent();
        String existingWord = incomingIntent.getStringExtra(EXTRA_WORD);
        String existingMeaning = incomingIntent.getStringExtra(EXTRA_MEANING);
        position = incomingIntent.getIntExtra(EXTRA_POSITION, -1);

        if (position != -1) {
            // Editing an existing entry - pre-fill the fields
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.edit_word);
            }
            etWord.setText(existingWord);
            etMeaning.setText(existingMeaning);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.add_word);
            }
        }

        btnSave.setOnClickListener(v -> saveWord());
    }

    private void saveWord() {
        String word = etWord.getText() != null ? etWord.getText().toString().trim() : "";
        String meaning = etMeaning.getText() != null ? etMeaning.getText().toString().trim() : "";

        if (TextUtils.isEmpty(word) || TextUtils.isEmpty(meaning)) {
            Toast.makeText(this, "Please enter both word and meaning", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_WORD, word);
        resultIntent.putExtra(EXTRA_MEANING, meaning);
        resultIntent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
