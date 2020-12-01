package com.example.webservicerv;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
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

        //Instanciar los controles del activity_main
        txtTitel = findViewById(R.id.txtMensaje);

        txtdataKushki = findViewById(R.id.txtDataKushki);
        txtdataKushki.setMovementMethod(new ScrollingMovementMethod());

        spOption = findViewById(R.id.spLibrary);

        //Instanciar el objeto requestQueue con la librería de Volley
        requestQueue = Volley.newRequestQueue(this);

        //Crear un ArrayAdapter para colocar en el Spinner de la App
        ArrayAdapter<CharSequence> listOption = ArrayAdapter.createFromResource(this, R.array.optionWebService,
                android.R.layout.simple_spinner_item);
        //Colocar la lista de datos en el Spinner
        spOption.setAdapter(listOption);
    }

    //Permite obtener todos los datos que se encuentrá en la API correspondiente en este caso "Kushki Pagos"

    private void getKushkipagoRetrofit()
    {
        //Se crea una variable tipo Retrofit para obtener los datos JSON de la API correspondiente, utilizando su convertidor
        Retrofit retrofit = new  Retrofit.Builder().baseUrl("https://api-uat.kushkipagos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Se crea una variable de la interfaz que me permite obtener los datos de la API
        KushkipagosR kushkipagosR =  retrofit.create(KushkipagosR.class);

        //Mapear los datos tipo JSON utilizando las variables de la librería de Retrofit
        Call<List<DataKushki>> call = kushkipagosR.getKushki();

        //Mostrar los datos que encontro Retrofit en la API que fue asignada
        call.enqueue(new Callback<List<DataKushki>>() {
            @Override
            public void onResponse(Call<List<DataKushki>> call, Response<List<DataKushki>> response) {
                //Permite identificar si existe un error tipo HTTP esta me identifica que código de estado HTTP me retorna
                if(!response.isSuccessful())
                {
                    txtdataKushki.setText("Código: " + response.code());
                    return;
                }

                List<DataKushki> kushkiList = response.body();

                //Mostrar los datos en el TextView
                for (DataKushki data: kushkiList)
                {
                    SpannableString myTextCode = new SpannableString("Código: " + data.getCode() + "\n");
                    SpannableString myTextName = new SpannableString("Nombre: " + data.getName() + "\n\n");
                    StyleSpan bold = new StyleSpan(Typeface.BOLD);
                    StyleSpan bold2 = new StyleSpan(Typeface.BOLD);

                    myTextCode.setSpan(bold, 0 , 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    myTextName.setSpan(bold2, 0 , 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    txtdataKushki.append(myTextCode);
                    txtdataKushki.append(myTextName);
                }
            }

            @Override
            public void onFailure(Call<List<DataKushki>> call, Throwable t) {
                //Mostrar mensaje de error
                String msj = "Mensaje de error: " + t.getMessage();
                txtdataKushki.setText(msj);
            }
        });

    }

    //Permite obtener todos los datos que se encuentrá en la API correspondiente en este caso "Kushki Pagos"
    private void getKushkipagoVolley()
    {
        //Declara una variable JsonArrayRequest para obtener los datos tipo JSON de la API
        //En vez de utilizar nuevas instacias de objetos, se utiliza lambda
        JsonArrayRequest dataVolley = new JsonArrayRequest(
                Request.Method.GET, URL, null,
                response -> {
                    int size = response.length();
                    for(int i = 0; i < size; i++)
                    {
                        try {
                            JSONObject data = new JSONObject(response.get(i).toString());

                            SpannableString myTextCode = new SpannableString("Código: " + data.getString("code") + "\n");
                            SpannableString myTextName = new SpannableString("Nombre: " + data.getString("name") + "\n\n");
                            StyleSpan bold = new StyleSpan(Typeface.BOLD);
                            StyleSpan bold2 = new StyleSpan(Typeface.BOLD);

                            myTextCode.setSpan(bold, 0 , 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            myTextName.setSpan(bold2, 0 , 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            txtdataKushki.append(myTextCode);
                            txtdataKushki.append(myTextName);
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
            Toast.makeText(this, "Su petición está siendo procesada.....", Toast.LENGTH_LONG).show();
            getKushkipagoRetrofit();
        }
        else if(spOption.getSelectedItem().toString().toUpperCase().equals("Volley".toUpperCase()))
        {
            Toast.makeText(this, "Su petición está siendo procesada.....", Toast.LENGTH_LONG).show();
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