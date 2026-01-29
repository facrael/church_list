package com.religiousmarket.app.data.repository

import com.religiousmarket.app.data.model.Prayer
import com.religiousmarket.app.data.model.Religion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepository @Inject constructor() {

    private val prayers = listOf(
        Prayer(
            id = "p1",
            title = "Отче наш",
            text = """Отче наш, Иже еси на небесех!
Да святится имя Твое,
да приидет Царствие Твое,
да будет воля Твоя,
яко на небеси и на земли.
Хлеб наш насущный даждь нам днесь;
и остави нам долги наша,
якоже и мы оставляем должником нашим;
и не введи нас во искушение,
но избави нас от лукаваго.""",
            religion = Religion.ORTHODOX,
            category = "Основные молитвы"
        ),
        Prayer(
            id = "p2",
            title = "Символ веры",
            text = """Верую во единаго Бога Отца, Вседержителя,
Творца небу и земли, видимым же всем и невидимым.
И во единаго Господа Иисуса Христа,
Сына Божия, Единороднаго,
Иже от Отца рожденнаго прежде всех век;
Света от Света, Бога истинна от Бога истинна,
рожденна, несотворенна, единосущна Отцу,
Имже вся быша.""",
            religion = Religion.ORTHODOX,
            category = "Основные молитвы"
        ),
        Prayer(
            id = "p3",
            title = "Богородице Дево",
            text = """Богородице Дево, радуйся,
Благодатная Марие, Господь с Тобою;
благословена Ты в женах
и благословен Плод чрева Твоего,
яко Спаса родила еси душ наших.""",
            religion = Religion.ORTHODOX,
            category = "Богородичные молитвы"
        ),
        Prayer(
            id = "p4",
            title = "Аль-Фатиха",
            text = """Во имя Аллаха, Милостивого, Милосердного!
Хвала Аллаху, Господу миров,
Милостивому, Милосердному,
Властелину Дня воздаяния!
Тебе одному мы поклоняемся
и Тебя одного молим о помощи.
Веди нас прямым путем,
путем тех, кого Ты облагодетельствовал,
не тех, на кого пал гнев,
и не заблудших.""",
            religion = Religion.ISLAM,
            category = "Суры Корана"
        ),
        Prayer(
            id = "p5",
            title = "Аят аль-Курси",
            text = """Аллах — нет божества, кроме Него,
Живого, Вседержителя.
Им не овладевают ни дремота, ни сон.
Ему принадлежит то, что на небесах,
и то, что на земле.
Кто станет заступаться перед Ним
без Его дозволения?""",
            religion = Religion.ISLAM,
            category = "Суры Корана"
        ),
        Prayer(
            id = "p6",
            title = "Шма Исраэль",
            text = """Слушай, Израиль: Господь — Бог наш,
Господь один!
Люби Господа, Бога твоего,
всем сердцем твоим,
и всей душою твоей,
и всеми силами твоими.
И да будут слова эти, которые Я заповедую тебе сегодня,
в сердце твоём.""",
            religion = Religion.JUDAISM,
            category = "Основные молитвы"
        ),
        Prayer(
            id = "p7",
            title = "Амида",
            text = """Благословен Ты, Господь, Бог наш
и Бог отцов наших,
Бог Авраама, Бог Ицхака и Бог Яакова,
Бог великий, могучий и грозный,
Бог всевышний,
творящий добро и милость,
владеющий всем.""",
            religion = Religion.JUDAISM,
            category = "Основные молитвы"
        )
    )

    fun getPrayers(): Flow<List<Prayer>> = flow {
        delay(300)
        emit(prayers)
    }

    fun getPrayersByReligion(religion: Religion?): Flow<List<Prayer>> = flow {
        delay(200)
        val filtered = if (religion == null) {
            prayers
        } else {
            prayers.filter { it.religion == religion }
        }
        emit(filtered)
    }

    fun getPrayerById(id: String): Flow<Prayer?> = flow {
        delay(100)
        emit(prayers.find { it.id == id })
    }

    fun searchPrayers(query: String): Flow<List<Prayer>> = flow {
        delay(200)
        val results = prayers.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.text.contains(query, ignoreCase = true)
        }
        emit(results)
    }
}
