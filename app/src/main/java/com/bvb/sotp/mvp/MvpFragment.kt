package com.bvb.sotp.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.disposables.CompositeDisposable

abstract class MvpFragment<P : AndroidPresenter<*>> : Fragment(), AndroidView {

    protected lateinit var presenter: P
    protected val compositeDisposable = CompositeDisposable()
    protected var rootView: View? = null
    private lateinit var viewUnbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParams()
        initPresenter()
    }

    abstract fun initPresenter()

    abstract fun attachViewToPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(layoutResId, container, false)
        viewUnbinder = ButterKnife.bind(this, rootView!!)
        initViews()
        setupViews()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (::presenter.isInitialized) {
            attachViewToPresenter()
        }
    }

    override fun onDestroyView() {
        viewUnbinder.unbind()
        if (::presenter.isInitialized) {
            presenter.detachView()
        }
        compositeDisposable.clear()
        rootView = null
        super.onDestroyView()
    }

    fun onBackPressed(): Boolean {
        val count = childFragmentManager.backStackEntryCount
        if (count > 0) {
            val tag = childFragmentManager.getBackStackEntryAt(count - 1).name
            val fragment = childFragmentManager.findFragmentByTag(tag) as? MvpFragment<*>
            if (fragment != null) {
                if (!fragment.onBackPressed()) {
                    childFragmentManager.popBackStack()
                }
            }
            return true
        }
        return false
    }
}