package com.aryvart.uticianvender.user;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.customgallery.AlbumImages;
import com.aryvart.uticianvender.customgallery.AlbumsModel;
import com.aryvart.uticianvender.customgallery.GalleryAlbumActivity;
import com.aryvart.uticianvender.customgallery.ShowAlbumImagesAdapter;
import com.aryvart.uticianvender.customgallery.itemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowAlbumImagesActivity extends AppCompatActivity implements ShowAlbumImagesAdapter.ViewHolder.ClickListener {

    public static final String BROADCAST_ACTION = "com.aryvart.multiselect";
    public ArrayList<Uri> mShareImages = new ArrayList<Uri>();
    Context context;
    Intent intent;
    private Toolbar toolbar;

    //    private List<Student> studentList;
    private RecyclerView mRecyclerView;
    private ShowAlbumImagesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //    private ActionModeCallback actionModeCallback = new ActionModeCallback();
//    private ActionMode actionMode;
    private Button btnSelection;
    private ArrayList<AlbumsModel> albumsModels;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_show_gallery_images);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            btnSelection = (Button) findViewById(R.id.btnShow);
            context = this;

            intent = new Intent(BROADCAST_ACTION);



            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Album Images");

            }
            mPosition = (int) getIntent().getIntExtra("position", 0);
            albumsModels = (ArrayList<AlbumsModel>) getIntent().getSerializableExtra("albumsList");
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            itemDecoration itemDecoration = new itemDecoration(10);
            mRecyclerView.addItemDecoration(itemDecoration);

            // create an Object for Adapter
            mAdapter = new ShowAlbumImagesAdapter(ShowAlbumImagesActivity.this, getAlbumImages(), this);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);

            btnSelection.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String data = "";
                    ArrayList<String> alPath = new ArrayList<String>();
                    List<AlbumImages> stList = ((ShowAlbumImagesAdapter) mAdapter)
                            .getAlbumImagesList();


                    //Send Broadcast intent from here........
                    if (mShareImages.size() > 0) {

                        int nCount = mShareImages.size();
                        for (int i = 0; i < nCount; i++) {
                            AlbumImages singleStudent = stList.get(i);
                            data = data + "\n" + singleStudent.getAlbumImages();
                            Log.i("TG", "Values : " + data);
                            alPath.add(singleStudent.getAlbumImages());
                        }

                        intent.putExtra("data", alPath.toString());
                        sendBroadcast(intent);
                        finish();

                    }



                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Uri> getShareImages() {
        ArrayList<Uri> uris = null;
        try {
            uris = mShareImages;
            for (int i = 0; i < uris.size(); i++) {
                Uri uri = uris.get(i);
                String path = uri.getPath();
                File imageFile = new File(path);
                uri = getImageContentUri(imageFile);
                uris.set(i, uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uris;
    }

    private Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return this.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    private ArrayList<AlbumImages> getAlbumImages() {
        ArrayList<AlbumImages> paths = null;
        try {
            Object[] abc = albumsModels.get(mPosition).folderImages.toArray();

            Log.i("imagesLength", "" + abc.length);
            paths = new ArrayList<AlbumImages>();
            int size = abc.length;
            for (int i = 0; i < size; i++) {

                AlbumImages albumImages = new AlbumImages();
                albumImages.setAlbumImages((String) abc[i]);
                paths.add(albumImages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;

    }

    @Override
    public void onItemClicked(int position) {

        toggleSelection(position);

    }

    @Override
    public boolean onItemLongClicked(int position) {



        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        try {
            int count = mAdapter.getSelectedItemCount();


            if (count < 10) {
                mAdapter.toggleSelection(position);
                Log.i("string path", "" + mAdapter.getAlbumImagesList().get(position).getAlbumImages());

                Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
                String path = uriPath.getPath();
                File imageFile = new File(path);
                Uri uri = getImageContentUri(imageFile);

                if (mAdapter.isSelected(position)) {
                    mShareImages.add(uri);
                } else {
                    mShareImages.remove(uri);
                }
                Log.i("uri path", "" + mShareImages);
            } else {
                Toast.makeText(ShowAlbumImagesActivity.this, "You reached your limit...!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ShowAlbumImagesActivity.this, GalleryAlbumActivity.class);
        startActivity(i);
        finish();
    }
}