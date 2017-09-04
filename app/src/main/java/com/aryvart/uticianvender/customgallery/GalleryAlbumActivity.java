package com.aryvart.uticianvender.customgallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.aryvart.uticianvender.R;

import java.util.ArrayList;

public class GalleryAlbumActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private GalleryAlbumAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

//    private List<Student> studentList;

    //  Button btnSelection;

    private ArrayList<AlbumsModel> albumsModels;
    Context context;
    String strtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_albums);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        //   btnSelection = (Button) findViewById(R.id.btnShow);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Album Thumbnail Images");

        }
        strtype = getIntent().getStringExtra("from");
        Log.i("SH", "strtype--from--" + strtype);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemDecoration itemDecoration = new itemDecoration(10);
        mRecyclerView.addItemDecoration(itemDecoration);


        // create an Object for Adapter
        mAdapter = new GalleryAlbumAdapter(GalleryAlbumActivity.this, getGalleryAlbumImages());

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);

      /*  btnSelection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String data = "";
                List<AlbumsModel> stList = ((GalleryAlbumAdapter) mAdapter)
                        .getGalleryImagesList ();

                for (int i = 0; i < stList.size(); i++) {
                    AlbumsModel singleStudent = stList.get(i);
                    if (singleStudent.isSelected() == true) {

                        data = data + "\n" + singleStudent.getFolderName ().toString();
                    }

                }

                Toast.makeText(GalleryAlbumActivity.this,
                        "Selected Students: \n" + data, Toast.LENGTH_LONG)
                        .show();
            }
        });*/

        mAdapter.SetOnItemClickListener(new GalleryAlbumAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                // do something with position


                Intent galleryAlbumsIntent = new Intent(GalleryAlbumActivity.this, ShowAlbumImagesActivity.class);
                galleryAlbumsIntent.putExtra("position", position);
                galleryAlbumsIntent.putExtra("from",strtype);

                galleryAlbumsIntent.putExtra("albumsList", getGalleryAlbumImages());
                startActivity(galleryAlbumsIntent);
                finish();
            }
        });

    }

    private ArrayList<AlbumsModel> getGalleryAlbumImages() {
        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, orderBy + " DESC");
        albumsModels = Utils.getAllDirectoriesWithImages(imagecursor);
        return albumsModels;

    }

}