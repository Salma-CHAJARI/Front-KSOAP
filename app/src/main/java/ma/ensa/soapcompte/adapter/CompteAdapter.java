package ma.ensa.soapcompte.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ma.ensa.soapcompte.R;
import ma.ensa.soapcompte.bean.Compte;

public class CompteAdapter extends RecyclerView.Adapter<CompteAdapter.CompteViewHolder> {

    // Initialisation de la liste comme une liste vide
    private List<Compte> comptes = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener;

    public void updateComptes(List<Compte> newComptes) {
        comptes.clear();
        if (newComptes != null) {
            comptes.addAll(newComptes);
        }
        notifyDataSetChanged();
    }

    public void setOnDeleteClick(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @Override
    public CompteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compte, parent, false);
        return new CompteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompteViewHolder holder, int position) {
        holder.bind(comptes.get(position));
    }

    @Override
    public int getItemCount() {
        // Pas besoin de vérifier nullité ici car la liste est déjà initialisée
        return comptes.size();
    }

    public void removeCompte(Compte compte) {
        int position = comptes.indexOf(compte);
        if (position >= 0) {
            comptes.remove(position);
            notifyItemRemoved(position);  // Notifie l'adaptateur qu'un item a été supprimé
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Compte compte);
    }

    public class CompteViewHolder extends RecyclerView.ViewHolder {

        private TextView tvId;
        private TextView tvSolde;
        private Chip tvType;
        private TextView tvDate;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public CompteViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvSolde = itemView.findViewById(R.id.tvSolde);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final Compte compte) {
            tvId.setText("Compte N° " + compte.getId());
            tvSolde.setText(compte.getSolde() + " DH");
            tvType.setText(compte.getType().name());
            tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(compte.getDateCreation()));

            btnEdit.setOnClickListener(v -> {
                // Implémenter ici l'édition si nécessaire
            });

            btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(compte);
                }
            });
        }
    }
}
