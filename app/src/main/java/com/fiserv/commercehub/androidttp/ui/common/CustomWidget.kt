package com.fiserv.commercehub.androidttp.ui.common


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.ui.main.screen.demo.adapter.LogsListView
import com.fiserv.commercehub.androidttp.ui.theme.FiservColorCode

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit,
) {
    Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(FiservColorCode)
    ) {
        Text(text, fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
) {
    Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .height(50.dp)
            .width(150.dp)
            .fillMaxWidth()
            .background(FiservColorCode)
    ) {
        Text(text, fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun CustomViewLogConsole(
    data: List<String>,
    onClickDisable: () -> Unit,
) {


    Row(
        modifier = Modifier
            .height(40.dp)
            .background(color = FiservColorCode)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.log_console),

            color = Color.White,
            modifier = Modifier.absolutePadding(left = 5.dp)
        )
        Spacer(Modifier.weight(1f))


        IconButton(onClick = onClickDisable) {
            Icon(
                painter = painterResource(id = R.drawable.cancel),
                contentDescription = "Your Icon",
                tint = White,  // Change the color of the icon
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp),  // Change the size of the icon
            )

        }
    }

    HorizontalDivider(
        color = Color.Black,
        thickness = 1.dp
    )
    Box(modifier = Modifier.height(10.dp))


    if (data.isNotEmpty())
        LogsListView(data)


}

@Composable
fun CircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(50.dp)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 4.dp
    )
}






