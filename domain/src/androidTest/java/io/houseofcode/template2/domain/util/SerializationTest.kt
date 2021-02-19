package io.houseofcode.template2.domain.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.houseofcode.template2.domain.serializer.DateAsStringSerializer
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class SerializationTest {

    @Serializable
    data class ProgrammingLanguage(
        val name: String,
        @Contextual
        val stableReleaseDate: Date
    )

    @Serializable
    data class User(
        @SerialName("first_name") val firstName: String,
        @SerialName("last_name") val lastName: String? = null,
    )

    private val jsonFormat = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            contextual(Date::class, DateAsStringSerializer)
        }
    }

    @Test
    fun testDateSerialization() {
        val originalDate: Date = Calendar.getInstance(TimeZone.getTimeZone("UK")).apply {
            set(Calendar.YEAR, 2020)
            set(Calendar.MONTH, 11)
            set(Calendar.DATE, 23)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 25)
            set(Calendar.SECOND, 30)
            set(Calendar.MILLISECOND, 0)
        }.time

        val originalData = ProgrammingLanguage("Kotlin", originalDate)
        val encodedData: String = jsonFormat.encodeToString(originalData)

        println("originalData: $originalData")
        println("encodedData: $encodedData")

        val parsedData =  jsonFormat.decodeFromString<ProgrammingLanguage>(encodedData)
        println("parsedData: $parsedData")

        assertThat(originalData.stableReleaseDate.time).isEqualTo(parsedData.stableReleaseDate.time)
        assertThat(originalData.stableReleaseDate.compareTo(parsedData.stableReleaseDate)).isEqualTo(0)
    }


    @Test
    fun testKotlinObjectSerialization() {
        val userFirstName = "Oliver"
        val user = User(userFirstName)

        val json: String = jsonFormat.encodeToString(user)
        println("json: $json")
        val jsonUser: User = jsonFormat.decodeFromString<User>(json)

        assertThat(user).isEqualTo(jsonUser)
        assertThat(jsonUser.firstName).isEqualTo(userFirstName)
        assertThat(jsonUser.lastName).isNull()
    }

    @Test
    fun testKotlinListSerialization() {
        val users = listOf<User>(
            User("Jack", "Brown"),
            User("Olivia", "Jones"),
            User("Charlie", "Taylor")
        )

        val jsonList: String = jsonFormat.encodeToString(users)
        println("json list: $jsonList")
        val jsonUsers: List<User> = jsonFormat.decodeFromString<List<User>>(jsonList)

        assertThat(users).isEqualTo(jsonUsers)
    }
}
