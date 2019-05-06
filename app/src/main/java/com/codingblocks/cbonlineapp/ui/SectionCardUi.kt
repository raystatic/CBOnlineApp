package com.codingblocks.cbonlineapp.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.alignParentEnd
import org.jetbrains.anko.alignParentStart
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.below
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.centerVertically
import org.jetbrains.anko.dip
import org.jetbrains.anko.endOf
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.startOf
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedImageButton
import org.jetbrains.anko.wrapContent

class SectionCardUi : AnkoComponent<ViewGroup> {
    lateinit var sectionRoot: LinearLayout
    lateinit var sectionContents: LinearLayout
    lateinit var title: TextView
    lateinit var lectures: TextView
    lateinit var lecturesTime: TextView
    lateinit var arrow: ImageButton
    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        frameLayout {
            cardView {
                radius = dip(8).toFloat()
                sectionRoot = linearLayout {
                    orientation = LinearLayout.VERTICAL
                    relativeLayout {
                        arrow = themedImageButton(R.drawable.plus) {
                            id = View.generateViewId()
                        }.lparams {
                            alignParentEnd()
                            centerVertically()
                            marginEnd = dip(20)
                        }
                        title = textView {
                            id = View.generateViewId()
                            textSize = 16F
                        }.lparams {
                            alignParentStart()
                            topMargin = dip(10)
                            startOf(arrow)
                            marginStart = dip(10)
                        }
                        lectures = textView {
                            id = View.generateViewId()
                            textSize = 13f
                            maxLines = 1
                        }.lparams {
                            alignParentStart()
                            below(title)
                            marginStart = dip(10)
                        }
                        lecturesTime = textView {
                            id = View.generateViewId()
                            textSize = 13f
                            maxLines = 1
                        }.lparams {
                            bottomMargin = dip(8)
                            below(title)
                            endOf(lectures)
                            marginStart = dip(10)
                        }
                    }
                    sectionContents = linearLayout {
                        orientation = LinearLayout.VERTICAL
                        visibility = View.GONE
                    }.lparams(matchParent, wrapContent)
                }.lparams {
                    minimumHeight = dip(60)
                }
            }.lparams(matchParent, wrapContent) {
                margin = dip(2)
            }
        }
    }
}

