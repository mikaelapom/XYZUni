//Times New Roman
//Blue #97cdec || Yellow #fbe475 || White #ffffff
package com.maxli.coursegpa



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.compose.foundation.border
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


import com.maxli.coursegpa.ui.theme.CourseTheme


val TimesNewRoman = FontFamily(
Font(R.font.times, FontWeight.Normal),
Font(R.font.times, FontWeight.Bold),
Font(R.font.times, FontWeight.Normal, FontStyle.Italic),
Font(R.font.times, FontWeight.Bold, FontStyle.Italic)
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), //fill max size is whole screen
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext
                                        as Application)
                        )

                        ScreenSetup(viewModel)
                    }
                }
            }
        }
    }
}

//initial screen setup, calls the main screen with the right data
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
            val timesNewRomanStyle = TextStyle(fontFamily = TimesNewRoman,
                fontSize = 25.sp,
                color = Color(0xFF1A2C57))

            Text(text = "Class Overview and GPA",
                style = timesNewRomanStyle)
        }
    }
}
@Composable
fun ScreenSetup(viewModel: MainViewModel) {
    val allCourses by viewModel.allCourses.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())
    Column {
        TopBanner()
        SecondBanner()
        MainScreen(
            allCourses = allCourses,
            searchResults = searchResults,
            viewModel = viewModel
        )
    }
}

//main ui with the buttons, text field, and list of courses
@Composable
fun MainScreen(
    allCourses: List<Course>,
    searchResults: List<Course>,
    viewModel: MainViewModel
) {
    var courseName by remember { mutableStateOf("") }
    var courseCreditHour by remember { mutableStateOf("") }
    var letterGrade by remember {
        mutableStateOf("")
    }

    var calculatedGPA by remember {
        mutableDoubleStateOf(-1.0)
    }

    var searching by remember { mutableStateOf(false) }

    var creditHourError by remember { mutableStateOf(false) }

    var letterGradeError by remember { mutableStateOf(false) }

    var courseNameError by remember { mutableStateOf(false) }

    var courseNameErrorMessage by remember {
        mutableStateOf("Please enter a course name")
    }

    val onCourseTextChange = { text : String ->
        courseName = text
        courseNameError = false
    }

    val onCreditHourTextChange = { text: String ->
        if (text.all { it.isDigit() }) {
            courseCreditHour = text
            creditHourError = false
        } else {
            creditHourError = true
        }
    }

    val onLetterGradeTextChange = { text: String ->
        val upper = text.uppercase()

        if (upper.length <= 1 && upper.all { it in "ABCDF" }) {
            letterGrade = upper
            letterGradeError = false
        } else {
            letterGradeError = true
        }
    }



    //use column to create text fields
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        CustomTextField(
            title = "Course Name",
            textState = courseName,
            onTextChange = onCourseTextChange,
            keyboardType = KeyboardType.Text,
            isError = courseNameError,
            errorMessage = courseNameErrorMessage
//            Modifier.border(width = 0.5.dp, Color(0xFF1A2C57))
        )

        CustomTextField(
            title = "Credit Hour",
            textState = courseCreditHour,
            onTextChange = onCreditHourTextChange,
            keyboardType = KeyboardType.Number,
            isError = creditHourError,
            errorMessage = "Please enter a number"
        )


        CustomTextField(
            title = "Letter Grade",
            textState = letterGrade,
            onTextChange = onLetterGradeTextChange,
            keyboardType = KeyboardType.Text,
            isError = letterGradeError,
            errorMessage = "Enter A, B, C, D, or F"
        )



        //use row to arrange the buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp) //was 10.dp
        ) {
            Button(
                onClick = {
                    //field checking
                    courseNameError = courseName.isBlank()
                    if (courseNameError) {
                        courseNameErrorMessage = "Please enter a course name"
                    }
                    creditHourError = courseCreditHour.isBlank() || !courseCreditHour.all { it.isDigit() }
                    letterGradeError = letterGrade.isBlank() || !(letterGrade.length == 1 && letterGrade.all { it in "ABCDF" })

                    //check all possible errors
                    if (!courseNameError && !creditHourError && !letterGradeError) {
                        val duplicate = allCourses.any { it.courseName.equals(courseName, ignoreCase = true) }
                        if (duplicate) {
                            courseNameError = true
                            courseNameErrorMessage = "Course already added"
                        } else {
                            //if no error then insrt
                            viewModel.insertCourse(
                                Course(courseName, courseCreditHour.toInt(), letterGrade)
                            )
                            searching = false
                            courseName = ""
                            courseCreditHour = ""
                            letterGrade = ""
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF97CDEC),
                    contentColor = Color(0xFF1A2C57)
                )
            ) { Text("Add") }

            Button(onClick = {
                searching = true
                viewModel.smartSearch(courseName, courseCreditHour, letterGrade)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF97CDEC), contentColor = Color(0xFF1A2C57))
            ) {
                Text("Search")
            }


            Button(onClick = {
                searching = false
                val courseToDelete = allCourses.firstOrNull {
                    it.courseName.equals(courseName, ignoreCase = true)
                }

// Only delete if a matching course exists
                courseToDelete?.let {
                    viewModel.deleteCourse(it.id)
                }

            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF97CDEC),contentColor = Color(0xFF1A2C57))
            ) {
                Text("Del")
            }
            calculatedGPA = calculateGPA2(allCourses)

            Button(onClick = {
                searching = false
                courseName = ""
                courseCreditHour = ""
                letterGrade = ""
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF97CDEC),contentColor = Color(0xFF1A2C57))
            ) {
                Text("Clear")
            }

            Button(onClick = {

                val gpa = calculateGPA2(allCourses)
                calculatedGPA = gpa

            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF97CDEC),contentColor = Color(0xFF1A2C57))
            ) {
                Text("GPA")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            Text("GPA: %.2f".format(calculatedGPA))
        }

        LazyColumn(
        ) {
            val list = if (searching) searchResults else allCourses

            item {
                TitleRow(head1 = "ID", head2 = "Course", head3 = "Credit Hour", head4 = "Letter Grade",)
            }

            items(list) { course ->
                CourseRow(
                    id = course.id,
                    name = course.courseName,
                    creditHour = course.creditHour,
                    letterGrade = course.letterGrade
                )
            }
        }
    }
}

