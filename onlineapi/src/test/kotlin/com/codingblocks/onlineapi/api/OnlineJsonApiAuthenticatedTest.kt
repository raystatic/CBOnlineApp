package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Progress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val jsonapi = Clients.onlineV2JsonApi
    val api = Clients.api


    @Before
    fun `set JWT` () {
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6bnVsbCwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTktMDUtMDZUMDk6MDU6MTYuMDIzWiIsImNsaWVudElkIjoiZTNiMWZjMmQtZjE0YS00YjIzLWE2YjMtZmQ3OWI5N2Y1ZGI5IiwiY2xpZW50Ijoid2ViIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTU3MTMzNTg5LCJleHAiOjE1NTcxMzUwODl9.JzaQRHJp91k1Oaj0xLKjaFsuYyVcWUfhLfbOd-s-UeKOSjXDWgG6_eUlUUbyA_Q16cJSBvD-hUQO62J0VCxJJtlYgmz6bipkVfa7l0zqC8iVwI7dWjkUzKfAKBevAXWxwRCT048H4odTdcC9c3WbTY3VAstwT5wmwhSPnRq5VuSXCXl1orPGBW7rm8RCJXumJvo67zzhmbe_umfNWIAerTbX4btV329PaFkRahtn9KYMBpIN2UtsR9Cj9FJcN4QJu5kR6udhrOT0MSZXH0srb1ltpnUimcAJCG8EEsyLAC-xxSO5m7ug_eMmqw9VrNI1OD0yrcJJSZGNqgBu9iPgkg"
    }

    @Test
    fun `GET section`() {
        suspend {
            val courses = jsonapi.getSections("795").await().body()
            courses?.let {
                assertEquals("Python Basics", it.name)
            }
        }
    }


    @Test
    fun `GET run`() {
        suspend {
            val run = jsonapi.enrolledCourseById("12744").execute().body()
            run?.let {
                assertEquals("Python Basics", it.run?.sections!![0].contents)
            }
        }
    }



//    @Test
//    fun `GET content`() {
//            val courses = jsonapi.getSectionContent("886").execute().body()
//            courses?.let {
//                assertEquals("Python Basics", it)
//            }
//    }

    @Test
    fun `GET comment`() {
        val comment = jsonapi.getCommentsById("428").execute().body()
        comment?.let {
            assertEquals(3, it.size)
        }
    }

    @Test
    fun `GET myCourses`() {
        val courses = jsonapi.getMyCourses().execute().body()
        courses?.let {
            assertNotEquals(4, it.size)
        }
    }

    @Test
    fun `GET enrolledCourse`() {
        suspend {
            val course = jsonapi.enrolledCourseById("8252").execute().body()
            course?.let {
                assertNotEquals(4, it.run?.sections)
            }
        }
    }

    @Test
    fun `SET progress`() {
        val p  = Progress()
        p.id = "316797"
        p.status = "DONE"
        p.runs?.id = "8252"
        p.content?.id = "443"
        val progress = jsonapi.updateProgress("316797",p).execute().body()
        progress?.let {
            assertEquals(1, 1)
        }
    }

    @Test
    fun `GET Quiz`() {
        val quizzes = jsonapi.getQuizAttempt("3").execute().body()
        quizzes?.let {
            assertNotNull(it.size)
        }
    }
    @Test
    fun `GET Question`() {
        val questions = jsonapi.getQuestionById("22").execute().body()
        questions?.let {
            assertNotNull(it.title)
        }
    }
    @Test
    fun `GET Quiz Attempt`() {
        val quizAttempt = jsonapi.getQuizAttempt("3").execute().body()
        quizAttempt?.let {
            assertNotNull(it.size)
        }
    }
    @Test
    fun `GET Doubts `() {
        val doubts = api.getDoubts("22").execute().body()
        doubts?.let {
            assertNotNull(it)
        }
    }

    @Test
    fun `GET DoubtByAttemptId `() {
        val doubts = jsonapi.getDoubtByAttemptId("8252").execute().body()
        doubts?.let {
            assertNotNull(it)
        }
    }
}
