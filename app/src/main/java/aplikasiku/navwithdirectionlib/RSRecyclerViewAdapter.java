package aplikasiku.navwithdirectionlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import aplikasiku.navwithdirectionlib.FDBMobel.Hospital;

/**
 * Created by Zulfa_K on 23-04-2017.
 */
public class RSRecyclerViewAdapter extends RecyclerView.Adapter<RSRecyclerViewAdapter.ViewHolder> {
    private List<Hospital> mDataset;
    CustomItemClickListener listener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextViewRsName;
        public TextView mTextViewRsLoc;
        public ImageView mImageViewRs;

        public ViewHolder(View v){
            super(v);
            mTextViewRsName = (TextView) v.findViewById(R.id.textViewRSName);
            mTextViewRsLoc = (TextView) v.findViewById(R.id.textViewRSLoc);
            mImageViewRs = (ImageView) v.findViewById(R.id.imageViewRSImage);
        }
    }

    public RSRecyclerViewAdapter( Context contextRS, List<Hospital> myDataset, CustomItemClickListener list) {
        mDataset = myDataset;
        listener = list;
        context = contextRS;

    }

    //implement method
    @Override
    public RSRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cusview_listrs,parent,false);
        final ViewHolder vh = new ViewHolder(rowView);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, vh.getAdapterPosition());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewRsName.setText(mDataset.get(position).RsName);
        holder.mTextViewRsLoc.setText(mDataset.get(position).RsLoc);
        Glide.with(context).load(mDataset.get(position).RsImageUrl).into(holder.mImageViewRs);
//        if(position==0){
//            holder.mImageViewRs.setBackgroundResource(R.drawable.gbrsardjito);
//        }
//        else if(position==1){
//            holder.mImageViewRs.setBackgroundResource(R.drawable.pantirapih);
//        }
//        else if(position==2){
//            holder.mImageViewRs.setBackgroundResource(R.drawable.bethesda);
//        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface CustomItemClickListener{
        public void onItemClick(View v, int position);
    }
}
