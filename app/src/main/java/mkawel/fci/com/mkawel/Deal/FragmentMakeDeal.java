package mkawel.fci.com.mkawel.Deal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import mkawel.fci.com.mkawel.R;

public class FragmentMakeDeal extends Fragment {
    View view;
    EditText deal_name, deal_cost, deal_duration, deal_desc;
    RatingBar deal_rate;
    Button confirm, cancel;

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
