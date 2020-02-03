package vip.yazilim.p2g.android.ui.roominvites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import vip.yazilim.p2g.android.R

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesFragment : Fragment() {

    private lateinit var roomInvitesViewModel: RoomInvitesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        roomInvitesViewModel =
            ViewModelProvider(this).get(RoomInvitesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_invites, container, false)
        val textView: TextView = root.findViewById(R.id.text_invites)
        roomInvitesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}