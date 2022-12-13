package com.trotfan.trot.ui.home.ranking.history.component.monthly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trotfan.trot.R
import com.trotfan.trot.ui.home.ranking.history.component.RankingHistoryGenderTab
import com.trotfan.trot.ui.theme.*

@Composable
fun RankingHistoryMonthlyHeader() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 24.dp, end = 24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Gray100)
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                modifier = Modifier
                    .align(CenterVertically)
                    .clip(CircleShape)
                    .background(Gray900)
                    .size(24.dp),
                onClick = {

                }) {

                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.rotate(180f)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(CenterVertically)
                    .fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_calendar),
                    contentDescription = null,
                    tint = Gray750
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "2022.12",
                    color = Gray900,
                    style = FanwooriTypography.body3
                )

            }

            IconButton(
                modifier = Modifier
                    .align(CenterVertically)
                    .clip(CircleShape)
                    .background(Gray900)
                    .size(24.dp),
                onClick = {

                }) {

                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow),
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        RankingHistoryGenderTab()
    }
}

@Preview
@Composable
fun RankingHistoryHeaderPreview() {
    FanwooriTheme {
        RankingHistoryMonthlyHeader()
    }
}
