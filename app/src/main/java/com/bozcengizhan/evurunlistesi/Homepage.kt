package com.bozcengizhan.evurunlistesi

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val currentUser = auth.currentUser

    var itemName by remember { mutableStateOf("") }
    var itemList by remember { mutableStateOf(listOf<ShoppingItem>()) }

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            // Bu satır Row içindeki her şeyi dikeyde tam merkeze hizalar
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol tarafı boşlukla doldurarak başlığı merkeze itiyoruz
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "EV İHTİYAÇ LİSTESİ",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            // Başlık ile İkon arasına esnek bir boşluk koyuyoruz
            // Böylece ikon en sağa yaslanacak
            Spacer(modifier = Modifier.weight(0.1f))

            IconButton(
                onClick = {
                    auth.signOut()
                    onLogout()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Çıkış Yap",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp) // İkon boyutunu yazıya göre biraz büyütebilirsin
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ürün Ekleme Alanı
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                shape = RoundedCornerShape(10.dp),
                value = itemName,
                onValueChange = { itemName = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Yeni ürün ekle...") }
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
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Ekle", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste Alanı
        LazyColumn {
            items(itemList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.name, modifier = Modifier.weight(1f))

                        // "Alındı" butonu (Tıklandığında siler)
                        IconButton(onClick = {
                            db.collection("users").document(currentUser!!.uid)
                                .collection("items").document(item.id).delete()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Sil",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}