package com.loyou.snomekop.ui.pokemon

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.loyou.snomekop.R
import com.loyou.snomekop.core.util.Resource
import com.loyou.snomekop.ui.TypeColors
import com.loyou.snomekop.ui.model.PokemonInfoModel
import kotlinx.coroutines.delay
import theme.SnomekopTheme
import kotlin.math.round
import kotlin.math.sqrt

@Composable
fun PokemonStatsScreen(
    navController: NavController,
    pokemonName: String,
    index: Int,
    dominantColors: Pair<Color, Color>,
    viewModel: PokemonViewModel = hiltViewModel()
) {

    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = dominantColors.first
    )

    val pokemonInfo by remember {
        viewModel.pokemonInfoModel
    }

    LaunchedEffect(key1 = 0) {
        viewModel.getPokemonDetails(pokemonName)
    }


    PokemonStatsScreenContent(navController, pokemonName, pokemonInfo, index, dominantColors)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokemonStatsScreenContent(
    navController: NavController?,
    pokemonName: String,
    pokemonInfo: Resource<PokemonInfoModel>,
    index: Int,
    dominantColors: Pair<Color, Color>
) {

    Box(modifier = Modifier
        .fillMaxSize()) {
        when (pokemonInfo) {
            is Resource.Success -> {
                val pokemonInfoModel = pokemonInfo.data
                PokemonStats(
                    pokemonName.replaceFirstChar { it.uppercase() },
                    pokemonInfoModel.height,
                    pokemonInfoModel.weight,
                    pokemonInfoModel.baseXp,
                    index,
                    pokemonInfoModel.hp / 255f,
                    pokemonInfoModel.attack / 255f,
                    pokemonInfoModel.defense / 255f,
                    pokemonInfoModel.specialAttack / 255f,
                    pokemonInfoModel.specialDefense / 255f,
                    pokemonInfoModel.speed / 255f,
                    pokemonInfoModel.types,
                    dominantColors,
                    navController
                )
            }
            is Resource.Loading -> {
                CircularProgressIndicator(
                    Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )
            }
            is Resource.Error -> {
                Text(text = pokemonInfo.message ?: "Oups", Modifier.align(Alignment.Center))
            }
        }
    }

}

