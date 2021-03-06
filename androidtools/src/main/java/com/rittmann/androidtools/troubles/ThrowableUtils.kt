package com.rittmann.androidtools.troubles

import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import kotlin.collections.ArrayList

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/02/12
 * desc  : utils about exception
</pre> *
 */
class ThrowableUtils private constructor() {
    companion object {
        private val LINE_SEP = System.getProperty("line.separator")
        fun getFullStackTrace(throwable: Throwable?): String {
            var throwableAdjusted = throwable
            val throwableList: MutableList<Throwable> =
                ArrayList()
            while (throwableAdjusted != null && !throwableList.contains(throwableAdjusted)) {
                throwableList.add(throwableAdjusted)
                throwableAdjusted = throwableAdjusted.cause
            }
            val size = throwableList.size
            val frames: MutableList<String> =
                ArrayList()
            var nextTrace =
                getStackFrameList(throwableList[size - 1])
            var i = size
            while (--i >= 0) {
                val trace = nextTrace
                if (i != 0) {
                    nextTrace = getStackFrameList(throwableList[i - 1])
                    removeCommonFrames(trace, nextTrace)
                }
                if (i == size - 1) {
                    frames.add(throwableList[i].toString())
                } else {
                    frames.add(" Caused by: " + throwableList[i].toString())
                }
                frames.addAll(trace)
            }
            val sb = StringBuilder()
            for (element in frames) {
                sb.append(element).append(LINE_SEP)
            }
            return sb.toString()
        }

        private fun getStackFrameList(throwable: Throwable): List<String> {
            val sw = StringWriter()
            val pw = PrintWriter(sw, true)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()
            val frames =
                StringTokenizer(stackTrace, LINE_SEP)
            val list: MutableList<String> =
                ArrayList()
            var traceStarted = false
            while (frames.hasMoreTokens()) {
                val token = frames.nextToken()
                // Determine if the line starts with <whitespace>at
                val at = token.indexOf("at")
                if (at != -1 && token.substring(0, at).trim { it <= ' ' }.isEmpty()) {
                    traceStarted = true
                    list.add(token)
                } else if (traceStarted) {
                    break
                }
            }
            return list
        }

        private fun removeCommonFrames(
            causeFrames: List<String>,
            wrapperFrames: List<String>
        ) {
            var causeFrameIndex = causeFrames.size - 1
            var wrapperFrameIndex = wrapperFrames.size - 1
            while (causeFrameIndex >= 0 && wrapperFrameIndex >= 0) {
                // Remove the frame from the cause trace if it is the same
                // as in the wrapper trace
                val causeFrame = causeFrames[causeFrameIndex]
                val wrapperFrame = wrapperFrames[wrapperFrameIndex]
                if (causeFrame == wrapperFrame) {
                    (causeFrames as ArrayList).removeAt(causeFrameIndex)
                }
                causeFrameIndex--
                wrapperFrameIndex--
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}