package mkawel.fci.com.mkawel.Deal;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.List;

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
        holder.ratingBar.setRating(deals.get(position).getRateDeal());

        /**
         * If Deal was with me action button settext("Edit")
         * if it's not
         * action button settext("Make Deal")
         * */
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** View Deal Action **/
            }
        });

        holder.start_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Make Deal or Edit Deal Action **/
                fragmentManager.beginTransaction()
                        .replace(R.id.home_frame, new FragmentMakeDeal()).addToBackStack("FragmentMakeDeal").commit();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("IDDD", deals.get(position).getId() + "");
                deals.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.home_frame, new FragmentProfile()).addToBackStack("FragmentProfile").commit();
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