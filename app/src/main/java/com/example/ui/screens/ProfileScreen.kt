package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    transactions: List<WalletTransaction>,
    onLinkGoogle: (email: String, name: String) -> Unit,
    onLogOut: () -> Unit,
    onWithdrawRequest: suspend (Int) -> Result<String>,
    onBack: () -> Unit
) {
    var showCashOutDialog by remember { mutableStateOf(false) }
    var cashOutAmountString by remember { mutableStateOf("") }
    var cashOutError by remember { mutableStateOf<String?>(null) }
    var cashOutSuccess by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PROFILE & WALLET",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F172A),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Profile Info Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar Badge
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (userProfile?.displayName?.take(1) ?: "G").uppercase(),
                                    color = Color(0xFF38BDF8),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = userProfile?.displayName ?: "Guest Player",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = userProfile?.email ?: "Offline Guest Account",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Google Sign-In Action
                            if (userProfile?.isGuest == true) {
                                Button(
                                    onClick = {
                                        // Simulate a successful credential manager Google sign-in securely
                                        onLinkGoogle("muhammadawais8226@gmail.com", "Muhammad Awais")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("google_signin_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudSync,
                                            contentDescription = "CloudSync",
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sign in with Google", fontWeight = FontWeight.Bold)
                                    }
                                }
                                
                                // Guest Mode Sync Warning
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Warning: If you do not Google Sign-In and clear app data or uninstall, your local progress, referral codes, and wallets will be permanently lost.",
                                            color = Color(0xFFFCA5A5),
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = onLogOut,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Log Out Google Account", color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Balance Sheet cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color(0xFF10B981))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Game Wallet", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(
                                    text = "${userProfile?.wallets?.gameCoins ?: 500} Coins",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Cash Wallet", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(
                                    text = "${userProfile?.wallets?.cashCoins ?: 0} Coins",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Cash Out Trigger Button
                item {
                    Button(
                        onClick = {
                            cashOutError = null
                            cashOutSuccess = null
                            cashOutAmountString = ""
                            showCashOutDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("cash_out_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CASH OUT COINS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Transaction Ledger
                item {
                    Text(
                        text = "TRANSACTION LEDGER",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFF1E293B).copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No transactions recorded yet.",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    items(transactions) { tx ->
                        TransactionItemRow(tx = tx)
                    }
                }
            }
        }

        // Cash Out Popup Dialog
        if (showCashOutDialog) {
            AlertDialog(
                onDismissRequest = { showCashOutDialog = false },
                title = { Text("Cash Out Portal", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Deduct from your Cash Wallet balance to submit a withdrawal request. Minimum limit is 500 Coins.",
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = cashOutAmountString,
                            onValueChange = { cashOutAmountString = it },
                            label = { Text("Amount to Cash Out") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("cash_out_input")
                        )
                        if (cashOutError != null) {
                            Text(
                                text = cashOutError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        if (cashOutSuccess != null) {
                            Text(
                                text = cashOutSuccess!!,
                                color = Color.Green,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = cashOutAmountString.toIntOrNull()
                            if (amount == null || amount <= 0) {
                                cashOutError = "Please enter a valid numeric value."
                                return@Button
                            }
                            coroutineScope.launch {
                                val res = onWithdrawRequest(amount)
                                if (res.isSuccess) {
                                    cashOutSuccess = res.getOrNull()
                                    cashOutError = null
                                } else {
                                    cashOutError = res.exceptionOrNull()?.message ?: "Transaction failed."
                                    cashOutSuccess = null
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        modifier = Modifier.testTag("confirm_cash_out_button")
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCashOutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun TransactionItemRow(tx: WalletTransaction) {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(Date(tx.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${tx.type.name.replace("_", " ")} • $formattedDate",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (tx.gameAmount != 0) {
                    Text(
                        text = (if (tx.gameAmount > 0) "+" else "") + "${tx.gameAmount} Game",
                        color = if (tx.gameAmount > 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (tx.cashAmount != 0) {
                    Text(
                        text = (if (tx.cashAmount > 0) "+" else "") + "${tx.cashAmount} Cash",
                        color = if (tx.cashAmount > 0) Color(0xFFF59E0B) else Color(0xFFEF4444),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
