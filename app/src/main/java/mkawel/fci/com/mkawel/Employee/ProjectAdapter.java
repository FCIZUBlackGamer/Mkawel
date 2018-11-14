package mkawel.fci.com.mkawel.Employee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import mkawel.fci.com.mkawel.R;

public class ProjectAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Project> categories;

    public ProjectAdapter(Context context, List<Project> categories) {
        this.mContext = context;
        this.categories = categories;
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
        final Project product = categories.get(position);

        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.adapter_project, null);

            final TextView name = (TextView) convertView.findViewById(R.id.dep_name);
            final TextView des = (TextView) convertView.findViewById(R.id.dep_desc);
            final RatingBar ratingBar = convertView.findViewById(R.id.project_rate);

            final ViewHolder viewHolder = new ViewHolder(name, des, ratingBar);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(product.getName());
        viewHolder.des.setText(String.valueOf(product.getDescription()));
        viewHolder.ratingBar.setRating(product.getRate());

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView des;
        RatingBar ratingBar;

        public ViewHolder(TextView name, TextView numProjects, RatingBar ratingBar) {
            this.name = name;
            this.des = numProjects;
            this.ratingBar = ratingBar;
        }
    }

}