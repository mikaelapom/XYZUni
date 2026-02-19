package com.maxli.coursegpa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState


val TimesNewRoman = FontFamily(
    Font(R.font.times, FontWeight.Normal),
    Font(R.font.times, FontWeight.Bold),
    Font(R.font.times, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.times, FontWeight.Bold, FontStyle.Italic)
)


//val answered = MutableList(11) {false}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }

    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Trivia", "Acad")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFFBE475),
                contentColor = Color(0xFF1A2C57)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = TextStyle(
                                    fontFamily = TimesNewRoman,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> TriviaScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> AcadScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun AcadScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Acad Screen",
            style = TextStyle(
                fontFamily = TimesNewRoman,
                fontSize = 30.sp,
                color = Color(0xFF1A2C57)
            )
        )
    }
}


@Composable
fun TriviaScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.allQuestions.observeAsState(emptyList())
    var questionIndex by remember { mutableIntStateOf(0) }


    var grade by remember { mutableDoubleStateOf(0.0) }
    var questionsAnswered by remember { mutableDoubleStateOf(0.0) }
    var questionsCorrect by remember { mutableDoubleStateOf(0.0) }


    val answered = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(10) { add(false) }
        }
    }

    if (questions.isEmpty()) {
        Text("No questions in database")
        return
    }

    val current = questions[questionIndex]

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopBanner()
        SecondBanner()

        QuestionBox(current, questionIndex)
        Spacer(modifier = Modifier.height(8.dp))
        Score(grade)
        AnswerButtons(
            current,
            questionIndex = questionIndex,
            answered,
            onAnswered = { isCorrect ->
                if (!answered[questionIndex]) {
                    answered[questionIndex] = true
                    questionsAnswered++
                    if (isCorrect) {
                        questionsCorrect++
                    }
                }
                grade = questionsCorrect / questionsAnswered
            })


        Spacer(modifier = Modifier.height(50.dp))

        BackForwardReset(
            currentIndex = questionIndex,
            total = questions.size,
            onBack = {
                if (questionIndex > 0) questionIndex--
            },
            onNext = {
                if (questionIndex < 9) questionIndex++
            },
            onReset = {
                questionIndex = 0
                questionsAnswered = 0.0
                questionsCorrect = 0.0
                answered.fill(false)
                grade = 0.0
            }
        )
    }
}




@Composable
fun AnswerButtons(
    question: TrivialQuestion,
    questionIndex: Int,
    answered: MutableList<Boolean>,
    onAnswered: (isCorrect: Boolean) -> Unit
) {
    val originalList = question.getShuffledAnswers()
    AnswerRow("A", originalList[0], questionIndex, 0, originalList, question, answered, onAnswered)
    Spacer(modifier = Modifier.height(25.dp))
    AnswerRow("B", originalList[1], questionIndex,1, originalList, question, answered, onAnswered)
    Spacer(modifier = Modifier.height(25.dp))
    AnswerRow("C", originalList[2], questionIndex,2, originalList, question, answered, onAnswered)
    Spacer(modifier = Modifier.height(25.dp))
    AnswerRow("D", originalList[3], questionIndex,3, originalList, question, answered, onAnswered)
}

@Composable
fun QuestionBox(question: TrivialQuestion, index: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A2C57))
            .height(150.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Question ${index + 1}: ${question.questionName}",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = TextStyle(
                fontFamily = TimesNewRoman,
                fontSize = 30.sp,
                color = Color(0xFFFBE475)
            )
        )
    }
}

@Composable
fun AnswerRow(label: String,
              text: String,
              questionIndex: Int,
              listIndex: Int,
              answerIndex: List<String>,
              question: TrivialQuestion,
              answered: List<Boolean>,
              onAnswered: (isCorrect: Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                val isCorrect = answerIndex[listIndex] == question.correct
                onAnswered(isCorrect)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (answered[questionIndex] && answerIndex[listIndex] == question.correct) Color.Green//Color(0xFF97CDEC),
                        else if (answered[questionIndex] && answerIndex[listIndex] != question.correct) Color.Red
                        else
                            Color(0xFF97CDEC),
                contentColor = Color(0xFF1A2C57)

            ),
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
            ) {
            Text(label,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 25.sp,
                    color = Color(0xFF1A2C57)
                )
                )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(text,
            fontFamily = TimesNewRoman,
            style = TextStyle(
                fontSize = 25.sp,
                color = Color(0xFF1A2C57)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SecondBanner() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFFFBE475))
            , contentAlignment = Alignment.Center
        )
        {

            Text(text = "Trivia Questionaire",
                style = TextStyle(
                    fontFamily = TimesNewRoman,
                    fontSize = 30.sp,
                    color = Color(0xFF1A2C57)
                ))
        }
    }
}
@Composable
fun TopBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFFFBE475)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.smith),
                contentDescription = "Smith College Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp)
            )

            Text(
                text = "Smith College",
                style = TextStyle(
                    fontFamily = TimesNewRoman,
                    fontSize = 35.sp,
                    color = Color(0xFF1A2C57)
                )
            )
        }

    }
}

@Composable
fun BackForwardReset(
    currentIndex: Int,
    total: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF97CDEC),
                contentColor = Color(0xFF1A2C57)
            ),
            shape = CircleShape,
            modifier = Modifier.size(100.dp),
            enabled = currentIndex > 0
        ) {
            Text("Back", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF97CDEC),
                contentColor = Color(0xFF1A2C57)
            ),
            shape = CircleShape,
            modifier = Modifier.size(100.dp)
        ) {
            Text("Reset", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF97CDEC),
                contentColor = Color(0xFF1A2C57)
            ),
            shape = CircleShape,
            modifier = Modifier.size(100.dp),
            enabled = currentIndex < total - 1
        ) {
            Text("Next", fontSize = 20.sp)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun Score(grade: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Grade:  ${String.format("%.2f",grade*100)}%",
            fontFamily = TimesNewRoman,
            style = TextStyle(
                fontSize = 25.sp,
                color = Color(0xFF1A2C57)
            )
        )
    }
}
