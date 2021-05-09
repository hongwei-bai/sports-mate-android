package com.hongwei.android_nba_assist.view.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.hongwei.android_nba_assist.R

class UrlProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(0, 1, 82)
    override val count: Int = values.count()
}

@Preview
@Composable
fun GamesLeftView(@PreviewParameter(UrlProvider::class) gamesLeft: Int) {
    Row(verticalAlignment = Alignment.Bottom) {
        if (gamesLeft > 0) {
            Text(
                    text = gamesLeft.toString(),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.secondary
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                    text = stringResource(R.string.games_left),
                    style = MaterialTheme.typography.h6
            )
        } else {
            Text(
                    text = stringResource(R.string.no_games_left),
                    style = MaterialTheme.typography.body1
            )
        }
    }
}