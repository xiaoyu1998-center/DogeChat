package com.xiaoyu.chat.activities.lr;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.activities.ActivateActivity;
import com.xiaoyu.chat.databinding.ActivityRegisterBinding;
import com.xiaoyu.chat.mail.MailSender;
import com.xiaoyu.chat.mail.Utils;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.OnSingleClickListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String encodedImage;
    private FirebaseFirestore database;
    private String name, email, pwd, cpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
        setInputTextListeners();
    }

    private void init() {
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.backArrow.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        binding.buttonSignUp.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                isValidSignUpDetails();
            }
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        final String verifyCode = getRandomSixDigitNumber();

        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, name);
        user.put(Constants.KEY_EMAIL, email);
        user.put(Constants.KEY_PASSWORD, md5(pwd));
        user.put(Constants.KEY_CODE, verifyCode);
        user.put(Constants.KEY_CODE_EXPIRATION, new Date());
        user.put(Constants.KEY_USER_REGISTER, new Date());
        user.put(Constants.KEY_STATUS, Constants.KEY_PENDING);
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {

                    sendOTPEmail(verifyCode);

                    Intent myIntent = new Intent(getApplicationContext(), ActivateActivity.class);
                    myIntent.putExtra(Constants.GET_USER_ID_BY_VERIFY, documentReference.getId());
                    myIntent.putExtra(Constants.GET_USER_NAME_BY_VERIFY, name);
                    myIntent.putExtra(Constants.GET_USER_EMAIL_BY_VERIFY, email);
                    startActivity(myIntent);
                    finish();

                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void isValidSignUpDetails() {

        if (encodedImage == null) {
            showToast(getString(R.string.upload_image));
        } else {
            if (!name.trim().isEmpty() &&
                    !email.trim().isEmpty() &&
                    !pwd.trim().isEmpty() &&
                    !cpwd.trim().isEmpty()) {
                if (!TextUtils.isEmpty(binding.inputName.getError())) {
                    showToast(getString(R.string.make_sure));
                } else if (!TextUtils.isEmpty(binding.inputEmail.getError())) {
                    showToast(getString(R.string.make_sure));
                } else if (!TextUtils.isEmpty(binding.inputPassword.getError())) {
                    showToast(getString(R.string.make_sure));
                } else if (!TextUtils.isEmpty(binding.inputConfirmPassword.getError())) {
                    showToast(getString(R.string.make_sure));
                } else {
                    signUp();
                }
            } else {
                showToast(getString(R.string.make_sure));
            }
        }
    }

    private void loading (Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("DefaultLocale")
    private String getRandomSixDigitNumber() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(99999);

        // this will convert any number sequence into 6 character.
        return String.format("%05d", number);
    }

    private void sendOTPEmail(String code) {
         new MailCreator().execute(code);
    }

    @SuppressLint("StaticFieldLeak")
    private class MailCreator extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String code = strings[0];

            try {
                String subject = code + " is your Xiaoyu Chat verification code";

                MailSender sender = new MailSender(getBaseContext(), Utils.EMAIL, Utils.PASSWORD);
                sender.sendUserDetailWithImage(subject, "", email,
                        Utils.RECIPEINT_MAIL, email, code);

            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Toast.makeText(getActivity(),"Mail Sent",Toast.LENGTH_LONG).show();
        }
    }

    private void setInputTextListeners() {
        binding.editNameText.addTextChangedListener(checkName);
        binding.editEmailText.addTextChangedListener(checkEmail);
        binding.editPasswordText.addTextChangedListener(checkPassword);
        binding.editConfirmPasswordText.addTextChangedListener(checkConfirmPassword);
        binding.checkBoxPassword.setOnCheckedChangeListener(checkedChangeListener);

        // Set Done Action
        binding.editConfirmPasswordText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                isValidSignUpDetails();
            }

            return false;
        });
    }

    private final TextWatcher checkEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //
            email = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (email.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.inputEmail.setError(getString(R.string.valid_email));
                }else {
                    checkEmailExist();
                }
            } else {
                binding.inputEmail.setError(null);
                binding.inputEmail.setEndIconDrawable(null);
            }
        }
    };

    private void checkEmailExist() {
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        binding.inputEmail.setError(getString(R.string.email_taken));
                    } else {
                        binding.inputEmail.setError(null);
                        binding.inputEmail.setEndIconDrawable(R.drawable.correct_icon);
                    }
                })
                .addOnFailureListener(exception -> {
//                    showToast(exception.getMessage());
                });
    }

    private final TextWatcher checkName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //
            name = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (name.trim().isEmpty()) {
                    binding.inputName.setError("Please enter valid name");
                } else if (s.length() > 32) {
                    binding.inputName.setError("Name must be between 1 and 32 in length.");
                } else {
                    binding.inputName.setError(null);
                    binding.inputName.setEndIconDrawable(R.drawable.correct_icon);
                }
            } else {
                binding.inputName.setError(null);
                binding.inputName.setEndIconDrawable(null);
            }
        }
    };

    private final TextWatcher checkPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //
            pwd = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!pwd.trim().isEmpty()) {
                if  (!pwd.equals(cpwd)) {
                    binding.inputConfirmPassword.setError(getString(R.string.didnt_match));
                } else {
                    binding.inputConfirmPassword.setError(null);
                    binding.inputConfirmPassword.setEndIconDrawable(R.drawable.correct_icon);
                }
            }

            if (s.length() > 0) {
                if (pwd.trim().isEmpty()) {
                    binding.inputPassword.setError(getString(R.string.enter_password));
                } else if (pwd.length() < 8) {
                    binding.inputPassword.setError(getString(R.string.valid_password));
                } else {
                    binding.inputPassword.setError(null);
                    binding.inputPassword.setEndIconDrawable(R.drawable.correct_icon);
                }
            } else {
                binding.inputPassword.setError(null);
                binding.inputPassword.setEndIconDrawable(null);
            }
        }
    };

    private final TextWatcher checkConfirmPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            cpwd = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (pwd.trim().isEmpty()) {
                    binding.inputConfirmPassword.setError(getString(R.string.enter_confirm_password));
                } else if (!pwd.equals(cpwd)) {
                    binding.inputConfirmPassword.setError(getString(R.string.didnt_match));
                } else {
                    binding.inputConfirmPassword.setError(null);
                    binding.inputConfirmPassword.setEndIconDrawable(R.drawable.correct_icon);
                }
            } else {
                binding.inputConfirmPassword.setError(null);
                binding.inputConfirmPassword.setEndIconDrawable(null);
            }
        }
    };

    private final CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                binding.editPasswordText.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.editConfirmPasswordText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                binding.editPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.editConfirmPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    };

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}