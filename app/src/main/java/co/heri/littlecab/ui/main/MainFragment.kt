package co.heri.littlecab.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import co.heri.littlecab.R
import co.heri.littlecab.adapters.BottomSheetSliderAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewLayout = inflater.inflate(R.layout.main_fragment, container, false)

        this.initBottomSheet(viewLayout);

        val viewPager = viewLayout.findViewById(R.id.pager) as ViewPager
        viewPager.adapter = BottomSheetSliderAdapter()

        viewLayout.findViewById<View>(R.id.place_picker_search).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


        return viewLayout;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

    }

    private fun initBottomSheet(viewLayout: View){

        val place_picker = viewLayout.findViewById<View>(R.id.place_picker);

        var place_picker_height = 0


        place_picker.afterLayout {
            place_picker_height = place_picker.height;
            place_picker.translationY = place_picker_height * -1f;
        }

        bottomSheetBehavior =
            BottomSheetBehavior.from(viewLayout.findViewById(R.id.bottom_sheet) as View);
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
                place_picker.translationY = interpolateValues(slideOffset, place_picker_height * -1f, 0f)
//                Log.e("D/SLIDE", interpolateValues(slideOffset,5f, 58f).toString())
                place_picker.alpha = interpolateValues(slideOffset, 0f, 1f)
            }
        })
    }




    private fun interpolateValues(fraction: Float, startValue: Float, endValue: Float): Float {
        return startValue + (fraction * (endValue - startValue)).toFloat()
    }

    fun View.afterLayout(what: () -> Unit) {
        if(isLaidOut) {
            what.invoke()
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    what.invoke()
                }
            })
        }
    }

}
