package mkawel.fci.com.mkawel.Employee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import mkawel.fci.com.mkawel.Deal.FragmentMakeDeal;
import mkawel.fci.com.mkawel.Home.Category;
import mkawel.fci.com.mkawel.Home.CategoryAdapter;
import mkawel.fci.com.mkawel.R;

public class FragmentProfile extends Fragment {

    // All Category
    mkawel.fci.com.mkawel.Employee.ExpandableHeightGridView gridView;
    ProjectAdapter projectAdapter;
    List<Project> categories;
    View view;

    FloatingActionButton call, makeDeal;
    FragmentManager fragmentManager;
    static int Id = -1; // 0 is user, 1 is employee
    static int EmployeeId = 0;

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

        makeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.home_frame, new FragmentMakeDeal()).commit();
            }
        });
    }

    private void loadEmployeeProfile(int employeeId) {
        for (int x=0; x<10; x++){
            Project category = new Project();
            category.setId(x+1);
            category.setName("car washing");
            category.setDescription("All is good");
            category.setRate(3.5f);
            categories.add(category);
        }

        projectAdapter = new ProjectAdapter(getActivity(), categories);
        gridView.setAdapter(projectAdapter);
    }

    private void loadMyProfile() {
        // display basic information
    }
}
