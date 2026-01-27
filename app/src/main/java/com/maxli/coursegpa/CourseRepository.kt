package com.maxli.coursegpa


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class CourseRepository(private val courseDao: CourseDao) {

    val allCourses: LiveData<List<Course>> = courseDao.getAllCourses()
    val searchResults = MutableLiveData<List<Course>>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertCourse(newcourse: Course) {
        coroutineScope.launch(Dispatchers.IO) {
            courseDao.insertCourse(newcourse)
        }
    }

    fun deleteCourse(name: String) {
        coroutineScope.launch(Dispatchers.IO) {
            courseDao.deleteCourse(name)
        }
    }

    fun findCourse(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name).await()
        }
    }

    private fun asyncFind(name: String): Deferred<List<Course>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async courseDao.findCourse(name)
        }
}