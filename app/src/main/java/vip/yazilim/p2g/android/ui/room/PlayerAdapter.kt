package vip.yazilim.p2g.android.ui.room

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.ColorCodes.SPOTIFY_GREEN
import vip.yazilim.p2g.android.constant.ColorCodes.WHITE
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.util.glide.GlideApp
import vip.yazilim.p2g.android.util.helper.RoomHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getHumanReadableTimestamp


/**
 * @author mustafaarifsisman - 23.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class PlayerAdapter(
    private var song: Song?,
    private val itemClickListener: OnItemClickListener,
    private val seekBarChangeListener: OnSeekBarChangeListener
) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    private lateinit var view: View

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentMs: Int = 0
        private var maxMs: Int = 0

        // Minimized views
        private val playerMini: ConstraintLayout = itemView.findViewById(R.id.player_mini)
        private val songName: TextView = itemView.findViewById(R.id.song_name)
        private val songArtists: TextView = itemView.findViewById(R.id.song_artists)
        private val songImage: ImageView = itemView.findViewById(R.id.song_image)
        private val seekBar: SeekBar = itemView.findViewById(R.id.seek_bar)
        private val playPauseButtonMini: ImageButton =
            itemView.findViewById(R.id.playPause_button_mini)


        // Expanded views
        private val songNameExp: TextView = itemView.findViewById(R.id.song_name_exp)
        private val songArtistsExp: TextView = itemView.findViewById(R.id.song_artists_exp)
        private val songImageExp: ImageView = itemView.findViewById(R.id.song_image_exp)
        private val seekBarExp: SeekBar = itemView.findViewById(R.id.seek_bar_exp)
        private val songCurrent: TextView = itemView.findViewById(R.id.song_current)
        private val songMax: TextView = itemView.findViewById(R.id.song_max)

        private val playPauseButton: ImageButton = itemView.findViewById(R.id.playPause_button)
        private val nextButton: ImageButton = itemView.findViewById(R.id.next_button)
        private val previousButton: ImageButton = itemView.findViewById(R.id.previous_button)
        private val repeatButton: ImageButton = itemView.findViewById(R.id.repeat_button)

        @SuppressLint("ClickableViewAccessibility")
        fun bindView(song: Song?) {
            if (song != null) {
                maxMs = song.durationMs
                currentMs = RoomViewModel.getCurrentSongMs(song)

                ///////////////////////
                // Minimized views bind
                ///////////////////////
                songName.text = song.songName
                songArtists.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

                seekBar.setOnTouchListener { _, _ -> true }
                seekBar.max = maxMs
                seekBar.progress = currentMs


                //////////////////////
                // Expanded views bind
                //////////////////////
                songNameExp.text = song.songName
                songArtistsExp.text = RoomHelper.getArtistsPlaceholder(song.artistNames, "")

                songCurrent.text = currentMs.getHumanReadableTimestamp()
                songMax.text = maxMs.getHumanReadableTimestamp()

                seekBarExp.max = maxMs
                seekBarExp.progress = currentMs


                ///////////////////////
                // Image views bind
                ///////////////////////
                if (song.imageUrl != null) {
                    songImage.visibility = View.VISIBLE
                    songImageExp.visibility = View.VISIBLE

                    GlideApp.with(view)
                        .load(song.imageUrl)
                        .into(songImage)

                    GlideApp.with(view)
                        .load(song.imageUrl)
                        .into(songImageExp)
                } else {
                    songImage.visibility = View.INVISIBLE
                    songImageExp.visibility = View.GONE
                }


                ////////////////////////
                // Controller views bind
                ////////////////////////
                if (song.songStatus == SongStatus.PLAYING.songStatus) {
                    playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_white_64dp)
                    playPauseButtonMini.setImageResource(R.drawable.ic_pause_white_24dp)
                } else {
                    playPauseButton.setImageResource(R.drawable.ic_play_circle_filled_white_64dp)
                    playPauseButtonMini.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                }

                if (song.repeatFlag) {
                    repeatButton.setColorFilter(Color.parseColor(SPOTIFY_GREEN))
                } else {
                    repeatButton.setColorFilter(Color.parseColor(WHITE))
                }

            } else {
                songImage.visibility = View.INVISIBLE
                songImageExp.visibility = View.GONE

                songName.text = ""
                songArtists.text = ""
                songImage.setImageResource(R.drawable.sample_cover_image)
                seekBar.progress = 0
                songNameExp.text = ""
                songArtistsExp.text = ""
                songImageExp.setImageResource(R.drawable.sample_cover_image)
                seekBarExp.progress = 0
                songCurrent.text = view.resources.getString(R.string.placeholder_song_default_time)
                songMax.text = view.resources.getString(R.string.placeholder_song_default_time)
            }
        }

        fun bindEvent(
            clickListener: OnItemClickListener,
            seekBarChangeListener: OnSeekBarChangeListener
        ) {
            playPauseButton.setOnClickListener { clickListener.onPlayPauseClicked() }
            playPauseButtonMini.setOnClickListener { clickListener.onPlayPauseMiniClicked() }
            nextButton.setOnClickListener { clickListener.onNextClicked() }
            previousButton.setOnClickListener { clickListener.onPreviousClicked() }
            repeatButton.setOnClickListener { clickListener.onRepeatClicked() }

            seekBarExp.setOnSeekBarChangeListener(seekBarChangeListener.onSeekBarChanged())

            playerMini.setOnClickListener { itemClickListener.onPlayerMiniClicked() }
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
        holder.bindEvent(itemClickListener, seekBarChangeListener)
        holder.bindView(song)
    }

    fun updatePlayerSong(data: Song?) {
        song = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onPlayerMiniClicked()
        fun onPlayPauseMiniClicked()
        fun onPlayPauseClicked()
        fun onNextClicked()
        fun onPreviousClicked()
        fun onRepeatClicked()
    }

    interface OnSeekBarChangeListener {
        fun onSeekBarChanged(): SeekBar.OnSeekBarChangeListener
    }

}