package org.mobiletrain.cameraandqrcodedemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureMultiSelectActivity extends AppCompatActivity {

    @ViewInject(R.id.gridView) private GridView gridView;

    private List<String> imageCollection;

    private MultiSelectGridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_multi_select);

        x.view().inject(this);

        loadData();
    }

    private void loadData() {
        imageCollection = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.i("Tag", "uri is " + uri);

        String dcimFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";

        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);

                if (picturePath.startsWith(dcimFolder) && new File(picturePath).exists()) {
                    imageCollection.add(0, picturePath);
                }
            }
            cursor.close();

            for (String item : imageCollection) {
                Log.i("Tag", "selected image : " + item);
            }
        }

        if (imageCollection.size() == 0) {
            Toast.makeText(this, "相册中没有任何图片", Toast.LENGTH_LONG).show();
            setResult(0);
            finish();
        }
        else {
            adapter = new MultiSelectGridViewAdapter(this, imageCollection);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    adapter.changeState(i);
                }
            });
        }
    }

    @Event(R.id.btnOK)
    private void btnOK_Click(View view) {
        List<Integer> selectedIndexes = adapter.getSelectedIndexes();

        String[] data = new String[selectedIndexes.size()];
        for (int i=0; i<selectedIndexes.size(); i++) {
            data[i] = imageCollection.get(selectedIndexes.get(i));
        }

        Intent intent = new Intent();
        intent.putExtra("data", data);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Event(R.id.btnCancel)
    private void btnCancel_Click(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
