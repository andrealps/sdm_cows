package es.uniovi.eii.cows.view;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.Html;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import es.uniovi.eii.cows.R;
import es.uniovi.eii.cows.model.NewsItem;

public class NewsActivity extends AppCompatActivity {

    //news item showing
    private NewsItem newsItem;

    private TextView title;
    private TextView source;
    private TextView date;
    private WebView description;
    private ImageView image;
    private FloatingActionButton fabLinkCompleteNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //setSupportActionBar(findViewById(R.id.app_bar));

        //Receiving intent
        Intent newsIntent = getIntent();
        newsItem = newsIntent.getParcelableExtra(MainActivity.SELECTED_NEWS_ITEM);

        //initialize and show to the user
        initializeNewsItemProperties();
        if(newsItem != null)
            showNewsItem(newsItem);
    }

    private void initializeNewsItemProperties(){

        title = (TextView) findViewById(R.id.idTitle_news);
        source = (TextView) findViewById(R.id.idSource_news);
        date = (TextView) findViewById(R.id.idDate_news);
        description = (WebView) findViewById(R.id.idDescription_news);
        image = (ImageView) findViewById(R.id.idImage_news);

        Glide.with(this).load(newsItem.getImageUrl())
                .thumbnail(Glide.with(this).load(R.drawable.loading))
                .error(R.drawable.no_image_available)
                .centerInside()
                .into(image);

        fabLinkCompleteNews = (FloatingActionButton) findViewById(R.id.floating_action_button_news);
        fabLinkCompleteNews.setOnClickListener(v -> linkToCompleteNewsItem());
    }

    private void showNewsItem(NewsItem newsItem){
        String title = newsItem.getTitle();
        String source = newsItem.getSource();
        String description = newsItem.getDescription();
        String date = newsItem.getDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));

        this.title.setText(title);
        this.source.setText(source);
        this.date.setText(date);

        loadNewsContent(description);


    }

    private void linkToCompleteNewsItem(){
        if(newsItem != null)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink())));
    }

    private void loadNewsContent(String content){

        //Format
        content = StringEscapeUtils.unescapeHtml4(content);

        //Loading
        this.description.loadData(content, "text/html; charset=utf-8", "UTF-8");
    }
}
