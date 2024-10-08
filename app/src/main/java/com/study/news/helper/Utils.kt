package com.study.news.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.study.news.R
import okhttp3.Request
import okhttp3.RequestBody
import java.io.UnsupportedEncodingException
import java.time.Duration
import java.time.Instant
import kotlin.random.Random


fun getRandomColor(): Int {
    return when (Random.nextInt(0,4)) {
        0 -> {
            R.color.card_1
        }

        1 -> {
            R.color.card_2
        }

        2 -> {
            R.color.card_3
        }

        3 -> {
            R.color.card_4
        }

        4 -> {
            R.color.card_5
        }

        else -> {R.color.card_1}
    }
}

fun timeAgo(dateString: String): String {
    val now = Instant.now()
    val past = Instant.parse(dateString)
    val duration = Duration.between(past, now)

    val secondsAgo = duration.seconds
    val minutesAgo = duration.toMinutes()
    val hoursAgo = duration.toHours()

    return when {
        secondsAgo < 60 -> "$secondsAgo seconds ago"
        minutesAgo < 60 -> "$minutesAgo minutes ago"
        hoursAgo < 24 -> "$hoursAgo hours ago"
        else -> "${hoursAgo / 24} days ago"
    }
}

fun Fragment.backPressedHandle(handle: () -> Unit) {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handle()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}

fun requestToCurl(request: Request): String {
    val builder = StringBuilder()
    builder.append("curl -X ${request.method} \\\n")

    // Append headers
    for ((name, value) in request.headers) {
        builder.append("    -H \"$name: $value\" \\\n")
    }

    // Append request body if present
    request.body?.let { requestBody ->
        if (requestBody is RequestBody) {
            try {
                val charset = requestBody.contentType()?.charset() ?: Charsets.UTF_8
                val bodyString = requestBodyToString(requestBody, charset)
                val escapedBody = escapeSpecialChars(bodyString)
                builder.append("    -d \"$escapedBody\" \\\n")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
    }

    // Append URL
    builder.append("    \"${request.url}\"")

    return builder.toString()
}

fun Activity.toastMsg(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.toastMsg(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}


@Throws(UnsupportedEncodingException::class)
private fun requestBodyToString(
    requestBody: RequestBody,
    charset: java.nio.charset.Charset,
): String {
    val buffer = okio.Buffer()
    requestBody.writeTo(buffer)
    return buffer.readString(charset)
}

private fun escapeSpecialChars(input: String): String {
    return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.setupUI(view: View, handle: () -> Unit) {

    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { v, event ->
            hideSoftKeyboard()
            handle()
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setupUI(innerView, handle)
        }
    }
}

fun Fragment.hideSoftKeyboard() {
    requireActivity().currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}