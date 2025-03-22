package com.example.stars;

import static android.content.ContentValues.TAG;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stars.adapter.StarAdapter;
import com.example.stars.beans.Star;
import com.example.stars.service.StarService;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private List<Star> stars;
    private RecyclerView recyclerView;
    private StarAdapter starAdapter = null;
    private StarService  service = StarService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stars = new ArrayList<>();
       if(service.findAll().isEmpty())  init();


        recyclerView = findViewById(R.id.recycle_view);
        starAdapter = new StarAdapter(this, service.findAll());
        recyclerView.setAdapter(starAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void init() {
        service.create(new Star("Soufiane El Khalidy", "https://diffusionph.cccommunication.biz/jpgok/RepGR/759/759439_42.jpg", 3.5f));
        service.create(new Star("Mouna Fettou", "https://tse2.mm.bing.net/th?id=OIP.HzYyLec8C8tsJA7oNeKVPwHaIy&w=474&h=474&c=7", 5.0f));
        service.create(new Star("Abdelkarim Derqaoui", "https://aujourdhui.ma/wp-content/uploads/2016/02/1829356499.jpg", 4.5f));
        service.create(new Star("Latifa Ahrar", "https://images.mubicdn.net/images/cast_member/93742/cache-614080-1606527192/image-w856.jpg", 3.0f));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("ListActivity", "onCreateOptionsMenu started");

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        if (menuItem == null) {
            Log.e("ListActivity", "menuItem is null! Check menu.xml");
            return true;
        }

        SearchView searchView = (SearchView) menuItem.getActionView();
        if (searchView == null) {
            Log.e("ListActivity", "SearchView is null! Cannot set listener.");
            return true;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (starAdapter != null) {
                    starAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            String shareText = "Check out these stars!";
            String mimeType = "text/plain";

            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle("Share via")
                    .setText(shareText)
                    .startChooser();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}





