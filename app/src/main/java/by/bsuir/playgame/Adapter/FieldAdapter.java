package by.bsuir.playgame.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import by.bsuir.playgame.R;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.MyHolder> {

    private Context context;
    private int images[];
    private String names[];
    protected ItemListener mListener;

    public FieldAdapter(Context context, int[] images, String[] names, ItemListener mListener) {
        this.context = context;
        this.images = images;
        this.names = names;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout,null);

        MyHolder myHolder = new MyHolder(layout);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.img.setImageResource(images[position]);
        holder.txt.setText(names[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public interface ItemListener {
        void onItemClick(String idField);
    }


    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView img;
        TextView txt;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            img = itemView.findViewById(R.id.imageView);
            txt = itemView.findViewById(R.id.textView);
            txt.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(txt.getText().toString());
            }
        }
    }
}
