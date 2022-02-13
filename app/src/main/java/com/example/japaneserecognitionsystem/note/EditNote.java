package com.example.japaneserecognitionsystem.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import com.example.japaneserecognitionsystem.MainActivity;
import com.example.japaneserecognitionsystem.R;
import com.example.japaneserecognitionsystem.model.Classifier;
import com.example.japaneserecognitionsystem.model.Note;
import com.example.japaneserecognitionsystem.paint.PaintView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNote extends AppCompatActivity {

    private static final String LABEL_FILE_0 = "testing.txt";
    private static final String MODEL_FILE_0 = "testing.pb";
    private static final String LABEL_FILE_1 = "katakana.txt";
    private static final String MODEL_FILE_1 = "katakana-model.pb";
    private static final String LABEL_FILE_2 = "hiragana.txt";
    private static final String MODEL_FILE_2 = "hiragana-model.pb";
    private static final String LABEL_FILE_3 = "kanji.txt";
    private static final String MODEL_FILE_3 = "kanji-model.pb";

    FirebaseAuth mAuth;
    Intent data;
    EditText editNoteTitle, editNoteContent;

    FirebaseFirestore db;
    DocumentReference documentReference;
    String userID;

    Classifier classifier;
    String[] currentTopLabels;
    PaintView writingSurface;
    Button alt1, alt2, alt3, alt4;
    LinearLayout altLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = findViewById(R.id.editNoteToolbar);
        setSupportActionBar(toolbar);

        //Initialise authentication and firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //Get data from firestore
        data = getIntent();

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        editNoteTitle = (EditText) findViewById(R.id.editNoteTitle);
        editNoteContent = (EditText) findViewById(R.id.editNoteContent);

        editNoteTitle.setText(noteTitle);
        editNoteContent.setText(noteContent);
        writingSurface = (PaintView) findViewById(R.id.editNoteWritingSurface);

        //Clear button
        Button clearButton = (Button) findViewById(R.id.editNoteButtonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        //Classify button
        Button classifyButton = (Button) findViewById(R.id.editNoteButtonClassify);
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classify();
                writingSurface.reset();
                writingSurface.invalidate();
            }
        });

        //Backspace button
        Button backspaceButton = (Button) findViewById(R.id.editNoteButtonBackspace);
        backspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backspace();
                altLayout.setVisibility(View.INVISIBLE);
                writingSurface.reset();
                writingSurface.invalidate();
            }
        });

        //Space button
        Button spaceButton = (Button) findViewById(R.id.editNoteButtonSpace);
        spaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                space();
            }
        });

        //Alternative characters (2nd to 5th confidence level character) layout
        altLayout = (LinearLayout) findViewById(R.id.editaltLayout);
        altLayout.setVisibility(View.INVISIBLE);

        alt1 = (Button) findViewById(R.id.editNotealt1);
        alt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useAltLabel(Integer.parseInt(v.getTag().toString()));
            }
        });
        alt2 = (Button) findViewById(R.id.editNotealt2);
        alt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useAltLabel(Integer.parseInt(v.getTag().toString()));
            }
        });
        alt3 = (Button) findViewById(R.id.editNotealt3);
        alt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useAltLabel(Integer.parseInt(v.getTag().toString()));
            }
        });
        alt4 = (Button) findViewById(R.id.editNotealt4);
        alt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useAltLabel(Integer.parseInt(v.getTag().toString()));
            }
        });

        loadModel();
        //loadModelKata();
        //loadModelHira();
        //loadModelKan();

        //Save button
        ImageButton saveButton = (ImageButton) findViewById(R.id.editNoteSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTitle = editNoteTitle.getText().toString();
                String nContent = editNoteContent.getText().toString();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(EditNote.this, "Cannot save note with empty field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                documentReference = db.collection("Notes").document(userID)
                        .collection("myNotes").document(data.getStringExtra("noteID"));
                Note note = new Note(nTitle, nContent);


                documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditNote.this, "Note Successfully Updated.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditNote.this, "Note Failed to Save.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Close button
        ImageButton closeButton = (ImageButton) findViewById(R.id.editNoteCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        writingSurface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        writingSurface.onPause();
    }

    //Delete the last character in the content
    private void backspace() {
        int len = editNoteContent.getText().length();
        if (len > 0) {
            editNoteContent.getText().delete(len - 1, len);
        }
    }

    //Add a space in the content
    private void space() {
        editNoteContent.append(" ");
    }

    //Clear the content and drawing box
    private void clear() {
        writingSurface.reset();
        writingSurface.invalidate();
        editNoteContent.setText("");
        altLayout.setVisibility(View.INVISIBLE);
    }

    //Perform classification and show the 5 highest confidence level characters
    private void classify() {
        float pixels[] = writingSurface.getPixelData();
        currentTopLabels = classifier.classify(pixels);
        editNoteContent.append(currentTopLabels[0]);
        altLayout.setVisibility(View.VISIBLE);
        alt1.setText(currentTopLabels[1]);
        alt2.setText(currentTopLabels[2]);
        alt3.setText(currentTopLabels[3]);
        alt4.setText(currentTopLabels[4]);
    }

    //Switch the chosen character with the highest confidence level character in the content
    private void useAltLabel(int index) {
        backspace();
        editNoteContent.append(currentTopLabels[index]);
    }

    private void loadModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = Classifier.create(getAssets(),
                            MODEL_FILE_0, LABEL_FILE_0, PaintView.FEED_DIMENSION,
                            "input", "keep_prob", "output");
                } catch (final Exception e) {
                    throw new RuntimeException("Error loading pre-trained model.", e);
                }
            }
        }).start();
    }

    private void loadModelKata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = Classifier.create(getAssets(),
                            MODEL_FILE_1, LABEL_FILE_1, PaintView.FEED_DIMENSION,
                            "input", "keep_prob", "output");
                } catch (final Exception e) {
                    throw new RuntimeException("Error loading pre-trained model.", e);
                }
            }
        }).start();
    }

    private void loadModelHira() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = Classifier.create(getAssets(),
                            MODEL_FILE_2, LABEL_FILE_2, PaintView.FEED_DIMENSION,
                            "input", "keep_prob", "output");
                } catch (final Exception e) {
                    throw new RuntimeException("Error loading pre-trained model.", e);
                }
            }
        }).start();
    }

    private void loadModelKan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = Classifier.create(getAssets(),
                            MODEL_FILE_3, LABEL_FILE_3, PaintView.FEED_DIMENSION,
                            "input", "keep_prob", "output");
                } catch (final Exception e) {
                    throw new RuntimeException("Error loading pre-trained model.", e);
                }
            }
        }).start();
    }
}