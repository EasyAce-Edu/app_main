package com.jingtao.sortablelistviewondraganddrop;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private MyArrayAdapter mAdapter;
    private ListView mListView;
    private boolean mSortable = false; // ソート中かどうか
    private String mDragString; // ドラッグ中のオブジェクト
    private int mPosition = -1; // ドラッグ位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);

        // いつものコード
        mAdapter = new MyArrayAdapter(this, R.layout.row_string);
        // ダミーデータ
        for (int i = 0; i < 100; i++) {
            mAdapter.add("Dummy ".concat(String.valueOf(i)));
        }

        mListView.setAdapter(mAdapter);

        // 大事な所
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!mSortable) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        // 現在のポジションを取得し
                        int position = mListView.pointToPosition((int) event.getX(), (int) event.getY());
                        if (position < 0) {
                            break;
                        }
                        // 移動が検出されたら入れ替え
                        if (position != mPosition) {
                            mPosition = position;
                            mAdapter.remove(mDragString);
                            mAdapter.insert(mDragString, mPosition);
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE: {
                        stopDrag();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void startDrag(String string) {
        mPosition = -1;
        mSortable = true;
        mDragString = string;
        mAdapter.notifyDataSetChanged(); // ハイライト反映・解除の為
    }

    public void stopDrag() {
        mPosition = -1;
        mSortable = false;
        mDragString = null;
        mAdapter.notifyDataSetChanged(); // ハイライト反映・解除の為
    }

    static class ViewHolder {
        TextView title;
        TextView handle;
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {

        private ArrayList<String> mStrings = new ArrayList<String>();
        private LayoutInflater mInflater;
        private int mLayout;

        public MyArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
        }

        @Override
        public void add(String row) {
            super.add(row);
            mStrings.add(row);
        }

        @Override
        public void insert(String row, int position) {
            super.insert(row, position);
            mStrings.add(position, row);
        }

        @Override
        public void remove(String row) {
            super.remove(row);
            mStrings.remove(row);
        }

        @Override
        public void clear() {
            super.clear();
            mStrings.clear();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(this.mLayout, null);
                assert view != null;
                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.handle = (TextView) view.findViewById(R.id.handle);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final String string = mStrings.get(position);

            holder.title.setText(string);

            // ハンドルタップでソート開始
            holder.handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        startDrag(string);
                        return true;
                    }
                    return false;
                }
            });

            // ドラッグ行のハイライト
            if (mDragString != null && mDragString.equals(string)) {
                view.setBackgroundColor(Color.parseColor("#9933b5e5"));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }

            return view;

        }
    }
}