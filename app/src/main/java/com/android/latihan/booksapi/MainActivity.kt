package com.android.latihan.booksapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.loopj.android.http.*
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var btnSearch: Button
    private lateinit var edtBook: EditText
    private lateinit var tvBookTitle: TextView
    private lateinit var tvBookAuthor: TextView
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtBook = findViewById(R.id.edt_book_search)

        tvBookTitle = findViewById(R.id.tv_book_title)

        tvBookAuthor = findViewById(R.id.tv_book_author)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.INVISIBLE

        btnSearch = findViewById(R.id.btn_book_search)
        btnSearch.setOnClickListener {
            searchBook()
        }
    }

    private fun searchBook() {
        progressBar.visibility = View.VISIBLE

        val search = edtBook.text.toString()
        val client = AsyncHttpClient()
        val url = "https://www.googleapis.com/books/v1/volumes?q={$search}"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d(TAG, result)
                progressBar.visibility = View.INVISIBLE

                try {
                    val jsonObject = JSONObject(result)
                    val itemsArray = jsonObject.getJSONArray("items")

                    var i = 0
                    var title = ""
                    var author = ""

                    while (i < itemsArray.length()) {
                        val book = itemsArray.getJSONObject(i)
                        val volumeInfo = book.getJSONObject("volumeInfo")
                        try {
                            title = volumeInfo.getString("title")
                            author = volumeInfo.getString("authors")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        i++
                    }

                    apply {
                        tvBookTitle.text = title
                        tvBookAuthor.text = author
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "StatusCode: Bad Request"
                    403 -> "StatusCode: Forbidden"
                    404 -> "StatusCode: Not Found"
                    else -> "StatusCode: ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}