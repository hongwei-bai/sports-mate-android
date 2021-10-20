package com.hongwei.android_nba_assist.season

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hongwei.android_nba_assist.data.league.nba.Conference
import com.hongwei.android_nba_assist.data.league.nba.getConferenceByTeam
import com.hongwei.android_nba_assist.data.local.AppSettings
import com.hongwei.android_nba_assist.season.common.SeasonStatus
import com.hongwei.android_nba_assist.ui.animation.LoadingContent
import com.hongwei.android_nba_assist.ui.component.DataStatusSnackBar
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun Season() {
    val seasonViewModel = hiltNavGraphViewModel<SeasonViewModel>()
    val seasonStatus = seasonViewModel.seasonStatus.observeAsState().value
    val dataStatus = seasonViewModel.dataStatus.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    if (seasonStatus != null) {
        val pages = listOf(
            SeasonScreens.WestStanding,
            SeasonScreens.WestPlayIn,
            SeasonScreens.WestPlayOff,
            SeasonScreens.Final,
            SeasonScreens.EastPlayOff,
            SeasonScreens.EastPlayIn,
            SeasonScreens.EastStanding
        )
        val tabForActiveStage = when (getConferenceByTeam(AppSettings.myTeam)) {
            Conference.Western -> when (seasonStatus) {
                SeasonStatus.PlayInTournament -> 1
                SeasonStatus.PlayOff -> 2
                SeasonStatus.GrandFinal -> 3
                else -> 0
            }
            Conference.Eastern -> when (seasonStatus) {
                SeasonStatus.PlayInTournament -> 5
                SeasonStatus.PlayOff -> 4
                SeasonStatus.GrandFinal -> 3
                else -> 6
            }
        }
        val pagerState = rememberPagerState(
            pageCount = pages.size,
            initialPage = tabForActiveStage
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(seasonViewModel.isRefreshing.observeAsState().value == true),
            onRefresh = { seasonViewModel.refresh() },
        ) {
            Column {
                DataStatusSnackBar(dataStatus)
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(
                                pagerState, tabPositions
                            )
                        )
                    }
                ) {
                    pages.forEachIndexed { index, stage ->
                        Tab(
                            icon = {
                                Icon(
                                    imageVector = stage.icon,
                                    tint = if (tabForActiveStage == index) MaterialTheme.colors.secondary
                                    else MaterialTheme.colors.onPrimary,
                                    contentDescription = null
                                )
                            },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                        )
                    }
                }

                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> Standing(seasonViewModel.westernStanding.observeAsState().value, true)
//                        1 -> PlayInTournament(seasonViewModel.westernPlayIn.observeAsState().value, true)
//                        2 -> PlayOff(seasonViewModel.westernPlayOff.observeAsState().value, true)
//                        3 -> Final(seasonViewModel.playOffGrandFinal.observeAsState().value)
//                        4 -> PlayOff(seasonViewModel.easternPlayOff.observeAsState().value, false)
//                        5 -> PlayInTournament(seasonViewModel.easternPlayIn.observeAsState().value, false)
                        6 -> Standing(seasonViewModel.easternStanding.observeAsState().value, false)
                        else -> {

                        }
                    }
                }
            }
        }
    } else {
        LoadingContent()
    }
}