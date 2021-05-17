package com.hongwei.android_nba_assist.view.season.playoff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hongwei.android_nba_assist.datasource.league.Tournament
import com.hongwei.android_nba_assist.util.ResourceByNameUtil
import com.hongwei.android_nba_assist.view.component.TeamLogo
import com.hongwei.android_nba_assist.view.theme.Grey80

object PlayOffHelper {
    @Composable
    fun PlayOffTeamLogoWrapper(teamAbbr: String, playOffTeamStatus: PlayOffTeamStatus = PlayOffTeamStatus.Normal) {
        if (teamAbbr.equals(Tournament.TBD, true)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .size(40.dp)
            ) {
                Text(
                    text = Tournament.TBD,
                    style = MaterialTheme.typography.body1,
                    color = when (playOffTeamStatus) {
                        PlayOffTeamStatus.Normal -> MaterialTheme.colors.onPrimary
                        PlayOffTeamStatus.Eliminated -> EliminatedTeamTextColor
                    },
                    textAlign = TextAlign.Center
                )
            }
        } else {
            TeamLogo(
                localPlaceholderResId = ResourceByNameUtil.getResourceIdByName(LocalContext.current, teamAbbr),
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .size(40.dp)
                    .alpha(
                        when (playOffTeamStatus) {
                            PlayOffTeamStatus.Normal -> 1f
                            PlayOffTeamStatus.Eliminated -> EliminatedAlpha
                        }
                    )
            )
        }
    }

    private const val EliminatedAlpha = 0.4f
    private val EliminatedTeamTextColor = Grey80
}