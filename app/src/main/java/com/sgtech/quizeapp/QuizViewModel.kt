package com.sgtech.quizeapp

import android.os.AsyncTask
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

@Suppress("DEPRECATION")
class QuizViewModel :
    ViewModel() {
    private val _score = mutableIntStateOf(0)
    val score = _score


    private val _quizList = mutableStateListOf<Quiz>()
    val quizList = _quizList

    init {
        fetchQuizList()
    }

    fun fetchQuizList() {
        _quizList.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val quizes = MyQuizAsyncTask().execute().get()
            _quizList.addAll(quizes)
        }
    }
}

data class Quiz(val question: String, val options: HashMap<String, Any>, val answer: String)

class MyQuizAsyncTask : AsyncTask<Void, Void, List<Quiz>>() {
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): List<Quiz> {
        val list = ArrayList<Quiz>()
        try {
            val okHttpClient = OkHttpClient()
            val apiKey = "RHJgvkewhutwXtZqCyV2FTdb3wjDxWtNoNdjCS0Y";
            val request = Request.Builder()
                .url("https://quizapi.io/api/v1/questions")
                .header("X-Api-Key", apiKey)
                .header("Content-Type", "application/json")
                .header("limit", "10")
                .build()
            val response = okHttpClient.newCall(request).execute()
            val body = Json.parseToJsonElement(response.body!!.string()).jsonArray

            for (quiz in body.iterator()) {
                val question = quiz.jsonObject["question"]!!.jsonPrimitive.content
                val options = quiz.jsonObject["answers"]!!.jsonObject
                val map = HashMap<String, Any>()
                for ((key, value) in options) {
                    if (value.toString() != "null") {
                        map[key] = value.jsonPrimitive.content
                    }

                }


                var ans: String? = null
                val correctAnswers = quiz.jsonObject["correct_answers"]!!.jsonObject
                for ((key, value) in correctAnswers) {
                    if (value.toString().isBlank()) {
                        continue
                    }
                    if (value.jsonPrimitive.content == "true") {
                        ans = key.replace("_correct", "")
                        ans = map[ans].toString()
                        break;
                    }

                }


                val quizData = Quiz(
                    question,
                    map,
                    ans.toString()
                )
                list.add(quizData)
                Log.d("quizItem", "doInBackground: $quizData")

            }
        } catch (e: Exception) {
            Log.d("quizItem", "doInBackground: $e")
        }

        return list
    }

}