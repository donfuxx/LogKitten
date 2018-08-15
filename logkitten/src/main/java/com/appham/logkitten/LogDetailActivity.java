package com.appham.logkitten;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class LogDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);
        setTextFromIntent();
    }

    private void setTextFromIntent() {
        Intent intent = getIntent();

        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    TextView txtLogDetail = findViewById(R.id.txtLogDetail);
                    txtLogDetail.setText(sharedText);
                    txtLogDetail.setMovementMethod(new ScrollingMovementMethod());
                }
            }
        }
    }
}
