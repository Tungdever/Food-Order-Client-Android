package com.uteating.foodapp.activity.Home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.custom.CustomMessageBox.CustomAlertDialog;
import com.uteating.foodapp.custom.CustomMessageBox.FailToast;
import com.uteating.foodapp.custom.CustomMessageBox.SuccessfulToast;
import com.uteating.foodapp.databinding.ActivityEditProfileBinding;
import com.uteating.foodapp.model.User;


import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private DatePickerDialog datePickerDialog;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imageUrl;
    private User user;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));

        Intent intent = getIntent();

        user = (User) intent.getSerializableExtra("user");

        initToolbar();

        initImagePickerActivity();

        getInfo();

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        binding.changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        binding.fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBinding();
            }
        });

        binding.phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateBinding();
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });
    }

    private void updateInfo() {
        String fullNameTxt = binding.fullName.getText().toString().trim();
        String phoneNumberTxt = binding.phoneNumber.getText().toString().trim();

        if (fullNameTxt.equals("")) {
            new FailToast(this, "Full name must not be empty!").showToast();
            return;
        }

        if (phoneNumberTxt.equals("")) {
            new FailToast(this, "Phone number must not be empty!").showToast();
            return;
        }

        user.setFullName(binding.fullName.getText().toString());
        user.setPhoneNumber(binding.phoneNumber.getText().toString());

        apiService =  RetrofitClient.getRetrofit().create(APIService.class);
        apiService.updateUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    new SuccessfulToast(EditProfileActivity.this, "Updated successfully!").showToast();
                } else {
                    Log.d("updateUser", response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("updateUserFailure", t.getMessage());
            }
        });
        finish();
    }

    private void deleteOldImage() {
        if (!Objects.equals(imageUrl, "")) {
            FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete();
        }
    }

    private void initImagePickerActivity() {
        // Init launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Uri imageUri = result.getData().getData();

                uploadImage(imageUri);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        imagePickerLauncher.launch(intent);
    }

    private void getInfo() {
        Glide.with(getApplicationContext()).load(user.getAvatarURL()).placeholder(R.drawable.default_avatar).into(binding.profileImage);
        binding.fullName.setText(user.getFullName());
        binding.userName.setText(user.getUserName());
        binding.email.setText(user.getEmail());
        binding.phoneNumber.setText(user.getPhoneNumber());
        imageUrl = user.getAvatarURL() != null ? user.getAvatarURL() : "";
    }

    void uploadImage(Uri imageUri) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Users").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            deleteOldImage();

                            imageUrl = uri.toString();
                            Glide.with(EditProfileActivity.this).load(imageUrl).placeholder(R.drawable.profile_image).into(binding.profileImage);

                            pd.dismiss();

                            binding.update.setEnabled(true);
                        }
                    });
                }
            });
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.update.isEnabled()) {
                    new CustomAlertDialog(EditProfileActivity.this,"Save changes?");
                    CustomAlertDialog.binding.btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.alertDialog.dismiss();
                            updateInfo();
                        }
                    });
                    CustomAlertDialog.binding.btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.alertDialog.dismiss();
                            finish();
                        }
                    });
                    CustomAlertDialog.showAlertDialog();
                }
                else {
                    finish();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }


    void updateBinding() {
        String fullName = binding.fullName.getText().toString();
        String phoneNumber = binding.phoneNumber.getText().toString();

        boolean isFullNameChanged = user.getFullName() != null ? !user.getFullName().equals(fullName) : fullName != null;
        boolean isPhoneNumberChanged = user.getPhoneNumber() != null ? !user.getPhoneNumber().equals(phoneNumber) : phoneNumber != null;

        binding.update.setEnabled(isFullNameChanged || isPhoneNumberChanged);
    }
}
