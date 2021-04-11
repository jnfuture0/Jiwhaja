package org.jiwhaja.Adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.jiwhaja.HelpFragment
import org.jiwhaja.ListFragment
import org.jiwhaja.MapFragment
import org.jiwhaja.SettingFragment

class TabAdapterMain (fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int): Fragment {
        return when(p0){
            0 -> MapFragment()

            1 -> ListFragment()

            2 -> HelpFragment()

            3 -> SettingFragment()

            else -> HelpFragment()
        }
    }

    override fun getCount() = 4

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //return fragmentTitleList[position]    //맨위 ABCD 글자로 넣을 때
        return null //이미지만 보이고 텍스트 지우고 싶을 때
    }

}