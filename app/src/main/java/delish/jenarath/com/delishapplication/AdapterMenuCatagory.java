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
 * Created by admin on 17/11/2018 AD.
 */

public class AdapterMenuCatagory extends BaseAdapter {

    private Activity activity;

    public AdapterMenuCatagory(Activity act) {
        this.activity = act;
    }

    public int getCount() {
        return ActivityCatagory.MenuID.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.catagory_list_item, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtText = (TextView) convertView.findViewById(R.id.txtText);
        holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);

        holder.txtText.setText(ActivityCatagory.FoodName.get(position));

//        Picasso.with(activity).load(Config.ADMIN_PANEL_URL+"/"+ActivityMenuList.Menu_image.get(position)).placeholder(R.drawable.loading).into(holder.imgThumb);
        Picasso.with(activity).load("http://128.199.170.20/softeng" + "/" + ActivityCatagory.Image.get(position)).into(holder.imgThumb);

        return convertView;
    }

    static class ViewHolder {
        TextView txtText;
        ImageView imgThumb;
    }


}