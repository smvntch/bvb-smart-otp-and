package com.bvb.sotp.screen.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bvb.sotp.R

class TutorialFragment : Fragment() {

    companion object {
        fun newInstance(pos: Int): TutorialFragment {
            val fragment = TutorialFragment()
            val args = Bundle()
            args.putInt("pos", pos)
            fragment.arguments = args
            return fragment
        }}

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_tutorial, container, false)

        val pos = arguments?.getInt("pos",0)

            if(pos==0){
                 view = inflater.inflate(R.layout.fragment_tutorial, container, false)
            }else if (pos ==1){
                 view = inflater.inflate(R.layout.fragment_tutorial_2, container, false)

            }else if (pos ==2){
                 view = inflater.inflate(R.layout.fragment_tutorial_3, container, false)

            }else if (pos ==3){
                 view = inflater.inflate(R.layout.fragment_tutorial_4, container, false)

            }
        return view

    }

}