package vip.yazilim.p2g.android.ui.invites

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
class InvitesFragment : Fragment() {

    private lateinit var invitesViewModel: InvitesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        invitesViewModel =
            ViewModelProvider(this).get(InvitesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_invites, container, false)
        val textView: TextView = root.findViewById(R.id.text_invites)
        invitesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}