package dev.jn.enois.presentation.auth.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TurnstileComponent(
    siteKey: String,
    baseUrl: String,
    onToken: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnToken by rememberUpdatedState(onToken)
    val currentOnError by rememberUpdatedState(onError)

    val html = remember(siteKey) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport"
                  content="width=device-width, initial-scale=1">
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    min-height: 70px;
                    background: transparent;
                }

                body {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    overflow: hidden;
                }

                #w {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                }
            </style>

            <script>
                function onLoad() {
                    turnstile.render('#w', {
                        sitekey: '$siteKey',
                        theme: 'dark',
                        callback: function(t) {
                            Android.onToken(t);
                        },
                        'error-callback': function(c) {
                            Android.onError(String(c));
                        },
                        'expired-callback': function() {
                            Android.onError('expired');
                        }
                    });
                }
            </script>

            <script
                src="https://challenges.cloudflare.com/turnstile/v0/api.js?onload=onLoad"
                async defer>
            </script>
        </head>
        <body>
            <div id="w"></div>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp),
        factory = { ctx ->
            WebView(ctx).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                val main = Handler(Looper.getMainLooper())
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onToken(t: String) {
                        main.post { currentOnToken(t) }
                    }

                    @JavascriptInterface
                    fun onError(c: String) {
                        main.post { currentOnError(c) }
                    }
                }, "Android")
                loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null)
            }
        },
        onRelease = { webView ->
            webView.removeJavascriptInterface("Android")
            webView.stopLoading()
            webView.destroy()
        }
    )

}