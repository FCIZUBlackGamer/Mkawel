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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    TextView login;
    EditText user_name, email, password, phone, address, other_cat, job_title;
    Button register;
    Spinner cap, category, type;
    CircleImageView user_image;
    private int PICK_IMAGE_REQUEST = 1;
    final int CAMERA_PIC_REQUEST = 1337;
    private static final int CAMERA_REQUEST = 1888;
    String final_iamge;
    Bitmap bitmap;
    List<Spin> spinList;
    List<String> stringList;
    String cat_state, catId;

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
        category = findViewById(R.id.category);
        email = findViewById(R.id.email);
        other_cat = findViewById(R.id.other_cat);
        login = findViewById(R.id.login);
        user_image = findViewById(R.id.user_image);
        job_title = findViewById(R.id.job_title);
        type = findViewById(R.id.type);
        other_cat.setVisibility(View.GONE);
        stringList = new ArrayList<>();
        spinList = new ArrayList<>();

        requestStoragePermission();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        loadSpinnerData();

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (category.getSelectedItem().equals("أخرى")) {
                    cat_state = "0";
                    other_cat.setVisibility(View.VISIBLE);
                } else {
                    cat_state = "1";
                    other_cat.setVisibility(View.GONE);
                    for (int x = 0; x < spinList.size(); x++) {
                        if (spinList.get(x).getValue().equals(category.getSelectedItem().toString())) {
                            catId = spinList.get(x).getId() + "";
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

                synchronized (this) {
                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setMessage("تسجيل مستخدم ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/Register.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            progressDialog.dismiss();
                            try {
                                JSONObject object = new JSONObject(response);
                                if (!object.getBoolean("error")) {
                                    Toast.makeText(RegisterActivity.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(RegisterActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {

                                Toast.makeText(RegisterActivity.this, "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            if (error instanceof ServerError)
                                Toast.makeText(RegisterActivity.this, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                            else if (error instanceof TimeoutError)
                                Toast.makeText(RegisterActivity.this, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                            else if (error instanceof NetworkError)
                                Toast.makeText(RegisterActivity.this, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Content-Type", "application/json; charset=utf-8");
                            map.put("user_name", user_name.getText().toString());
//                        Log.e("user_name", user_name.getText().toString());
                            map.put("email", email.getText().toString());
//                        Log.e("email", email.getText().toString());
                            map.put("password", password.getText().toString());
//                        Log.e("password", password.getText().toString());
                            map.put("phone", phone.getText().toString());
//                        Log.e("phone", phone.getText().toString());
                            map.put("job_title", job_title.getText().toString());
//                        Log.e("job_title", job_title.getText().toString());
                            map.put("cat_state", cat_state);
//                        Log.e("cat_state", cat_state);
                            map.put("address", address.getText().toString());
//                        Log.e("address", address.getText().toString());
                            map.put("capital", cap.getSelectedItem().toString());
//                        Log.e("capital", cap.getSelectedItem().toString());
                            if (cat_state.equals("1")) {
                                map.put("cat", catId);
//                            Log.e("cat", catId);
                            } else {
                                map.put("cat", other_cat.getText().toString());
//                            Log.e("cat", other_cat.getText().toString());
                            }
                            if (type.getSelectedItem().toString().equals("عامل")) {
                                map.put("type", "emp");
//                            Log.e("type", "emp");
                            } else {
                                map.put("type", "none");
//                            Log.e("type", "none");
                            }
                            map.put("longitude", 231564.54 + "");
                            map.put("latitude", 231564.54 + "");
                            map.put("userImage", final_iamge);
                            return map;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(RegisterActivity.this).add(stringRequest);
                }
            }
        });

    }


    private void loadSpinnerData() {

        final RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/ListCat.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("CatUser");
                    if (array.length() > 0) {
                        stringList = new ArrayList<>();
                        spinList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject1 = array.getJSONObject(i);
                            Spin spin = new Spin(jsonObject1.getInt("catId"), jsonObject1.getString("catName"));
                            stringList.add(jsonObject1.getString("catName"));
                            spinList.add(spin);
                        }
                    }
                    stringList.add("أخرى");
                    category.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, stringList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(RegisterActivity.this, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(RegisterActivity.this, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(RegisterActivity.this, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
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
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), filePath);
                user_image.setImageBitmap(bitmap);
                final_iamge = getStringImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
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
