package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.DownloadStarter
import com.codingblocks.cbonlineapp.adapters.SectionDetailsAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.CourseSection
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.services.DownloadService
import com.codingblocks.cbonlineapp.ui.CourseContentUi
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startService

private const val ARG_ATTEMPT_ID = "attempt_id"

class CourseContentFragment : Fragment(), AnkoLogger, DownloadStarter {
    lateinit var attemptId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val ui = CourseContentUi<Fragment>()
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context!!)
    }
    private val courseDao by lazy {
        database.courseRunDao()
    }
    private val sectionDao by lazy {
        database.sectionDao()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            CourseContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ATTEMPT_ID, param1)
                }
            }
    }

    override fun startDownload(url: String, id: String, lectureContentId: String, title: String, attemptId: String, contentId: String) {
        startService<DownloadService>("id" to id, "url" to url, "lectureContentId" to lectureContentId, "title" to title, "attemptId" to attemptId, "contentId" to contentId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        arguments?.let {
            attemptId = it.getString(ARG_ATTEMPT_ID)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = ui.createView(AnkoContext.create(ctx, this))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.swipeRefreshLayout.setOnRefreshListener {
            try {
                (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
            } catch (cce: ClassCastException) {
            }
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val sectionsList = ArrayList<CourseSection>()
        val sectionAdapter = SectionDetailsAdapter(sectionsList, activity!!, this)

        ui.rvSection.layoutManager = LinearLayoutManager(context)
        ui.rvSection.adapter = sectionAdapter
        ui.sectionProgressBar.show()
        sectionDao.getCourseSection(attemptId).observer(this) {
            if (it.isNotEmpty()) {
                ui.sectionProgressBar.hide()
            }
            courseDao.getRunByAtemptId(attemptId).observeOnce { courseRun ->
                sectionAdapter.setData(it as ArrayList<CourseSection>, courseRun.premium, courseRun.crStart)
            }
            if (ui.swipeRefreshLayout.isRefreshing) {
                ui.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseContent")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }
}
