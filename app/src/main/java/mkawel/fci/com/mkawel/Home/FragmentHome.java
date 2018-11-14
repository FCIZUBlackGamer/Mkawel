package mkawel.fci.com.mkawel.Home;

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

import java.util.ArrayList;
import java.util.List;

import mkawel.fci.com.mkawel.Deal.FragmentListDeals;
import mkawel.fci.com.mkawel.R;

public class FragmentHome extends Fragment {
    // Trend Category
//    RecyclerView recyclerView;
//    RecyclerView.Adapter adapter;
//    List<Category> categoryList;

    // All Category
    GridView gridView;
    CategoryAdapter categoryAdapter;
    List<Category> categories;
    View view;

    FragmentManager fragmentManager;
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

        for (int x=0; x<10; x++){
            Category category = new Category(x+1,
                    12,
                    "cars");
            categories.add(category);
        }

        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        gridView.setAdapter(categoryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Category c = categories.get(position);
                Log.e("Id",c.getId()+"");

            }
        });



    }
}
