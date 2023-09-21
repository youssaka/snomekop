package com.loyou.snomekop.ui.list


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.loyou.snomekop.R
import com.loyou.snomekop.ui.model.PokemonModel

@Composable
fun Header() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = Color.Black
    )

    Column{
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.pokemon_logo),
            contentDescription = "Pokemon",
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            onChange = {
                if(it.isBlank()) {
                    viewModel.getPokemons()
                } else {
                    viewModel.getPokemonsByName(it)
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        PokemonList(navController = navController, viewModel = viewModel)
    }


}

@Composable
fun SearchBar(
    modifier: Modifier,
    onChange: (String) -> Unit
) {
    var text by rememberSaveable() {
        mutableStateOf("")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onChange(it)
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SimpleComposable() {
    Text(
        "Hello World",
        color = Color.White
    )
}

@Preview
@Composable
fun ComposablePreview() {
    SimpleComposable()
}


@Composable
fun PokemonList(
    viewModel: ListViewModel,
    navController: NavController,
) {

    val isSearching by remember {
        viewModel.isSearching
    }


    val shouldDisplaySearchResults by remember {
        viewModel.shouldDisplaySearchResults
    }

    LaunchedEffect(key1 = 0 ) {
        viewModel.getPokemons()
    }

    val pokemonListItems: LazyPagingItems<PokemonModel> =
        viewModel.pokemonList.collectAsLazyPagingItems()

    val pokemonSearchResult by remember { viewModel.pokemonSearchResult }

    if (shouldDisplaySearchResults) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pokemonSearchResult.size) { index ->
                PokemonListEntry(
                    model = pokemonSearchResult[index],
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
        if (isSearching) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Recherche en cours")
                CircularProgressIndicator()
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pokemonListItems.itemCount) { index ->
                    pokemonListItems[index]?.let {
                        PokemonListEntry(
                            model = it,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }

            when (pokemonListItems.loadState.refresh) {
                is LoadState.Loading -> {
                    Log.d("DBG", "Refresh loading")
                    CircularProgressIndicator()
                }
                else -> {}
            }
            when (pokemonListItems.loadState.append) {
                is LoadState.Loading -> {
                    Log.d("DBG", "append loading")
                    CircularProgressIndicator()
                }
                is LoadState.Error -> {
                    Text("Erreur de chargement")
                }

                is LoadState.NotLoading -> {
                    Log.d("DBG", "append not")
                }
                else -> {}
            }

        }
    }
}

@Composable
fun PokemonListEntry(model: PokemonModel, navController: NavController, viewModel: ListViewModel) {
    val index = getPokemonIndex(model)
    val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png"

    var dominantColors by remember { mutableStateOf(Pair(Color.White, Color.White)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = dominantColors.first),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp)
                    .clickable {

                        navController.navigate("pokemon_details/${model.name}/$index/${dominantColors.first.toArgb()}/${dominantColors.second.toArgb()}")
                    }) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentDescription = "Pokemon ${model.name} / ${model.url}",
                    modifier = Modifier.size(90.dp),
                    onSuccess = { success ->
                        viewModel.getDominantColors(success.result.drawable) {
                            dominantColors = it
                        }

                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = model.name, color = Color.White)
        }
    }
    }
}

private fun getPokemonIndex(model: PokemonModel): Int? {
    val regex = """(?<=pokemon/)\d+""".toRegex()
    return if(model.url != null) {
        val matchResult = regex.find(model.url!!)
        matchResult?.value?.toInt()
    } else {
        return model.id
    }
}

@Composable
fun PokemonListEntryP() {
    val pokemonModel = PokemonModel(5, "https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.mewtwo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = pokemonModel.name, color = Color.White)
        }
    }
}

@Preview
@Composable
fun PokemonListEntryPreview() {
    PokemonListEntryP()
}


//@Composable
//fun PokemonList(
//    navController: NavController,
//    viewModel: ListViewModel = hiltNavGraphViewModel()
//) {
//
//    val pokemonList by remember { viewModel.pokemonList }
//    val endReached by remember { viewModel.endReached }
//    val loadError by remember { viewModel.loadError }
//    val isLoading by remember { viewModel.isLoading }
//    val isSearching by remember { viewModel.isSearching }
//
//    LazyColumn(contentPadding = PaddingValues(16.dp)) {
//        val itemCount = if(pokemonList.size % 2 == 0) {
//            pokemonList.size / 2
//        } else {
//            pokemonList.size / 2 + 1
//        }
//        items(itemCount) {
//            if(it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
//                LaunchedEffect(key1 = true) {
//                    viewModel.loadPokemonPaginated()
//                }
//            }
//            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
//        }
//    }
//
//}