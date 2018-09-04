package com.appham.logkitten.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.appham.logkitten.IntentFactory;
import com.appham.logkitten.LogcatProcessor;
import com.appham.logkitten.R;
import com.appham.logkitten.service.LogEntry;

public class LogDetailActivity extends AppCompatActivity {

    private TextView txtLogDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logkitten_activity_log_detail);
        txtLogDetail = findViewById(R.id.txtLogDetail);
        txtLogDetail.setMovementMethod(new ScrollingMovementMethod());
        setTextFromIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logkitten_log_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareLogText();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(SettingsActivity.getLaunchIntent(this));
            return true;
        } else if (id == R.id.action_info) {
            gotoInfo();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void gotoInfo() {
        startActivity(IntentFactory.getBrowserIntent("https://github.com/donfuxx/LogKitten"));
    }

    private void shareLogText() {
        LogEntry logEntry = new LogEntry();
        logEntry.setContent(txtLogDetail.getText().toString());
        Intent chooserIntent = IntentFactory.getChooserIntent(logEntry, this);
        startActivity(chooserIntent);
    }

    private void setTextFromIntent() {
        Intent intent = getIntent();

        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    txtLogDetail.setText(sharedText);
                }
            }
        } else {
            dumpLogcat();
        }
    }

    private void dumpLogcat() {
        txtLogDetail.setText(LogcatProcessor.dumpLogcat() + getString(R.string.logkitten_powered_by));
    }
}
