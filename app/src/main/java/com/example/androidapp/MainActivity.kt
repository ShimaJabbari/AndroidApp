package com.example.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.androidapp.ui.theme.AndroidAppTheme
import org.json.JSONArray
import java.io.InputStream
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sortedItems = displayData()

        setContent{
            AndroidAppTheme {
                SortedItemList(sortedItems)
            }
        }

    }

    fun readFile(): String? {
        var file: String? = null
        try {
            val  inputStream: InputStream = assets.open("hiring.json")
            file = inputStream.bufferedReader().use{it.readText()}
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return file
    }

    data class Item(val listId: Int, val name: String?)

    fun displayData(): List<Pair<Int, List<Item>>> {
        val rawJson = readFile()
        val jsonArray = JSONArray(rawJson)

        val items = buildList {
            for (i in 0 until jsonArray.length()) {
                val obj    = jsonArray.getJSONObject(i)
                val listId = obj.getInt("listId")
                val name   = obj.optString("name", null)
                add(Item(listId, name))
            }
        }

        return items

            .filter {
                !it.name.isNullOrBlank() && it.name != "null"
            }
            .groupBy { it.listId }
            .toSortedMap()
            .mapValues { (_, group) ->
                group.sortedBy { it.name?.substringAfter("Item ")?.toIntOrNull() ?: Int.MAX_VALUE }
            }

            .toList()
    }

    @Composable
    fun SortedItemList(groups: List<Pair<Int, List<Item>>>, modifier: Modifier = Modifier) {
        LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
            groups.forEach { (listId, items) ->

                item {
                    Text(
                        text   = "List $listId",
                        style  = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(items) { item ->
                    Text(
                        text     = item.name ?: "",
                        modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}
