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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mkawel.fci.com.mkawel.R;

public class FragmentMakeDeal extends Fragment {
    View view;
    EditText deal_name, deal_cost, deal_duration, deal_desc;
    TextView employeeName;
    CircleImageView userImage;
    RatingBar deal_rate, userRate;
    Button confirm, cancel;
    LinearLayout deal_layout;
    static int employeeId = 0;
    static String employeeimage, employeename;
    static float user_Rate;
    static int dealId = 0;

    public static FragmentMakeDeal setId(int id, String image, String anme, float userRate){
        FragmentMakeDeal fragmentMakeDeal = new FragmentMakeDeal();
        employeeId = id;
        employeeimage = image;
        employeename = anme;
        user_Rate = userRate;
        return fragmentMakeDeal;
    }

    public static FragmentMakeDeal setDeal(int deal_Id){
        FragmentMakeDeal fragmentMakeDeal = new FragmentMakeDeal();
        dealId = deal_Id;
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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (dealId != 0){
            Picasso.get().load(employeeimage).into(userImage);
            userRate.setRating(user_Rate);
            employeeName.setText(employeename);
            deal_layout.setVisibility(View.GONE);
            cancel.setVisibility(View.INVISIBLE);
        }else {
            /** get deal details from api with dealId **/
            /**
             * if deal state == 1 can rate, cancel, can't edit on deal
             * if deal state == 2 can can't rate, edit or cancel
             * */

        }






    }
}
