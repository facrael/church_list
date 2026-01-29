package com.religiousmarket.app.data.repository

import com.religiousmarket.app.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChurchRepository @Inject constructor() {

    // Sample data - in production, this would come from API
    private val sampleChurches = listOf(
        Church(
            id = "1",
            name = "Храм Христа Спасителя",
            religion = Religion.ORTHODOX,
            denomination = "Русская православная церковь",
            description = "Кафедральный собор Русской православной церкви. Крупнейший православный храм России.",
            address = "ул. Волхонка, 15",
            city = "Москва",
            latitude = 55.7447,
            longitude = 37.6056,
            phone = "+7 495 952-40-54",
            email = "info@xxc.ru",
            website = "xxc.ru",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Christ_the_Savior_Cathedral_Moscow.jpg/400px-Christ_the_Savior_Cathedral_Moscow.jpg",
            rating = 4.8f,
            reviewsCount = 1250,
            workingHours = mapOf(
                "Понедельник" to "08:00-20:00",
                "Вторник" to "08:00-20:00",
                "Среда" to "08:00-20:00",
                "Четверг" to "08:00-20:00",
                "Пятница" to "08:00-20:00",
                "Суббота" to "08:00-20:00",
                "Воскресенье" to "07:00-21:00"
            ),
            isVerified = true,
            distance = "1.2 км"
        ),
        Church(
            id = "2",
            name = "Храм Василия Блаженного",
            religion = Religion.ORTHODOX,
            denomination = "Русская православная церковь",
            description = "Собор Покрова Пресвятой Богородицы, что на Рву. Памятник русской архитектуры.",
            address = "Красная площадь, 2",
            city = "Москва",
            latitude = 55.7525,
            longitude = 37.6231,
            phone = "+7 495 698-83-04",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Saint_Basil%27s_Cathedral_2017.jpg/400px-Saint_Basil%27s_Cathedral_2017.jpg",
            rating = 4.9f,
            reviewsCount = 3500,
            workingHours = mapOf(
                "Понедельник" to "10:00-18:00",
                "Вторник" to "10:00-18:00",
                "Среда" to "10:00-18:00",
                "Четверг" to "10:00-18:00",
                "Пятница" to "10:00-18:00",
                "Суббота" to "10:00-18:00",
                "Воскресенье" to "10:00-18:00"
            ),
            isVerified = true,
            distance = "2.5 км"
        ),
        Church(
            id = "3",
            name = "Московская Соборная мечеть",
            religion = Religion.ISLAM,
            denomination = "Суннитский ислам",
            description = "Главная мечеть Москвы. Одна из крупнейших мечетей в России и Европе.",
            address = "Выползов пер., 7",
            city = "Москва",
            latitude = 55.7708,
            longitude = 37.6313,
            phone = "+7 495 681-66-33",
            email = "info@moscowmosque.ru",
            website = "moscowmosque.ru",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Moscow_Cathedral_Mosque.jpg/400px-Moscow_Cathedral_Mosque.jpg",
            rating = 4.9f,
            reviewsCount = 890,
            workingHours = mapOf(
                "Понедельник" to "09:00-21:00",
                "Вторник" to "09:00-21:00",
                "Среда" to "09:00-21:00",
                "Четверг" to "09:00-21:00",
                "Пятница" to "09:00-21:00",
                "Суббота" to "09:00-21:00",
                "Воскресенье" to "09:00-21:00"
            ),
            isVerified = true,
            distance = "3.8 км"
        ),
        Church(
            id = "4",
            name = "Московская хоральная синагога",
            religion = Religion.JUDAISM,
            denomination = "Ортодоксальный иудаизм",
            description = "Главная синагога Москвы. Историческое здание в центре города.",
            address = "Большой Спасоглинищевский пер., 10",
            city = "Москва",
            latitude = 55.7565,
            longitude = 37.6485,
            phone = "+7 495 923-43-66",
            email = "info@mjcc.ru",
            website = "mjcc.ru",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Moscow_Choral_Synagogue.jpg/400px-Moscow_Choral_Synagogue.jpg",
            rating = 4.7f,
            reviewsCount = 320,
            workingHours = mapOf(
                "Понедельник" to "09:00-18:00",
                "Вторник" to "09:00-18:00",
                "Среда" to "09:00-18:00",
                "Четверг" to "09:00-18:00",
                "Пятница" to "09:00-15:00",
                "Суббота" to "Закрыто",
                "Воскресенье" to "09:00-18:00"
            ),
            isVerified = true,
            distance = "4.2 км"
        )
    )

    private val servicesByChurch = mapOf(
        "1" to listOf(
            Service("s1", "1", "Крещение", "Таинство крещения для детей и взрослых", "Таинства", 3000, 60),
            Service("s2", "1", "Венчание", "Таинство венчания для новобрачных", "Таинства", 5000, 90),
            Service("s3", "1", "Записка о здравии", "Поминовение о здравии на Литургии", "Записки", 50, 0),
            Service("s4", "1", "Записка о упокоении", "Поминовение о упокоении на Литургии", "Записки", 50, 0),
            Service("s5", "1", "Молебен", "Молебен о здравии", "Молитвы", 500, 30),
            Service("s6", "1", "Панихида", "Панихида об упокоении", "Молитвы", 500, 30),
            Service("s7", "1", "Освящение автомобиля", "Освящение транспортного средства", "Освящение", 1000, 30),
            Service("s8", "1", "Освящение жилья", "Освящение квартиры или дома", "Освящение", 2000, 60)
        ),
        "2" to listOf(
            Service("s9", "2", "Крещение", "Таинство крещения", "Таинства", 3000, 60),
            Service("s10", "2", "Записка о здравии", "Поминовение о здравии", "Записки", 50, 0),
            Service("s11", "2", "Молебен", "Молебен о здравии", "Молитвы", 500, 30)
        ),
        "3" to listOf(
            Service("s12", "3", "Никах", "Исламская церемония бракосочетания", "Обряды", 5000, 60),
            Service("s13", "3", "Ду'а", "Коллективная молитва-просьба", "Молитвы", 1000, 30),
            Service("s14", "3", "Джаназа", "Погребальная молитва", "Обряды", 3000, 45),
            Service("s15", "3", "Консультация имама", "Духовная беседа с имамом", "Консультации", 0, 30)
        ),
        "4" to listOf(
            Service("s16", "4", "Бар-мицва", "Церемония совершеннолетия мальчика", "Обряды", 10000, 120),
            Service("s17", "4", "Бат-мицва", "Церемония совершеннолетия девочки", "Обряды", 10000, 120),
            Service("s18", "4", "Йорцайт", "Поминовение усопших", "Молитвы", 500, 30),
            Service("s19", "4", "Консультация раввина", "Духовная беседа с раввином", "Консультации", 0, 30)
        )
    )

    fun getChurches(): Flow<List<Church>> = flow {
        delay(500) // Simulate network delay
        emit(sampleChurches)
    }

    fun getChurchesByReligion(religion: Religion?): Flow<List<Church>> = flow {
        delay(300)
        val filtered = if (religion == null) {
            sampleChurches
        } else {
            sampleChurches.filter { it.religion == religion }
        }
        emit(filtered)
    }

    fun getChurchById(id: String): Flow<Church?> = flow {
        delay(200)
        emit(sampleChurches.find { it.id == id })
    }

    fun getServicesForChurch(churchId: String): Flow<List<Service>> = flow {
        delay(200)
        emit(servicesByChurch[churchId] ?: emptyList())
    }

    fun searchChurches(query: String): Flow<List<Church>> = flow {
        delay(300)
        val results = sampleChurches.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.address.contains(query, ignoreCase = true)
        }
        emit(results)
    }
}
