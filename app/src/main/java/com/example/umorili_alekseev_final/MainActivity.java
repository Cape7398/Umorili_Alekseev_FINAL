package com.example.umorili_alekseev_final;


import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Call;



public class MainActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    List<UPost> mPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mPosts = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        UmoriliAdapter adapter = new UmoriliAdapter(mPosts);
        mRecyclerView.setAdapter(adapter);
        UmoriliService umoriliService = UmoriliService.retrofit.create(UmoriliService.class);
        final Call<List<UPost>> call = UmoriliService.getData("bash", 50);

        call.enqueue((new Callback<List<UPost>>() {
            @Override
            public void onResponse(Call<List<UPost>> call, Response<List<UPost>> response) {
                // response.isSuccessfull() возвращает true если код ответа 2xx
                if (response.isSuccessful()) {
                    // Выводим массив имён
                    mPosts.addAll(response.body());
                    mRecyclerView.getAdapter().notifyDataSetChanged();

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    // Обрабатываем ошибку
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Toast.makeText(MainActivity.this, errorBody.string(),
                                Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UPost>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Что то пошло не так", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

        }));
    }
    public class UmoriliAdapter extends RecyclerView.Adapter<UmoriliAdapter.ViewHolder> {
        private List<UPost> posts;

        UmoriliAdapter(List<UPost> posts) {
            this.posts = posts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,
                    false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UPost post = posts.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.postTextView.setText(Html.fromHtml(post.getElementPureHtml(), Html
                        .FROM_HTML_MODE_LEGACY));
            } else {
                holder.postTextView.setText(Html.fromHtml(post.getElementPureHtml()));
            }
        }

        @Override
        public int getItemCount() {
            if (posts == null)
                return 0;
            return posts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView postTextView;

            ViewHolder(View itemView) {
                super(itemView);
                postTextView =  itemView.findViewById(R.id.textView_item_post);
            }
        }
    }
}
