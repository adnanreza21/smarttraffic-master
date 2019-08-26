package aplikasiku.navwithdirectionlib;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTL;

/**
 * Created by Zulfa_K on 30-05-2017.
 */

public class AdminListTLAdapter extends RecyclerView.Adapter<AdminListTLAdapter.ViewHolder> {
    private NewSkemaTL[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextViewTLName,mTextViewTLAlamat,mTextViewTLProv,mTextViewTLLat,mTextViewTLLong;

        public ViewHolder(View v){
            super(v);
            mTextViewTLName = (TextView) v.findViewById(R.id.textViewListTLName);
            mTextViewTLAlamat = (TextView) v.findViewById(R.id.textViewListTLAlamat);
            mTextViewTLLat = (TextView) v.findViewById(R.id.textViewListTLLat);
            mTextViewTLLong = (TextView) v.findViewById(R.id.textViewListTLLong);
        }
    }

    public AdminListTLAdapter(NewSkemaTL[] myDataset) {
        mDataset = myDataset;
    }

    //implement method
    @Override
    public AdminListTLAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_lampu_admin,parent,false);
        ViewHolder vh = new ViewHolder(rowView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewTLName.setText(mDataset[position].TLname);
        holder.mTextViewTLAlamat.setText(mDataset[position].TLloc);
        holder.mTextViewTLLat.setText(Double.toString(mDataset[position].TLlat));
        holder.mTextViewTLLong.setText(Double.toString(mDataset[position].TLlong));
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}