// GPA calculation functionality
private fun calculateGPA2(allCourses: List<Course>): Double {
    // Dummy data for illustration. Replace with actual data retrieval and calculation logic

    val gradePoints = mapOf("A" to 4.0, "B" to 3.0, "C" to 2.0, "D" to 1.0, "F" to 0.0,"a" to 4.0, "b" to 3.0, "c" to 2.0, "d" to 1.0, "f" to 0.0)
    val totalCreditHours = allCourses.sumOf { it.creditHour }
    val totalPoints = allCourses.sumOf { it.creditHour * (gradePoints[it.letterGrade] ?: 0.0) }

    return if (totalCreditHours > 0) totalPoints / totalCreditHours else 0.0
}

@Composable
fun TitleRow(head1: String, head2: String, head3: String, head4: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFFFBE475))
            //.border(width = 0.5.dp, Color.Black)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        val timesNewRomanStyle = TextStyle(fontFamily = TimesNewRoman,
            fontSize = 18.sp,
            color = Color(0xFF1A2C57)
        )

        Text(head1, style = timesNewRomanStyle,
            modifier = Modifier
                .weight(0.1f))
        Text(head2, style = timesNewRomanStyle,
            modifier = Modifier
                .weight(0.2f))
        Text(head3, style = timesNewRomanStyle,
            modifier = Modifier.weight(0.2f))
        Text(head4, style = timesNewRomanStyle,
            modifier = Modifier.weight(0.2f))
    }
}

@Composable
fun CourseRow(id: Int, name: String, creditHour: Int, letterGrade: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(id.toString(), modifier = Modifier
            .weight(0.1f))
        Text(name, modifier = Modifier.weight(0.2f))
        Text(creditHour.toString(), modifier = Modifier.weight(0.2f))
        Text(letterGrade, modifier = Modifier.weight(0.2f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        OutlinedTextField(
            value = textState,
            onValueChange = onTextChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            label = { Text(title) },
            isError = isError,
            modifier = modifier.padding(10.dp),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}



//creates view model
class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}
