package mkawel.fci.com.mkawel.Employee;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import mkawel.fci.com.mkawel.Deal.FragmentMakeDeal;
import mkawel.fci.com.mkawel.LoginActivity;
import mkawel.fci.com.mkawel.NavActivity;
import mkawel.fci.com.mkawel.R;
import mkawel.fci.com.mkawel.RegisterActivity;
import mkawel.fci.com.mkawel.User;

import static android.app.Activity.RESULT_OK;

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

    FloatingActionButton call, makeDeal, edit;
    FragmentManager fragmentManager;
    static int Id = -1; // 0 is user, 1 is employee
    static int EmployeeId = 0;
    int employee_id = 0;
    String Image, Name, CatId;
    float userRate = 0;

    //Edit Views
    EditText ed_name, ed_phone, ed_address, ed_jobTitle, ed_password;
    Button btn_save;
    CircleImageView civ_user_image;
    ImageView iv_cancel;
    private int PICK_IMAGE_REQUEST = 1;
    final int CAMERA_PIC_REQUEST = 1337;
    private static final int CAMERA_REQUEST = 1888;
    String final_iamge;
    Bitmap bitmap;
    int cam_state = 0;
    User edit_user = new User();

    final Realm realm = Realm.getDefaultInstance();

    public static FragmentProfile userOrEmployee(int type, int employeId) {
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
        edit = view.findViewById(R.id.edit);
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

        requestStoragePermission();

        if (Id == 0) {
            gridView.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            makeDeal.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            loadMyProfile();
        } else {
            gridView.setVisibility(View.VISIBLE);
            call.setVisibility(View.VISIBLE);
            makeDeal.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            loadEmployeeProfile(EmployeeId);
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view;
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.popup_edit_profile, null);
                //ed_address = view.findViewById(R.id.ed_address);
                ed_name = view.findViewById(R.id.ed_name);
                ed_jobTitle = view.findViewById(R.id.ed_jobTitle);
                ed_password = view.findViewById(R.id.ed_password);
                ed_phone = view.findViewById(R.id.ed_phone);
                iv_cancel = view.findViewById(R.id.iv_cancel);
                civ_user_image = view.findViewById(R.id.civ_user_image);
                btn_save = view.findViewById(R.id.btn_save);

                civ_user_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View Camera_view = inflater.inflate(R.layout.camera_view, null);

                        ImageView cam = Camera_view.findViewById(R.id.cam);
                        ImageView gal = Camera_view.findViewById(R.id.gal);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view)
                        .setNegativeButton("اغلاق", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ViewGroup parent = (ViewGroup) view.getParent();
                                if (parent != null) {
                                    parent.removeAllViews();
                                }
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        dialog.dismiss();
                    }
                });

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_user.setJob_title(ed_jobTitle.getText().toString());
                        edit_user.setPhone(ed_phone.getText().toString());
                        edit_user.setName(ed_name.getText().toString());
                        edit_user.setPassword(ed_password.getText().toString());
                        if (cam_state != 0){
                            edit_user.setImage(final_iamge);
                        }
                        updateMyProfile(edit_user);
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        dialog.dismiss();
                    }
                });

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user_phone.getText().toString().isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + user_phone.getText()));
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        startActivity(callIntent);
                    }
                    //startActivity(callIntent);
                } else {
                    Toast.makeText(getActivity(), "لا تتوفر ارقام دليل", Toast.LENGTH_SHORT).show();
                }
            }
        });

        makeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentMakeDeal().setId(EmployeeId, Image, Name, userRate, CatId)).commit();
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
                        ((NavActivity)getActivity()).setActionBarTitle(Name);
                        user_job.setText(object1.getString("job_title"));
                        user_phone.setText(object1.getString("phone"));
                        user_rate.setRating((float) object1.getDouble("rate"));
                        userRate = (float) object1.getDouble("rate");
                        num_projects.setText(object1.getString("numProjects"));
                        Picasso.get().load(object1.getString("image")).into(user_image);
                        Image = object1.getString("image");
                        CatId = object1.getString("catId");
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
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadMyProfile() {
        // display basic information From Internal Database
        final User user = realm.where(User.class).findFirst();

        user_name.setText(user.getName());
        ((NavActivity)getActivity()).setActionBarTitle("الشخصية");
        user_job.setText(user.getJob_title());
        user_phone.setText(user.getPhone());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/GetUserProfile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray personalData = object.getJSONArray("profileData");
                    if (personalData.length() > 0) {

                        JSONObject object1 = personalData.getJSONObject(0);
                        user_rate.setRating((float) object1.getDouble("rate"));
                        userRate = (float) object1.getDouble("rate");
                        num_projects.setText(object1.getString("numProjects"));


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
                map.put("userId", user.getUserId() + "");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);

    }

    private void updateMyProfile(User edit_user) {

        final User user = realm.where(User.class).findFirst();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://abdelkreimahmed.000webhostapp.com/EditProf.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        Toast.makeText(getActivity(), "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "صيغه استقبال غير صحيحه", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                map.put("user_name", edit_user.getName() + "");
                map.put("userId", user.getUserId() + "");
                map.put("password", edit_user.getPassword() + "");
                map.put("phone", edit_user.getPhone() + "");
                map.put("job_title", edit_user.getJob_title() + "");
                if (cam_state != 0){
                    map.put("userImage", edit_user.getImage());
                }
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);

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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filePath;

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                user_image.setImageBitmap(bitmap);
                final_iamge = getStringImage(bitmap);
                cam_state = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            user_image.setImageBitmap(bitmap);
            final_iamge = getStringImage(bitmap);
            cam_state = 1;
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
