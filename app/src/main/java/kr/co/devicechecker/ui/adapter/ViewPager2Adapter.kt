package kr.co.devicechecker.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val fragmentList = mutableListOf<Fragment>()

    // Fragment를 추가하는 메서드
    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged() // 데이터 변경을 어댑터에 알립니다.
    }

    fun setFragmentList(list:List<Fragment>){
        this.fragmentList.clear()
        this.fragmentList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}