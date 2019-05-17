package com.codingblocks.cbonlineapp.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.ChoicesAdapter
import com.codingblocks.cbonlineapp.adapters.QuizChoiceAdapter
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.ViewPagerAdapter
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.QUIZ_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizSubmission
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizResult
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.bottom_question_sheet.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.textColor



class QuizFragment : Fragment(), AnkoLogger, ViewPager.OnPageChangeListener, View.OnClickListener, ViewPagerAdapter.ChoiceSelected, ChoicesAdapter.ChoiceClickListener  {


    private lateinit var quizId: String
    private lateinit var qnaId: String
    private lateinit var attemptId: String
    private lateinit var quizAttemptId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var quizSubmissions: ArrayList<QuizSubmission>


    lateinit var mAdapter: ViewPagerAdapter
    var questionList = HashMap<Int, String>()
    var sheetBehavior: BottomSheetBehavior<*>? = null
    var markedpositions = ArrayList<Int>()
    lateinit var choiceNumberAdapter: ChoicesAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
        : View? = inflater.inflate(R.layout.fragment_quiz, container, false).apply {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizId = it.getString(QUIZ_ID)!!
            qnaId = it.getString(QUIZ_QNA)!!
            attemptId = it.getString(RUN_ATTEMPT_ID)!!
            quizAttemptId = it.getString(QUIZ_ATTEMPT_ID)!!
        }

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        nextBtn.setOnClickListener(this)
        prevBtn.setOnClickListener(this)
        questionBtn.setOnClickListener(this)

        Clients.onlineV2JsonApi.getQuizById(quizId).enqueue(retrofitCallback { _, response ->
            response?.body()?.let { quiz ->
                quiz.questions?.forEachIndexed { index, question ->
                    questionList[index] = question.id ?: ""
                    if (index == quiz.questions!!.size - 1) {
                        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { _, attemptResponse ->
                            attemptResponse?.body().let {
                                mAdapter = ViewPagerAdapter(this,context!!, qnaId, quizAttemptId, attemptId, questionList, it?.submission, it?.result)
                                quizViewPager.adapter = mAdapter
                                quizViewPager.currentItem = 0
                                quizViewPager.setOnPageChangeListener(this)
                                quizViewPager.offscreenPageLimit = 3
                                it.result?.let { it1 -> setUpQuestionBottomSheet(it1) }
                            }
                        })

                    }
                }

            }
        })

    }

    override fun markedPosition() {
        markedpositions.add(quizViewPager.currentItem)
        choiceNumberAdapter.notifyDataSetChanged()
    }

    override fun onChoiceClicked(pos: Int) {
        quizViewPager.currentItem = pos
    }

    private fun setUpQuestionBottomSheet(questions: QuizResult) {

        choiceNumberAdapter = ChoicesAdapter(this)
        numbersRv.layoutManager = GridLayoutManager(context,3)
        numbersRv.adapter = choiceNumberAdapter
        choiceNumberAdapter.setdata(questions, markedpositions)
        choiceNumberAdapter.notifyDataSetChanged()

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

        when {
            position + 1 == questionList.size -> {

                nextBtn.text = "End"
                prevBtn.setTextColor(Color.parseColor("#000000"))
            }
            position == 0 -> {
                nextBtn.text = "Next"
                prevBtn.setTextColor(Color.parseColor("#808080"))
                nextBtn.setTextColor(Color.parseColor("#000000"))
            }
            else -> {
                nextBtn.text = "Next"
                nextBtn.setTextColor(Color.parseColor("#000000"))
                prevBtn.setTextColor(Color.parseColor("#000000"))

            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nextBtn -> if (nextBtn.text == "End") {
                Clients.onlineV2JsonApi.sumbitQuizById(quizAttemptId).enqueue(retrofitCallback { throwable, response ->
                    val fragmentManager = fragmentManager!!
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    fragmentTransaction.replace(R.id.framelayout_quiz,
                        QuizResultFragment.newInstance(quizAttemptId))
                    fragmentTransaction.commit()
                })
            } else
                quizViewPager.currentItem = if (quizViewPager.currentItem < questionList.size - 1)
                    quizViewPager.currentItem + 1
                else
                    0

            R.id.prevBtn -> quizViewPager.currentItem = if (quizViewPager.currentItem > 0)
                quizViewPager.currentItem - 1
            else
                0
            R.id.questionBtn -> {
                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(quizId: String,qnaId:String, attemptId: String, quizAttemptId: String) =
                QuizFragment().apply {
                    arguments = Bundle().apply {
                        putString(QUIZ_ID, quizId)
                        putString(QUIZ_QNA, qnaId)
                        putString(RUN_ATTEMPT_ID, attemptId)
                        putString(QUIZ_ATTEMPT_ID, quizAttemptId)


                }
            }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val params = Bundle()
            params.putString("authid", getPrefs()?.SP_ONEAUTH_ID)
            params.putString("name", "Quiz")
            firebaseAnalytics.logEvent("Open", params)
        }
    }


}
