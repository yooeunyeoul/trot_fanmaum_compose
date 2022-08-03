package com.trotfan.trot.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trotfan.trot.R
import com.trotfan.trot.ui.theme.FanwooriTypography
import com.trotfan.trot.ui.theme.Gray100
import com.trotfan.trot.ui.theme.Gray400
import com.trotfan.trot.ui.theme.Gray800
import com.trotfan.trot.ui.utils.clickable

enum class SearchStatus {
    TrySearch, NoResult, SearchResult
}

@Composable
fun SearchTextField(
    onclick: () -> Unit = {},
    inputText: (String) -> Unit,
    isEnabled: Boolean = true,
    onClickSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current


    var showClearIcon by remember {
        mutableStateOf(false)
    }

    showClearIcon = text.isNotEmpty()

    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .clickable {
                onclick.invoke()
            },
        value = text,
        shape = RoundedCornerShape(12.dp),
        enabled = isEnabled,
        onValueChange = { outpout ->
            text = outpout
            // refresh search results
            inputText.invoke(outpout)

        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.icon_search),
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (showClearIcon) {
                Icon(
                    painterResource(id = R.drawable.input_clear),
                    contentDescription = "Clear Text",
                    modifier = Modifier.clickable {
                        text = ""
                        inputText.invoke("")
                    }
                )
            }
        },
        maxLines = 1,
        singleLine = true,
        textStyle = FanwooriTypography.body3,
        placeholder = {
            Text(
                text = stringResource(id = R.string.hint_search_star),
                style = FanwooriTypography.body3,
                color = Gray400
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = Gray100,
            cursorColor = Gray800,
        ),
        keyboardActions = KeyboardActions {
            onClickSearch.invoke(text)
            focusManager.clearFocus()
        }
    )
}

@Preview
@Composable
fun PreviewSearchTextField() {
    SearchTextField(inputText = {})
}