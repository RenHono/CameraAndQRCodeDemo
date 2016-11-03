package org.mobiletrain.cameraandqrcodedemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.imageView) private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x.view().inject(this);
    }

    private final static int REQUEST_GETPICTUREFROMLIBRARY = 0;
    private final static int REQUEST_GETMULTIPICTUREFROMLIBRARY = 1;
    private final static int REQUEST_GETPICTUREFROMCAMERA = 2;
    private final static int REQUEST_GETPICTUREFROMCAMERA_GETORIIMAGE = 3;
    private final static int REQUEST_GETVIDEOFROMCAMERA = 4;
    private final static int REQUEST_GETAUDIO = 5;

    private final static int REQUEST_SCANCODE = 6;

    @Event(R.id.btnGetPictureFromLibrary)
    private void btnGetPictureFromLibrary_Click(View view) {
        // intent.setClass(this, TargetActivity.class);
        // intent.setAction("actionName");

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GETPICTUREFROMLIBRARY);
    }

    @Event(R.id.btnGetMultiPicturesFromLibrary)
    private void btnGetMultiPicturesFromLibrary_Click(View v) {
        Intent intent = new Intent();
        intent.setClass(this, PictureMultiSelectActivity.class);

        startActivityForResult(intent, REQUEST_GETMULTIPICTUREFROMLIBRARY);
    }

    @Event(R.id.btnGetPictureFromCamera)
    private void btnGetPictureFromCamera_Click(View view) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        this.startActivityForResult(intent, REQUEST_GETPICTUREFROMCAMERA);
    }

    @Event(R.id.btnGetOriPictureFromCamera)
    private void btnGetOriPictureFromCamera_Click(View view) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/temp1603.jpg";
        File outputFile = new File(fileName);
        Log.i("Tag", "Output FileName is " + fileName);
        Uri uri = Uri.fromFile(outputFile);

        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        this.startActivityForResult(intent, REQUEST_GETPICTUREFROMCAMERA_GETORIIMAGE);
    }

    @Event(R.id.btnGetVideoFromCamera)
    private void btnGetVideoFromCamera_Click(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, REQUEST_GETVIDEOFROMCAMERA);
    }

    @Event(R.id.btnGetAudio)
    private void btnGetAudio_Click(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr");
        startActivityForResult(intent, REQUEST_GETAUDIO);
    }

    @Event(R.id.btnScanCode)
    private void btnScanCode_Click(View view) {
        Intent intent = new Intent();
        intent.setClass(this, BarcodeScanActivity.class);
        startActivityForResult(intent, REQUEST_SCANCODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Tag", "requestCode is " + requestCode + ", resultCode is " + resultCode);

        switch (requestCode) {
            case REQUEST_GETPICTUREFROMLIBRARY:
                handlerGetImageFromLibraryResult(resultCode, data);
                break;
            case REQUEST_GETMULTIPICTUREFROMLIBRARY:
                handlerGetMultiPictureFromCamera(resultCode, data);
                break;
            case REQUEST_GETPICTUREFROMCAMERA:
                handlerGetPictureFromCamera(resultCode, data);
                break;
            case REQUEST_GETPICTUREFROMCAMERA_GETORIIMAGE:
                handlerGetOriPictureFromCamera(resultCode, data);
                break;
            case REQUEST_GETVIDEOFROMCAMERA:
                handlerGetVideoFromCamera(resultCode, data);
                break;
            case REQUEST_GETAUDIO:
                handlerGetAudio(resultCode, data);
                break;
            case REQUEST_SCANCODE:
                handlerScanCode(resultCode, data);
                break;
        }
    }

    private void handlerGetImageFromLibraryResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Log.i("Tag", "Return Uri is " + selectedImage.toString());

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Log.i("Tag", "Selected Image path is " + picturePath);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }
    }

    private void handlerGetMultiPictureFromCamera(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String[] selectedPictureFileNames = data.getStringArrayExtra("data");
            if (selectedPictureFileNames == null || selectedPictureFileNames.length == 0) {
                Toast.makeText(this, "没有选择任何的照片", Toast.LENGTH_LONG).show();
            }
            else {
                Log.i("Tag", "选择的图片有：");
                for (String item : selectedPictureFileNames) {
                    Log.i("Tag", "    " + item);
                }
            }
        }
    }

    private void handlerGetPictureFromCamera(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bitmap bm_camera = (Bitmap) data.getExtras().get("data");
            if (bm_camera != null) {
                Log.i("Tag", "Width is " + bm_camera.getWidth() +
                        ", Height is " + bm_camera.getHeight());

                imageView.setImageBitmap(bm_camera);
            }
        }
    }

    private void handlerGetOriPictureFromCamera(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/temp1603.jpg";

            Bitmap bmp = BitmapFactory.decodeFile(outputFile);
            imageView.setImageBitmap(bmp);
        }
    }

    private void handlerGetVideoFromCamera(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uriVideo = data.getData();
            Cursor c=this.getContentResolver().query(uriVideo, null, null, null, null);
            if (c != null && c.moveToNext()) {
                /* _data：文件的绝对路径 ，_display_name：文件名 */
                String strVideoPath = c.getString(c.getColumnIndex("_data"));
                Log.i("Tag", "Video File " + strVideoPath);
            }
        }
    }

    private void handlerGetAudio(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uriRecorder = data.getData();
            Cursor c2=this.getContentResolver().query(uriRecorder, null, null, null, null);
            if (c2 != null && c2.moveToNext()) {
                /* _data：文件的绝对路径 ，_display_name：文件名 */
                String strRecorderPath = c2.getString(c2.getColumnIndex("_data"));
                Log.i("Audio", "Audio File " + strRecorderPath);
            }
        }
    }

    private void handlerScanCode(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String resultText = data.getStringExtra("data");
            Log.i("Tag", resultText);
            Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
        }
    }
}
