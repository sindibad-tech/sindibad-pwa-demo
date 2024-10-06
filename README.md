# WebView Sample Application

This project is a **sample application** demonstrating how to use `WebViewActivity` to integrate and display web content inside your Android application. By following this guide, you will understand how to open and use `WebViewActivity` effectively.

## How to Open the WebView

To use our application within your app, you simply need to open the `WebViewActivity`. The `WebViewActivity` handles everything required to load and display the application content inside a WebView.

### Step-by-Step Instructions:

1. **Open the WebViewActivity**
   - Whenever you need to display the application, simply open the `WebViewActivity` in your code.
   - You can do this by calling the following method in your Activity or Fragment using Kotlin:
   
     ```kotlin
     val intent = Intent(this, WebViewActivity::class.java)
     startActivity(intent)
     ```

2. **That's It!**
   - Once the `WebViewActivity` is opened, our application will be displayed inside the WebView. You don’t need to worry about anything else – the activity takes care of loading the necessary content.

### Additional Notes:
- The WebViewActivity is already configured to handle the application’s URL and settings, so you don’t need to pass any extra parameters.
- You can reopen the `WebViewActivity` whenever you need to access our application inside the WebView.

### Example Usage:

```kotlin
// Inside a button click or trigger event
button.setOnClickListener {
    val intent = Intent(this, WebViewActivity::class.java)
    startActivity(intent)
}
```

Important:
Ensure that the required permissions for internet access are declared in your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

This project serves as a sample application using WebViewActivity to help you understand how to integrate web content inside a WebView. Just open the WebViewActivity whenever necessary, and our application will be available inside the WebView.

Also this codebase is a sample Application using the WebViewActivity to open Sindibad. You can fetch and run it to see how it works.

If you have any questions or encounter any issues, feel free to reach out.
