package com.example.html5.newui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.test_recycler_view);
        setRecyclerView();
    }

    private void setRecyclerView() {
        List<String> stringList = loadRecyclerViewData();
        mRecyclerView.setAdapter(new MyAdapter(stringList));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<String> loadRecyclerViewData() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            stringList.add("this is a simulate data"+i);
        }
        return stringList;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        private List<String> mStringList;

        public MyAdapter(List<String> stringList) {
            if (stringList == null) {
                mStringList = new ArrayList<>();
            } else {
                mStringList = stringList;
            }
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_simple_text, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.onBinder(mStringList.get(position));
        }

        @Override
        public int getItemCount() {
            return mStringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            private View mView;
            private final TextView mTextView;

            public MyHolder(View itemView) {
                super(itemView);
                Log.w(TAG, "MyHolder: " );

                mView = itemView;
                mTextView = (TextView) mView.findViewById(R.id.test_text_view);
            }

            public void onBinder(String data){
                Log.w(TAG, "onBinder: " );

                mTextView.setText(data);
            }
        }
    }
}
