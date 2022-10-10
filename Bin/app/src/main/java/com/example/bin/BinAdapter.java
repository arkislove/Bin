package com.example.bin;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import androidx.annotation.NonNull;

public class BinAdapter extends FirebaseRecyclerAdapter<BinModel,BinAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BinAdapter(@NonNull FirebaseRecyclerOptions<BinModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull BinModel model) {
       holder.binNo.setText(model.getBin_number());
       holder.fillLevel.setText(model.getFill_level());
       holder.location.setText(model.getLocation());

       Glide.with(holder.img.getContext())
               .load(model.getSurl())
               .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
               .circleCrop()
               .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
               .into(holder.img);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true,1200)
                        .create();

                //dialogPlus.show();

                View view1 = dialogPlus.getHolderView();
                EditText binNo = view1.findViewById(R.id.txtBinNo);
                EditText fillLevel = view1.findViewById(R.id.txtFillLevel);

                Button btnEdit = view1.findViewById((R.id.btnUpdate));
                Button btnDelete = view1.findViewById((R.id.btnDelete));


                binNo.setText(model.getBin_number());
                fillLevel.setText(model.getFill_level());

                dialogPlus.show();

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("bin_number",binNo.getText().toString());
                        map.put("fill_level",fillLevel.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Bins")
                                .child(getRef(holder.getBindingAdapterPosition()).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.binNo.getContext(),"Update Successfully",Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Toast.makeText(holder.binNo.getContext(),"Update Failed",Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });

                    }


                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.binNo.getContext());
                        builder.setTitle("Are You Sure?");
                        builder.setMessage("Deleted Data Can't Be Undo.");

                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("Bins")
                                        .child(getRef(holder.getBindingAdapterPosition()).getKey()).removeValue();

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(holder.binNo.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                });


            }

        });
}
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView binNo;
        TextView fillLevel;
        TextView location;

        Button btnEdit, btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img1);
            binNo = itemView.findViewById(R.id.bin_number);
            fillLevel = itemView.findViewById(R.id.fill_level);
            location = itemView.findViewById(R.id.locationText);

            btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button)itemView.findViewById(R.id.btnDelete);

        }
    }

}