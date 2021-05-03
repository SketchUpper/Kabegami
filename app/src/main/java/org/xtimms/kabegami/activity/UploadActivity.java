package org.xtimms.kabegami.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.model.CategoryItem;
import org.xtimms.kabegami.model.WallpaperItem;
import org.xtimms.kabegami.model.vision.ComputerVision;
import org.xtimms.kabegami.model.vision.URLUpload;
import org.xtimms.kabegami.remote.IComputerVision;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    ImageView imageView;
    Button btn_upload, btn_browse, btn_submit;
    MaterialSpinner spinner;

    Map<String, String> spinnerData = new HashMap<>();

    private Uri filePath;
    String categoryIdSelect = "", directUrl = "", nameOfFile = "";

    FirebaseStorage storage;
    StorageReference storageReference;

    IComputerVision mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mService = Common.getComputerVisionAPI();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageView = findViewById(R.id.image);
        btn_browse = findViewById(R.id.btn_browse);
        btn_upload = findViewById(R.id.btn_upload);
        btn_submit = findViewById(R.id.btn_submit);
        spinner = findViewById(R.id.spinner);

        loadCategoriesToSpinner();

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedIndex() == 0) {
                    Toast.makeText(UploadActivity.this, "Please, choose category", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectAdultContent(directUrl);
            }
        });

    }

    private void detectAdultContent(String directUrl) {
        if (directUrl.isEmpty()) {
            Toast.makeText(this, "Picture not uploaded", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Analyzing...");
            progressDialog.show();

            mService.analyzeImage(Common.getAPIAdultEndPoint(), new URLUpload(directUrl))
                    .enqueue(new Callback<ComputerVision>() {
                        @Override
                        public void onResponse(Call<ComputerVision> call, Response<ComputerVision> response) {
                            if (response.isSuccessful()) {
                                if (!response.body().getAdult().isAdultContent()) {
                                    progressDialog.dismiss();
                                    saveUrlToCategory(categoryIdSelect, directUrl);
                                    Toast.makeText(UploadActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    deleteFileFromStorage(nameOfFile);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ComputerVision> call, Throwable t) {
                            Toast.makeText(UploadActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteFileFromStorage(String nameOfFile) {
        storageReference.child("images/" + nameOfFile)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadActivity.this, "Your image is an adult content and will be deleted from storage", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            nameOfFile = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child(new StringBuilder("images/").append(nameOfFile).toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            taskSnapshot.getStorage()
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            directUrl = uri.toString();
                                            btn_submit.setEnabled(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                        }
                    });
        }
    }

    private void saveUrlToCategory(String categoryIdSelect, String imageLink) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .push()
                .setValue(new WallpaperItem(imageLink, categoryIdSelect))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                btn_upload.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture: "), Common.PICK_IMAGE_REQUEST);
    }

    private void loadCategoriesToSpinner() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_CATEGORY_BACKGROUND)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshost:snapshot.getChildren()) {
                            CategoryItem item = postSnapshost.getValue(CategoryItem.class);
                            String key = postSnapshost.getKey();

                            spinnerData.put(key, item.getName());
                        }
                        Object[] valueArray = spinnerData.values().toArray();
                        List<Object> valueList = new ArrayList<>();
                        valueList.add(0, "Category");
                        valueList.addAll(Arrays.asList(valueArray));
                        spinner.setItems(valueList);
                        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                Object[] keyArray = spinnerData.keySet().toArray();
                                List<Object> keyList = new ArrayList<>();
                                keyList.add(0, "Category_Key");
                                keyList.addAll(Arrays.asList(keyArray));
                                categoryIdSelect = keyList.get(position).toString();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        deleteFileFromStorage(nameOfFile);
        super.onBackPressed();
    }

}