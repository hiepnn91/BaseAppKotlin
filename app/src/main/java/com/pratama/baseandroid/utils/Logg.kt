package com.pratama.baseandroid.utils

import android.os.Build
import android.util.Log
import com.pratama.baseandroid.BuildConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xml.sax.InputSource
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.sax.SAXTransformerFactory
import javax.xml.transform.stream.StreamResult

object Logg {
    private fun tag(): String? {
        return Thread.currentThread().stackTrace[4].let {
            "App# ${it.className.substringAfterLast(".")}.${it.methodName}(${it.fileName}:${it.lineNumber})"
        }
    }

    private fun log(tag: String, msg: String) {
        Timber.e(msg)
    }

    private fun getJsonObjFromStr(test: Any): Any? {
        var o: Any? = null
        try {
            o = JSONObject(test.toString())
        } catch (ex: JSONException) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    o = JSONArray(test)
                }
            } catch (ex1: JSONException) {
                return null
            }
        }
        return o
    }

    private fun isDebuggable(): Boolean {
        return BuildConfig.DEBUG
    }

    private fun format(tag: String, source: Any) {
        log("$tag ", "" + source)
    }

    fun formatXml(xml: String): String? {
        return try {
            val serializer = SAXTransformerFactory.newInstance().newTransformer()
            serializer.setOutputProperty(OutputKeys.INDENT, "yes")
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val xmlSource: Source = SAXSource(InputSource(ByteArrayInputStream(xml.toByteArray())))
            val res = StreamResult(ByteArrayOutputStream())
            serializer.transform(xmlSource, res)
            String((res.outputStream as ByteArrayOutputStream).toByteArray())
        } catch (e: java.lang.Exception) {
            xml
        }
    }

    fun jsonFormat(tag: String?, source: Any?) {
        val stackTraceElement = Thread.currentThread().stackTrace
        var currentIndex = -1
        for (i in stackTraceElement.indices) {
            if (stackTraceElement[i].methodName.compareTo("jsonFormat") == 0) {
                currentIndex = i + 1
                break
            }
        }
        val fullClassName = stackTraceElement[currentIndex].className
        val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        val methodName = stackTraceElement[currentIndex].methodName
        val lineNumber = stackTraceElement[currentIndex].lineNumber.toString()
        if (isDebuggable()) {
            val o = getJsonObjFromStr(source!!)
            if (o != null) {
                try {
                    if (o is JSONObject) {
                        format(
                            tag!!, """
     ${o.toString(2)}
     at $fullClassName.$methodName($className.java:$lineNumber)
     """.trimIndent()
                        )
                    } else if (o is JSONArray) {
                        format(tag!!, o.toString(2))
                    } else {
                        format(tag!!, source)
                    }
                } catch (e: JSONException) {
                    format(tag!!, source)
                }
            } else {
                format(tag!!, source)
            }
        }
    }

    fun v(msg: String?) {
        Log.v(tag(), "" + msg)
    }

    fun d(msg: String?) {
        Log.d(tag(), "" + msg)
    }

    fun i(msg: String?) {
        Log.i(tag(), "" + msg)
    }

    fun w(msg: String?) {
        Log.w(tag(), "" + msg)
    }

    fun w(e: Throwable?) {
        Log.w(tag(), "" + e?.localizedMessage)
    }

    fun w(e: Exception?) {
        Log.w(tag(), "" + e?.localizedMessage)
    }

    fun e(msg: String?) {
        Log.e(tag(), "" + msg)
    }
}