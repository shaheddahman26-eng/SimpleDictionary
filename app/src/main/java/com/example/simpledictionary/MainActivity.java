package com.example.simpledictionary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simpledictionary.adapter.WordAdapter;
import com.example.simpledictionary.model.Word;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Home screen of SimpleDictionary.
 * Displays every Word in a RecyclerView, lets the user add a new
 * word via the FAB, edit a word by tapping it, and delete a word
 * by long-pressing it. The word list survives configuration
 * changes (e.g. screen rotation) via onSaveInstanceState().
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_WORD_LIST = "key_word_list";

    private WordAdapter adapter;
    private ArrayList<Word> wordList;
    private TextView tvEmptyList;

    // Handles the result coming back from AddEditActivity (both add and edit flows)
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String word = data.getStringExtra(AddEditActivity.EXTRA_WORD);
                    String meaning = data.getStringExtra(AddEditActivity.EXTRA_MEANING);
                    int position = data.getIntExtra(AddEditActivity.EXTRA_POSITION, -1);

                    if (position == -1) {
                        // No position was passed in -> this was a new word
                        wordList.add(new Word(word, meaning));
                        adapter.notifyItemInserted(wordList.size() - 1);
                        Toast.makeText(this, "Word added", Toast.LENGTH_SHORT).show();
                    } else {
                        // Existing position -> this was an edit
                        wordList.set(position, new Word(word, meaning));
                        adapter.notifyItemChanged(position);
                        Toast.makeText(this, "Word updated", Toast.LENGTH_SHORT).show();
                    }
                    updateEmptyState();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerViewWords = findViewById(R.id.recyclerViewWords);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        FloatingActionButton fabAddWord = findViewById(R.id.fabAddWord);

        // Restore the list after a configuration change, otherwise seed sample data
        if (savedInstanceState != null) {
            //noinspection unchecked
            wordList = (ArrayList<Word>) savedInstanceState.getSerializable(KEY_WORD_LIST);
        }
        if (wordList == null) {
            wordList = getInitialWords();
        }

        adapter = new WordAdapter(wordList);
        recyclerViewWords.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWords.setAdapter(adapter);

        adapter.setOnItemClickListener(new WordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Word selectedWord = wordList.get(position);
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(AddEditActivity.EXTRA_WORD, selectedWord.getWord());
                intent.putExtra(AddEditActivity.EXTRA_MEANING, selectedWord.getMeaning());
                intent.putExtra(AddEditActivity.EXTRA_POSITION, position);
                activityResultLauncher.launch(intent);
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteConfirmation(position);
            }
        });

        fabAddWord.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            activityResultLauncher.launch(intent);
        });

        updateEmptyState();
    }

    private void showDeleteConfirmation(int position) {
        Word wordToDelete = wordList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Delete Word")
                .setMessage("Delete \"" + wordToDelete.getWord() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    wordList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, wordList.size());
                    Toast.makeText(MainActivity.this, "Word deleted", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateEmptyState() {
        tvEmptyList.setVisibility(wordList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private ArrayList<Word> getInitialWords() {
        ArrayList<Word> list = new ArrayList<>();
        list.add(new Word("Serendipity", "The occurrence of events by chance in a happy way."));
        list.add(new Word("Ephemeral", "Lasting for a very short time."));
        list.add(new Word("Ubiquitous", "Present, appearing, or found everywhere."));
        return list;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Preserve the in-memory word list across configuration changes (e.g. rotation)
        outState.putSerializable(KEY_WORD_LIST, wordList);
    }
}
