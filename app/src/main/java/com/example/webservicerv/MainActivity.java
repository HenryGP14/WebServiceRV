package com.example.webservicerv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.webservicerv.Interface.KushkipagosR;
import com.example.webservicerv.Model.DataKushki;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Spinner spOption;
    TextView txtdataKushki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spOption = findViewById(R.id.spLibrary);
        txtdataKushki = findViewById(R.id.txtDataKushki);

        ArrayAdapter<CharSequence> listOption = ArrayAdapter.createFromResource(this, R.array.optionWebService,
                android.R.layout.simple_spinner_item);

        spOption.setAdapter(listOption);
    }

    private void getKushkipago()
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
                txtdataKushki.setText(t.getMessage());
            }
        });

    }

    public void btnActualizar_Click(View view)
    {
        getKushkipago();
    }
}