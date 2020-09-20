package com.dreampany.tube.ui.player

import android.os.Bundle
import androidx.lifecycle.Observer
import com.dreampany.framework.data.model.Response
import com.dreampany.framework.misc.constant.Constants
import com.dreampany.framework.misc.exts.*
import com.dreampany.framework.misc.func.SmartError
import com.dreampany.framework.ui.activity.InjectActivity
import com.dreampany.framework.ui.model.UiTask
import com.dreampany.tube.R
import com.dreampany.tube.data.enums.Action
import com.dreampany.tube.data.enums.State
import com.dreampany.tube.data.enums.Subtype
import com.dreampany.tube.data.enums.Type
import com.dreampany.tube.data.model.Video
import com.dreampany.tube.databinding.VideoPlayerActivityBinding
import com.dreampany.tube.ui.home.model.VideoItem
import com.dreampany.tube.ui.home.vm.VideoViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import timber.log.Timber

/**
 * Created by roman on 7/7/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class VideoPlayerActivity : InjectActivity() {

    private lateinit var bind: VideoPlayerActivityBinding
    private lateinit var vm: VideoViewModel
    private lateinit var input: Video

    override val layoutRes: Int = R.layout.video_player_activity
    override val menuRes: Int = R.menu.videos_menu
    override val searchMenuItemId: Int = R.id.item_search

    override val params: Map<String, Map<String, Any>?>?
        get() {
            val params = HashMap<String, HashMap<String, Any>?>()

            val param = HashMap<String, Any>()
            param.put(Constants.Param.PACKAGE_NAME, packageName)
            param.put(Constants.Param.VERSION_CODE, versionCode)
            param.put(Constants.Param.VERSION_NAME, versionName)
            param.put(Constants.Param.SCREEN, "Tube.VideoPlayerActivity")

            params.put(Constants.Event.ACTIVITY, param)
            return params
        }

    override fun onStartUi(state: Bundle?) {
        val task = (task ?: return) as UiTask<Type, Subtype, State, Action, Video>
        input = task.input ?: return
        initUi()
        vm.loadVideo(input)
    }

    override fun onStopUi() {
    }

    private fun onFavoriteClicked(input: Video) {
        vm.toggleFavorite(input)
    }

    private fun initUi() {
        if (::bind.isInitialized) return
        bind = getBinding()
        vm = createVm(VideoViewModel::class)
        vm.subscribe(this, Observer { this.processResponse(it) })
        vm.subscribes(this, Observer { this.processResponses(it) })

        bind.swipe.init(this)
        bind.swipe.disable()
        bind.title.text = input.title
        bind.info.text = getString(
            R.string.video_info_format,
            input.channelTitle,
            input.viewCount.count,
            input.publishedAt.time
        )
        bind.favorite.setOnSafeClickListener {
            onFavoriteClicked(input)
        }

        lifecycle.addObserver(bind.player)
        bind.player.getPlayerUiController().apply {
            enableLiveVideoUi(input.isLive)
            setVideoTitle(input.title.value)
            showBufferingProgress(true)
            showMenuButton(true)
        }
        bind.player.enableBackgroundPlayback(true)
        //bind.player.getPlayerUiController().enableLiveVideoUi(video.isLive)
        bind.player.addYouTubePlayerListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {

            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
            }

            override fun onPlaybackQualityChange(
                youTubePlayer: YouTubePlayer,
                playbackQuality: PlayerConstants.PlaybackQuality
            ) {
            }

            override fun onPlaybackRateChange(
                player: YouTubePlayer,
                playbackRate: PlayerConstants.PlaybackRate
            ) {
            }

            override fun onReady(player: YouTubePlayer) {
                player.loadOrCueVideo(lifecycle, input.id, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
            }

            override fun onVideoDuration(player: YouTubePlayer, duration: Float) {
            }

            override fun onVideoId(player: YouTubePlayer, videoId: String) {
            }

            override fun onVideoLoadedFraction(
                youTubePlayer: YouTubePlayer,
                loadedFraction: Float
            ) {

            }

        })
    }

    private fun processResponse(response: Response<Type, Subtype, State, Action, VideoItem>) {
        if (response is Response.Progress) {
            bind.swipe.refresh(response.progress)
        } else if (response is Response.Error) {
            processError(response.error)
        } else if (response is Response.Result<Type, Subtype, State, Action, VideoItem>) {
            Timber.v("Result [%s]", response.result)
            processResult(response.result)
        }
    }

    private fun processResponses(response: Response<Type, Subtype, State, Action, List<VideoItem>>) {
        if (response is Response.Progress) {
            bind.swipe.refresh(response.progress)
        } else if (response is Response.Error) {
            processError(response.error)
        } else if (response is Response.Result<Type, Subtype, State, Action, List<VideoItem>>) {
            Timber.v("Result [%s]", response.result)
            processResults(response.result)
        }
    }

    private fun processError(error: SmartError) {
        val titleRes = if (error.hostError) R.string.title_no_internet else R.string.title_error
        val message =
            if (error.hostError) getString(R.string.message_no_internet) else error.message
        showDialogue(
            titleRes,
            messageRes = R.string.message_unknown,
            message = message,
            onPositiveClick = {

            },
            onNegativeClick = {

            }
        )
    }

    private fun processResults(result: List<VideoItem>?) {

    }

    private fun processResult(result: VideoItem?) {
        if (result != null) {
            bind.favorite.isLiked = result.favorite
        }
    }
}