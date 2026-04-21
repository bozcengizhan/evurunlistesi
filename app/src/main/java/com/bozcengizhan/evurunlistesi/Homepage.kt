package com.bozcengizhan.evurunlistesi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser

    var itemName by remember { mutableStateOf("") }
    var itemList by remember { mutableStateOf(listOf<ShoppingItem>()) }

    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFFFFD1D1) else Color(0xFFFFD1D1)
    val topbarColor = if (isDarkTheme) Color(0xFFFF9494) else Color(0xFFFF9494)
    val cardColor = if (isDarkTheme) Color.White else Color.White
    val textColor = if (isDarkTheme) Color.Black else Color.Black
    val iconColor = if (isDarkTheme) Color(0xFFFF8080) else Color(0xFFFF8080)

    // Verileri Firestore'dan anlık olarak çekme
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("items")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        itemList = snapshot.documents.map { doc ->
                            ShoppingItem(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                isBought = doc.getBoolean("isBought") ?: false
                            )
                        }
                    }
                }
        }
    }

    Column(modifier = Modifier.background(backgroundColor).fillMaxSize().padding(top = 16.dp).padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Üst Başlık ve Logout Butonu Sabit Kalsın
        Row(
            modifier = Modifier.fillMaxWidth().background(topbarColor, shape = RoundedCornerShape(8.dp)).border(3.dp, Color.Black, shape = RoundedCornerShape(8.dp)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "STACK",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(0.25f))
            IconButton(onClick = { auth.signOut(); onLogout() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Çıkış Yap",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.weight(0.4f))
        }

        // Liste Alanı
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // --- DEĞİŞİKLİK BURADA: Ekleme alanını listenin ilk öğesi yapıyoruz ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), // Altına boşluk ekledik
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                        placeholder = { Text("Yeni ürün ekle...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black
                        )
                    )
                    TextButton(
                        onClick = {
                            if (itemName.isNotBlank()) {
                                val newItem = hashMapOf("name" to itemName, "isBought" to false)
                                db.collection("users").document(currentUser!!.uid).collection("items")
                                    .add(newItem)
                                itemName = ""
                            }
                        },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text("Ekle", fontSize = 24.sp, color = Color.Black)
                    }
                }
            }

            // Mevcut ürün listesi
            items(itemList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(vertical = 6.dp).background(Color.White,shape = RoundedCornerShape(16.dp)).shadow(8.dp, RoundedCornerShape(16.dp)),
                ) {
                    Row(
                        modifier = Modifier.background(cardColor, shape = RoundedCornerShape(16.dp)).padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.name, modifier = Modifier.weight(1f), color = textColor)
                        IconButton(onClick = {
                            db.collection("users").document(currentUser!!.uid)
                                .collection("items").document(item.id).delete()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = iconColor
                            )
                        }
                    }
                }
            }
        }
    }
}