@OptIn(ExperimentalTextApi::class)
@Composable
fun PokemonStats(
    pokemonName: String,
    pokemonHeight: Int,
    pokemonWeight: Int,
    pokemonBaseXp: Int,
    index: Int,
    pv: Float = 0.5f,
    attack: Float = 0.5f,
    defense: Float = 0.5f,
    spAttack: Float = 0.5f,
    spDefense: Float = 0.5f,
    speed: Float = 0.5f,
    types: List<String>,
    dominantColors: Pair<Color, Color>,
    navController: NavController? = null
) {


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize(),
    ) {


        //val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$index.png"
        //val imageUrl = "https://img.pokemondb.net/artwork/${pokemonName}.jpg"
        val imageUrl =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png"
        val shape = RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp)

        var imageSize by remember {
            mutableStateOf(200.dp)
        }


        val (topBox, separation, detailData, baseStats) = createRefs()

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.25f)
            .background(
                brush = Brush.linearGradient(
                    dominantColors.toList()
                ),
                shape = shape
            )
            .constrainAs(topBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            SubcomposeAsyncImage(
                model = imageUrl,
                loading = {
                    CircularProgressIndicator()
                },
                contentDescription = "Pokemon",
                modifier = Modifier
                    .align(BottomCenter)
                    .size(imageSize)
                    .offset(y = imageSize / 2)
                )
            
            Column(modifier = Modifier
                .align(TopStart)
                .padding(start = 8.dp),
                verticalArrangement = Arrangement.Top) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            navController?.popBackStack()
                        }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = pokemonName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.height(12.dp))
                SmallTypes(types)
            }


            Text(
                text = "#${String.format("%04d", index)}",
                modifier = Modifier
                    .align(Alignment.TopEnd) // align the text to the bottom left corner
                    .padding(end = 12.dp, top = 8.dp), // add some padding
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = White
            )
        }
        Spacer(modifier = Modifier
            .height(0.5f * imageSize + 24.dp)
            .constrainAs(separation) {
                top.linkTo(topBox.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) //0.3
        PokemonDetailDataSection(
            modifier = Modifier.constrainAs(detailData) {
                top.linkTo(separation.bottom)
            },
            pokemonWeight = pokemonWeight,
            pokemonHeight = pokemonHeight,
            pokemonBaseXp = pokemonBaseXp,
        )
        BaseStats(
            modifier = Modifier.constrainAs(baseStats) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(detailData.bottom)
                bottom.linkTo(parent.bottom)
            },
            dominantColor = dominantColors.first,
            pv = pv,
            defense = defense,
            spDefense = spDefense,
            speed = speed,
            spAttack = spAttack,
            attack = attack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SnomekopTheme {
        PokemonStats(pokemonName = "Android",
            pokemonBaseXp = 50,
            pokemonHeight = 10,
            pokemonWeight = 10,
            index = 1,
            pv = 1f,
            spAttack = 0.8f,
            dominantColors = Pair(White, White),
            types = listOf("fire", "normal"))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PokemonStatsScreenContentPreview() {
    SnomekopTheme {
        val pokemonInfo = PokemonInfoModel(
         name= "Pikachu",
         picture= "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/21.png",
         height= 20,
         weight= 40,
         baseXp = 50,
         types= listOf(),
         hp= 200,
         attack= 255,
         defense= 100,
         speed= 160,
         specialAttack= 50,
         specialDefense= 50
        )
        PokemonStatsScreenContent(navController = null, pokemonName = "Charizard", index = 5, pokemonInfo = Resource.Success(pokemonInfo), dominantColors = Pair(White, White))
    }
}

@Composable
fun RoundedRectWithText(color: Color, text: String, height: Dp = 70.dp, fontSize: TextUnit = 20.sp, padding: Dp = 48.dp ) {
    Box(
        modifier = Modifier
            .background(
                color = color,
                shape = RoundedCornerShape(25.dp)
            )
            .clip(RoundedCornerShape(0.dp))
            .height(height)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            modifier = Modifier
                .padding(end = padding, start = padding)
                .align(Center)
                .wrapContentHeight(CenterVertically),
                    textAlign = TextAlign.Center,
            color = White,
            style = TextStyle(textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun NameAndTypes(name: String, types: List<String>) {
    Column {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement  = Arrangement.Center
        ) {
            types.forEachIndexed { index, type ->
                RoundedRectWithText(TypeColors.colours[type]!!, type)
                if (index < types.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }

}

@Composable
fun SmallTypes(types: List<String>) {
    Column(modifier = Modifier,
        horizontalAlignment = CenterHorizontally,
        verticalArrangement  = Arrangement.Center
    ) {
        types.forEachIndexed { index, type ->
            RoundedRectWithText(TypeColors.colours[type]!!, type, 25.dp, 14.sp, 24.dp)
            if (index < types.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun Types(types: List<String>) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement  = Arrangement.Center
    ) {
        types.forEachIndexed { index, type ->
            RoundedRectWithText(TypeColors.colours[type]!!, type)
            if (index < types.size - 1) {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
fun BaseStats(
    dominantColor: Color,
    pv: Float,
    defense: Float,
    spDefense: Float,
    speed: Float,
    spAttack: Float,
    attack: Float,
    modifier: Modifier,) {

    val textMeasurer = rememberTextMeasurer()

    val style = TextStyle(
        fontSize = 12.sp,
        color = White,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    val pvText = "Hp"

    val pvTextLayoutResult = remember(pvText) {
        textMeasurer.measure(pvText, style)
    }

    val defenseText = "Def"

    val defenseTextLayoutResult = remember(defenseText) {
        textMeasurer.measure(defenseText, style)
    }

    val spDefenseText = "Sp-Def"

    val spDefenseTextLayoutResult = remember(spDefenseText) {
        textMeasurer.measure(spDefenseText, style)
    }

    val speedText = "Spd"

    val speedTextTextLayoutResult = remember(speedText) {
        textMeasurer.measure(speedText, style)
    }

    val spAttackText = "Sp-Att"

    val spAttackTextTextLayoutResult = remember(spAttackText) {
        textMeasurer.measure(spAttackText, style)
    }

    val attackText = "Att"

    val attackTextLayoutResult = remember(attackText) {
        textMeasurer.measure(attackText, style)
    }


    Box(modifier = modifier, contentAlignment = Center) {

        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp

        val canvasSize = 300

        Canvas(
            modifier = Modifier
                .size(height = canvasSize.dp, width = screenWidth)
                .align(Center)
        ) {

            val canvasHeight = size.height.dp
            val cp3 = (sqrt(3.0) / 2).toFloat();
            val cp6 = 0.5f
            val c = canvasSize.toFloat()

            val offsetPv = Offset(0f, -c)
            val offsetDefense = Offset(c * cp3, -c * cp6)
            val offsetSpDefense = Offset(c * cp3, c * cp6)
            val offsetSpeed = Offset(0f, c)
            val offsetSpAttack = Offset(-c * cp3, c * cp6)
            val offsetAttack = Offset(-c * cp3, -c * cp6)

            val radarCenter = center + Offset(x = 0f, y = -0f)

            val p0 = radarCenter + offsetPv //TOP
            val p1 = radarCenter + offsetDefense //TOP RIGHT
            val p2 = radarCenter + offsetSpDefense //Bottom Right
            val p3 = radarCenter + offsetSpeed //Bottom
            val p4 = radarCenter + offsetSpAttack //Bottom left
            val p5 = radarCenter + offsetAttack //Top Left

            val r0 = radarCenter + offsetPv * 0.75F
            val r1 = radarCenter + offsetDefense * 0.75F
            val r2 = radarCenter + offsetSpDefense * 0.75F
            val r3 = radarCenter + offsetSpeed * 0.75F
            val r4 = radarCenter + offsetSpAttack * 0.75F
            val r5 = radarCenter + offsetAttack * 0.75F

            val q0 = radarCenter + offsetPv * 0.5F
            val q1 = radarCenter + offsetDefense * 0.5F
            val q2 = radarCenter + offsetSpDefense * 0.5F
            val q3 = radarCenter + offsetSpeed * 0.5F
            val q4 = radarCenter + offsetSpAttack * 0.5F
            val q5 = radarCenter + offsetAttack * 0.5F

            val t0 = radarCenter + offsetPv * 0.25F
            val t1 = radarCenter + offsetDefense * 0.25F
            val t2 = radarCenter + offsetSpDefense * 0.25F
            val t3 = radarCenter + offsetSpeed * 0.25F
            val t4 = radarCenter + offsetSpAttack * 0.25F
            val t5 = radarCenter + offsetAttack * 0.25F

            val s0 = radarCenter + offsetPv * pv
            val s1 = radarCenter + offsetDefense * defense
            val s2 = radarCenter + offsetSpDefense * spDefense
            val s3 = radarCenter + offsetSpeed * speed
            val s4 = radarCenter + offsetSpAttack * spAttack
            val s5 = radarCenter + offsetAttack * attack


            val basePath = Path().apply {
                moveTo(p0.x, p0.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
                lineTo(p5.x, p5.y)
                close()
            }

            val secondaryPath = Path().apply {
                moveTo(r0.x, r0.y)
                lineTo(r1.x, r1.y)
                lineTo(r2.x, r2.y)
                lineTo(r3.x, r3.y)
                lineTo(r4.x, r4.y)
                lineTo(r5.x, r5.y)
                close()
            }

            val tertiaryPath = Path().apply {
                moveTo(q0.x, q0.y)
                lineTo(q1.x, q1.y)
                lineTo(q2.x, q2.y)
                lineTo(q3.x, q3.y)
                lineTo(q4.x, q4.y)
                lineTo(q5.x, q5.y)
                close()
            }

            val quaternaryPath = Path().apply {
                moveTo(t0.x, t0.y)
                lineTo(t1.x, t1.y)
                lineTo(t2.x, t2.y)
                lineTo(t3.x, t3.y)
                lineTo(t4.x, t4.y)
                lineTo(t5.x, t5.y)
                close()
            }

            val statPath = Path().apply {
                moveTo(s0.x, s0.y)
                lineTo(s1.x, s1.y)
                lineTo(s2.x, s2.y)
                lineTo(s3.x, s3.y)
                lineTo(s4.x, s4.y)
                lineTo(s5.x, s5.y)
                close()
            }

            drawLine(
                color = Color.Gray,
                strokeWidth = 2f,
                start = p0,
                end = p3
            )

            drawLine(
                color = Color.Gray,
                strokeWidth = 2f,
                start = p1,
                end = p4
            )

            drawLine(
                color = Color.Gray,
                strokeWidth = 2f,
                start = p5,
                end = p2
            )


            drawPath(
                path = basePath,
                color = Color.Gray,
                style = Stroke(width = 10f)
            )

            drawPath(
                path = secondaryPath,
                color = Color.Gray,
                style = Stroke(width = 2f)
            )
            drawPath(
                path = tertiaryPath,
                color = Color.Gray,
                style = Stroke(width = 2f)
            )

            drawPath(
                path = quaternaryPath,
                color = Color.Gray,
                style = Stroke(width = 2f)
            )


            drawPath(
                path = statPath,
                color = dominantColor,
                style = Fill,
                alpha = 0.75f
            )


/*            //HP Label
            drawImage(
                image = hpBitmap,
                topLeft = Offset(
                    x = radarCenter.x - hpBitmap.width/2,
                    y = radarCenter.y - (c + 10) - hpBitmap.height
                )
            )

            //Defense Label
            drawImage(
                image = defBitmap,
                topLeft = Offset(
                    x = radarCenter.x + (c +0) * cp3,
                    y = radarCenter.y - (c +0) * cp6 - defBitmap.height
                )
            )

            //Sp Def Label
            drawImage(
                image = spDefBitmap,
                topLeft = Offset(
                    x = radarCenter.x + c * cp3,
                    y = radarCenter.y + c  * cp6
                )
            )

            //Speed Label
            drawImage(
                image = speedBitmap,
                topLeft = Offset(
                    x = radarCenter.x - speedBitmap.width/2,
                    y = radarCenter.y + c + 10
                )
            )

            //Sp Att Label
            drawImage(
                image = spAttBitmap,
                topLeft = Offset(
                    x = radarCenter.x - c * cp3 - spAttBitmap.width,
                    y = radarCenter.y + c * cp6
                )
            )

            //Attack Label
            drawImage(
                image = attBitmap,
                topLeft = Offset(
                    x = radarCenter.x - c  * cp3 - attBitmap.width,
                    y = radarCenter.y - c * cp6 - attBitmap.height
                )
            )*/

            drawText(
                textMeasurer = textMeasurer,
                text = pvText,
                style = style,
                topLeft = Offset(
                    x = radarCenter.x - pvTextLayoutResult.size.width/2,
                    y = radarCenter.y - (c + 10) - pvTextLayoutResult.size.height
                )
            )

            //Defense Label
            drawText(
                textMeasurer = textMeasurer,
                text = defenseText,
                style = style,
                topLeft = Offset(
                    x = radarCenter.x + (c +0) * cp3,
                    y = radarCenter.y - (c +0) * cp6 - defenseTextLayoutResult.size.height
                )
            )

            //Sp Def Label
            drawText(
                textMeasurer = textMeasurer,
                text = spDefenseText,
                style = style,
                topLeft = Offset(
                    x = radarCenter.x + c * cp3,
                    y = radarCenter.y + c  * cp6
                )
            )

            //Speed Label
            drawText(
                textMeasurer = textMeasurer,
                text = speedText,
                style = style,
                topLeft = Offset(
                    x = radarCenter.x - speedTextTextLayoutResult.size.width/2,
                    y = radarCenter.y + c + 10
                )
            )

            //Sp Att Label
            drawText(
                textMeasurer = textMeasurer,
                text = spAttackText,
                style = style,
                topLeft = Offset(
                    x = radarCenter.x - c * cp3 - spAttackTextTextLayoutResult.size.width,
                    y = radarCenter.y + c * cp6
                )
            )

            //Attack Label
            drawText(
                textMeasurer = textMeasurer,
                text = attackText,
                style = style,
                topLeft = Offset(
                    x = radarCenter.x - c  * cp3 - attackTextLayoutResult.size.width,
                    y = radarCenter.y - c * cp6 - attackTextLayoutResult.size.height
                )
            )


        }
    }

}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    pokemonBaseXp: Int,
    modifier: Modifier
) {
    val pokemonWeightInKg = remember {
        round(pokemonWeight * 100f) / 1000f
    }
    val pokemonHeightInMeters = remember {
        round(pokemonHeight * 100f) / 1000f
    }
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = CenterVertically
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
        )

        PokemonDetailDataItem(
            dataValue = pokemonBaseXp.toFloat(),
            dataUnit = "xp",
            dataIcon = painterResource(id = R.drawable.ic_xp),
        )

        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_height),
        )
    }
}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(painter = dataIcon, contentDescription = null, tint = White, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue$dataUnit",
            color = White
        )
    }
}

@Preview
@Composable
fun RoundedRectWithTextPreview() {
    RoundedRectWithText(TypeColors.colours["normal"]!!, "fire")
}

@Preview
@Composable
fun NameAndTypesPreview() {
    val list = listOf("normal", "fire")
    NameAndTypes("Salameche", list)
}

@Preview
@Composable
fun BaseStatsPreview() {
    BaseStats(
        dominantColor = Color.Cyan,
        pv = 0.5f,
        defense = 0.7f,
        spDefense = 0.2f,
        speed = 0.8f,
        spAttack = 0.3f,
        attack = 0.4f,
        modifier = Modifier
    )
}
