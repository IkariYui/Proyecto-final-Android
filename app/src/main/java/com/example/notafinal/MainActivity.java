package com.example.notafinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText jetCodigo, jetNombre;
    RadioButton jrbAccion, jrbFantasia, jrbSuspenso;
    Boolean respuesta;
    CheckBox jcbStock;
    String Codigo, Nombre, Genero, Stock, Ident_Doc;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        jetCodigo = findViewById(R.id.etCodigo);
        jetNombre = findViewById(R.id.etNombre);
        jrbAccion = findViewById(R.id.rbAccion);
        jrbFantasia = findViewById(R.id.rbFantasia);
        jrbSuspenso = findViewById(R.id.rbSuspenso);
        jcbStock = findViewById(R.id.cbStock);

    }

    public void Adicionar(View view) {
        Codigo = jetCodigo.getText().toString();
        Nombre = jetNombre.getText().toString();

        if (Codigo.isEmpty() || Nombre.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetCodigo.requestFocus();
        } else {
            if (jrbAccion.isChecked())
                Genero = "Acción";
            else if (jrbFantasia.isChecked())
                Genero = "Fantasia";

            else
                Genero = "Suspenso";

            Map<String, Object> pelicula = new HashMap<>();
            pelicula.put("Codigo", Codigo);
            pelicula.put("Nombre", Nombre);
            pelicula.put("Genero", Genero);
            pelicula.put("Stock", "si");

// Add a new document with a generated ID
            db.collection("Peliculas")
                    .add(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "Pelicula Adicionada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MainActivity.this, "Error al adicionar", Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }

    public void Consultar(View view) {
        Buscar_Pelicula();

    }

    public void Buscar_Pelicula() {
        respuesta = false;
        Codigo = jetCodigo.getText().toString();
        if (Codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es necesario", Toast.LENGTH_SHORT).show();
            jetCodigo.requestFocus();
        } else {
            db.collection("Peliculas")
                    .whereEqualTo("Codigo",Codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    respuesta=true;
                                    if (document.getString("Stock").equals("no")){
                                        Toast.makeText(MainActivity.this, "El documento existe pero no está activo", Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                    }
                                    Ident_Doc=document.getId();
                                    jetNombre.setText(document.getString("Nombre"));
                                    jetCodigo.setText(document.getString("Codigo"));
                                    if (document.getString("Genero").equals("Accion"))
                                        jrbAccion.setChecked(true);
                                    else
                                    if (document.getString("Genero").equals("Fantasia"))
                                        jrbFantasia.setChecked(true);
                                    else
                                        jrbSuspenso.setChecked(true);
                                    if (document.getString("Stock").equals("si"))
                                        jcbStock.setChecked(true);
                                    else
                                        jcbStock.setChecked(false);
                                    //  Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                // Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

        }
    }



    public void Anular (View view){
    Codigo=jetCodigo.getText().toString();
    Nombre=jetNombre.getText().toString();

    if (Codigo.isEmpty()) {
        Toast.makeText(this, "El codigo es requerido", Toast.LENGTH_SHORT).show();
        jetCodigo.requestFocus();

    }
    else{
        if (respuesta==true){
            if (jrbAccion.isChecked())
                Genero="Accion";
            else if(jrbFantasia.isChecked())
                Genero="Fantasia";
            else
                Genero="Suspenso";

            Map<String, Object> pelicula = new HashMap<>();
            pelicula.put("Codigo", Codigo);
            pelicula.put("Nombre", Nombre);
            pelicula.put("Genero", Genero);
            pelicula.put("Stock", "no");

            db.collection("Peliculas").document(Ident_Doc)
                    .set(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Pelicula anulada", Toast.LENGTH_SHORT).show();
                           // Limpiar();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error al anular", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else{
            Toast.makeText(this, "Debe primero consultar", Toast.LENGTH_SHORT).show();
            jetCodigo.requestFocus();
        }
    }

    }



    public void Limpiar(View view){
        jetCodigo.setText("");
        jetNombre.setText("");
        jrbAccion.setChecked(true);
        jcbStock.setChecked(false);
        jetCodigo.requestFocus();
        respuesta=false;
    }
}

