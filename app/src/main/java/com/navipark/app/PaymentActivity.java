package com.navipark.app;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        WebView webView = findViewById(R.id.webView);
        int totalPrice = getIntent().getIntExtra("totalPrice", 0);
        
        // Forward these to BookingConfirmation after payment
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String duration = getIntent().getStringExtra("duration");
        String location = getIntent().getStringExtra("location");
        String spot = getIntent().getStringExtra("spot");
        
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("return")) {
                    Toast.makeText(PaymentActivity.this, "Payment Successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PaymentActivity.this, BookingConfirmationActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("duration", duration);
                    intent.putExtra("location", location);
                    intent.putExtra("spot", spot);
                    intent.putExtra("totalPrice", totalPrice);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        
        String merchantId = "10000100";
        String merchantKey = "46f0cd694581a";
        String returnUrl = "https://www.google.com/search?q=return";
        String cancelUrl = "https://www.google.com/search?q=cancel";
        
        String html = "<html><body>" +
                "<form id='payfastForm' action='https://sandbox.payfast.co.za/eng/process' method='post'>" +
                "<input type='hidden' name='merchant_id' value='" + merchantId + "'>" +
                "<input type='hidden' name='merchant_key' value='" + merchantKey + "'>" +
                "<input type='hidden' name='return_url' value='" + returnUrl + "'>" +
                "<input type='hidden' name='cancel_url' value='" + cancelUrl + "'>" +
                "<input type='hidden' name='amount' value='" + totalPrice + ".00'>" +
                "<input type='hidden' name='item_name' value='NaviPark Parking Bay'>" +
                "</form>" +
                "<script>document.getElementById('payfastForm').submit();</script>" +
                "</body></html>";
                
        webView.loadData(html, "text/html", "UTF-8");
    }
}
