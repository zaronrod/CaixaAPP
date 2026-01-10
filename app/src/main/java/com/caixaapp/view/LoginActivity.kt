package com.caixaapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.caixaapp.databinding.ActivityLoginBinding
import com.caixaapp.model.SettingsRepository

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Habilita o modo ponta a ponta

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository
        settingsRepository = SettingsRepository(this)

        setupLogin()
    }

    private fun setupLogin() {
        val secureLoginStatus = settingsRepository.getSecureLoginStatus()

        if (secureLoginStatus == 1) {
            // Secure Login
            binding.loginTypeLabel.text = "Login Seguro"
            binding.loginButton.setOnClickListener {
                authenticateUser()
            }
        } else {
            // Simple Login (bypass)
            binding.loginTypeLabel.text = "Login Simples"
            binding.loginButton.setOnClickListener {
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        // startActivity(Intent(this, TransactionActivity::class.java))
        // Change the destination from TransactionActivity to MainMenu
        startActivity(Intent(this, MainMenu::class.java))
        finish()
    }

    private fun authenticateUser() {
        val executor = ContextCompat.getMainExecutor(this)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação necessária")
            .setSubtitle("Use a biometria ou o PIN do dispositivo")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                navigateToNextScreen()
            }
        })

        biometricPrompt.authenticate(promptInfo)
    }
}
