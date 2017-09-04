package com.aryvart.uticianvender.customgallery;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowAlbumImagesActivity extends AppCompatActivity implements ShowAlbumImagesAdapter.ViewHolder.ClickListener {

    public static final String BROADCAST_ACTION = "com.aryvart.multiselect";
    public ArrayList<Uri> mShareImages = new ArrayList<Uri>();
    public ArrayList<String> mShareImagesFile = new ArrayList<String>();
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
    String strtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery_images);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnSelection = (Button) findViewById(R.id.btnShow);
        context = this;

        intent = new Intent(BROADCAST_ACTION);

//        studentList = new ArrayList<Student>();

//        for (int i = 1; i <= 15; i++) {
//            Student st = new Student("Student " + i, "androidstudent" + i
//                    + "@gmail.com", false);
//
//            studentList.add(st);
//        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Album Images");

        }
        mPosition = (int) getIntent().getIntExtra("position", 0);
        albumsModels = (ArrayList<AlbumsModel>) getIntent().getSerializableExtra("albumsList");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        strtype = getIntent().getStringExtra("from");
        Log.i("SH", "strtype---" + strtype);
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

/*
                for (int i = 0; i < stList.size(); i++) {
                    AlbumImages singleStudent = stList.get(i);
                    data = data + "\n" + singleStudent.getAlbumImages();
                    Log.i("TG", "Values : " + data);
                    alPath.add(singleStudent.getAlbumImages());

                }*/

                  /*
                Toast.makeText(ShowAlbumImagesActivity.this,
                        "Selected Students: \n" + data, Toast.LENGTH_LONG)
                        .show();*/

                //Send Broadcast intent from here........
                if (mShareImages.size() > 0) {

                   /* int nCount = mShareImages.size();
                    for (int i = 0; i < nCount; i++) {
                        AlbumImages singleStudent = stList.get(i);
                        data = data + "\n" + singleStudent.getAlbumImages();
                        Log.i("TG", "Values : " + data);
                        alPath.add(singleStudent.getAlbumImages());
                    }*/

                    Log.i("SRVN", "mShareImagesFile Size   : " + mShareImagesFile.size());
                    Log.i("SRVN", "mShareImagesFile Data  : " + mShareImagesFile.toString());
                    int nCount = mShareImages.size();
                    for (int i = 0; i < nCount; i++) {
                        alPath.add(mShareImagesFile.get(i));
                    }

                    Log.i("SRVN", "alPath Size  : " + alPath.size());
                    Log.i("SRVN", "alPath Data  : " + alPath.toString());

                    intent.putExtra("data", alPath.toString());
                    sendBroadcast(intent);
                    finish();

                }



                /*Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, 5);
                intent.setType("image*//*"); *//* This example is sharing jpeg images. *//*
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mShareImages);
                startActivity(intent);*/

            }
        });

    }

    private ArrayList<Uri> getShareImages() {
        ArrayList<Uri> uris = mShareImages;
        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            String path = uri.getPath();
            File imageFile = new File(path);
            uri = getImageContentUri(imageFile);
            uris.set(i, uri);
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
        Object[] abc = albumsModels.get(mPosition).folderImages.toArray();

        Log.i("imagesLength", "" + abc.length);
        ArrayList<AlbumImages> paths = new ArrayList<AlbumImages>();
        int size = abc.length;
        for (int i = 0; i < size; i++) {

            AlbumImages albumImages = new AlbumImages();
            albumImages.setAlbumImages((String) abc[i]);
            paths.add(albumImages);
        }
        return paths;

    }

    @Override
    public void onItemClicked(int position) {

        toggleSelection(position);

    }

    @Override
    public boolean onItemLongClicked(int position) {

//        if (actionMode == null) {
//            actionMode = startSupportActionMode(actionModeCallback);
//        }

        toggleSelection(position);

        return true;
    }

 /*   private void toggleSelection(int position) {

        int count = mAdapter.getSelectedItemCount();

//        if (count == 0) {
//            actionMode.finish();
//        } else {
//            actionMode.setTitle(String.valueOf(count));
//            actionMode.invalidate();
//        }
//        Log.i ("image path",mAdapter.getAlbumImagesList ().get (position).getAlbumImages ());
//        Utils.copyFileToExternal(ShowAlbumImagesActivity.this,mAdapter.getAlbumImagesList ().get (position).getAlbumImages ());
//
//        File file = new File(Environment.getExternalStorageState ()+"/ZappShare" + "/SharedImages" + mAdapter.getAlbumImagesList ().get (position).getAlbumImages ());
//        Uri uri = Uri.fromFile(file);

        if (count < 12) {
            mAdapter.toggleSelection(position);
            Log.i("string path", "" + mAdapter.getAlbumImagesList().get(position).getAlbumImages());

            Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
            String path = uriPath.getPath();
            File imageFile = new File(path);
            Uri uri = getImageContentUri(imageFile);

            if (mAdapter.isSelected(position)) {
                mShareImages.add(uri);
                mShareImagesFile.add(imageFile.toString());
            } else {
                mShareImages.remove(uri);
                mShareImagesFile.remove(imageFile.toString());
            }
            Log.i("uri path", "" + mShareImages);
            Log.i("File path", "" + mShareImagesFile.toString());
        } else {
            Toast.makeText(ShowAlbumImagesActivity.this, "You reached your limit...!", Toast.LENGTH_SHORT).show();
        }

    }
*/





    private void toggleSelection(int position) {

        int count = mAdapter.getSelectedItemCount();

        Log.i("SH", "strtype" + strtype);
        if(strtype.equalsIgnoreCase("gallery")) {

            if (count <= 11) {
                mAdapter.toggleSelection(position);
                Log.i("string path", "" + mAdapter.getAlbumImagesList().get(position).getAlbumImages());

                Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
                String path = uriPath.getPath();
                File imageFile = new File(path);
                Uri uri = getImageContentUri(imageFile);

                if (mAdapter.isSelected(position)) {
                    mShareImages.add(uri);
                    mShareImagesFile.add(imageFile.toString());
                } else {
                    mShareImages.remove(uri);
                    mShareImagesFile.remove(imageFile.toString());
                }
                Log.i("uri path", "" + mShareImages);
                Log.i("File path", "" + mShareImagesFile.toString());
            } else {
                if (count > 11) {
                    if (mAdapter.isSelected(position)) {
                        mAdapter.toggleSelection(position);

                        Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
                        String path = uriPath.getPath();
                        File imageFile = new File(path);
                        Uri uri = getImageContentUri(imageFile);


                        mShareImages.remove(uri);
                        mShareImagesFile.remove(imageFile.toString());

                    } else {
                        Toast.makeText(ShowAlbumImagesActivity.this, "You reached your limit...!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }

       else {

            if (count <= 0) {
                mAdapter.toggleSelection(position);
                Log.i("string path", "" + mAdapter.getAlbumImagesList().get(position).getAlbumImages());

                Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
                String path = uriPath.getPath();
                File imageFile = new File(path);
                Uri uri = getImageContentUri(imageFile);

                if (mAdapter.isSelected(position)) {
                    mShareImages.add(uri);
                    mShareImagesFile.add(imageFile.toString());
                } else {
                    mShareImages.remove(uri);
                    mShareImagesFile.remove(imageFile.toString());
                }
                Log.i("uri path", "" + mShareImages);
                Log.i("File path", "" + mShareImagesFile.toString());
            } else {
                if (count > 0) {
                    if (mAdapter.isSelected(position)) {
                        mAdapter.toggleSelection(position);

                        Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
                        String path = uriPath.getPath();
                        File imageFile = new File(path);
                        Uri uri = getImageContentUri(imageFile);


                        mShareImages.remove(uri);
                        mShareImagesFile.remove(imageFile.toString());

                    } else {
                        Toast.makeText(ShowAlbumImagesActivity.this, "You reached your limit...!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }







    }


















//    private class ActionModeCallback implements ActionMode.Callback {
//        @SuppressWarnings("unused")
//        private final String TAG = ActionModeCallback.class.getSimpleName();
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_remove:
////                    adapter.removeItems(adapter.getSelectedItems());
//                    mode.finish();
//                    toolbar.setVisibility (View.VISIBLE);
//
//                    return true;
//
//                default:
//                    return false;
//            }
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            mAdapter.clearSelection();
////            actionMode = null;
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ShowAlbumImagesActivity.this, GalleryAlbumActivity.class);
        startActivity(i);
        finish();
    }
}