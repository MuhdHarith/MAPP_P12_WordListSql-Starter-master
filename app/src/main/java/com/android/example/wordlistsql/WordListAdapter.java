/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.wordlistsql;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implements a simple Adapter for a RecyclerView.
 * Demonstrates how to add a click handler for each item in the ViewHolder.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    /**
     *  Custom view holder with a text view and two buttons.
     */
    class WordViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        Button delete_button;
        Button edit_button;

        public WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = (TextView) itemView.findViewById(R.id.word);
            delete_button = (Button)itemView.findViewById(R.id.delete_button);
            edit_button = (Button)itemView.findViewById(R.id.edit_button);
        }
    }

    private static final String TAG = WordListAdapter.class.getSimpleName();

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";
    public static final String EXTRA_POSITION = "POSITION";

    private final LayoutInflater mInflater;
    Context mContext;
    WordListOpenHelper mDB;

    public WordListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        //holder.wordItemView.setText("placeholder");
        WordItem current = mDB.query(position);
        holder.wordItemView.setText(current.getWord());

        // Keep a reference to view holder for the click listener
        final WordViewHolder h = holder; //must be final for use in callback

        //attach a click listener to the DELETE button
        holder.delete_button.setOnClickListener(
                new MyButtonOnClickListener(current.getId(), null) {

                    void showDialog(){
                        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(mContext);
                        // Set the dialog title.
                        myAlertBuilder.setTitle("DELETE ALERT");
                        // Set the dialog message.
                        myAlertBuilder.setMessage("Are you sure you want to delete this?");
                        // Add the buttons.
                        myAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked OK button.
                                Toast.makeText(mContext.getApplicationContext(), "Pressed OK",
                                        Toast.LENGTH_SHORT).show();
                                int deleted = mDB.delete(id);
                                if (deleted >= 0) {
                                    notifyItemRemoved(h.getAdapterPosition());
                                }
                            }
                        });
                        myAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User cancelled the dialog.
                                Toast.makeText(mContext.getApplicationContext(), "Pressed Cancel",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        myAlertBuilder.show();
                    }

            @Override
            public void onClick(View v ) {
                showDialog();
            }
        }
        );

        holder.edit_button.setOnClickListener(new MyButtonOnClickListener (
                current.getId(), current.getWord()) {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditWordActivity.class);

                intent.putExtra(EXTRA_ID, id);
                intent.putExtra(EXTRA_POSITION, h.getAdapterPosition());
                intent.putExtra(EXTRA_WORD, word);

                // start empty edit activity
                ((Activity) mContext).startActivityForResult(
                        intent, MainActivity.WORD_EDIT);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Placeholder so we can see some mock data.
        return (int) mDB.count();
    }

    public WordListAdapter(Context context, WordListOpenHelper db) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDB = db;
    }



}


