package com.example.pm012p2023;

import static java.util.Base64.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.INotificationSideChannel;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm012p2023.RestApiMethods.Methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class ActivityCreate extends AppCompatActivity {

    Bitmap bitmap;
    ImageView imagen;
    Button btngaleria, btnenviar;

    EditText nombres, apellidos;

    CalendarView calendar;
    static final int Result_galeria = 101;
    String POSTMethod, currentPath;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        ControlSet();

        btngaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GaleriaImagenes();
            }
        });

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConsumeCreateApi();
            }
        });
    }

    private void GaleriaImagenes()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Result_galeria);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;
        if(resultCode == RESULT_OK && requestCode == Result_galeria)
        {
            imageUri = data.getData();
           // imagen.setImageURI(imageUri);
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                imagen.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void ControlSet()
    {
        imagen = (ImageView) findViewById(R.id.imagen);
        btngaleria = (Button) findViewById(R.id.btngaleria);
        btnenviar = (Button) findViewById(R.id.btnenviar);
        nombres= (EditText) findViewById(R.id.nombres);
        apellidos = (EditText) findViewById(R.id.apellidos);
        calendar = (CalendarView) findViewById(R.id.calendar);
    }

    private void ConsumeCreateApi()
    {
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("nombres", nombres.getText().toString());
        parametros.put("apellidos", apellidos.getText().toString());
        parametros.put("fechanac","10-11-2001");
        parametros.put("foto",ImageToBase64(bitmap));

        POSTMethod = Methods.ApiCreate;
        JSONObject JsonAlumn = new JSONObject(parametros);

        RequestQueue peticion = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                POSTMethod,
                JsonAlumn, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i<= jsonArray.length(); i++)
                    {
                        JSONObject msg = jsonArray.getJSONObject(i);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        peticion.add(jsonObjectRequest);
    }

    public static String ImageToBase64(Bitmap image)
    {
      ByteArrayOutputStream arreglo= new ByteArrayOutputStream();
      image.compress(Bitmap.CompressFormat.JPEG,100,arreglo);
      byte[] imageByte = arreglo.toByteArray();
      String imageString = Base64.encodeToString(imageByte, Base64.DEFAULT);
      return imageString;
    }
}