package mkawel.fci.com.mkawel.Deal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mkawel.fci.com.mkawel.Employee.FragmentProfile;
import mkawel.fci.com.mkawel.R;

public class AdapterProject extends RecyclerView.Adapter<AdapterProject.Vholder> {

    Context context;
    List<Deal> deals;
    FragmentManager fragmentManager;


    public AdapterProject(Context context, List<Deal> talabats) {
        this.context = context;
        this.deals = talabats;
    }

    @NonNull
    @Override
    public Vholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_myproject, parent, false);
        fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        return new Vholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vholder holder, final int position) {

        holder.project_name.setText(deals.get(position).getDealName());
        holder.user_name.setText(deals.get(position).getUserName());

        try {
            if (!deals.get(position).getUserImage().isEmpty()) {
                Picasso.get().load(deals.get(position).getUserImage()).into(holder.image);
            }
        } catch (Exception e) {

        }
        Log.e("Rate", deals.get(position).getRateDeal()+"");
        holder.ratingBar.setRating(deals.get(position).getRateDeal());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Deal Details **/
                fragmentManager.beginTransaction()
                        .replace(R.id.home_frame, new FragmentMakeDeal().setDeal(deals.get(position).getId(), deals.get(position).getWorkerId())).addToBackStack("FragmentMakeDeal").commit();
            }
        });

        holder.start_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Deal Details **/
                fragmentManager.beginTransaction()
                        .replace(R.id.home_frame, new FragmentMakeDeal().setDeal(deals.get(position).getId(), deals.get(position).getWorkerId())).addToBackStack("FragmentMakeDeal").commit();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("إلغاء الصفقة؟")
                        .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("IDDD", deals.get(position).getId() + "");
                                final ProgressDialog progressDialog = new ProgressDialog(context);
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
                                            Toast.makeText(context, object.getString("res"), Toast.LENGTH_SHORT).show();
                                            deals.remove(position);
                                            notifyDataSetChanged();
                                        } catch (Exception e) {

                                            Toast.makeText(context, "خطأ فى صيغة الاستقبال", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        if (error instanceof ServerError)
                                            Toast.makeText(context, "خطأ فى الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                                        else if (error instanceof TimeoutError)
                                            Toast.makeText(context, "خطأ فى مدة الاتصال", Toast.LENGTH_SHORT).show();
                                        else if (error instanceof NetworkError)
                                            Toast.makeText(context, "شبكه الانترنت ضعيفه حاليا", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("Content-Type", "application/json; charset=utf-8");
                                        map.put("deal_id", deals.get(position).getId() + "");
                                        map.put("deal_rate", 0 + "");
                                        map.put("deal_state", 2 + "");
                                        return map;
                                    }
                                };
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                                        3,  // maxNumRetries = 2 means no retry
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                Volley.newRequestQueue(context).add(stringRequest);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.home_frame, new FragmentProfile().userOrEmployee(1, deals.get(position).getWorkerId())).addToBackStack("FragmentProfile").commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class Vholder extends RecyclerView.ViewHolder {
        TextView project_name, user_name;
        RatingBar ratingBar;
        FloatingActionButton delete;
        CircleImageView image;
        Button start_deal;
        LinearLayout linearLayout;

        public Vholder(View itemView) {
            super(itemView);
            project_name = itemView.findViewById(R.id.project_name);
            user_name = itemView.findViewById(R.id.user_name);
            ratingBar = itemView.findViewById(R.id.deal_rate);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.user_image);
            start_deal = itemView.findViewById(R.id.start_deal);
            linearLayout = itemView.findViewById(R.id.lin);
        }

    }

}