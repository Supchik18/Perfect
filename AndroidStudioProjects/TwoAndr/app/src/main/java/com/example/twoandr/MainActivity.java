package com.example.twoandr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private  static final int PHOTO_ID = 123;
    private ArrayList<Uri> images = new ArrayList<>();

    private ArrayAdapter<Uri> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button cameraOpen = findViewById(R.id.camera_button);
        ListView imageList = findViewById(R.id.image_list);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, images);
        imageList.setAdapter(adapter);

        loadImagesFromCache();

        cameraOpen.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PHOTO_ID));
        imageList.setOnItemClickListener((parent, view, position, id) -> {
            Uri ImageIri = (Uri) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, FulImageActivity.class);
            intent.putExtra("image", ImageIri.toString());
            startActivity(intent);
        });
    }

    private void loadImagesFromCache() {
        File cacheDir = getApplicationContext().getCacheDir();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                Uri uri = Uri.fromFile(file);
                images.add(uri);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_ID){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri imageUri1 = saveImageStorage(photo);
            if (imageUri1 != null){
                images.add(imageUri1);
                adapter.notifyDataSetChanged();
            }
        }
    }




    private Uri saveImageStorage(Bitmap bitmapImage){
        File outputDirectory = getApplicationContext().getCacheDir();
        File imageFile = new File(outputDirectory, System.currentTimeMillis()+".jpg");

        try (FileOutputStream fileOutputStream = new FileOutputStream(imageFile)) {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            return Uri.fromFile(imageFile);


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}