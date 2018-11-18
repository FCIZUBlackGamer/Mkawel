package mkawel.fci.com.mkawel.Home;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import mkawel.fci.com.mkawel.Deal.FragmentListDeals;
import mkawel.fci.com.mkawel.Employee.FragmentProfile;
import mkawel.fci.com.mkawel.R;

public class CategoryAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Category> categories;
    FragmentManager fragmentManager;
    int type; // 0 is cat, 1 is employee

    public CategoryAdapter(Context context, List<Category> categories, int type) {
        this.mContext = context;
        this.categories = categories;
        this.type = type;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Category product = categories.get(position);

        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            convertView = layoutInflater.inflate(R.layout.adapter_category, null);

            final TextView name = (TextView) convertView.findViewById(R.id.dep_name);
            final TextView numProjects = (TextView) convertView.findViewById(R.id.dep_num);
            final Button action = (Button) convertView.findViewById(R.id.action);
            final RatingBar rate = (RatingBar) convertView.findViewById(R.id.rate);

            final ViewHolder viewHolder = new ViewHolder(name, numProjects, action, rate);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (type == 0 ){
            viewHolder.ratingBar.setVisibility(View.GONE);
        }else {
            viewHolder.ratingBar.setVisibility(View.VISIBLE);
            viewHolder.ratingBar.setRating(product.getRate());
        }
        viewHolder.name.setText(product.getName());
        viewHolder.numProjects.setText(String.valueOf(product.getNumProjects()));
        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0)
                fragmentManager.beginTransaction()
                        .replace(R.id.home_frame, new FragmentHome().catOrEmployee(product.getId())).addToBackStack("FragmentHome").commit();
                else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.home_frame, new FragmentProfile().userOrEmployee(1, product.getId())).addToBackStack("FragmentProfile").commit();
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView numProjects;
        Button action;
        RatingBar ratingBar;

        public ViewHolder(TextView name, TextView numProjects, Button action, RatingBar ratingBar) {
            this.name = name;
            this.numProjects = numProjects;
            this.action = action;
            this.ratingBar = ratingBar;
        }

    }

}