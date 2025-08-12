package com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiserv.commercehub.androidttp.ui.theme.AndroidTapToPayDemoTheme
import kotlinx.coroutines.launch

/**
 * Displays a scrollable list of logs.
 *
 * @param data: list of String logs to be displayed
 * @param listState: state object to handle scrolling behavior; default rememberLazyListState
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LogsListView(
    data: List<String>,
    listState: LazyListState = rememberLazyListState()) {

    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        listState.animateScrollToItem(data.size - 1)
    }

    LazyColumn(modifier = Modifier
        .padding(5.dp)
        .heightIn(200.dp), state = listState) {
        items(data) { product ->
            ShowLogsListItem(product)
        }
    }
}

/**
 * Displays a single log entry.
 *
 * @param data: the log string to display
 */
@Composable
fun ShowLogsListItem(data: String) {
    Row(modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth()
    ) {
        Text(
            text = data.toString(),
            color = Color.Black,
            fontSize = 12.sp
        )
    }
}

/**
 * Preview the ShowLogsListItem component.
 * Displays a single log entry with sample text.
 */
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AndroidTapToPayDemoTheme(useSystemUiController = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            ShowLogsListItem("Hello")
        }
    }
}
