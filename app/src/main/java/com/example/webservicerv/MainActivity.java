package com.example.webservicerv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.webservicerv.Interface.KushkipagosR;
import com.example.webservicerv.Model.DataKushki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Spinner spOption;
    TextView txtdataKushki, txtTitel;
    RequestQueue requestQueue;

    private final String URL = "https://api-uat.kushkipagos.com/transfer/v1/bankList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTitel = findViewById(R.id.txtMensaje);

        txtdataKushki = findViewById(R.id.txtDataKushki);
        txtdataKushki.setMovementMethod(new ScrollingMovementMethod());

        spOption = findViewById(R.id.spLibrary);

        requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> listOption = ArrayAdapter.createFromResource(this, R.array.optionWebService,
                android.R.layout.simple_spinner_item);

        spOption.setAdapter(listOption);
    }

    private void getKushkipagoRetrofit()
    {
        Retrofit retrofit = new  Retrofit.Builder().baseUrl("https://api-uat.kushkipagos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KushkipagosR kushkipagosR =  retrofit.create(KushkipagosR.class);

        Call<List<DataKushki>> call = kushkipagosR.getKushki();

        call.enqueue(new Callback<List<DataKushki>>() {
            @Override
            public void onResponse(Call<List<DataKushki>> call, Response<List<DataKushki>> response) {
                if(!response.isSuccessful())
                {
                    txtdataKushki.setText("Código: " + response.code());
                    return;
                }

                List<DataKushki> kushkiList = response.body();

                for (DataKushki data: kushkiList)
                {
                    String content = "";
                    content += "code: " + data.getCode() + "\n";
                    content += "name: " + data.getName() + "\n";

                    txtdataKushki.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<DataKushki>> call, Throwable t) {
                String msj = "Mensaje de error: " + t.getMessage();
                txtdataKushki.setText(msj);
            }
        });

    }

    private void getKushkipagoVolley()
    {
        JsonArrayRequest dataVolley = new JsonArrayRequest(
                Request.Method.GET, URL, null,
                response -> {
                    int size = response.length();
                    for(int i = 0; i < size; i++)
                    {
                        try {
                            JSONObject objet = new JSONObject(response.get(i).toString());
                            String content = "";
                            content += "code: " + objet.getString("code") + "\n";
                            content += "name: " + objet.getString("name") + "\n";

                            txtdataKushki.append(content);
                        } catch (JSONException e) {
                            String msj = "Mensaje de error: " + e.getMessage();
                            txtdataKushki.setText(msj);
                        }

                    }
                },
                error -> {
                    String msj = "Mensaje de error: " + error.getMessage();
                    txtdataKushki.setText(msj);
                }
        )
        {
            public Map getHeaders()
            {
                HashMap headers = new HashMap();
                headers.put("Public-Merchant-Id","8376ea5f58f44f2fb3304faddcfd9660");
                return headers;
            }
        };
        requestQueue.add(dataVolley);
    }

    public void btnActualizar_Click(View view)
    {
        txtTitel.setText("Utilizando la librería " + spOption.getSelectedItem());
        txtdataKushki.setText("");
        if(spOption.getSelectedItem().toString().toUpperCase().equals("Retrofit".toUpperCase()))
        {
            getKushkipagoRetrofit();
        }
        else if(spOption.getSelectedItem().toString().toUpperCase().equals("Volley".toUpperCase()))
        {
            getKushkipagoVolley();
        }
        else
        {
            txtTitel.setText("Título de librería");
            Toast.makeText(spOption.getContext(), "Seleccionar una librería para mostrar datos", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(spOption.getContext(), "Selección: " + spOption.getSelectedItem(), Toast.LENGTH_LONG).show();
    }
}