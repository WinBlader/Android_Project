package com.example.taskify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Base activity that automatically applies theme settings
 * Extend this instead of AppCompatActivity for automatic theme support
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setting content view
        ThemeManager.getInstance(this).applyTheme(this);
        super.onCreate(savedInstanceState);
    }
}

