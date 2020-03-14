package com.example.mylistviewcrudsqlite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText mEdtTarjeta, mEdtVencimiento, mEdtCupo, mEdtDeuda, mEdtNombre, mEdtFranquicia;
    Button mBtnAdd, mBtnList;
    ImageView mImageView;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Nuevo Registro");

        mEdtTarjeta = findViewById(R.id.edtTarjeta);
        mEdtVencimiento = findViewById(R.id.edtVencimiento);
        mEdtCupo = findViewById(R.id.edtCupo);
        mEdtDeuda = findViewById(R.id.edtDeuda);
        mEdtNombre = findViewById(R.id.edtNombre);
        mEdtFranquicia = findViewById(R.id.edtFranquicia);

        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnList = findViewById(R.id.btnList);

        mImageView = findViewById(R.id.ImageView);

        //creating database
        mSQLiteHelper = new SQLiteHelper(this,"RECORDDB.sqlite", null,1);

        //creating table in database
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD (id INTEGER PRIMARY KEY AUTOINCREMENT, tarjeta VARCHAR, vencimiento VARCHAR, cupo VARCHAR, deuda VARCHAR, nombre VARCHAR, franquicia VARCHAR, image BLOB)");



        //select Image by on Imageview click
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read external storage permission to select image from gallery
                //runtime permission for devices android 6.0 and above
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );

            }
        });

        //add record to sqlite

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSQLiteHelper.insertData(
                            mEdtTarjeta.getText().toString().trim(),
                            mEdtVencimiento.getText().toString().trim(),
                            mEdtCupo.getText().toString().trim(),
                            mEdtDeuda.getText().toString().trim(),
                            mEdtNombre.getText().toString().trim(),
                            mEdtFranquicia.getText().toString().trim(),
                            imageViewToByte(mImageView)
                    );
                    Toast.makeText(MainActivity.this,"¡Registro Exitoso!", Toast.LENGTH_SHORT).show();
                    //reset views
                    mEdtTarjeta.setText("");
                    mEdtVencimiento.setText("");
                    mEdtCupo.setText("");
                    mEdtDeuda.setText("");
                    mEdtNombre.setText("");
                    mEdtFranquicia.setText("");
                    mImageView.setImageResource(R.drawable.addphoto);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        //show redord list
        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start recordlist activity
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });

    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return  byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //galery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(this,"No se concedió acceso al almacenamiento del dispositivo...", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
            .setAspectRatio(1,1) //image will be square
            .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //set image choosed from galley to image view
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
