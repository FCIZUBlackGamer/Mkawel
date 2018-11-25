package mkawel.fci.com.mkawel.Deal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import mkawel.fci.com.mkawel.Home.FragmentHome;
import mkawel.fci.com.mkawel.NavActivity;
import mkawel.fci.com.mkawel.R;
import mkawel.fci.com.mkawel.User;

public class FragmentMakeDeal extends Fragment {
    View view;
    EditText deal_name, deal_cost, deal_duration, deal_desc;
    TextView employeeName;
    CircleImageView userImage;
    RatingBar deal_rate, userRate;
    Button confirm, cancel;
    LinearLayout deal_layout;
    static int employeeId = 0;
    static String employeeimage, employeename, employeeCat;
    static float user_Rate;
    static int dealId = 0;
    String deal_state = "-1";
    Realm realm;
    User user;
    int userId;
    String userType;
    FragmentManager fragmentManager;

    public static FragmentMakeDeal setId(int id, String image, String anme, float userRate, String cat) {
        FragmentMakeDeal fragmentMakeDeal = new FragmentMakeDeal();
        employeeId = id;
        employeeimage = image;
        employeename = anme;
        user_Rate = userRate;
        employeeCat = cat;
        return fragmentMakeDeal;
    }

    public static FragmentMakeDeal setDeal(int deal_Id, int employee_Id) {
        FragmentMakeDeal fragmentMakeDeal = new FragmentMakeDeal();
        dealId = deal_Id;
        employeeId = employee_Id;
        return fragmentMakeDeal;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_make_deal, container, false);
        deal_name = view.findViewById(R.id.deal_name);
        deal_cost = view.findViewById(R.id.deal_cost);
        deal_duration = view.findViewById(R.id.deal_duration);
        deal_rate = view.findViewById(R.id.deal_rate);
        confirm = view.findViewById(R.id.confirm);
        deal_desc = view.findViewById(R.id.deal_desc);
        cancel = view.findViewById(R.id.cancel);
        deal_layout = view.findViewById(R.id.deal_layout);
        employeeName = view.findViewById(R.id.user_name);
        userRate = view.findViewById(R.id.user_rate);
        userImage = view.findViewById(R.id.user_image);
        //Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
        fragmentManager= getFragmentManager();
        return view;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onStart() {
        super.onStart();

        //realm.beginTransaction();
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

        ((NavActivity)getActivity()).setActionBarTitle("اتفاقية");

        if (dealId != 0) {
            /** get deal details from api with dealId **/
            /**
             * if deal state == 0 can rate, cancel, can't edit on deal
             * if deal state == 2 can can't rate, edit or cancel
             * */

            deal_layout.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("جارى تحميل بيانات الصفقة ...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/GetDealDetails.php", new Response.Listener<String>() {
                @SuppressLint("ResourceType")
                @Override
                public void onResponse(String response) {
                    Log.e("Response", response);
                    progressDialog.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONArray array = object.getJSONArray("dealData");
                        if (array.length() > 0) {
                            JSONObject jsonObject = array.getJSONObject(0);
                            Picasso.get().load(jsonObject.getString("image")).into(userImage);
                            userRate.setRating((float)jsonObject.getDouble("userRate"));
                            deal_name.setText(jsonObject.getString("projectName"));
                            deal_rate.setRating((float) jsonObject.getDouble("projectRate"));
                            deal_desc.setText(jsonObject.getString("description"));
                            deal_cost.setText(jsonObject.getString("cost"));
                            deal_duration.setText(jsonObject.getString("duration"));
                            deal_state = jsonObject.getString("status");
                            deal_name.setEnabled(false);
                            deal_name.setBackgroundColor(Color.parseColor("#ffffff"));
                            deal_desc.setEnabled(false);
                            deal_desc.setBackgroundColor(Color.parseColor("#ffffff"));
                            deal_duration.setEnabled(false);
                            deal_duration.setBackgroundColor(Color.parseColor("#ffffff"));
                            deal_cost.setEnabled(false);
                            deal_cost.setBackgroundColor(Color.parseColor("#ffffff"));
                            if (!deal_state.equals("0")) {
                                deal_rate.setEnabled(false);
                                confirm.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);
                            } else {
                                confirm.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                confirm.setText("تمت الصفقة");
                            }
                        } else {
                            Toast.makeText(getActivity(), "لا يوجد بيانات للصفقة", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

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
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json; charset=utf-8");
                    map.put("dealId", dealId + "");
                    map.put("userType", userType + "");
                    return map;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    3,  // maxNumRetries = 2 means no retry
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(getActivity()).add(stringRequest);

        }else {

            Picasso.get().load(employeeimage).into(userImage);
            userRate.setRating(user_Rate);
            employeeName.setText(employeename);
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deal_state.equals("-1")) { // insert new deal
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("جارى حجز صفقة ...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/MakeNewDeal.php", new Response.Listener<String>() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            progressDialog.dismiss();
                            try {
                                JSONObject object = new JSONObject(response);
                                Toast.makeText(getActivity(), object.getString("res"), Toast.LENGTH_SHORT).show();
                                fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome()).commit();
                            } catch (Exception e) {

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
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Content-Type", "application/json; charset=utf-8");
                            map.put("deal_name", deal_name.getText().toString() + "");
                            map.put("deal_cost", deal_cost.getText().toString() + "");
                            map.put("deal_duration", deal_duration.getText().toString() + "");
                            map.put("userId",  userId+ ""); //Todo: from internal db
                            map.put("employeeId", employeeId + "");
                            map.put("deal_desc", deal_desc.getText().toString() + "");
                            map.put("catId", employeeCat + "");
                            return map;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            3,  // maxNumRetries = 2 means no retry
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(getActivity()).add(stringRequest);
                } else if (deal_state.equals("0")) { // finish deal
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("جارى انهاء الصفقة ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/FinishDeal.php", new Response.Listener<String>() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            progressDialog.dismiss();
                            try {
                                JSONObject object = new JSONObject(response);
                                Toast.makeText(getActivity(), object.getString("res"), Toast.LENGTH_SHORT).show();
                                fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome()).commit();
                            } catch (Exception e) {

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
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Content-Type", "application/json; charset=utf-8");
                            map.put("deal_id", dealId + "");
                            map.put("deal_rate", deal_rate.getRating() + "");
                            map.put("deal_state", 1 + "");
                            map.put("employeeId", employeeId + "");
                            return map;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            3,  // maxNumRetries = 2 means no retry
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(getActivity()).add(stringRequest);
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancel deal
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("إلغاء الصفقة؟")
                        .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               // Log.e("IDDD", deals.get(position).getId() + "");
                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("جارى إلغاء الصفقة ...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/FinishDeal.php", new Response.Listener<String>() {
                                    @SuppressLint("ResourceType")
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("Response", response);
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            Toast.makeText(getActivity(), object.getString("res"), Toast.LENGTH_SHORT).show();
//                                            deals.remove(position);
//                                            notifyDataSetChanged();
                                        } catch (Exception e) {

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
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("Content-Type", "application/json; charset=utf-8");
                                        map.put("deal_id", dealId + "");
                                        map.put("deal_rate", deal_rate.getRating() + "");
                                        map.put("deal_state", 2 + "");
                                        return map;
                                    }
                                };
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                                        3,  // maxNumRetries = 2 means no retry
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                Volley.newRequestQueue(getActivity()).add(stringRequest);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
//                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//                progressDialog.setMessage("جارى إلغاء الصفقة ...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/FinishDeal.php", new Response.Listener<String>() {
//                    @SuppressLint("ResourceType")
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("Response", response);
//                        progressDialog.dismiss();
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            Toast.makeText(getActivity(), object.getString("res"), Toast.LENGTH_SHORT).show();
//                            fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentHome()).commit();
//                        } catch (Exception e) {
//
//                            Toast.makeText(getActivity(), "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
//                        if (error instanceof ServerError)
//                            Toast.makeText(getActivity(), "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
//                        else if (error instanceof TimeoutError)
//                            Toast.makeText(getActivity(), "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
//                        else if (error instanceof NetworkError)
//                            Toast.makeText(getActivity(), "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("Content-Type", "application/json; charset=utf-8");
//                        map.put("deal_id", dealId + "");
//                        map.put("deal_rate", deal_rate.getRating() + "");
//                        map.put("deal_state", 2 + "");
//                        return map;
//                    }
//                };
//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                        3,  // maxNumRetries = 2 means no retry
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                Volley.newRequestQueue(getActivity()).add(stringRequest);
            }
        });

    }
}
