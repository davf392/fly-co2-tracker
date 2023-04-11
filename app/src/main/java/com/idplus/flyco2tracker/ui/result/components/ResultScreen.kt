package com.idplus.flyco2tracker.ui.result.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idplus.flyco2tracker.R


@Composable
fun CarbonFootprintCard(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    @DrawableRes drawable: Int,
    carbonResult: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
//            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RectangleShape)
                .padding(6.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            Text(text = stringResource(id = label), fontWeight = FontWeight.Bold)
            Text(text = stringResource(R.string.unit_carbon_footprint, carbonResult))
        }
    }
}

@Composable
fun CarbonFootprintCardColumn() {
    CarbonFootprintCard(drawable = R.drawable.ic_eco_green, label = R.string.text_carbon_budget, carbonResult = 2000)
    CarbonFootprintCard(drawable = R.drawable.ic_airplane, label = R.string.text_result_plane, carbonResult = 0)
    CarbonFootprintCard(drawable = R.drawable.ic_car, label = R.string.text_result_car, carbonResult = 0)
    CarbonFootprintCard(drawable = R.drawable.ic_autobus, label = R.string.text_result_bus, carbonResult = 0)
    CarbonFootprintCard(drawable = R.drawable.ic_train, label = R.string.text_result_train, carbonResult = 0)
}

@Composable
fun ResultScreen(modifier: Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(id = R.string.text_view_result),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        CarbonFootprintCardColumn()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun CarbonFootprintCardPreview() {
    CarbonFootprintCard(drawable = R.drawable.ic_eco_green, label = R.string.text_carbon_budget, carbonResult = 2000)
}

@Preview(widthDp = 360, heightDp = 640, showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun ResultScreenPreview() {
    ResultScreen(Modifier.padding(8.dp))
}