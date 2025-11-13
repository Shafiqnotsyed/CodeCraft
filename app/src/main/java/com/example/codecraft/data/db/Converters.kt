package com.example.codecraft.data.db

import androidx.room.TypeConverter
import com.example.codecraft.data.Course
import com.example.codecraft.data.Test
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromCourse(value: Course): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toCourse(value: String): Course {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromTestList(value: List<Test>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toTestList(value: String): List<Test> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        return value.split(",").toSet()
    }

    @TypeConverter
    fun fromStringIntMap(value: Map<String, Int>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringIntMap(value: String): Map<String, Int> {
        return Json.decodeFromString(value)
    }
}
