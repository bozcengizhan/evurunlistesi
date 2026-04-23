package com.bozcengizhan.evurunlistesi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bozcengizhan.evurunlistesi.ui.theme.EvurunlistesiTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.animation.*
import androidx.compose.animation.core.tween

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("420012590198-q8r51hjhmo08ti14oi8esjl88g1c438d.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            EvurunlistesiTheme {
                val auth = FirebaseAuth.getInstance()
                var currentUser by remember { mutableStateOf(auth.currentUser) }

                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose { auth.removeAuthStateListener(listener) }
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                        auth.signInWithCredential(credential).addOnCompleteListener { taskResult ->
                            if (taskResult.isSuccessful) {
                                currentUser = auth.currentUser
                                println("Giriş Başarılı: ${auth.currentUser?.displayName}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Giriş Hatası: ${e.message}")
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // AnimatedContent, currentUser değiştiğinde geçişi otomatik animasyonlu yapar
                        AnimatedContent(
                            targetState = currentUser,
                            transitionSpec = {
                                // Giriş mi yapıyoruz (null -> user) yoksa çıkış mı (user -> null)?
                                if (targetState != null) {
                                    // GİRİŞ: Aşağıdan yukarı süzülerek gel
                                    (slideInVertically(animationSpec = tween(600), initialOffsetY = { it }) +
                                            fadeIn(animationSpec = tween(600)))
                                        .togetherWith(fadeOut(animationSpec = tween(400)))
                                } else {
                                    // ÇIKIŞ: Yukarıdan aşağıya süzülerek git
                                    fadeIn(animationSpec = tween(500)) // Yeni ekran (Login) yavaşça belirsin
                                        .togetherWith(
                                            slideOutVertically(animationSpec = tween(600), targetOffsetY = { it }) +
                                                    fadeOut(animationSpec = tween(600))
                                        )
                                }
                            },
                            label = "ScreenTransition"
                        ) { targetUser ->
                            if (targetUser == null) {
                                LoginScreen(
                                    onSignInClick = {
                                        val signInIntent = googleSignInClient.signInIntent
                                        launcher.launch(signInIntent)
                                    }
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    HomeScreen(onLogout = {
                                        googleSignInClient.signOut().addOnCompleteListener {
                                            // Firebase Auth Listener zaten currentUser'ı güncelleyip
                                            // animasyonu tetikleyecektir.
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


