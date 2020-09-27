package com.ssi.audiotolibras;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    //inicializando o espaço para o texto retornar
    private TextView txtResult;
    private TextView txtGlosa;

    //post test
    private RequestQueue requestQueue;
    private Button btnSendToServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResult = (TextView) findViewById(R.id.txvResult);
        txtGlosa = (TextView) findViewById(R.id.txtGlosa);

        //post test
        btnSendToServer = (Button)findViewById(R.id.btnSendToServer);

        btnSendToServer.setOnClickListener((v) -> {
            String data =
            "{"+"\"text\"" + ":" + " " + "\"" + txtResult.getText().toString() + "\" "+"}";
            post(data);
        });
    }

    //ação que está associada a imagem do microfone
    public void getSpeechInput(View view) {

        //inicia uma acitivity que ira pegar o speech do usuario e enviar para o recognizer. os reultados serão
        //retornados via resultados em uma atividade
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //este parametro extra pegará a lingua defaut do seu android
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        //validando se o dispositivo suporta a operção
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Seu dispositivo não suporta Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtResult.setText(result.get(0));
                }
                break;
        }
    }


    //requisição post

    public void post(String data) {
        final String savedata = data;
        String url = "https://traducao2.vlibras.gov.br/translate";
        Log.d("String url", url);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                txtGlosa.setText(response);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
        })
        {
            @Override
            public String getBodyContentType(){ return "application/json; charset=utf-8";}

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.d("Request", savedata);
                try{
                    return savedata==null ? null : savedata.getBytes("utf-8");
                }catch (UnsupportedEncodingException uee){
                    return null;
                }
            }
        };
        requestQueue.add(stringRequest);
        Log.d("Data", data.toString());
        Log.d("SaveData", savedata.toString());
        Log.d("POST REQUEST", stringRequest.toString());

    }
}