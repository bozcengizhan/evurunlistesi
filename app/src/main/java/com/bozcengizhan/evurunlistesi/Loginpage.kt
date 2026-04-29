package com.bozcengizhan.evurunlistesi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bozcengizhan.evurunlistesi.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun LoginScreen(
    onSignInClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFFFFD1D1) else Color(0xFFFFD1D1)
    val cardColor = if (isDarkTheme) Color(0xFFFF9494) else Color(0xFFFF9494)
    val textColor = if (isDarkTheme) Color.White else Color.White

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Canvas(modifier = Modifier.size(300.dp).align(Alignment.TopStart).offset(x = (-100).dp, y = (-50).dp)) {
                drawCircle(color = Color(0xFFFF9494).copy(alpha = 0.4f))
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Spacer(modifier = Modifier.weight(0.1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = BorderStroke(3.dp, Color.Black),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("S", fontSize = 50.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 6.sp,
                        color = Color.Black
                    )

                    Text(
                        text = stringResource(R.string.login_slogan),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(0.2f))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.login_welcome),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onSignInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            elevation = ButtonDefaults.buttonElevation(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(R.string.login_google_button), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        Text(
                            text = stringResource(R.string.login_terms),
                            fontSize = 11.sp,
                            color = Color.Black.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )

                    }
                }



                Spacer(modifier = Modifier.weight(0.3f))

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    factory = { context ->
                        AdView(context).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = "ca-app-pub-3048251460083679/8505568797"
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )

            }
        }
    }
}