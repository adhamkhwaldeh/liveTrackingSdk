package com.kerberos.trackingSdk.ui.trip


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.material.pullrefresh.PullRefreshIndicator
//import androidx.compose.material.pullrefresh.pullRefresh
//import androidx.compose.material.pullrefresh.rememberPullRefreshState
// In your Composable file
//import eu.bambooapps.pullrefresh.rememberPullRefreshState
//import eu.bambooapps.pullrefresh.pullRefresh
//import eu.bambooapps.pullrefresh.PullRefreshIndicator
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.adhamkhwaldeh.commonlibrary.base.states.BaseState
import com.kerberos.trackingSdk.viewModels.TripManageViewModel
import com.kerberos.trackingSdk.viewModels.TripViewModel
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    viewModel: TripViewModel = koinViewModel(),
    tripManageViewModel: TripManageViewModel = koinViewModel(),
) {
    val lazyTripItems = viewModel.tripList.collectAsLazyPagingItems()

    val deleteResult = tripManageViewModel.deleteResult.collectAsState(BaseState.Initial())

    val isRefreshing = lazyTripItems.loadState.refresh is LoadState.Loading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { lazyTripItems.refresh() }
    )

    LaunchedEffect(deleteResult) {
        if (deleteResult.value is BaseState.BaseStateLoadedSuccessfully) {
            lazyTripItems.refresh()
        }
    }


    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lazyTripItems.itemCount) { index ->
                lazyTripItems[index]?.let { trip ->
                    TripItem(trip = trip, onDelete = { deletedTrip ->
                        tripManageViewModel.deleteTrip(deletedTrip)
                    })
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        val refreshState = lazyTripItems.loadState.refresh
        val appendState = lazyTripItems.loadState.append
        if (refreshState is LoadState.Loading && lazyTripItems.itemCount == 0) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (refreshState is LoadState.Error && lazyTripItems.itemCount == 0) {
            val error = (refreshState as LoadState.Error).error
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Error: ${error.message ?: "Unknown"}")
                Button(onClick = { lazyTripItems.retry() }) {
                    Text(text = "Retry")
                }
            }
        } else if (appendState is LoadState.Error) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Failed to load more: ${(appendState as LoadState.Error).error.message ?: "Unknown"}")
                Button(onClick = { lazyTripItems.retry() }) {
                    Text(text = "Retry")
                }
            }
        } else if (lazyTripItems.loadState.append.endOfPaginationReached && lazyTripItems.itemCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No trips found.")
            }
        }
    }

}
