package co.heri.littlecab.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import co.heri.littlecab.R



class BottomSheetSliderAdapter : PagerAdapter() {
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {

        val pager: ViewGroup = collection.findViewById(R.id.pager);

        val v: View = pager.getChildAt(position);

        return v;
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return if(position == 0){
            "Personal"
        } else {
            "Corporate"
        }
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) { // No super
    }
}