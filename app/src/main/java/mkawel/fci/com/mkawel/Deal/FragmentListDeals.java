package mkawel.fci.com.mkawel.Deal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mkawel.fci.com.mkawel.Home.Category;
import mkawel.fci.com.mkawel.R;

public class FragmentListDeals extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Deal> dealList;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_deals, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.deal_rec);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        dealList = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        for (int x=0; x<10; x++){
            Deal category = new Deal();
            category.setCost(12);
            category.setDealName("Deal Name");
            category.setUserName("Mo'men Shaheen");
            category.setDescription("ivhwioehvievhwoirhvgriugviegwvigegiweuigriuwgeriuvgwe");
            category.setRateDeal(4);
            dealList.add(category);
        }

        adapter = new AdapterProject(getActivity(), dealList);
        recyclerView.setAdapter(adapter);
    }
}
