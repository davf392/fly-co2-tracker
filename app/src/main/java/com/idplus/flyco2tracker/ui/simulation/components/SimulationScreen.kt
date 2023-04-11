package com.idplus.flyco2tracker.ui.simulation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import coil.compose.rememberAsyncImagePainter
import com.idplus.flyco2tracker.R


// https://stackoverflow.com/questions/64419367/does-jetpack-compose-offer-a-material-autocomplete-textview-replacement

@Composable
fun AirportSearchAutoCompleteDropDown(
    modifier: Modifier,
    @StringRes hintLabel: Int,
    @DrawableRes leadingIconDrawable: Int,  // icon located at the left of the text field
    value: TextFieldValue = TextFieldValue(""),  // current value of the text field
    setValue: (TextFieldValue) -> Unit = {},  // callback triggered whenever the current value is updated
    onDismissRequest: () -> Unit = {},  // callback triggered whenever focus is out of text field
    onClearText: () -> Unit = {},  // callback triggered whenever the user clicks on the clear icon button
    dropDownExpanded: Boolean = false,  // determines if the drop down is displayed or not
    listAirports: List<String> = emptyList()  // the list of airports to display in the drop down list
) {
    val visible by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = setValue,
            placeholder = { Text(stringResource(id = hintLabel)) },
            leadingIcon = {
                Image(
                    painter = painterResource(leadingIconDrawable),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp))
            },
            modifier = modifier
                .heightIn(56.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused)
                        onDismissRequest()
                }
        )
        if(visible) {
            IconButton(
                onClick = onClearText,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_close_24),
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            expanded = dropDownExpanded,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = onDismissRequest
        ) {
            listAirports.forEach { airport ->
                DropdownMenuItem(onClick = {
                    setValue(TextFieldValue(airport, TextRange(airport.length)))
                }) {
                    Text(text = airport)
                }
            }
        }
    }
}

@Composable
fun FiltersButtonToggleGroup(items: List<String>) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = { /*indexChanged(index)*/ },
                shape = when (index) {
                    // left outer button
                    0 -> RoundedCornerShape(topStart = 8.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 0.dp)
                    // right outer button
                    items.size - 1 -> RoundedCornerShape(topStart = 0.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 8.dp)
                    // middle button
                    else -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                },
                border = BorderStroke(1.dp, if(1 == index) { MaterialTheme.colors.primary } else { Color.DarkGray.copy(alpha = 0.75f)}),
                colors = if(1 == index) {
                    // selected colors
                    ButtonDefaults.outlinedButtonColors(backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colors.primary)
                } else {
                    // not selected colors
                    ButtonDefaults.outlinedButtonColors(backgroundColor = MaterialTheme.colors.surface, contentColor = MaterialTheme.colors.primary)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item,
                    color = if(1 == index) MaterialTheme.colors.primary else Color.DarkGray.copy(alpha = 0.9f)
                )
            }
        }

    }
}

@Composable
fun ImageCityDestination(
    modifier: Modifier = Modifier,
    urlImage: String?
) {
    Image(
        painter = if(urlImage != null)
            rememberAsyncImagePainter(urlImage)
        else
            painterResource(id = R.drawable.ic_broken_image),
        contentDescription =  null,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(150.dp)
            .padding(top = 8.dp)
    )
}

@Composable
fun SimulationScreen(
    modifier: Modifier,
    onCalculateClick: () -> Unit = {}
) {
    Column(modifier) {
        AirportSearchAutoCompleteDropDown(
            modifier = Modifier,
            hintLabel = R.string.label_hint_departure,
            leadingIconDrawable = R.drawable.ic_plane_take_off
        )
        Spacer(modifier = Modifier.height(8.dp))
        AirportSearchAutoCompleteDropDown(
            modifier = Modifier,
            hintLabel = R.string.label_hint_arrival,
            leadingIconDrawable = R.drawable.ic_plane_land
        )
        Spacer(Modifier.height(8.dp))
        FiltersButtonToggleGroup(listOf("Aller Simple", "Aller Retour"))
        FiltersButtonToggleGroup(listOf("Normal", "Business", "Confort"))
        ImageCityDestination(urlImage = null)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onCalculateClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Calculer")
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun AirportSearchAutoCompleteDropDownPreview() {
    AirportSearchAutoCompleteDropDown(
        modifier = Modifier.padding(8.dp),
        hintLabel = R.string.label_hint_departure,
        leadingIconDrawable = R.drawable.ic_plane_land
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun FiltersButtonToggleGroupPreview() {
    FiltersButtonToggleGroup(listOf("Aller Simple", "Aller Retour"))
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun ImageCityDestinationPreview() {
    ImageCityDestination(
        modifier = Modifier,
        urlImage = null
    )
}

@Preview(widthDp = 360, heightDp = 640, showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun SimulationScreenPreview() {
    SimulationScreen(Modifier.padding(8.dp))
}

