package vip.yazilim.p2g.android.ui.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getHumanReadableTimestamp

/**
 * @author mustafaarifsisman - 23.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class PlayerAdapter(private var playerSongList: MutableList<Song>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    private lateinit var view: View

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentMs: Long = 0
        private var maxMs: Long = 0

        // Minimized views
        private val songName: TextView = itemView.findViewById(R.id.song_name)
        private val songArtists: TextView = itemView.findViewById(R.id.song_artists)
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)
        private val seekBar: SeekBar = itemView.findViewById(R.id.seek_bar)

        // Expanded views
        private val songNameExp: TextView = itemView.findViewById(R.id.song_name_exp)
        private val songArtistsExp: TextView = itemView.findViewById(R.id.song_artists_exp)
        private val songImageExp: ImageView = itemView.findViewById(R.id.song_image_exp)
        private val seekBarExp: SeekBar = itemView.findViewById(R.id.seek_bar_exp)
        private val songCurrent: TextView = itemView.findViewById(R.id.song_current)
        private val songMax: TextView = itemView.findViewById(R.id.song_max)

        @SuppressLint("ClickableViewAccessibility")
        fun bindView(song: Song) {
            maxMs = song.durationMs.toLong()
            currentMs = when (song.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    val passed = Duration.between(song.playingTime, LocalDateTime.now()).toMillis()
                    if (passed > maxMs) {
                        0
                    } else {
                        passed
                    }
                }
                SongStatus.PAUSED.songStatus -> {
                    song.currentMs.toLong()
                }
                else -> {
                    0
                }
            }

            ///////////////////////
            // Minimized views bind
            ///////////////////////
            songName.text = song.songName
            songArtists.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

            seekBar.setOnTouchListener { _, _ -> true }
            seekBar.progress = currentMs.toInt()
            seekBar.max = maxMs.toInt()


            //////////////////////
            // Expanded views bind
            //////////////////////
            songNameExp.text = song.songName
            songArtistsExp.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

            songCurrent.text = getHumanReadableTimestamp(currentMs)
            songMax.text = getHumanReadableTimestamp(maxMs)

            seekBarExp.progress = currentMs.toInt()
            seekBarExp.max = maxMs.toInt()


            ///////////////////////
            // Image views bind
            ///////////////////////
            if (song.imageUrl != null) {
                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(songImage)

                GlideApp.with(view)
                    .load(song.imageUrl)
                    .into(songImageExp)
            } else {
                songImage.setImageResource(R.mipmap.ic_launcher)
                songImageExp.setImageResource(R.mipmap.ic_launcher)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.row_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        var playingSong: Song? = null
        var pausedSong: Song? = null
        var nextSong: Song? = null

        playerSongList.forEach {
            when (it.songStatus) {
                SongStatus.PLAYING.songStatus -> {
                    if (playingSong == null) {
                        playingSong = it
                    }
                }
                SongStatus.PAUSED.songStatus -> {
                    if (pausedSong == null) {
                        pausedSong = it
                    }
                }
                SongStatus.NEXT.songStatus -> {
                    if (nextSong == null) {
                        nextSong = it
                    }
                }
                //TODO else player visibility gone
//                else -> {
//                    view.visibility = View.GONE
//                }
            }
        }

        playingSong?.let {
            holder.bindView(it)
            return
        }
        pausedSong?.let {
            holder.bindView(it)
            return
        }
        nextSong?.let {
            holder.bindView(it)
            return
        }
    }

    fun updatePlayerSongList(data: MutableList<Song>) {
        playerSongList = data
        notifyDataSetChanged()
    }

}