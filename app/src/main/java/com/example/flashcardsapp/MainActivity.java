package com.example.flashcardsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

TextView question;
TextView answer;

FlashcardDatabase flashcardDatabase;
List<Flashcard> allFlashcards;
int cardIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.flashcard_question);
        answer = findViewById(R.id.flashcard_answer);

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setVisibility(View.INVISIBLE);
                answer.setVisibility(View.VISIBLE);
            }
        });

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setVisibility(View.VISIBLE);
                answer.setVisibility(View.INVISIBLE);
            }
        });

        ImageView addQuestionImageView = findViewById(R.id.flashcard_add_button);
        addQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
                //startActivityForResult(intent, 100);
            }
        });

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0) { // not null or empty
            Flashcard firstCard = allFlashcards.get(0);
            question.setText(firstCard.getQuestion());
            answer.setText(firstCard.getAnswer());
        }

        findViewById(R.id.flashcard_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // don't try to go to the next card if you have no cards to begin with
                if (allFlashcards == null || allFlashcards.size() == 0) {
                    return;                }
                cardIndex++;

                if(cardIndex >= allFlashcards.size()) {
                    Snackbar.make(v,
                            "You have reached the end of the card, going back to the start.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    cardIndex = 0; // reset index so user can go back to the beginning of the cards.
                }

                Flashcard currentCard = allFlashcards.get(cardIndex);
                question.setText(currentCard.getQuestion());
                answer.setText(currentCard.getAnswer());
            }
        });

        findViewById(R.id.flashcard_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                allFlashcards = flashcardDatabase.getAllCards();

                if (allFlashcards == null || allFlashcards.size() == 0) {
                    return;                }
                cardIndex++;

                if(cardIndex >= allFlashcards.size()) {
                    Snackbar.make(v,
                            "You have reached the end of the card, going back to the start.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    cardIndex = 0; // reset index so user can go back to the beginning of the cards.
                }

                Flashcard currentCard = allFlashcards.get(cardIndex);
                question.setText(currentCard.getQuestion());
                answer.setText(currentCard.getAnswer());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            // get data
            if(data != null){
                String questionStr = data.getExtras().getString("question_key");
                String answerStr = data.getExtras().getString("answer_key");
                question.setText(questionStr);
                answer.setText(answerStr);

                Flashcard flashcard = new Flashcard(questionStr, answerStr);
                flashcardDatabase.insertCard(flashcard);

                allFlashcards = flashcardDatabase.getAllCards();
            }
        }
    }
}