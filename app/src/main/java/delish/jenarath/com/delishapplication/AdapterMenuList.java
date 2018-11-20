package delish.jenarath.com.delishapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by admin on 13/11/2018 AD.
 */

public class AdapterMenuList extends BaseAdapter {

    private Activity activity;

    public AdapterMenuList(Activity act) {
        this.activity = act;
    }

    public int getCount() {
        return ActivityMenuList.MenuID.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menu_list_item, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String test = ActivityMenuList.Image.get(position);
        System.out.print("position = \n" + position);

        System.out.print("testtttttttttttt = \n" + test);

        holder.txtText = (TextView) convertView.findViewById(R.id.txtText);
        holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);

        holder.txtText.setText(ActivityMenuList.FoodName.get(position));

        Picasso.with(activity).load("http://128.199.170.20/softeng" + "/" + ActivityMenuList.Image.get(position)).into(holder.imgThumb);
//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);

        return convertView;
    }

    static class ViewHolder {
        TextView txtText;
        ImageView imgThumb;
    }


}