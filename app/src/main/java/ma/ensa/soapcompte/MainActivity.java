package ma.ensa.soapcompte;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import ma.ensa.soapcompte.adapter.CompteAdapter;
import ma.ensa.soapcompte.bean.Compte;
import ma.ensa.soapcompte.bean.TypeCompte;
import ma.ensa.soapcompte.ws.SoapService;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private CompteAdapter adapter;
    private SoapService soapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadComptes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        adapter = new CompteAdapter();
        soapService = new SoapService();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClick(compte -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Supprimer le compte")
                    .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        // Exécuter la tâche de suppression en arrière-plan
                        new DeleteCompteTask(compte).execute();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddCompteDialog());
    }

    private void showAddCompteDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_compte, null);

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Nouveau compte")
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    TextInputEditText etSolde = dialogView.findViewById(R.id.etSolde);
                    RadioButton radioCourant = dialogView.findViewById(R.id.radioCourant);

                    double solde = Double.parseDouble(etSolde.getText().toString());
                    TypeCompte type = radioCourant.isChecked() ? TypeCompte.COURANT : TypeCompte.EPARGNE;

                    // Créer un nouveau compte en arrière-plan avec AsyncTask
                    new CreateCompteTask(solde, type).execute();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void loadComptes() {
        new LoadComptesTask().execute();
    }

    // AsyncTask pour charger les comptes en arrière-plan
    private class LoadComptesTask extends AsyncTask<Void, Void, List<Compte>> {

        @Override
        protected List<Compte> doInBackground(Void... voids) {
            try {
                return soapService.getComptes();  // Charger les comptes
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Compte> comptes) {
            super.onPostExecute(comptes);
            if (comptes != null && !comptes.isEmpty()) {
                adapter.updateComptes(comptes);
            } else {
                Toast.makeText(MainActivity.this, "Aucun compte trouvé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask pour créer un compte
    private class CreateCompteTask extends AsyncTask<Void, Void, Void> {
        private double solde;
        private TypeCompte type;

        public CreateCompteTask(double solde, TypeCompte type) {
            this.solde = solde;
            this.type = type;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                soapService.createCompte(solde, type);  // Créer le compte
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadComptes();  // Recharger la liste des comptes après la création
        }
    }

    // AsyncTask pour supprimer un compte
    private class DeleteCompteTask extends AsyncTask<Void, Void, Boolean> {
        private Compte compte;

        public DeleteCompteTask(Compte compte) {
            this.compte = compte;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Suppression du compte sur le serveur
                return soapService.deleteCompte(compte.getId());
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Retourner false en cas d'échec
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success) {
                // Si la suppression a réussi, on retire l'élément de la liste dans l'UI
                adapter.removeCompte(compte);
                Toast.makeText(MainActivity.this, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show();
            } else {
                // Si la suppression a échoué, on affiche un message d'erreur
                Toast.makeText(MainActivity.this, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
