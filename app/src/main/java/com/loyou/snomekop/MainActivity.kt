package com.loyou.snomekop

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.loyou.snomekop.ui.list.ListScreen
import com.loyou.snomekop.ui.pokemon.PokemonStatsScreen
import theme.SnomekopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnomekopTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "list_screen"
                ) {
                    composable("list_screen") {
                        ListScreen(navController = navController)
                    }

                    composable("pokemon_details/{pokemonName}/{index}/{dominantColor1}/{dominantColor2}",
                                arguments = listOf(
                                    navArgument("pokemonName") {
                                        type = NavType.StringType
                                    },
                                    navArgument("index") {
                                        type = NavType.IntType
                                    },
                                    navArgument("dominantColor1") {
                                        type = NavType.IntType
                                    },
                                    navArgument("dominantColor2") {
                                        type = NavType.IntType
                                    }
                                )
                    ) {
                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }
                        val index = remember {
                            it.arguments?.getInt("index")
                        }
                        val dominantColors = remember {
                            val c1 = it.arguments?.getInt("dominantColor1") ?: 0xFFFFFF
                            val c2 = it.arguments?.getInt("dominantColor2") ?: 0xFFFFFF
                            val color1 = Color(c1)
                            val color2 = Color(c2)

                            Pair(color1, color2)
                        }

                        PokemonStatsScreen(navController = navController, pokemonName = pokemonName ?: "", index = index ?: 1, dominantColors = dominantColors) //TODO - null management
                    }

                }

            }
        }
    }
}
