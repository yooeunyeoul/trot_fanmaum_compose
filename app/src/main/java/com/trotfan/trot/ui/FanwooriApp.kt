package com.trotfan.trot.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.trotfan.trot.PurchaseHelper
import com.trotfan.trot.model.Ticket
import com.trotfan.trot.model.TicketItem
import com.trotfan.trot.model.VoteMainStar
import com.trotfan.trot.ui.components.dialog.VerticalDialog
import com.trotfan.trot.ui.home.HomeSections
import com.trotfan.trot.ui.home.TrotBottomBar
import com.trotfan.trot.ui.home.dialog.VotingCompleteDialog
import com.trotfan.trot.ui.home.viewmodel.HomeViewModel
import com.trotfan.trot.ui.home.vote.dialog.VotingBottomSheet
import com.trotfan.trot.ui.home.vote.viewmodel.VoteHomeViewModel
import com.trotfan.trot.ui.home.vote.voteTopShareText
import com.trotfan.trot.ui.theme.FanwooriTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Destinations {
    const val HOME_ROUTE = "home"
    const val DETAIL_ROUTE = "detail"
    const val DETAIL_ID_KEY = "detailId"
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FanwooriApp(
    viewModel: HomeViewModel = hiltViewModel(),
    purchaseHelper: PurchaseHelper
) {
    FanwooriTheme {
        val systemUiController = rememberSystemUiController()
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        val navController: NavHostController = rememberNavController()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val votingBottomSheetState =
            rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )
        val star: VoteMainStar? by viewModel.voteStar.collectAsState()
        val voteId: Int by viewModel.voteId.collectAsState()
        val votingCompleteState by viewModel.votingCompleteState.collectAsState()
        val context = LocalContext.current
        var currentRoute by remember {
            mutableStateOf(HomeSections.Vote)
        }
//        val lazyListState = rememberLazyListState()
        val lazyListStates = hashMapOf(
            Pair(first = HomeSections.Vote.route, rememberLazyListState()),
            Pair(first = HomeSections.Ranking.route, rememberLazyListState()),
            Pair(first = HomeSections.Charge.route, rememberLazyListState()),
            Pair(first = HomeSections.MyPage.route, rememberLazyListState())
        )
        var voteHomeViewModel: VoteHomeViewModel? = null

        var votesQuantity by remember {
            mutableStateOf(0L)
        }
        systemUiController.setSystemBarsColor(color = Color.White)

        ModalBottomSheetLayout(
            sheetContent = {
                VotingBottomSheet(
                    votingBottomSheetState,
                    homeViewModel = viewModel,
                    onDismiss = { star_id: Int?, quantity: Long? ->
                        votesQuantity = quantity ?: 0
                        star_id?.let {
                            viewModel.postVoteTicket(voteId = voteId, star_id, quantity!!)
                        }
                    },
                    onChargeClick = {
                        coroutineScope.launch {
                            votingBottomSheetState.hide()
                        }
                        currentRoute = HomeSections.Charge
                    }
                )
            },
            sheetState = votingBottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Scaffold(
                bottomBar = {
                    when (navBackStackEntry?.destination?.route) {
                        HomeSections.Vote.route, HomeSections.Ranking.route,
                        HomeSections.MyPage.route, HomeSections.Charge.route -> {
                            TrotBottomBar(
                                tabs = HomeSections.values(),
                                currentRoute = currentRoute,
                                onSelected = { route ->
                                    currentRoute = route
                                    navController.navigate(route.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(HomeSections.Vote.route) {
                                            saveState = true
                                        }
                                    }
                                },
                                lazyListStates = lazyListStates
                            )
                        }
                    }
                },
                scaffoldState = scaffoldState
            ) {
                BackHandler(enabled = votingBottomSheetState.isVisible) {
                    coroutineScope.launch {
                        votingBottomSheetState.hide()
                    }
                }
                NavigationComponent(
                    navController = navController,
                    onVotingClick = { voteId: Int, voteTicket: Ticket, star: VoteMainStar?, voteViewModel: VoteHomeViewModel ->
                        coroutineScope.launch {
                            star?.let {
                                voteHomeViewModel = voteViewModel
                                viewModel.voteTicket.emit(voteTicket)
                                viewModel.voteStar.emit(star)
                                viewModel.voteId.emit(voteId)
                                viewModel.voteCnt.emit(TextFieldValue(""))
                                votingBottomSheetState.show()
                            }
                        }
                    },
                    onNavigateBottomBar = { section ->
                        currentRoute = section
                    },
                    lazyListStates = lazyListStates,
                    purchaseHelper = purchaseHelper
                )

                when (votingCompleteState) {
                    1 -> {
                        LaunchedEffect(Unit) {
                            voteHomeViewModel?.addMyTicketsToBoard(
                                votes = votesQuantity.toInt(),
                                starName = star?.name ?: ""
                            )
                            voteHomeViewModel?.refreshLocalVoteList(
                                votes = votesQuantity.toInt(),
                                star = star
                            )
                        }
                        coroutineScope.launch {
                            votingBottomSheetState.hide()
                        }
                        VotingCompleteDialog(
                            starName = star?.name ?: "",
                            quantity = votesQuantity.toInt(),
                            onDismiss = {
                                coroutineScope.launch {
                                    viewModel.votingCompleteState.emit(0)
                                    voteHomeViewModel?.getVoteTickets(purchaseHelper)
                                }
                            }, onPositive = {
                                val sendIntent: Intent = Intent().apply {
                                    Firebase.dynamicLinks.shortLinkAsync {
                                        link = Uri.parse("https://play.google.com/store/apps/details?id=com.trotfan.trot")
                                        domainUriPrefix = "https://fanmaum.page.link"
                                    }.addOnSuccessListener {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT, voteTopShareText(star?.name, it.shortLink.toString())
                                        )

                                        type = "text/plain"
                                        val shareIntent = Intent.createChooser(this, null)
                                        context.startActivity(shareIntent)
                                    }
                                }
                            }
                        )
                    }
                    909 -> {
                        coroutineScope.launch {
                            votingBottomSheetState.hide()
                        }
                        VerticalDialog(
                            contentText = "지금은 집계 시간이에요!\n" +
                                    "집계 중에는 투표할 수 없어요.\n" +
                                    "23:30:00 ~ 23:59:59", buttonOneText = "확인"
                        ) {
                            coroutineScope.launch {
                                viewModel.votingCompleteState.emit(0)
                            }
                        }
                    }
                    44 -> {
                        VerticalDialog(
                            contentText = "네트워크 오류로\n" +
                                    "투표가 완료되지 않았습니다.\n" +
                                    "잠시 후 다시 시도해주세요.", buttonOneText = "확인"
                        ) {
                            coroutineScope.launch {
                                viewModel.votingCompleteState.emit(0)
                            }
                        }
                    }
                }
            }
        }
    }
}