package mkawel.fci.com.mkawel.Employee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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
import mkawel.fci.com.mkawel.Deal.FragmentMakeDeal;
import mkawel.fci.com.mkawel.R;

public class FragmentProfile extends Fragment {

    // Basic info
    CircleImageView user_image;
    TextView user_name, user_job, user_phone, num_projects;
    RatingBar user_rate;

    // All Category
    mkawel.fci.com.mkawel.Employee.ExpandableHeightGridView gridView;
    ProjectAdapter projectAdapter;
    List<Project> categories;
    View view;

    FloatingActionButton call, makeDeal;
    FragmentManager fragmentManager;
    static int Id = -1; // 0 is user, 1 is employee
    static int EmployeeId = 0;
    int employee_id = 0;
    String Image, Name;
    float userRate;

    public static FragmentProfile userOrEmployee(int type, int employeId){
        FragmentProfile profile = new FragmentProfile();
        Id = type;
        EmployeeId = employeId;
        return profile;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        user_image = view.findViewById(R.id.user_image);
        user_name = view.findViewById(R.id.user_name);
        user_job = view.findViewById(R.id.user_job);
        user_phone = view.findViewById(R.id.user_phone);
        user_rate = view.findViewById(R.id.user_rate);
        num_projects = view.findViewById(R.id.num_projects);
        call = view.findViewById(R.id.call);
        makeDeal = view.findViewById(R.id.make_deal);
        gridView = (mkawel.fci.com.mkawel.Employee.ExpandableHeightGridView) view.findViewById(R.id.cat_grid);
        gridView.setExpanded(true);
        categories = new ArrayList<>();
        fragmentManager = getFragmentManager();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(Id == 0){
            gridView.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            makeDeal.setVisibility(View.GONE);
            loadMyProfile();
        }else {
            gridView.setVisibility(View.VISIBLE);
            call.setVisibility(View.VISIBLE);
            makeDeal.setVisibility(View.VISIBLE);
            loadEmployeeProfile(EmployeeId);
        }


//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Project c = categories.get(position);
////                fragmentManager.beginTransaction()
////                        .replace(R.id.frame_home, new FragmentProductDetails().setId(book.getId(),0)).addToBackStack("FragmentOfferDetails").commit();
//
//            }
//        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user_phone.getText().toString().isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + user_phone.getText()));
                    startActivity(callIntent);
                }else {
                    Toast.makeText(getActivity(), "لا تتوفر ارقام دليل", Toast.LENGTH_SHORT).show();
                }
            }
        });

        makeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentMakeDeal().setId(EmployeeId, Image, Name, userRate)).commit();
            }
        });
    }

    private void loadEmployeeProfile(final int employeeId) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل الاقسام ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/GetEmployeeProfile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray personalData = object.getJSONArray("profileData");
                    if (personalData.length() > 0) {

                        JSONObject object1 = personalData.getJSONObject(0);
                        employee_id = object1.getInt("userId");
                        user_name.setText(object1.getString("name"));
                        Name = object1.getString("name");
                        user_job.setText(object1.getString("job_title"));
                        user_phone.setText(object1.getString("phone"));
                        user_rate.setRating((float)object1.getDouble("rate"));
                        userRate = (float)object1.getDouble("rate");
                        num_projects.setText(object1.getString("numProjects"));
                        Picasso.get().load(object1.getString("image")).into(user_image);
                        Image = object1.getString("image");
                        JSONArray array = object.getJSONArray("projectData");
                        if (array.length() > 0) {
                            for (int x = 0; x < array.length(); x++) {
                                JSONObject object2 = array.getJSONObject(x);
                                Project category = new Project();
                                category.setId(object2.getInt("projectId"));
                                category.setName(object2.getString("projectName"));
                                category.setDescription(object2.getString("description"));
                                category.setRate((float) object2.getDouble("rate"));
                                categories.add(category);
                            }
                            projectAdapter = new ProjectAdapter(getActivity(), categories);
                            gridView.setAdapter(projectAdapter);
                        } else {
                            Toast.makeText(getActivity(), "لا توجد اقسام حاليا", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "لا توجد بيانات للعامل", Toast.LENGTH_SHORT).show();
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
                map.put("userId", employeeId + "");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                3,  // maxNumRetries = 2 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadMyProfile() {
        // display basic information From Internal Database

    }
}
