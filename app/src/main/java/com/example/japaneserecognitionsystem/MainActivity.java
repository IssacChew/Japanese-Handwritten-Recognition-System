package com.example.japaneserecognitionsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.japaneserecognitionsystem.auth.ChangePassword;
import com.example.japaneserecognitionsystem.auth.Login;
import com.example.japaneserecognitionsystem.auth.Profile;
import com.example.japaneserecognitionsystem.model.Adapter;
import com.example.japaneserecognitionsystem.model.Note;
import com.example.japaneserecognitionsystem.model.User;
import com.example.japaneserecognitionsystem.note.AddNote;
import com.example.japaneserecognitionsystem.note.EditNote;
import com.example.japaneserecognitionsystem.note.NoteDetail;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView navProfilePicture;
    TextView navProfileName;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView noteList;

    String userID;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Authentication and Firestore initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //Note query
        Query query = db.collection("Notes").document(userID)
                .collection("myNotes").orderBy("noteTitle", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> myNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        //Create note holder (sticky note)
        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(myNotes) {
            @Override
            protected void onBindViewHolder(@NonNull final NoteViewHolder noteViewHolder, int position, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getNoteTitle());
                noteViewHolder.noteContent.setText(note.getNoteContent());
                final int code = getRandomColor();
                noteViewHolder.cardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code,null));
                final String noteID = noteAdapter.getSnapshots().getSnapshot(position).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), NoteDetail.class);
                        intent.putExtra("title", note.getNoteTitle());
                        intent.putExtra("content", note.getNoteContent());
                        intent.putExtra("code", code);
                        intent.putExtra("noteID", noteID);
                        v.getContext().startActivity(intent);
                    }
                });

                //3-dots menu
                ImageView menuIcon = (ImageView) noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String noteID = noteAdapter.getSnapshots().
                                getSnapshot(noteViewHolder.getBindingAdapterPosition()).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent (v.getContext(), EditNote.class);
                                intent.putExtra("title", note.getNoteTitle());
                                intent.putExtra("content", note.getNoteContent());
                                intent.putExtra("noteID", noteID);
                                startActivity(intent);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                documentReference = db.collection("Notes").document(userID)
                                        .collection("myNotes").document(noteID);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this, "Selected Note Deleted.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Unable to Delete.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();

                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
                return new NoteViewHolder(view);
            }
        };



        noteList = (RecyclerView) findViewById(R.id.noteList);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        noteList.setHasFixedSize(true);
        //sticky notes arrange vertically in 2 lines
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(noteAdapter);

        updateNavHeader();

        //Add note floating button
        FloatingActionButton fab = findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddNote.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter != null){
            noteAdapter.startListening();
        }
    }

    //side bar navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.addNote:
                startActivity(new Intent(MainActivity.this, AddNote.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, Profile.class));
                break;
            case R.id.nav_password:
                startActivity(new Intent(MainActivity.this, ChangePassword.class));
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                logOutUser();
                break;
            default:
                Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Side bar username and profile picture
    private void updateNavHeader() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navProfilePicture = (CircleImageView) headerView.findViewById(R.id.nav_profile_picture);
        navProfileName = (TextView) headerView.findViewById(R.id.nav_name);

        documentReference = db.collection("Notes").document(userID)
                .collection("Users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String name = user.getUserName();
                String imageURL = user.getImageURL();

                Picasso.get().load(imageURL).into(navProfilePicture);

                navProfileName.setText(name);
            }
        });
    }

    //Generate a random color for the sticky note
    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.white);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.blue);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int numColor = randomColor.nextInt(colorCode.size());
        return colorCode.get(numColor);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle, noteContent;
        View view;
        CardView cardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent =  itemView.findViewById(R.id.content);
            cardView =  itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    //log out
    private void logOutUser() {
        Intent logOut = new Intent(getApplicationContext(), Login.class);
        logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logOut);
        finish();
    }
}