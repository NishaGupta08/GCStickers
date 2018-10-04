package com.example.nishagupta.gcstickers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nishagupta.gcstickers.adapter.Adapter_Image;
import com.example.nishagupta.gcstickers.listeners.PaginationScrollListener;
import com.example.nishagupta.gcstickers.models.GiphyResponsePOJO;
import com.example.nishagupta.gcstickers.models.Stickers;
import com.example.nishagupta.gcstickers.utils.Api;
import com.example.nishagupta.gcstickers.utils.ItemOffsetDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private Adapter_Image adapterImage;
    private Stickers stickers;


    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;
    private ArrayList<String> imageUrls;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.search_box)
    EditText searchBox;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
            else
                initialSetup();
        } else {

            initialSetup();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialSetup();
                } else {
                    Toast.makeText(MainActivity.this,"Please allow internet access in the settings to let the app work.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void initialSetup()
    {
        imageUrls = new ArrayList<>();
        adapterImage = new Adapter_Image(this, imageUrls);
        progressBar.setVisibility(View.INVISIBLE);


        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    performSearch(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });


        initializeRecyclerView(imageUrls);
    }
    //Fetch Stickers using GIPHY APIs

    private void loadStickers( final int item_count) {
        // display a progress dialog
        /*final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show(); // show progress dialog*/


        adapterImage.addLoadingFooter();

        (Api.getClient().getStickersList("trending",item_count,"")).enqueue(new Callback<GiphyResponsePOJO>() {
            @Override
            public void onResponse(Call<GiphyResponsePOJO> call, Response<GiphyResponsePOJO> response) {
                //progressDialog.dismiss(); //dismiss progress dialog
                GiphyResponsePOJO giphyResponse = response.body();

                adapterImage.removeLoadingFooter();  // 2

                for(int i=0;i<giphyResponse.getData().size();i++) {
                    GiphyResponsePOJO.DataBean dataBean = giphyResponse.getData().get(i);
                    imageUrls.add(dataBean.getId().toString());
                }

                isLoading = false;   // 3
                adapterImage.setmImagesList(imageUrls);
                //recyclerView.scrollToPosition(item_count);

                if(imageUrls.size() %10 != 0)
                {
                    isLastPage = true;
                }


            }

            @Override
            public void onFailure(Call<GiphyResponsePOJO> call, Throwable t) {
                // if error occurs in network transaction then we can get the error in this method.
                adapterImage.removeLoadingFooter();  // 2
                isLoading = false;
                Toast.makeText(MainActivity.this, "Please check your internet connectivity..", Toast.LENGTH_LONG).show();
                //progressDialog.dismiss(); //dismiss progress dialog
            }
        });
    }

    private void initializeRecyclerView(ArrayList<String> imageUrls) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
        recyclerView.setAdapter(adapterImage);
        loadStickers(0);
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                if(searchBox.getText().toString().length() > 0)
                    loadNextSearchPage(searchBox.getText().toString());
                else
                    loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //loadFirstPage();
    }


    private void loadFirstPage() {
        progressBar.setVisibility(View.INVISIBLE);
        loadStickers(adapterImage.getItemCount());
        if (currentPage <= TOTAL_PAGES) adapterImage.addLoadingFooter();
        else isLastPage = true;
    }

    private void loadNextPage() {
        loadStickers(adapterImage.getItemCount());
        //adapterImage.addAll(stickers_array);   // 4

        if (currentPage == TOTAL_PAGES)
            isLastPage = true;   // 5
        //else adapterImage.addLoadingFooter();
    }

    private void loadNextSearchPage(String keyword) {

        fetchSearchStickers(keyword,adapterImage.getItemCount());
        if (currentPage == TOTAL_PAGES)
            isLastPage = true;   // 5
    }

    private void resetPaging()
    {
        isLoading = false;
        isLastPage = false;
        currentPage = PAGE_START;
        imageUrls.clear();
        adapterImage.setmImagesList(imageUrls);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));*/
        recyclerView.setAdapter(adapterImage);
    }

    private void performSearch(String keyword)
    {
        resetPaging();
        hideKeyboard(this);
        if(keyword != null && keyword.length() > 0)
            fetchSearchStickers(keyword,adapterImage.getItemCount());
        else
            loadStickers(adapterImage.getItemCount());
    }

    private void fetchSearchStickers(String keyword, int item_count)
    {
        adapterImage.addLoadingFooter();

        (Api.getClient().getStickersList("search",item_count,keyword)).enqueue(new Callback<GiphyResponsePOJO>() {
            @Override
            public void onResponse(Call<GiphyResponsePOJO> call, Response<GiphyResponsePOJO> response) {
                GiphyResponsePOJO giphyResponse = response.body();

                adapterImage.removeLoadingFooter();  // 2

                for(int i=0;i<giphyResponse.getData().size();i++) {
                    GiphyResponsePOJO.DataBean dataBean = giphyResponse.getData().get(i);
                    imageUrls.add(dataBean.getId().toString());
                }

                isLoading = false;   // 3
                adapterImage.setmImagesList(imageUrls);
                if(imageUrls.size() %10 != 0)
                {
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<GiphyResponsePOJO> call, Throwable t) {
                // if error occurs in network transaction then we can get the error in this method.
                adapterImage.removeLoadingFooter();  // 2
                isLoading = false;
                Toast.makeText(MainActivity.this, "Please check your internet connectivity..", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{INTERNET, ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
        //ActivityCompat.requestPermissions(this, new String[]{INTERNET, ACCESS_NETWORK_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
}
