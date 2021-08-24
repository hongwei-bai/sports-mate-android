package com.hongwei.android_nba_assist.viewmodel

import androidx.lifecycle.*
import com.hongwei.android_nba_assist.repository.NbaStatRepository
import com.hongwei.android_nba_assist.view.season.PlayOffGrandFinalViewObject
import com.hongwei.android_nba_assist.view.season.TeamStat
import com.hongwei.android_nba_assist.view.season.common.SeasonStatus
import com.hongwei.android_nba_assist.view.season.playin.PlayInStat
import com.hongwei.android_nba_assist.view.season.playoff.PlayOffStat
import com.hongwei.android_nba_assist.viewmodel.helper.ExceptionHelper.nbaExceptionHandler
import com.hongwei.android_nba_assist.viewmodel.mapper.PlayOffViewObjectMapper.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val nbaStatRepository: NbaStatRepository
) : ViewModel() {
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    val dataStatus = nbaStatRepository.dataStatus.asLiveData()

    val seasonStatus: LiveData<SeasonStatus> =
        nbaStatRepository.getPlayOff()
            .map {
                when {
                    it.playOffOngoing -> SeasonStatus.PlayOff
                    it.playInOngoing -> SeasonStatus.PlayInTournament
                    it.seasonOngoing -> SeasonStatus.RegularSeason
                    else -> SeasonStatus.End
                }
            }.asLiveData()

    val westernPlayOff: LiveData<PlayOffStat> =
        nbaStatRepository.getPlayOff().combine(nbaStatRepository.getStanding()) { playOff, standing ->
            playOff.playOff.western.map(standing.western, playOff.playIn.western)
        }.asLiveData()

    val easternPlayOff: LiveData<PlayOffStat> =
        nbaStatRepository.getPlayOff().combine(nbaStatRepository.getStanding()) { playOff, standing ->
            playOff.playOff.eastern.map(standing.eastern, playOff.playIn.eastern)
        }.asLiveData()

    val playOffGrandFinal: LiveData<PlayOffGrandFinalViewObject> =
        nbaStatRepository.getPlayOff()
            .map {
                PlayOffGrandFinalViewObject(
                    teamFromWestern = it.playOff.grandFinal.teamFromWestern,
                    teamFromEastern = it.playOff.grandFinal.teamFromEastern,
                    scoreWesternWinner = it.playOff.grandFinal.scoreWesternWinner,
                    scoreEasternWinner = it.playOff.grandFinal.scoreEasternWinner,
                    winner = it.playOff.grandFinal.winner
                )
            }.asLiveData()

    val westernPlayIn: LiveData<PlayInStat> =
        nbaStatRepository.getPlayOff().combine(nbaStatRepository.getStanding()) { playOff, standing ->
            PlayInStat(
                teamsAbbr = standing.western.standings.subList(0, 10).map { it.team.abbrev.toLowerCase(Locale.US) },
                winnerOf78 = playOff.playIn.western.winnerOf78,
                loserOf78 = playOff.playIn.western.loserOf78,
                winnerOf910 = playOff.playIn.western.winnerOf910,
                loserOf910 = playOff.playIn.western.loserOf910,
                lastWinner = playOff.playIn.western.lastWinner
            )
        }.asLiveData()

    val easternPlayIn: LiveData<PlayInStat> =
        nbaStatRepository.getPlayOff().combine(nbaStatRepository.getStanding()) { playOff, standing ->
            PlayInStat(
                teamsAbbr = standing.eastern.standings.subList(0, 10).map { it.team.abbrev.toLowerCase(Locale.US) },
                winnerOf78 = playOff.playIn.eastern.winnerOf78,
                loserOf78 = playOff.playIn.eastern.loserOf78,
                winnerOf910 = playOff.playIn.eastern.winnerOf910,
                loserOf910 = playOff.playIn.eastern.loserOf910,
                lastWinner = playOff.playIn.eastern.lastWinner
            )
        }.asLiveData()

    val westernStanding: LiveData<List<TeamStat>> =
        nbaStatRepository.getStanding().map {
            it.western.standings.map { entity ->
                TeamStat(
                    rank = entity.rank,
                    teamAbbr = entity.team.abbrev.toLowerCase(Locale.US),
                    team = entity.team.name ?: entity.team.abbrev.toUpperCase(Locale.US),
                    logoResourceName = entity.team.abbrev.toLowerCase(Locale.US),
                    logoUrl = entity.team.logo,
                    wins = entity.wins,
                    losses = entity.losses,
                    gamesBack = entity.gamesBack,
                    currentStreak = entity.currentStreak,
                    last10Records = entity.last10Records
                )
            }
        }.asLiveData()

    val easternStanding: LiveData<List<TeamStat>> =
        nbaStatRepository.getStanding().map {
            it.eastern.standings.map { entity ->
                TeamStat(
                    rank = entity.rank,
                    teamAbbr = entity.team.abbrev.toLowerCase(Locale.US),
                    team = entity.team.name ?: entity.team.abbrev.toUpperCase(Locale.US),
                    logoResourceName = entity.team.abbrev.toLowerCase(Locale.US),
                    logoUrl = entity.team.logo,
                    wins = entity.wins,
                    losses = entity.losses,
                    gamesBack = entity.gamesBack,
                    currentStreak = entity.currentStreak,
                    last10Records = entity.last10Records
                )
            }
        }.asLiveData()

    fun refresh() {
        isRefreshing.value = true
        viewModelScope.launch(Dispatchers.IO + nbaExceptionHandler) {
            val jobs = listOf(
                async { nbaStatRepository.fetchStandingFromBackend() },
                async { nbaStatRepository.fetchPlayOffFromBackend() }
            )
            jobs.awaitAll()
            isRefreshing.postValue(false)
        }
    }
}