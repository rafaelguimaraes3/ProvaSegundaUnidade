package com.olx.cursoandroid.jamiltondamasceno.vendafacil.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.olx.cursoandroid.jamiltondamasceno.olxapp.R;
import com.olx.cursoandroid.jamiltondamasceno.vendafacil.adapter.AdapterAnuncios;
import com.olx.cursoandroid.jamiltondamasceno.vendafacil.helper.ConfiguracaoFirebase;
import com.olx.cursoandroid.jamiltondamasceno.vendafacil.helper.RecyclerItemClickListener;
import com.olx.cursoandroid.jamiltondamasceno.vendafacil.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //Configurações iniciais
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child( ConfiguracaoFirebase.getIdUsuario() );

        inicializarComponentes();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurar RecyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter( adapterAnuncios );

        //Recupera anúncios para o usuário
        recuperarAnuncios();

        //Adiciona evento de clique no recyclerview
        recyclerAnuncios.addOnItemTouchListener(
            new RecyclerItemClickListener(
                    this,
                    recyclerAnuncios,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                            Anuncio anuncioSelecionado = anuncios.get(position);
                            anuncioSelecionado.remover();

                            adapterAnuncios.notifyDataSetChanged();

                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    }
            )
        );

    }

    private void recuperarAnuncios(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage("Recuperando anúncios")
                .setCancelable( false )
                .build();
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                anuncios.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren() ){
                    anuncios.add( ds.getValue(Anuncio.class) );
                }

                Collections.reverse( anuncios );
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void inicializarComponentes(){

        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);

    }

}
