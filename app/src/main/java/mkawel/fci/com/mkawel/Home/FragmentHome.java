package mkawel.fci.com.mkawel.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import mkawel.fci.com.mkawel.Deal.FragmentListDeals;
import mkawel.fci.com.mkawel.LoginActivity;
import mkawel.fci.com.mkawel.R;
import mkawel.fci.com.mkawel.RegisterActivity;

public class FragmentHome extends Fragment {
    // Trend Category
//    RecyclerView recyclerView;
//    RecyclerView.Adapter adapter;
//    List<Category> categoryList;

    // All Category
    GridView gridView;
    CategoryAdapter categoryAdapter;
    List<Category> categories;
    List<String> names;
    List<Integer> num;
    View view;

    FragmentManager fragmentManager;
    static int catId = -1;

    public static FragmentHome catOrEmployee(int Id){
        FragmentHome home = new FragmentHome();
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
        categories = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (catId != -1){
            loadEmployees(catId);
        }else {
            loadCategorys();
        }




    }

    private void loadEmployees(int catId) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل بيانات العمال ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/ListCat.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("realData");
                    if (array.length() > 0){
                        categories = new ArrayList<>();

                        for (int x=0; x<array.length(); x++){
                            JSONObject jsonObject = array.getJSONObject(x);
                            Category category = new Category(jsonObject.getInt("catId"),
                                    jsonObject.getString("catName"));
                            categories.add(category);
                        }

                    }else {
                        Toast.makeText(getActivity(), "لا توجد بيانات للعمال", Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){

                    Toast.makeText(getActivity(), "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
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


        categoryAdapter = new CategoryAdapter(getActivity(), categories, 0);
        gridView.setAdapter(categoryAdapter);


        for (int x=0; x<10; x++){
            Category category = new Category(x+1,
                    12,
                    "Ahmed");
            category.setRate(3.5f);
            categories.add(category);
        }

        categoryAdapter = new CategoryAdapter(getActivity(), categories, 1);
        gridView.setAdapter(categoryAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Category c = categories.get(position);
//                Log.e("Id",c.getId()+"");
//
//            }
//        });
    }

    private void loadCategorys() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل الاقسام ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/ListCat.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("realData");
                    if (array.length() > 0){
                        categories = new ArrayList<>();

                        for (int x=0; x<array.length(); x++){
                            JSONObject jsonObject = array.getJSONObject(x);
                            Category category = new Category(jsonObject.getInt("catId"),
                                    jsonObject.getString("catName"));
                            categories.add(category);
                        }

                    }else {
                        Toast.makeText(getActivity(), "لا توجد اقسام حاليا", Toast.LENGTH_SHORT).show();
                    }

                    JSONArray name = object.getJSONArray("data");
                    if (name.length() > 0){
//                        names = new ArrayList<>();
//                        num = new ArrayList<>();

                        String[] arr = new String[name.length()];
                        for(int i = 0; i < name.length(); i++)
                            arr[i] = name.getString(i);

                        for (int i = 0; i < arr.length; i++) {
                            Log.e("Index"+i, arr[i]);
                        }
//                        for (int x=0; x<name.length(); x++){
//                            JSONObject jsonObject = array.getJSONObject(x);
//
//                            names.add(jsonObject.getString("d"));
//                        }

                    }

                }catch (Exception e){

                    Toast.makeText(getActivity(), "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
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


        categoryAdapter = new CategoryAdapter(getActivity(), categories, 0);
        gridView.setAdapter(categoryAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Category c = categories.get(position);
//                Log.e("Id",c.getId()+"");
//
//            }
//        });
    }
}
