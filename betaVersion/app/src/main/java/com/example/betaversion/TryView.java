//package com.example.betaversion;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.databinding.BindingAdapter;
//
//import android.os.Bundle;
//import android.webkit.WebView;
//
//public class TryView extends AppCompatActivity {
//
//    WebView webView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_try_view);
//        webView = (WebView) findViewById(R.id.west);
//    }
//    // this binding adapter helps load custom html from assets folder
//    @BindingAdapter("htmlToScreen")
//    void bindTextViewHtml(WebView webView , String htmlValue ) {
//
//        webView.getSettings().setJavaScriptEnabled(true);
//
//    /*webView.webViewClient = object : WebViewClient() {
//        override fun onPageFinished(view: WebView, url: String) {
//            super.onPageFinished(view, url)
//            val handler = Handler()
//            handler.postDelayed(
//                {
//                    //webView.loadUrl("javascript:globalVariable('" + 370 + "')")
//                    //webView.loadUrl("javascript:(function(){l=document.getElementById('music_sheet');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()")
//
//                },
//                10
//            )
//        }
//    }*/
//
//        webView.loadDataWithBaseURL("fake://not/needed", htmlValue, "text/html", "UTF-8", "");
//    }
//}