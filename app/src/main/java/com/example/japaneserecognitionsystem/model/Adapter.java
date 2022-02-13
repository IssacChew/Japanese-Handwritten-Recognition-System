package com.example.japaneserecognitionsystem.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japaneserecognitionsystem.note.NoteDetail;
import com.example.japaneserecognitionsystem.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles;
    List<String> contents;

    public Adapter(List<String> title, List<String> content){
        this.titles = title;
        this.contents = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
        return new ViewHolder(view);
    }

    //Sticky note build
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(contents.get(position));
        final int code = getRandomColor();
        holder.cardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NoteDetail.class);
                intent.putExtra("title", titles.get(holder.getBindingAdapterPosition()));
                intent.putExtra("content", contents.get(holder.getBindingAdapterPosition()));
                intent.putExtra("code", code);
                v.getContext().startActivity(intent);
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

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = (TextView) itemView.findViewById(R.id.titles);
            noteContent = (TextView) itemView.findViewById(R.id.content);
            cardView = (CardView) itemView.findViewById(R.id.noteCard);
            view = itemView;


        }
    }
}
