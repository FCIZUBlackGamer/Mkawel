package mkawel.fci.com.mkawel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    TextView login;
    EditText user_name, email, password, phone, address;
    Button register;
    Spinner cap;
    CircleImageView user_image;
    private int PICK_IMAGE_REQUEST = 1;
    final int CAMERA_PIC_REQUEST = 1337;
    private static final int CAMERA_REQUEST = 1888;
    String final_iamge;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        register = findViewById(R.id.register);
        cap = findViewById(R.id.cap);
        email = findViewById(R.id.email);
        login = findViewById(R.id.login);
        user_image = findViewById(R.id.user_image);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        user_image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View Camera_view = inflater.inflate(R.layout.camera_view, null);

                ImageView cam = Camera_view.findViewById(R.id.cam);
                ImageView gal = Camera_view.findViewById(R.id.gal);

                final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setCancelable(false)
                        .setView(Camera_view);

                final AlertDialog dialog = builder.create();
                dialog.show();

                gal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGalary();
                        dialog.dismiss();
                    }
                });

                cam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCamera();
                        dialog.dismiss();
                    }
                });



            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Registering");
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/mkawel/Register.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response",response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof ServerError)
//                            text.setText("خطأ فى الاتصال بالخادم");
                            Log.e("Err",error.getMessage().toString());
                        else if (error instanceof TimeoutError)
//                            text.setText("خطأ فى مدة الاتصال");
                            Log.e("Err",error.getMessage().toString());
                        else if (error instanceof NetworkError)
//                            text.setText("شبكه الانترنت ضعيفه حاليا");
                            Log.e("Err",error.getMessage().toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Content-Type", "application/json; charset=utf-8");
                        map.put("user_name",user_name.getText().toString());
                        map.put("email",email.getText().toString());
                        map.put("password",password.getText().toString());
                        map.put("phone",phone.getText().toString());
                        map.put("address",address.getText().toString());
                        map.put("capital",cap.getSelectedItem().toString());
                        map.put("longitude",231564.54+"");
                        map.put("latitude",231564.54+"");
                        map.put("userImage",final_iamge);
                        return map;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        3,  // maxNumRetries = 2 means no retry
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(RegisterActivity.this).add(stringRequest);
            }
        });

    }

    private void openGalary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        //Todo: Open Camera

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filePath;

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath= data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), filePath);
                //displayImage(bitmap, filePath);
                user_image.setImageBitmap(bitmap);

                final_iamge = getStringImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
//            filePath = null;
            //displayImage(bitmap, filePath);
//            imageView.setImageBitmap(photo);
            user_image.setImageBitmap(bitmap);
            final_iamge = getStringImage(bitmap);
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


}
