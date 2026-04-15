package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.sunflower.R;
import com.example.sunflower.adapters.BoTuAdapter;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.api.request.CreateCardRequest;
import com.example.sunflower.api.request.CreateDeckRequest;
import com.example.sunflower.models.BoTu;
import com.example.sunflower.models.DeckDetail;
import com.example.sunflower.models.FlashCard;
import com.example.sunflower.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlashcardActivity extends AppCompatActivity {

    private RecyclerView rvDecks;
    private FloatingActionButton fabAddDeck;
    private TextView tvEmpty;
    private List<BoTu> deckList;
    private BoTuAdapter adapter;
    private boolean isFlipped = false;
    private Animation flipIn, flipOut;

    private AlertDialog currentDialog;
    private DeckDetail currentDeckDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.setAuthToken(token);
        }

        initViews();
        setupAnimations();
        loadDecks();
    }

    private void initViews() {
        rvDecks = findViewById(R.id.rvDecks);
        fabAddDeck = findViewById(R.id.fabAddDeck);
        tvEmpty = findViewById(R.id.tvEmpty);

        deckList = new ArrayList<>();
        adapter = new BoTuAdapter(deckList, this::onDeckClick, this::onDeleteDeck);
        rvDecks.setLayoutManager(new LinearLayoutManager(this));
        rvDecks.setAdapter(adapter);

        fabAddDeck.setOnClickListener(v -> showAddDeckDialog());
    }

    private void setupAnimations() {
        try {
            flipIn = AnimationUtils.loadAnimation(this, R.anim.flip_in);
            flipOut = AnimationUtils.loadAnimation(this, R.anim.flip_out);
        } catch (Exception e) {
            flipIn = null;
            flipOut = null;
        }
    }

    private void loadDecks() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<BoTu>> call = apiService.getDecks();

        call.enqueue(new Callback<List<BoTu>>() {
            @Override
            public void onResponse(Call<List<BoTu>> call, Response<List<BoTu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deckList.clear();
                    deckList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (deckList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvDecks.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvDecks.setVisibility(View.VISIBLE);
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(FlashcardActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(FlashcardActivity.this).logout();
                    startActivity(new Intent(FlashcardActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(FlashcardActivity.this, "Không có bộ từ nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BoTu>> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadDecks() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<BoTu>> call = apiService.getDecks();

        call.enqueue(new Callback<List<BoTu>>() {
            @Override
            public void onResponse(Call<List<BoTu>> call, Response<List<BoTu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deckList.clear();
                    deckList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (deckList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvDecks.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvDecks.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BoTu>> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi tải lại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onDeckClick(BoTu deck) {
        if (deck == null) {
            Toast.makeText(this, "Lỗi: bộ từ không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        int deckId = deck.getId();
        Log.d("FLASHCARD", "onDeckClick - ID: " + deckId);

        if (deckId <= 0) {
            Toast.makeText(this, "Lỗi: ID bộ từ không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<DeckDetail> call = apiService.getDeckDetail(deckId);

        call.enqueue(new Callback<DeckDetail>() {
            @Override
            public void onResponse(Call<DeckDetail> call, Response<DeckDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDeckDetail = response.body();
                    showFlashcardDialog(currentDeckDetail);
                } else if (response.code() == 404) {
                    Toast.makeText(FlashcardActivity.this, "Không tìm thấy bộ từ", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(FlashcardActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(FlashcardActivity.this).logout();
                    startActivity(new Intent(FlashcardActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(FlashcardActivity.this, "Không thể tải flashcard", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeckDetail> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFlashcardDialog(DeckDetail deck) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_flashcard, null);

        CardView cardFlashcard = view.findViewById(R.id.cardFlashcard);
        TextView tvWord = view.findViewById(R.id.tvWord);
        TextView tvMeaning = view.findViewById(R.id.tvMeaning);
        TextView tvPhonetic = view.findViewById(R.id.tvPhonetic);
        TextView tvExample = view.findViewById(R.id.tvExample);
        TextView tvCounter = view.findViewById(R.id.tvCounter);
        LinearLayout layoutFront = view.findViewById(R.id.layoutFront);
        ScrollView scrollBackView = view.findViewById(R.id.scrollBack);
        Button btnPrev = view.findViewById(R.id.btnPrev);
        Button btnNext = view.findViewById(R.id.btnNext);
        Button btnFlip = view.findViewById(R.id.btnFlip);
        Button btnAddCard = view.findViewById(R.id.btnAddCard);
        Button btnEditCard = view.findViewById(R.id.btnEditCard);
        Button btnDeleteCard = view.findViewById(R.id.btnDeleteCard);

        final List<FlashCard> cards = new ArrayList<>();
        if (deck.getCards() != null) {
            cards.addAll(deck.getCards());
        }
        final int[] currentIndex = {0};

        if (cards.isEmpty()) {
            tvWord.setText("📭 Chưa có thẻ nào");
            tvPhonetic.setText("");
            tvMeaning.setText("Hãy bấm nút '+' để thêm thẻ mới");
            tvExample.setText("");
            tvCounter.setText("0 / 0");
            btnEditCard.setVisibility(View.GONE);
            btnDeleteCard.setVisibility(View.GONE);
        } else {
            // Hiển thị thẻ đầu tiên
            updateFlashcardDisplay(cards, currentIndex[0], tvWord, tvPhonetic, tvMeaning, tvExample, tvCounter);
        }

        // Lật thẻ
        View.OnClickListener flipListener = v -> {
            if (cards.isEmpty()) return;

            if (isFlipped) {
                if (flipOut != null) cardFlashcard.startAnimation(flipOut);
                layoutFront.setVisibility(View.VISIBLE);
                scrollBackView.setVisibility(View.GONE);
                isFlipped = false;
                if (flipIn != null) cardFlashcard.startAnimation(flipIn);
            } else {
                if (flipOut != null) cardFlashcard.startAnimation(flipOut);
                layoutFront.setVisibility(View.GONE);
                scrollBackView.setVisibility(View.VISIBLE);
                isFlipped = true;
                if (flipIn != null) cardFlashcard.startAnimation(flipIn);
            }
        };

        cardFlashcard.setOnClickListener(flipListener);
        btnFlip.setOnClickListener(flipListener);

        btnPrev.setOnClickListener(v -> {
            if (!cards.isEmpty() && currentIndex[0] > 0) {
                currentIndex[0]--;
                updateFlashcardDisplay(cards, currentIndex[0], tvWord, tvPhonetic, tvMeaning, tvExample, tvCounter);
                if (isFlipped) {
                    if (flipOut != null) cardFlashcard.startAnimation(flipOut);
                    layoutFront.setVisibility(View.VISIBLE);
                    scrollBackView.setVisibility(View.GONE);
                    isFlipped = false;
                    if (flipIn != null) cardFlashcard.startAnimation(flipIn);
                }
                btnEditCard.setVisibility(View.VISIBLE);
                btnDeleteCard.setVisibility(View.VISIBLE);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (!cards.isEmpty() && currentIndex[0] < cards.size() - 1) {
                currentIndex[0]++;
                updateFlashcardDisplay(cards, currentIndex[0], tvWord, tvPhonetic, tvMeaning, tvExample, tvCounter);
                if (isFlipped) {
                    if (flipOut != null) cardFlashcard.startAnimation(flipOut);
                    layoutFront.setVisibility(View.VISIBLE);
                    scrollBackView.setVisibility(View.GONE);
                    isFlipped = false;
                    if (flipIn != null) cardFlashcard.startAnimation(flipIn);
                }
                btnEditCard.setVisibility(View.VISIBLE);
                btnDeleteCard.setVisibility(View.VISIBLE);
            }
        });

        btnAddCard.setOnClickListener(v -> {
            showAddCardDialog(deck.getId(), () -> {
                loadDeckDetailAndRefreshDialog(deck.getId());
            });
        });

        btnEditCard.setOnClickListener(v -> {
            if (!cards.isEmpty()) {
                showEditCardDialog(deck.getId(), cards.get(currentIndex[0]), () -> {
                    loadDeckDetailAndRefreshDialog(deck.getId());
                });
            }
        });

        btnDeleteCard.setOnClickListener(v -> {
            if (!cards.isEmpty()) {
                confirmDeleteCard(deck.getId(), cards.get(currentIndex[0]), () -> {
                    loadDeckDetailAndRefreshDialog(deck.getId());
                });
            }
        });

        builder.setTitle(deck.getTitle())
                .setView(view)
                .setPositiveButton("Đóng", (dialog, which) -> {
                    currentDialog = null;
                    currentDeckDetail = null;
                })
                .setCancelable(true);

        currentDialog = builder.show();
    }

    // ✅ HÀM CẬP NHẬT CẢ MẶT TRƯỚC VÀ MẶT SAU
    private void updateFlashcardDisplay(List<FlashCard> cards, int index,
                                        TextView tvWord, TextView tvPhonetic,
                                        TextView tvMeaning, TextView tvExample,
                                        TextView tvCounter) {
        if (cards != null && !cards.isEmpty() && index < cards.size()) {
            FlashCard card = cards.get(index);

            // Mặt trước: Từ vựng + Phiên âm
            tvWord.setText(card.getFront() != null ? card.getFront() : "???");
            tvPhonetic.setText(card.getPhonetic() != null && !card.getPhonetic().isEmpty()
                    ? "/" + card.getPhonetic() + "/" : "");

            // Mặt sau: Nghĩa + Ví dụ (KHÔNG phiên âm)
            tvMeaning.setText(card.getBack() != null ? card.getBack() : "???");
            tvExample.setText(card.getExample() != null && !card.getExample().isEmpty()
                    ? "📝 " + card.getExample() : "");

            tvCounter.setText((index + 1) + " / " + cards.size());
        }
    }

    private void loadDeckDetailAndRefreshDialog(int deckId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<DeckDetail> call = apiService.getDeckDetail(deckId);

        call.enqueue(new Callback<DeckDetail>() {
            @Override
            public void onResponse(Call<DeckDetail> call, Response<DeckDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentDeckDetail = response.body();
                    if (currentDialog != null && currentDialog.isShowing()) {
                        currentDialog.dismiss();
                    }
                    showFlashcardDialog(currentDeckDetail);
                    reloadDecks();
                }
            }

            @Override
            public void onFailure(Call<DeckDetail> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi tải lại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_deck, null);
        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDescription = view.findViewById(R.id.etDescription);

        builder.setTitle("Tạo bộ từ mới")
                .setView(view)
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    if (!title.isEmpty()) {
                        createDeck(title, description);
                    } else {
                        Toast.makeText(this, "Vui lòng nhập tên bộ từ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createDeck(String title, String description) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<BoTu> call = apiService.createDeck(new CreateDeckRequest(title, description, "🌱"));

        call.enqueue(new Callback<BoTu>() {
            @Override
            public void onResponse(Call<BoTu> call, Response<BoTu> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FlashcardActivity.this, "Tạo bộ từ thành công", Toast.LENGTH_SHORT).show();
                    reloadDecks();
                } else {
                    Toast.makeText(FlashcardActivity.this, "Tạo thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoTu> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCardDialog(int deckId, Runnable onSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_card, null);

        EditText etFront = view.findViewById(R.id.etFront);
        EditText etBack = view.findViewById(R.id.etBack);
        EditText etPhonetic = view.findViewById(R.id.etPhonetic);
        EditText etExample = view.findViewById(R.id.etExample);

        builder.setTitle("Thêm thẻ mới")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String front = etFront.getText().toString().trim();
                    String back = etBack.getText().toString().trim();
                    String phonetic = etPhonetic.getText().toString().trim();
                    String example = etExample.getText().toString().trim();

                    if (!front.isEmpty() && !back.isEmpty()) {
                        createCard(deckId, front, back, phonetic, example, onSuccess);
                    } else {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ mặt trước và mặt sau", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditCardDialog(int deckId, FlashCard card, Runnable onSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_card, null);

        EditText etFront = view.findViewById(R.id.etFront);
        EditText etBack = view.findViewById(R.id.etBack);
        EditText etPhonetic = view.findViewById(R.id.etPhonetic);
        EditText etExample = view.findViewById(R.id.etExample);

        etFront.setText(card.getFront());
        etBack.setText(card.getBack());
        etPhonetic.setText(card.getPhonetic());
        etExample.setText(card.getExample());

        builder.setTitle("Sửa thẻ")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String front = etFront.getText().toString().trim();
                    String back = etBack.getText().toString().trim();
                    String phonetic = etPhonetic.getText().toString().trim();
                    String example = etExample.getText().toString().trim();

                    if (!front.isEmpty() && !back.isEmpty()) {
                        updateCard(card.getId(), front, back, phonetic, example, onSuccess);
                    } else {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ mặt trước và mặt sau", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmDeleteCard(int deckId, FlashCard card, Runnable onSuccess) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa thẻ")
                .setMessage("Bạn có chắc muốn xóa thẻ \"" + card.getFront() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCard(card.getId(), onSuccess))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createCard(int deckId, String front, String back, String phonetic, String example, Runnable onSuccess) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<FlashCard> call = apiService.createCard(deckId, new CreateCardRequest(front, back, phonetic, example));

        call.enqueue(new Callback<FlashCard>() {
            @Override
            public void onResponse(Call<FlashCard> call, Response<FlashCard> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FlashcardActivity.this, "Thêm thẻ thành công", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Toast.makeText(FlashcardActivity.this, "Thêm thẻ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlashCard> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCard(int cardId, String front, String back, String phonetic, String example, Runnable onSuccess) {
        Toast.makeText(this, "Chức năng sửa đang phát triển", Toast.LENGTH_SHORT).show();
        if (onSuccess != null) onSuccess.run();
    }

    private void deleteCard(int cardId, Runnable onSuccess) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = apiService.deleteCard(cardId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FlashcardActivity.this, "Xóa thẻ thành công", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Toast.makeText(FlashcardActivity.this, "Xóa thẻ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FlashcardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onDeleteDeck(BoTu deck) {
        if (deck == null || deck.getId() <= 0) {
            Toast.makeText(this, "Không thể xóa bộ từ này", Toast.LENGTH_SHORT).show();
            return;
        }

        final int deckId = deck.getId();
        final String deckTitle = deck.getTitle();

        new AlertDialog.Builder(this)
                .setTitle("Xóa bộ từ")
                .setMessage("Bạn có chắc muốn xóa bộ từ \"" + deckTitle + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    ApiService apiService = RetrofitClient.getApiService();
                    Call<Void> call = apiService.deleteDeck(deckId);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(FlashcardActivity.this, "Đã xóa bộ từ", Toast.LENGTH_SHORT).show();
                                reloadDecks();
                            } else if (response.code() == 404) {
                                Toast.makeText(FlashcardActivity.this, "Không tìm thấy bộ từ", Toast.LENGTH_SHORT).show();
                                reloadDecks();
                            } else {
                                Toast.makeText(FlashcardActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(FlashcardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}