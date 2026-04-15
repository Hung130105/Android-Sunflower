package com.example.sunflower.api;

import com.example.sunflower.api.request.*;
import com.example.sunflower.models.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ========== AUTH ==========
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // ========== EXAM ==========
    @GET("api/exams/")
    Call<List<DeThi>> getExams();

    // ========== EXAM REVIEW ==========
    @GET("api/exams/history/{session_id}")
    Call<SessionDetailResponse> getSessionDetail(@Path("session_id") int sessionId);

    @GET("api/exams/{exam_id}")
    Call<ExamDetail> getExamDetail(@Path("exam_id") int examId);

    @POST("api/exams/{exam_id}/submit")
    Call<SubmitResponse> submitExam(@Path("exam_id") int examId, @Body SubmitRequest request);

    @GET("api/exams/history")
    Call<List<HistorySession>> getHistory();

    // ========== FLASHCARD ==========
    @GET("api/flashcards/decks")
    Call<List<BoTu>> getDecks();

    @POST("api/flashcards/decks")
    Call<BoTu> createDeck(@Body CreateDeckRequest request);

    @GET("api/flashcards/decks/{deck_id}")
    Call<DeckDetail> getDeckDetail(@Path("deck_id") int deckId);

    @POST("api/flashcards/decks/{deck_id}/cards")
    Call<FlashCard> createCard(@Path("deck_id") int deckId, @Body CreateCardRequest request);

    @DELETE("api/flashcards/decks/{deck_id}")
    Call<Void> deleteDeck(@Path("deck_id") int deckId);

    @DELETE("api/flashcards/cards/{card_id}")
    Call<Void> deleteCard(@Path("card_id") int cardId);

    // ========== DICTIONARY ==========
    @GET("api/dictionary/lookup/{word}")
    Call<DictionaryResponse> lookupWord(@Path("word") String word);

    // ========== RESPONSE CLASSES ==========
    class LoginResponse {
        private String message;
        private String access_token;
        private User user;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getAccess_token() { return access_token; }
        public void setAccess_token(String access_token) { this.access_token = access_token; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    class RegisterResponse {
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}