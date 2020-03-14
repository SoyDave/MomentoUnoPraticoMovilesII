package com.example.mylistviewcrudsqlite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;

    ImageView imageViewIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Lista de Tarjetas");


        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row,mList);
        mListView.setAdapter(mAdapter);

        //get all data from sqlite
        final Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String tarjeta = cursor.getString(1);
            String vencimiento = cursor.getString(2);
            String cupo = cursor.getString(3);
            String deuda = cursor.getString(4);
            String nombre = cursor.getString(5);
            String franquicia = cursor.getString(6);
            byte[] image = cursor.getBlob(7);
            //add to list
            mList.add(new Model(id, tarjeta, vencimiento, cupo, deuda, nombre, franquicia, image));
        }
        mAdapter.notifyDataSetChanged();
        if (mList.size()==0){
            //if there is no record in table of database which means listview is empty
            Toast.makeText(this, "No se encontro ningún registro...", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alert dialog to display option of update and delete
                final CharSequence[] items = {"Actualizar", "Eliminar"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Elige una acción");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            //update
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this, arrID.get(position));
                        }
                        if (i==1) {
                            //delete
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID =new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;

            }
        });
    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("¡Espera!");
        dialogDelete.setMessage("¿Estás seguro de eliminar?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "¡Eliminado con Exito!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate (final Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Actualizar");

        imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtTarjeta = dialog.findViewById(R.id.edtTarjeta);
        final EditText edtVencimiento = dialog.findViewById(R.id.edtVencimiento);
        final EditText edtCupo = dialog.findViewById(R.id.edtCupo);
        final EditText edtDeuda = dialog.findViewById(R.id.edtDeuda);
        final EditText edtNombre = dialog.findViewById(R.id.edtNombre);
        final EditText edtFranquicia = dialog.findViewById(R.id.edtFranquicia);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        //get data of row clicked from sqlite
        final Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String tarjeta = cursor.getString(1);
            edtTarjeta.setText(tarjeta); //set name to update dialog
            String vencimiento = cursor.getString(2);
            edtVencimiento.setText(vencimiento);//set age
            String cupo = cursor.getString(3);
            edtCupo.setText(cupo);//set cupo
            String deuda = cursor.getString(4);
            edtDeuda.setText(deuda);//set deuda
            String nombre = cursor.getString(5);
            edtNombre.setText(nombre);//set Phone
            String franquicia = cursor.getString(6);
            edtFranquicia.setText(franquicia);//set cupo
            byte[] image = cursor.getBlob(7);
            //set image got from sqlite
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            //add to list
            mList.add(new Model(id, tarjeta, vencimiento, cupo, deuda, nombre, franquicia, image));
        }

        //set width of dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);

        //set height of dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        //in update dialog click image view to update image
        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check external storage permission
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.mSQLiteHelper.updateData(
                            edtTarjeta.getText().toString().trim(),
                            edtVencimiento.getText().toString().trim(),
                            edtCupo.getText().toString().trim(),
                            edtDeuda.getText().toString().trim(),
                            edtNombre.getText().toString().trim(),
                            edtFranquicia.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewIcon),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "¡Actualizado con Exito!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error){
                    Log.e("Error al actualizar", error.getMessage());
                }
                updateRecordList ();

            }
        });

    }

    private void updateRecordList() {
        //get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String tarjeta = cursor.getString(1);
            String vencimiento = cursor.getString(2);
            String cupo = cursor.getString(3);
            String deuda = cursor.getString(4);
            String nombre = cursor.getString(5);
            String franquicia = cursor.getString(6);
            byte[] image = cursor.getBlob(7);

            mList.add(new Model(id, tarjeta, vencimiento, cupo, deuda, nombre, franquicia, image));
        }
        mAdapter.notifyDataSetChanged();
    }

    //Copy of Main Activity

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return  byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 888) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //galery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
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

        if (requestCode == 888 && resultCode == RESULT_OK){
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
                imageViewIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
