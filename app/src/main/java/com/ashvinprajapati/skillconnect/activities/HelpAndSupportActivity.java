package com.ashvinprajapati.skillconnect.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;

public class HelpAndSupportActivity extends AppCompatActivity {
    private CardView faqCardView, contactSupportCardView, reportIssueCardView;
    private String GOOGLE_FORM_LINK = "https://docs.google.com/forms/d/e/1FAIpQLSdPNu3uhTp4HKtaqmQcX8EyYj-dDczvg0eRdxlhyyreaf13Eg/viewform?usp=dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_and_support);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        faqCardView.setOnClickListener(v ->{
            startActivity(new Intent(this, FAQActivity.class));
        });

        contactSupportCardView.setOnClickListener(v -> openMail());

        reportIssueCardView.setOnClickListener(v -> {
            // Navigate to google form
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_FORM_LINK)));
        });
    }

    private void openMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"pashvin6665@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "SkillConnect Support");
        startActivity(intent);

    }

    private void init() {
        faqCardView = findViewById(R.id.faqCardView);
        contactSupportCardView = findViewById(R.id.contactSupportCardView);
        reportIssueCardView = findViewById(R.id.reportIssueCardView);
    }
}