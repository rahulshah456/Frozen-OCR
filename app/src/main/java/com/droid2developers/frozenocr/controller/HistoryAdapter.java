package com.droid2developers.frozenocr.controller;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.droid2developers.frozenocr.R;
import com.droid2developers.frozenocr.model.OCRModel;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private static final String TAG = HistoryAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private List<OCRModel> ocrModelList;
    private Context mContext;


    public HistoryAdapter(Context context,List<OCRModel> ocrModelList) {
        mContext = context;
        this.ocrModelList = ocrModelList;
    }


    public interface OnItemClickListener {
        void OnItemClick(int position);
        void OnItemOptionsClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView optionsButton;
        TextView title,description;

        MyViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.imageViewId);
            optionsButton = itemView.findViewById(R.id.optionsButtonId);
            title = itemView.findViewById(R.id.dateHeaderId);
            description = itemView.findViewById(R.id.extraTextId);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.OnItemClick(getLayoutPosition());
                }
            });

            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.OnItemOptionsClick(getLayoutPosition());
                }
            });

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        OCRModel ocrModel = ocrModelList.get(position);

        holder.title.setText(getDate(ocrModel.getTimeStamp()));
        holder.description.setText(ocrModel.getExtaText());
        String imageURL = ocrModel.getImageUri();

        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        Glide.with(mContext)
                .load(imageURL)
                .transition(withCrossFade(factory))
                .thumbnail(0.5f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.imageView);

        ViewCompat.setTransitionName(holder.imageView,String.valueOf(ocrModel.getTimeStamp()));


    }


    @Override
    public int getItemCount() {
        return ocrModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void addImages(List<OCRModel> list){
        int lastSize = ocrModelList.size();
        int newSize = list.size();
        ocrModelList.addAll(list);
        notifyItemRangeChanged(lastSize,newSize);
    }

    public void clearList(){
        ocrModelList.clear();
    }


    public OCRModel getItemAt(int position){
        return ocrModelList.get(position);
    }


    public void removeItem(int position){
        ocrModelList.remove(position);
        notifyDataSetChanged();
    }


    public List<OCRModel> getItemList(){
        return ocrModelList;
    }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("dd-MM-yyyy", cal).toString();
    }

}
