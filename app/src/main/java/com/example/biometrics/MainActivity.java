 package com.example.biometrics;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    Button print;
    ImageView image;
    TextView tvstatus;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print = (Button) findViewById(R.id.print);
        image = (ImageView) findViewById(R.id.image);
        tvstatus = (TextView) findViewById(R.id.tvs);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) != PackageManager
                .PERMISSION_GRANTED) {
            tvstatus.setText("Biometric authentication permission not granted.");
        } else {
            FingerprintManagerCompat fmc =
                    FingerprintManagerCompat.from(this);

            if (!fmc.isHardwareDetected()) {
               tvstatus.setText("Step 1: There is no fingerprint sensor hardware found.");
           } else if (!fmc.hasEnrolledFingerprints()) {
                tvstatus.setText("Step 1: There are no fingerprints currently enrolled.");
            } else {
                tvstatus.setText("Step 1: Fingerprint authentication is ready for testing.");

                BiometricPrompt biometricPrompt = new BiometricPrompt
                        .Builder(this)
                        .setTitle("Biometric Authentication")
                        .setSubtitle("Please authenticate to continue")
                        .setDescription("Fingerprinting in biometric authentication API is being tested.")
                        .setNegativeButton("Cancel", this.getMainExecutor(),
                                new DialogInterface.OnClickListener() {
                             @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tvstatus.setText(tvstatus.getText() + "Step 2: Fingerprint authentication cancelled.");
                            }
                        })
                        .build();

                // Authenticate with callback functions
                biometricPrompt.authenticate(getCancellationSignal(),
                        getMainExecutor(),
                        getAuthenticationCallback());

            }
        }
      }
    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback
    getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                tvstatus.setText(tvstatus.getText() + "Step 2: Fingerprint authentication error: " + errString);
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                tvstatus.setText(tvstatus.getText() + "Step 2: Fingerprint authentication Succeeded.");
                super.onAuthenticationSucceeded(result);
            }
        };
    }
    private CancellationSignal cancellationSignal;
    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                tvstatus.setText(tvstatus.getText() + "Step 2: Fingerprint authentication cancelled by signal.");
            }
        });

        return cancellationSignal;
    }
}



