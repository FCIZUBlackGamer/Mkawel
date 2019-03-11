package mkawel.fci.com.mkawel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import mkawel.fci.com.mkawel.Deal.FragmentListDeals;
import mkawel.fci.com.mkawel.Employee.FragmentProfile;
import mkawel.fci.com.mkawel.Home.Category;
import mkawel.fci.com.mkawel.Home.FragmentHome;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    Realm realm;
    CircleImageView user_image;
    TextView user_name, user_job;
    String name, image, job, cap;
    User user;
    int userId, type;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();


        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome()).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        user_image = view.findViewById(R.id.user_image);
        user_name = view.findViewById(R.id.user_name);
        user_job = view.findViewById(R.id.user_job);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user = realm.where(User.class).findFirst();
                name = user.getName();
                job = user.getJob_title();
                image = user.getImage();
                cap = user.getCap();
                userId = user.getUserId();
                if (user.getType().equals("emp"))
                    type = 1;
                else
                    type = 0;
            }
        });

        Picasso.get().load(image).into(user_image);
        user_job.setText(job);
        user_name.setText(name);

        realm.beginTransaction();
        user = realm.where(User.class).findFirst();
        realm.commitTransaction();
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opClo();
                fragmentManager.beginTransaction().replace(R.id.home_frame, FragmentProfile.setUser(user)).commit();
            }
        });
        user_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opClo();
                fragmentManager.beginTransaction().replace(R.id.home_frame, FragmentProfile.setUser(user)).commit();
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opClo();
                fragmentManager.beginTransaction().replace(R.id.home_frame, FragmentProfile.setUser(user)).commit();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer == null)
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void opClo() {
        if (drawer == null)
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final EditText editText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_for_cat) {
            editText.setHint("ادخل كلمه بحث عن قسم");
            builder.setTitle("بحث")
                    .setCancelable(false)
                    .setView(editText).setNegativeButton("اغلاق", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewGroup parent = (ViewGroup) editText.getParent();
                    if (parent != null) {
                        parent.removeAllViews();
                    }
                    dialog.dismiss();
                }
            }).setPositiveButton("بحث", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do Nothing
                    final ProgressDialog progressDialog = new ProgressDialog(NavActivity.this);
                    progressDialog.setMessage("جارى البحث ...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/SearchCat.php", new Response.Listener<String>() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            progressDialog.dismiss();
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getString("error").equals("No Error")) {
                                    JSONArray array = object.getJSONArray("data");
                                    if (array.length() > 0) {
                                        List<Category> categories = new ArrayList<>();
                                        for (int x = 0; x < array.length(); x++) {
                                            JSONObject jsonObject = array.getJSONObject(x);
                                            Category category = new Category(jsonObject.getInt("catId"),
                                                    jsonObject.getInt("numUsers"),
                                                    jsonObject.getString("catName"));
                                            categories.add(category);
                                        }
                                        Log.e("Size", categories.size() + "");
                                        fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome().loadCat(-1, categories)).commit();
                                    } else {
                                        Toast.makeText(NavActivity.this, "لا توجد اقسام متشابهه", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(NavActivity.this, object.getString("error"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {

                                Toast.makeText(NavActivity.this, "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            if (error instanceof ServerError)
                                Toast.makeText(NavActivity.this, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                            else if (error instanceof TimeoutError)
                                Toast.makeText(NavActivity.this, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                            else if (error instanceof NetworkError)
                                Toast.makeText(NavActivity.this, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Content-Type", "application/json; charset=utf-8");
                            map.put("cat", editText.getText().toString());
                            return map;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            3,  // maxNumRetries = 2 means no retry
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(NavActivity.this).add(stringRequest);
                    ViewGroup parent = (ViewGroup) editText.getParent();
                    if (parent != null) {
                        parent.removeAllViews();
                    }
                    dialog.dismiss();
                }
            });
            final AlertDialog dialog2 = builder.create();
            dialog2.show();
        } else if (id == R.id.action_search_for_employee) {
            editText.setHint("ادخل كلمه بحث عن عمال");
            builder.setTitle("بحث")
                    .setCancelable(false)
                    .setView(editText)
                    .setNegativeButton("اغلاق", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewGroup parent = (ViewGroup) editText.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("بحث", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do Nothing
                            final ProgressDialog progressDialog = new ProgressDialog(NavActivity.this);
                            progressDialog.setMessage("جارى البحث ...");
                            progressDialog.show();
                            progressDialog.setCancelable(false);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/SearchUser.php", new Response.Listener<String>() {
                                @SuppressLint("ResourceType")
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Response", response);
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        if (object.getString("error").equals("No Error")) {
                                            JSONArray array = object.getJSONArray("data");
                                            if (array.length() > 0) {
                                                List<Category> categories = new ArrayList<>();
                                                for (int x = 0; x < array.length(); x++) {
                                                    JSONObject jsonObject = array.getJSONObject(x);
                                                    Category category = new Category(jsonObject.getInt("userId"),
                                                            jsonObject.getString("name"));
                                                    category.setRate((float) jsonObject.getDouble("userRate"));
                                                    category.setImage(jsonObject.getString("image"));
                                                    category.setNumProjects(jsonObject.getInt("numProjects"));
                                                    categories.add(category);
                                                }
                                                fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome().loadCat(1, categories)).commit();
                                            } else {
                                                Toast.makeText(NavActivity.this, "لا يوجد عمال بهذا الأسم", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(NavActivity.this, object.getString("error"), Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {

                                        Toast.makeText(NavActivity.this, "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    if (error instanceof ServerError)
                                        Toast.makeText(NavActivity.this, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                                    else if (error instanceof TimeoutError)
                                        Toast.makeText(NavActivity.this, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                                    else if (error instanceof NetworkError)
                                        Toast.makeText(NavActivity.this, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("Content-Type", "application/json; charset=utf-8");
                                    map.put("userName", editText.getText().toString());
                                    return map;
                                }
                            };
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                                    3,  // maxNumRetries = 2 means no retry
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            Volley.newRequestQueue(NavActivity.this).add(stringRequest);
                            ViewGroup parent = (ViewGroup) editText.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog2 = builder.create();
            dialog2.show();
        } else if (id == R.id.action_refresh) {
            fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome()).commit();
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome()).commit();

        } else if (id == R.id.nav_projects) {
            fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentListDeals()).commit();

        } else if (id == R.id.nav_nearest) {
            fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome().nearest(cap)).commit();

        } else if (id == R.id.nav_out) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<User> result = realm.where(User.class).findAll();
                    result.deleteAllFromRealm();
                    startActivity(new Intent(NavActivity.this, LoginActivity.class));
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
