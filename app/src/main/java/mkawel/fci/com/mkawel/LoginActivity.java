package mkawel.fci.com.mkawel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    TextView register, forget_pass;
    EditText email, password;
    Button login;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        forget_pass = findViewById(R.id.forget_pass);
        login = findViewById(R.id.login);
        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

        final List<User> userList = realm.where(User.class).findAll();
        if (userList.size() > 0) {
            Intent intent = new Intent(LoginActivity.this, NavActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("جارى البحث ...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/Login.php", new Response.Listener<String>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean("error")) {
                                JSONArray array = object.getJSONArray("data");
                                if (array.length() > 0) {
                                    for (int x = 0; x < array.length(); x++) {
                                        JSONObject jsonObject = array.getJSONObject(x);
                                        // create realm object and store it
                                        user = new User(jsonObject.getInt("userId"),
                                                jsonObject.getInt("catId"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("image"),
                                                jsonObject.getString("cap"));
                                        user.setJob_title(jsonObject.getString("job_title"));
                                        user.setRate((float)jsonObject.getDouble("rate"));
                                        user.setPhone(jsonObject.getString("phone"));
                                        user.setType(jsonObject.getString("type"));
                                        realm.beginTransaction();
                                        realm.insertOrUpdate(user);
                                        realm.commitTransaction();
                                    }
                                    Intent intent = new Intent(LoginActivity.this, NavActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {

                            Toast.makeText(LoginActivity.this, "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof ServerError)
                            Toast.makeText(LoginActivity.this, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                        else if (error instanceof TimeoutError)
                            Toast.makeText(LoginActivity.this, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                        else if (error instanceof NetworkError)
                            Toast.makeText(LoginActivity.this, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Content-Type", "application/json; charset=utf-8");
                        map.put("email", email.getText().toString());
                        map.put("password", password.getText().toString());
                        return map;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        3,  // maxNumRetries = 2 means no retry
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(LoginActivity.this).add(stringRequest);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view;
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.popup_forget_password, null);

                EditText editText = view.findViewById(R.id.editText);
                editText.setHint("ادخل بريدك الالكترونى");
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(view)
                        .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setMessage("جارى ارسال كلمة المرور الى البريد الالكترنى المختار ...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/ForgetPass.php", new Response.Listener<String>() {
                                    @SuppressLint("ResourceType")
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("Response", response);
                                        progressDialog.dismiss();
                                        try {
                                            //JSONObject object = new JSONObject(response);
                                            Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {

                                            Toast.makeText(LoginActivity.this, "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        if (error instanceof ServerError)
                                            Toast.makeText(LoginActivity.this, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                                        else if (error instanceof TimeoutError)
                                            Toast.makeText(LoginActivity.this, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                                        else if (error instanceof NetworkError)
                                            Toast.makeText(LoginActivity.this, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("Content-Type", "application/json; charset=utf-8");
                                        map.put("email", editText.getText().toString() + "");
                                        return map;
                                    }
                                };
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                                        3,  // maxNumRetries = 2 means no retry
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
                                ViewGroup parent = (ViewGroup) editText.getParent();
                                if (parent != null) {
                                    parent.removeAllViews();
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewGroup parent = (ViewGroup) editText.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }
}
