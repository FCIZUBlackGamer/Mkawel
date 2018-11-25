package mkawel.fci.com.mkawel.Deal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import io.realm.Realm;
import mkawel.fci.com.mkawel.Employee.Project;
import mkawel.fci.com.mkawel.Employee.ProjectAdapter;
import mkawel.fci.com.mkawel.Home.Category;
import mkawel.fci.com.mkawel.NavActivity;
import mkawel.fci.com.mkawel.R;
import mkawel.fci.com.mkawel.User;

public class FragmentListDeals extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Deal> dealList;
    View view;
    Realm realm;
    User user;
    int userId;
    String userType;
    boolean res = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_deals, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.deal_rec);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        dealList = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((NavActivity)getActivity()).setActionBarTitle("عرض المشاريع");
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user = realm.where(User.class).findFirst();
                userId = user.getUserId();
                userType = user.getType();
                //realm.commitTransaction();
                Log.e("UserId", userId+"");

            }
        });

            loadMyProjects();

    }

    private void loadMyProjects() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل المشاريع ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/GetUserDeals.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                res = true;
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);

                        JSONArray array = object.getJSONArray("projectData");
                        if (array.length() > 0) {
                            dealList = new ArrayList<>();
                            for (int x = 0; x < array.length(); x++) {
                                JSONObject object2 = array.getJSONObject(x);
                                Deal category = new Deal();
                                category.setCost((float) object2.getDouble("userRate"));
                                category.setId(object2.getInt("projectId"));
                                category.setDealName(object2.getString("projectName"));
                                category.setUserName(object2.getString("name"));
                                category.setDescription(object2.getString("description"));
                                category.setRateDeal((float) object2.getDouble("projectRate"));
                                category.setUserImage(object2.getString("image"));
                                category.setWorkerId(object2.getInt("userId"));

                                dealList.add(category);
                            }
                            adapter = new AdapterProject(getActivity(), dealList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getActivity(), "لا توجد مشاريع", Toast.LENGTH_SHORT).show();
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
                map.put("userId", userId + "");
                Log.e("userId", userId + "");
                map.put("userType", userType + "");
                Log.e("userType", userType + "");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}
