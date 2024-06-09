package com.sgtech.quizeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeScreen(val viewModel: QuizViewModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quiz App",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Button(onClick = {
                if (!viewModel.quizList.isEmpty()) {
                    navigator.push(QuizScreen(viewModel))
                }
            }) {
                Text("Start Quiz")
            }
        }
    }


}

class QuizScreen(private val viewModel: QuizViewModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        QuizScreenContent(viewModel = viewModel, navigator = navigator)
    }

}

@Composable
fun QuizScreenContent(viewModel: QuizViewModel, navigator: Navigator) {
    val list = viewModel.quizList
    val index = remember {
        mutableIntStateOf(0)
    }

    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Q.${index.intValue + 1} : " + list[index.intValue].question,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Justify,
            fontSize = 18.sp
        )
        list[index.intValue].options.forEach {
            OptionScreenContent(title = it.value.toString(),
                onOptionSelected = { res, bgState, txtState ->
                    if (res == list[index.intValue].answer) {
                        viewModel.score.intValue++
                        bgState.value = Color.Green
                        txtState.value = Color.White
                    } else {
                        bgState.value = Color.Red
                        txtState.value = Color.White
                    }
                    scope.launch {
                        delay(1000)
                        index.intValue++
                        bgState.value = Color(0xffd3d3d3)
                        txtState.value = Color.Black
                        if (index.intValue == 10) {
                            navigator.push(ResultScreen(viewModel))
                        }
                    }
                })

        }


    }


}

class ResultScreen(val viewModel: QuizViewModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ResultScreenContent(viewModel = viewModel, navigator)
    }


}

@Composable
fun ResultScreenContent(viewModel: QuizViewModel, navigator: Navigator) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text ="Congratulations",fontWeight = FontWeight.Bold,fontSize = 24.sp)
        Spacer(modifier = Modifier.padding(10.dp))
        Image(
            painter = painterResource(id = R.drawable.trophy_icon),
            contentDescription = "trophy",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            "Result : ",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            "Your Score is ${viewModel.score.intValue} out of 10",
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Button(onClick = {
            viewModel.score.intValue = 0
            navigator.popAll()
            viewModel.fetchQuizList()
        }) {
            Text("Try Again")
        }
    }

}


@Composable
fun OptionScreenContent(
    title: String,
    onOptionSelected: (String, MutableState<Color>, MutableState<Color>) -> Unit,

    ) {
    val bgColor = remember {
        mutableStateOf(Color(0xffd3d3d3))
    }
    val textColor = remember {
        mutableStateOf(Color.Black)
    }
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .clickable {
                onOptionSelected(title, bgColor, textColor)
            },
        colors = CardDefaults.cardColors(
            containerColor = bgColor.value
        )
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Text(text = title, color = textColor.value, fontWeight = FontWeight.Thin)
        }

    }
}