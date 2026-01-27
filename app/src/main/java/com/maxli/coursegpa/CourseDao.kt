package com.maxli.coursegpa


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CourseDao {

    @Insert
    fun insertCourse(course: Course)

    @Query("SELECT * FROM courses WHERE courseName = :name")
    fun findCourse(name: String): List<Course>

    @Query("DELETE FROM courses WHERE courseName = :name")
    fun deleteCourse(name: String)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): LiveData<List<Course>>


}