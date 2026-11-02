package com.example.catland;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class OCRAuthChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView titleText, subtitleText;
    private Button btnLogin, btnRegister;
    private View loadingOverlay;
    private View gradientBackground;
    private Handler handler = new Handler();
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        enableEdgeToEdge();

        setContentView(R.layout.activity_ocr_auth_choice);

        initializeViews();
        setupAnimations();
        setupClickListeners();
    }

    private void enableEdgeToEdge() {
        // Hide system bars for immersive experience
        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(decorView);

        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }

        // Set status bar color
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        gradientBackground = findViewById(R.id.gradientBackground);
    }

    private void setupAnimations() {
        // Start background animation
        startBackgroundAnimation();

        // Animate entrance of UI elements
        animateEntranceSequence();

        // Add subtle breathing animation to buttons
        startButtonBreathingAnimation();
    }

    private void startBackgroundAnimation() {
        // Create a subtle rotation animation for the gradient background
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(gradientBackground, "rotation", 0f, 360f);
        rotationAnimator.setDuration(30000); // 30 seconds for slow rotation
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotationAnimator.start();
    }

    private void animateEntranceSequence() {
        // Hide elements initially
        titleText.setAlpha(0f);
        titleText.setTranslationY(-50f);
        subtitleText.setAlpha(0f);
        subtitleText.setTranslationY(-30f);
        btnLogin.setAlpha(0f);
        btnLogin.setTranslationY(50f);
        btnRegister.setAlpha(0f);
        btnRegister.setTranslationY(70f);

        // Animate title
        handler.postDelayed(() -> {
            ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(titleText, "alpha", 0f, 1f);
            ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(titleText, "translationY", -50f, 0f);

            AnimatorSet titleSet = new AnimatorSet();
            titleSet.playTogether(titleAlpha, titleTranslation);
            titleSet.setDuration(800);
            titleSet.setInterpolator(new AccelerateDecelerateInterpolator());
            titleSet.start();
        }, 200);

        // Animate subtitle
        handler.postDelayed(() -> {
            ObjectAnimator subtitleAlpha = ObjectAnimator.ofFloat(subtitleText, "alpha", 0f, 1f);
            ObjectAnimator subtitleTranslation = ObjectAnimator.ofFloat(subtitleText, "translationY", -30f, 0f);

            AnimatorSet subtitleSet = new AnimatorSet();
            subtitleSet.playTogether(subtitleAlpha, subtitleTranslation);
            subtitleSet.setDuration(600);
            subtitleSet.setInterpolator(new AccelerateDecelerateInterpolator());
            subtitleSet.start();
        }, 500);

        // Animate login button
        handler.postDelayed(() -> {
            ObjectAnimator loginAlpha = ObjectAnimator.ofFloat(btnLogin, "alpha", 0f, 1f);
            ObjectAnimator loginTranslation = ObjectAnimator.ofFloat(btnLogin, "translationY", 50f, 0f);

            AnimatorSet loginSet = new AnimatorSet();
            loginSet.playTogether(loginAlpha, loginTranslation);
            loginSet.setDuration(600);
            loginSet.setInterpolator(new AccelerateDecelerateInterpolator());
            loginSet.start();
        }, 800);

        // Animate register button
        handler.postDelayed(() -> {
            ObjectAnimator registerAlpha = ObjectAnimator.ofFloat(btnRegister, "alpha", 0f, 1f);
            ObjectAnimator registerTranslation = ObjectAnimator.ofFloat(btnRegister, "translationY", 70f, 0f);

            AnimatorSet registerSet = new AnimatorSet();
            registerSet.playTogether(registerAlpha, registerTranslation);
            registerSet.setDuration(600);
            registerSet.setInterpolator(new AccelerateDecelerateInterpolator());
            registerSet.start();
        }, 1000);
    }

    private void startButtonBreathingAnimation() {
        // Subtle scale animation for buttons
        handler.postDelayed(() -> {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnLogin, "scaleX", 1f, 1.02f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnLogin, "scaleY", 1f, 1.02f, 1f);

            AnimatorSet breathingSet = new AnimatorSet();
            breathingSet.playTogether(scaleX, scaleY);
            breathingSet.setDuration(2000);
//            breathingSet.setRepeatCount(ObjectAnimator.INFINITE);
            breathingSet.setInterpolator(new AccelerateDecelerateInterpolator());
            breathingSet.start();
        }, 1500);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isAnimating) return;

        isAnimating = true;
        animateButtonClick(v);

        // Determine which activity to launch
        Class<?> targetActivity = null;
        if (v.getId() == R.id.btnLogin) {
            targetActivity = LoginActivity.class;
        } else if (v.getId() == R.id.btnRegister) {
            targetActivity = RegisterActivity.class;
        }

        // Show loading and launch activity
        showLoadingAndLaunch(targetActivity);
    }

    private void animateButtonClick(View button) {
        // Scale down and up animation
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f);

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.95f, 1f);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(scaleDownX, scaleDownY);
        scaleDown.setDuration(100);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);
        scaleUp.setDuration(100);

        AnimatorSet clickAnimation = new AnimatorSet();
        clickAnimation.playSequentially(scaleDown, scaleUp);
        clickAnimation.start();
    }

    private void showLoadingAndLaunch(Class<?> targetActivity) {
        // Show loading overlay with fade in
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(loadingOverlay, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.start();

        loadingOverlay.setVisibility(View.VISIBLE);

        // Launch activity after delay
        handler.postDelayed(() -> {
            if (targetActivity != null) {
                Intent intent = new Intent(this, targetActivity);

                // Add smooth transition
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            // Hide loading overlay
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(loadingOverlay, "alpha", 1f, 0f);
            fadeOut.setDuration(300);
            fadeOut.start();

            handler.postDelayed(() -> {
                loadingOverlay.setVisibility(View.GONE);
                isAnimating = false;
            }, 300);

        }, 800); // Show loading for 800ms
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset animation state
        isAnimating = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clean up any running animations
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}