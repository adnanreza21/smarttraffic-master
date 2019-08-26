package aplikasiku.navwithdirectionlib;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTL;

/**
 * Created by Zulfa_K on 07-10-2017.
 */

public class KontrolerListTLAdapter extends RecyclerView.Adapter<KontrolerListTLAdapter.TLViewHolder> {

    private ViewHolderClickListener mOnViewHolderClickListener;
    private DataTL[] dataTL;

    public static class DataTL{
        String uid_tl;
        String TLname;
        String TLloc;
        boolean isSelected;

        public DataTL(){}
    }

    public KontrolerListTLAdapter(DataTL[] listTL, ViewHolderClickListener viewHolderClick){
        this.dataTL = listTL;
        mOnViewHolderClickListener = viewHolderClick;
    }

    public void setDataTL(DataTL[] dtTL){
        dataTL = dtTL;
    }

    //viewholder
    public class TLViewHolder extends RecyclerView.ViewHolder{
        //ui component item
        TextView textViewTLname, textViewTLloc;
        RadioButton radioButtonTLselected;

        public TLViewHolder(final View itemView) {
            super(itemView);
            //findviewbyid
            textViewTLname = (TextView) itemView.findViewById(R.id.textView_kontroler_tlname);
            textViewTLloc = (TextView) itemView.findViewById(R.id.textView_kontroler_tlloc);
            radioButtonTLselected = (RadioButton) itemView.findViewById(R.id.radio_kontroler_tlselected);

            radioButtonTLselected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioButtonTLselected.setChecked(false);
                    mOnViewHolderClickListener.onViewHolderClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    public KontrolerListTLAdapter.TLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_lampu_kontroler,parent,false);
        final TLViewHolder viewHolder = new TLViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnViewHolderClickListener.onViewHolderClick(view, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(KontrolerListTLAdapter.TLViewHolder holder, int position) {
        holder.textViewTLname.setText(dataTL[position].TLname);
        holder.textViewTLloc.setText(dataTL[position].TLloc);
        holder.radioButtonTLselected.setChecked(dataTL[position].isSelected);
    }

    @Override
    public int getItemCount() {
        return dataTL.length;
    }

    public interface ViewHolderClickListener{
        void onViewHolderClick(View v, int position);
    }

}