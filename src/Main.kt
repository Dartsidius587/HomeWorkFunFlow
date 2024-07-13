import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
suspend fun main() {
	val listId: MutableList<String> = mutableListOf()
	val listPassword: MutableList<String> = mutableListOf()
	var map: Map<String, String>
	println("Введите количество пользователей:")
	val length: Int
	try {
		length = readln().toInt()
	} catch (e: Exception) {
		println("Недопустимый формат, вводите целое число")
		return
	}
	println("Введите начальный символ пароля:")
	val input: String = readln()
	val time = measureTimeMillis {
		withContext(newSingleThreadContext("list_thread_context")) {
			launch {
				getIdFlow(length).collect { i ->
					listId += i
				}
			}
			launch {
				getPasswordFlow(input, length).collect { i ->
					listPassword += i
				}
			}
		}
		map = (listId zip listPassword).toMap()
	}
	map.forEach { (k, v) -> println("ID user: $k, Password: $v") }
	
	println("Затраченное время на создание $length паролей: $time mc")
}

fun createPassword(): String {
	val password: MutableList<String> = mutableListOf()
	repeat(6) {
		password += (((0..9) + ('a'..'z')).random()).toString()
	}
	
	for (i in password.indices) {
		if ((i % 2) != 0 && password[i].toIntOrNull() == null) password[i] = password[i].uppercase()
	}
	return password.joinToString("")
}

fun getListOfPassword(input: String, length: Int): List<String> {
	val listOfPassword: MutableList<String> = mutableListOf()
	var count = 0
	while (count < length) {
		val password = createPassword()
		if (password.first().toString() == input) {
			listOfPassword += password
			count++
		}
	}
	return listOfPassword
}

fun getListId(length: Int): List<String> {
	val listId: MutableList<String> = mutableListOf()
	for (i in 1..length) {
		when (i) {
			in 1..9 -> listId += "00000$i"
			in 10..99 -> listId += "0000$i"
			in 100..999 -> listId += "000$i"
			in 1000..9999 -> listId += "00$i"
			in 10000..99999 -> listId += "0$i"
			100000 -> listId += "$i"
		}
	}
	return listId
}

fun getIdFlow(length: Int) = getListId(length).asFlow()

fun getPasswordFlow(input: String, length: Int) = getListOfPassword(input, length).asFlow()