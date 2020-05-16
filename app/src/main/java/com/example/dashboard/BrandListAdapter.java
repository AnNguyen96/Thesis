package com.example.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BrandListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Brand> brandsList;

    public BrandListAdapter(Context context, int layout, ArrayList<Brand> brandsList) {
        this.context = context;
        this.layout = layout;
        this.brandsList = brandsList;
    }

    @Override
    public int getCount() {
        return brandsList.size();
    }

    @Override
    public Object getItem(int i) {
        return brandsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtDesc;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtDesc = (TextView) row.findViewById(R.id.txtDesc);
            holder.imageView = (ImageView) row.findViewById(R.id.imgBrand);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Brand brand = brandsList.get(i);

        holder.txtName.setText(brand.getName());
        holder.txtDesc.setText(brand.getDesc());

        byte[] brandImage = brand.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(brandImage, 0 , brandImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
