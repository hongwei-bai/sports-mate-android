package com.hongwei.android_nba_assist.view.season

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hongwei.android_nba_assist.R
import com.hongwei.android_nba_assist.util.ResourceByNameUtil
import com.hongwei.android_nba_assist.view.component.TeamLogo
import com.hongwei.android_nba_assist.view.theme.BlackAlphaA0
import com.hongwei.android_nba_assist.view.theme.Grey60
import java.util.*

@Composable
fun PlayInTournament(standing: List<RankedTeamViewObject>?, onLeft: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = BlackAlphaA0)
                .verticalScroll(ScrollState(0))
        ) {
            PlayInHeader(onLeft)
            standing?.subList(0, 10)?.forEach {
                PlayInTeam(it)
            }
        }
    }
}

@Composable
private fun PlayInTeam(rank: RankedTeamViewObject) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(40.dp)
            .padding(top = 0.dp, bottom = 0.dp)
    ) {
        RankTeam(modifier = Modifier.weight(3f), rank = rank, isLogoOnly = false)
        val colorOnPrimary = MaterialTheme.colors.onPrimary
        Row(modifier = Modifier.weight(2f)) {
            when (rank.rank) {
                7 -> TournamentLinePrePromotion(colorOnPrimary, true)
                8 -> TournamentLinePrePromotion(colorOnPrimary, false)
                9 -> TournamentLinePrePromotion(colorOnPrimary, true)
                10 -> TournamentLinePrePromotion(colorOnPrimary, false)
                else -> {
                }
            }
        }
        RankTeam(modifier = Modifier.weight(2f), rank = rank)
        Row(modifier = Modifier.weight(2f)) {
            Canvas(modifier = Modifier) {

            }
        }
        RankTeam(modifier = Modifier.weight(2f), rank = rank)
    }
}

@Composable
private fun TournamentLinePrePromotion(onPrimary: Color, highRankTeam: Boolean) {
    val stroke = 5f
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val nextRoundWidth = canvasWidth * 0.6f
        drawLine(
            start = Offset(x = 0f, y = canvasHeight / 2),
            end = Offset(x = nextRoundWidth, y = canvasHeight / 2),
            color = onPrimary,
            strokeWidth = stroke
        )
        if (highRankTeam) {
            drawLine(
                start = Offset(x = nextRoundWidth, y = canvasHeight / 2),
                end = Offset(x = nextRoundWidth, y = canvasHeight),
                color = onPrimary,
                strokeWidth = stroke
            )
        } else {
            drawLine(
                start = Offset(x = nextRoundWidth, y = 0f),
                end = Offset(x = nextRoundWidth, y = canvasHeight / 2),
                color = onPrimary,
                strokeWidth = stroke
            )
        }
    }
}

@Composable
private fun RankTeam(modifier: Modifier, rank: RankedTeamViewObject, isLogoOnly: Boolean = true) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (!isLogoOnly) {
            Text(
                text = rank.rank.toString(),
                style = MaterialTheme.typography.subtitle2,
                color = getTextColorByRank(rank.rank),
                textAlign = TextAlign.End,
                modifier = Modifier.width(32.dp)
            )
        }
        TeamLogo(
            logoUrl = rank.team.logo,
            localPlaceholderResId = ResourceByNameUtil.getResourceIdByName(LocalContext.current, rank.team.abbrev),
            modifier = Modifier
                .size(40.dp)
                .padding(start = 4.dp, end = 4.dp)
                .alpha(getTeamLogoAlphaByRank(rank.rank))
        )
        if (!isLogoOnly) {
            Text(
                text = rank.team.abbrev.toUpperCase(Locale.US),
                style = MaterialTheme.typography.subtitle2,
                color = getTextColorByRank(rank.rank),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Clip,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun PlayInHeader(onLeft: Boolean) {
    Row(
        horizontalArrangement = if (onLeft) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = stringResource(
                    if (onLeft) R.string.season_play_in_tournament_title_western
                    else R.string.season_play_in_tournament_title_eastern
                ),
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
private fun getTextColorByRank(rank: Int): Color = when (rank) {
    in 7..8 -> MaterialTheme.colors.onPrimary
    in 9..10 -> MaterialTheme.colors.onPrimary
    else -> Grey60
}

private fun getTeamLogoAlphaByRank(rank: Int): Float = when (rank) {
    in 7..8 -> 1f
    in 9..10 -> 0.9f
    else -> 0.6f
}