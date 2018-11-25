package mkawel.fci.com.mkawel.Home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mkawel.fci.com.mkawel.NavActivity;
import mkawel.fci.com.mkawel.R;

public class FragmentHome extends Fragment {
    // Trend Category
//    RecyclerView recyclerView;
//    RecyclerView.Adapter adapter;
//    List<Category> categoryList;

    // All Category
    static GridView gridView;
    static CategoryAdapter categoryAdapter;
    static List<Category> categories = new ArrayList<>();
    View view;

    FragmentManager fragmentManager;
    static int catId = -1;
    static String capital = "";

    public static FragmentHome init() {
        FragmentHome home = new FragmentHome();
        categories = new ArrayList<>();
        catId = -1;
        return home;
    }

    public static FragmentHome catOrEmployee(int Id) {
        FragmentHome home = new FragmentHome();
        categories = new ArrayList<>();
        catId = Id;
        return home;
    }

    public static FragmentHome nearest(String cap) {
        FragmentHome home = new FragmentHome();
        categories = new ArrayList<>();
        capital = cap;
        return home;
    }

    public static FragmentHome loadCat(int Id, List<Category> categorie) {
        FragmentHome home = new FragmentHome();
        categories = categorie;
        catId = Id;
        return home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView = (RecyclerView) view.findViewById(R.id.trend_rec);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(layoutManager);
//        categoryList = new ArrayList<>();

        fragmentManager = getFragmentManager();
        gridView = (GridView) view.findViewById(R.id.cat_grid);

//        if (categories == null)
//            categories = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("d",categories.size()+"");
        ((NavActivity) getActivity()).setActionBarTitle("الرئيسية");
        if (categories.size() > 0 && catId == -1) {
            Log.e("Inside","Here");
            loadCategorys(categories);
            categories = new ArrayList<>();
        } else if (categories.size() == 0 &&catId != -1) {
            Log.e("Inside","Right");
            loadEmployees(catId);
            catId = -1;
        } else if (!capital.isEmpty()) {
            Log.e("Inside","H");
            loadEmployees(capital);
        } else if (categories.size() == 0 && catId == -1){
            Log.e("Inside","No");
            loadCategorys();
        }else if (categories.size() > 0 && catId != -1) {
            Log.e("Inside","Shit");
            catId = -1;
            loadEmployees(categories);
            categories = new ArrayList<>();
        }


    }

    /**
     * http://abdelkreimahmed.000webhostapp.com/ListUsersInCat.php
     */
    private void loadEmployees(final int catId) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل الاقسام ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/ListUsersInCat.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("data");
                    if (array.length() > 0) {
                        categories = new ArrayList<>();
                        for (int x = 0; x < array.length(); x++) {
                            JSONObject object1 = array.getJSONObject(x);
                            Category category = new Category(object1.getInt("userId"),
                                    object1.getInt("numProjects"),
                                    object1.getString("name"));
                            category.setRate((float) object1.getDouble("rate"));
                            category.setImage(object1.getString("image"));
                            categories.add(category);
                        }
                        categoryAdapter = new CategoryAdapter(getActivity(), categories, 1);
                        gridView.setAdapter(categoryAdapter);
                    } else {
                        Toast.makeText(getActivity(), "لا توجد اقسام حاليا", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "صيغه استقبال غير صحيحه", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof ServerError)
                    Toast.makeText(getActivity(), "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(getActivity(), "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(getActivity(), "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("catId", catId + "");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadEmployees(final String cap) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل العمال الأقرب ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/Nearest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("data");
                    if (array.length() > 0) {
                        categories = new ArrayList<>();
                        for (int x = 0; x < array.length(); x++) {
                            JSONObject object1 = array.getJSONObject(x);
                            Category category = new Category(object1.getInt("userId"),
                                    object1.getInt("numProjects"),
                                    object1.getString("name"));
                            category.setRate((float) object1.getDouble("rate"));
                            category.setImage(object1.getString("image"));
                            categories.add(category);
                        }
                        categoryAdapter = new CategoryAdapter(getActivity(), categories, 1);
                        gridView.setAdapter(categoryAdapter);
                    } else {
                        Toast.makeText(getActivity(), "لا توجد اقسام حاليا", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "صيغه استقبال غير صحيحه", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof ServerError)
                    Toast.makeText(getActivity(), "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(getActivity(), "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(getActivity(), "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("cap", cap + "");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadEmployees(List<Category> categoryList) {
        categoryAdapter = new CategoryAdapter(getActivity(), categoryList, 1);
        gridView.setAdapter(categoryAdapter);
    }

    /**
     * http://abdelkreimahmed.000webhostapp.com/ListCat.php
     */
    private void loadCategorys() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل الاقسام ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/ListCat.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("CatUser");
                    if (array.length() > 0) {
                        categories = new ArrayList<>();
                        for (int x = 0; x < array.length(); x++) {
                            JSONObject object1 = array.getJSONObject(x);
                            Category category = new Category(object1.getInt("catId"),
                                    object1.getInt("numUsers"),
                                    object1.getString("catName"));
                            categories.add(category);
                        }
                        categoryAdapter = new CategoryAdapter(getActivity(), categories, 0);
                        gridView.setAdapter(categoryAdapter);
                    } else {
                        Toast.makeText(getActivity(), "لا توجد اقسام حاليا", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "صيغه استقبال غير صحيحه", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof ServerError)
                    Toast.makeText(getActivity(), "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(getActivity(), "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(getActivity(), "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                3,  // maxNumRetries = 2 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadCategorys(List<Category> categoryList) {

        categoryAdapter = new CategoryAdapter(getActivity(), categoryList, 0);
        gridView.setAdapter(categoryAdapter);
    }
}
