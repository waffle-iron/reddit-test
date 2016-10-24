package com.prueba.rappi;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    JSONArray redit = null;
    List<Post> listadoPost = new ArrayList<Post>();

    DbHelper mDbHelper = new DbHelper(this);

    public static final Map<String, Post> ITEM_MAP = new HashMap<String, Post>();

    private final String URL_TEMPLATE = "https://www.reddit.com/reddits.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        new JSONParse().execute();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ItemListActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject datos = null;
            String raw = LeerUrl.leerContenidos(URL_TEMPLATE);

            try {
                datos = new JSONObject(raw).getJSONObject("data");
            } catch (Exception e) {
                Log.e("Obtener Posts: ", e.toString());
            }

            return datos;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            try {
                if (json != null) {
                    db.delete(DbHelper.PostEntry.TABLE_NAME, null, null);
                    redit = json.getJSONArray("children");

                    for (int i = 0; i < redit.length(); i++) {
                        JSONObject cur = redit.getJSONObject(i).getJSONObject("data");

                        Post p = new Post();
                        p.setId(cur.optString("id"));
                        p.setTitulo(cur.optString("title"));
                        p.setDesc_corta(cur.optString("public_description"));
                        p.setDescripcion(cur.optString("description_html"));
                        p.setImagen(cur.optString("header_img"));


                        if (p.getTitulo() != null) {
                            listadoPost.add(p);
                            ITEM_MAP.put(p.getId(), p);

                            ContentValues values = new ContentValues();
                            values.put(DbHelper.PostEntry.COLUMN_NAME_TITULO, p.getTitulo());
                            values.put(DbHelper.PostEntry.COLUMN_NAME_CORTA, p.getDesc_corta());
                            values.put(DbHelper.PostEntry.COLUMN_NAME_LARGA, p.getDescripcion());
                            values.put(DbHelper.PostEntry.COLUMN_NAME_IMAGEN, p.getImagen());

                            db.insert(DbHelper.PostEntry.TABLE_NAME, null, values);
                        }
                    }
                } else {
                    String[] pTabla = {
                            DbHelper.PostEntry._ID,
                            DbHelper.PostEntry.COLUMN_NAME_TITULO,
                            DbHelper.PostEntry.COLUMN_NAME_CORTA,
                            DbHelper.PostEntry.COLUMN_NAME_LARGA,
                            DbHelper.PostEntry.COLUMN_NAME_IMAGEN
                    };

                    Cursor cursor = db.query(DbHelper.PostEntry.TABLE_NAME, pTabla, null, null, null, null, null);

                    String array[] = new String[cursor.getCount()];
                    int i = 0;

                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        array[i] = cursor.getString(1);

                        Post p = new Post();
                        p.setId(cursor.getString(0));
                        p.setTitulo(cursor.getString(1));
                        p.setDesc_corta(cursor.getString(2));
                        p.setDescripcion(cursor.getString(3));
                        p.setImagen(cursor.getString(4));

                        listadoPost.add(p);
                        ITEM_MAP.put(p.getId(), p);

                        i++;
                        cursor.moveToNext();
                    }
                }

                View recyclerView = findViewById(R.id.item_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(listadoPost));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Post> mValues;

        public SimpleItemRecyclerViewAdapter(List<Post> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mImagen.setImageDrawable(mValues.get(position).getImagen());
            holder.mContentView.setText(mValues.get(position).getTitulo());
            holder.mDetalles.setText(Html.fromHtml(Html.fromHtml(mValues.get(position).getDesc_corta()).toString()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public final TextView mDetalles;
            public final LoaderImageView mImagen;
            public Post mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImagen = (LoaderImageView) view.findViewById(R.id.loaderImageView);
                mContentView = (TextView) view.findViewById(R.id.content);
                mDetalles = (TextView) view.findViewById(R.id.detalles);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
