package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.ArrowLevels
import com.example.model.GameLevel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val userProfile by gameViewModel.userProfile.collectAsStateWithLifecycle()
                val transactions by gameViewModel.transactions.collectAsStateWithLifecycle()
                val levelProgressMap by gameViewModel.levelProgressMap.collectAsStateWithLifecycle()
                val coroutineScope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        // 0. Startup Splash Screen Route
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("menu") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 1. Main Menu Screen Route
                        composable("menu") {
                            MainMenuScreen(
                                userProfile = userProfile,
                                onNavigateToPlay = {
                                    navController.navigate("play")
                                },
                                onNavigateToLevels = {
                                    navController.navigate("levels")
                                },
                                onNavigateToChallenge = {
                                    navController.navigate("challenge")
                                },
                                onNavigateToRandomPlay = {
                                    val randomLvl = ArrowLevels.levels.random()
                                    gameViewModel.loadLevel(randomLvl)
                                    navController.navigate("play")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                },
                                onNavigateToMembership = {
                                    navController.navigate("membership")
                                },
                                onNavigateToReferral = {
                                    navController.navigate("referral")
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                }
                            )
                        }

                        // Challenge Mode Route
                        composable("challenge") {
                            ChallengeLevelsScreen(
                                onChallengeSelected = { level ->
                                    gameViewModel.loadLevel(level)
                                    navController.navigate("play")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 2. Active Game Puzzle Board Route
                        composable("play") {
                            GameScreen(
                                viewModel = gameViewModel,
                                userProfile = userProfile,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 3. Campaign Levels Stage Route
                        composable("levels") {
                            LevelsScreen(
                                highestLevelUnlocked = userProfile?.highestLevel ?: 1,
                                levelProgressMap = levelProgressMap,
                                onLevelSelected = { selectedLevel ->
                                    gameViewModel.loadLevel(selectedLevel)
                                    navController.navigate("play")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 4. User Profile & Wallet Route
                        composable("profile") {
                            ProfileScreen(
                                userProfile = userProfile,
                                transactions = transactions,
                                onLinkGoogle = { email, name ->
                                    coroutineScope.launch {
                                        val success = gameViewModel.repository.linkGoogleAccount(
                                            googleUserEmail = email,
                                            googleDisplayName = name,
                                            googlePhotoUrl = null
                                        )
                                        if (success) {
                                            Toast.makeText(this@MainActivity, "Google Account Linked successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onLogOut = {
                                    coroutineScope.launch {
                                        gameViewModel.repository.logOutGoogle()
                                        Toast.makeText(this@MainActivity, "Google Account Disconnected.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onWithdrawRequest = { amount ->
                                    gameViewModel.repository.requestWithdrawal(amount)
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 5. Membership Subscription Route
                        composable("membership") {
                            MembershipScreen(
                                userProfile = userProfile,
                                onPurchaseMembership = { type ->
                                    coroutineScope.launch {
                                        val success = gameViewModel.repository.buyMembership(type)
                                        if (success) {
                                            Toast.makeText(this@MainActivity, "Subscribed to ${type.name} plan! Gained package coins bonus.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 6. Referral Promo Code Route
                        composable("referral") {
                            ReferralScreen(
                                userProfile = userProfile,
                                onRedeemReferral = { code ->
                                    coroutineScope.launch {
                                        val res = gameViewModel.repository.redeemReferral(code)
                                        if (res.isSuccess) {
                                            Toast.makeText(this@MainActivity, res.getOrThrow(), Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(this@MainActivity, "Error: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 7. Settings Options Route
                        composable("settings") {
                            SettingsScreen(
                                onResetProgress = {
                                    coroutineScope.launch {
                                        gameViewModel.repository.resetGameProgress()
                                        gameViewModel.resetLevel()
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
