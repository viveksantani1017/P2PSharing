package com.example.p2psharing;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.p2psharing.R;
import com.example.p2psharing.networking.Client;
import com.example.p2psharing.networking.Listener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);

        EditText etIPAddress = findViewById(R.id.etIPAddress);
        Button btnSend = findViewById(R.id.btnSend);

        Executor clientExecutor = Executors.newSingleThreadExecutor();
        Executor listenerExecutor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        listenerExecutor.execute(() ->
        {
            try
            {
                Listener listener = new Listener();
                Client client = listener.start(5000);

                String fileLocation = String.format("%s/DCIM", Environment.getExternalStorageDirectory().getAbsolutePath());
                client.receiveFile(fileLocation);

                handler.post(() -> Toast.makeText(MainActivity.this, "File received!", Toast.LENGTH_LONG).show());
            }
            catch (Exception ex)
            {
                Log.e("LISTENER ERROR", ex.toString());
            }
        });

        btnSend.setOnClickListener(v ->
        {
            String ipAddress = etIPAddress.getText().toString();
            clientExecutor.execute(() ->
            {
                try
                {
                    Client client = new Client(MainActivity.this);
                    client.connect(ipAddress, 5000);

                    String fileLocation = String.format("%s/Download/MT.png", Environment.getExternalStorageDirectory().getAbsolutePath());
                    client.sendFile(fileLocation);
                }
                catch (Exception ex)
                {
                    Log.e("CLIENT ERROR", ex.toString());
                }
            });
        });
    }
}