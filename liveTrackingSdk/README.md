# Module LiveTrackingSdk
# Integration Guide: LiveTrackingSDK

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Setup Instructions](#setup-instructions)
    1. [Step 1: Add Gradle Dependencies](#step-1-add-gradle-dependencies)
    2. [Step 2: Add Permissions to AndroidManifest.xml](#step-2-add-permissions-to-androidmanifestxml)
    3. [Step 3: Initialize the SDK](#step-3-initialize-the-sdk)
4. [Usage](#usage)
    1. [Listeners](#listeners)
    2. [Actions](#actions)
    3. [Using a Custom Background Service (Advanced)](#using-a-custom-background-service-advanced)
5. [API Reference](#api-reference)
6. [Troubleshooting](#troubleshooting)
7. [Demo](#demo)

---

## Introduction

This guide provides step-by-step instructions for integrating the `LiveTrackingSDK` into your Android project. This SDK allows you to easily track user location in both foreground and background modes.

---

## Prerequisites

Before you begin the integration process, make sure you have the following:

- An Android project with a minimum SDK version of 21.
- The following permissions declared in your `AndroidManifest.xml`:
    - `android.permission.ACCESS_FINE_LOCATION`
    - `android.permission.ACCESS_COARSE_LOCATION`
    - `android.permission.POST_NOTIFICATIONS` (for Android 13 and above)

---

## Setup Instructions

### Step 1: Add Gradle Dependencies

Add the `liveTrackingSdk` module as a dependency in your app's `build.gradle.kts` file:

```gradle
dependencies {
    implementation("com.github.adhamkhwaldeh:liveTrackingSdk:1.0.3")
}
```

### Step 2: Add Permissions to AndroidManifest.xml

Add the following permissions to your `app/src/main/AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### Step 3: Initialize the SDK

Initialize the `LiveTrackingManager` in your Application class or a central location in your app. Use the `Builder` to configure the SDK according to your needs.

```kotlin
import com.kerberos.livetrackingsdk.LiveTrackingManager
import com.kerberos.livetrackingsdk.enums.LiveTrackingMode
import com.kerberos.livetrackingsdk.models.DefaultNotificationConfiguration

// ...

val liveTrackingManager = LiveTrackingManager.Builder(applicationContext)
    .setLiveTrackingMode(LiveTrackingMode.FOREGROUND_SERVICE) // Or LiveTrackingMode.BACKGROUND_SERVICE
    .setMinDistanceMeters(10f) // Optional: Set minimum distance for location updates (in meters). If not set, updates will be based on time interval only.
    .setLocationUpdateInterval(5000) // 5 seconds
    // To use a custom background service with LiveTrackingMode.BACKGROUND_SERVICE,
    // uncomment and set your service class below. See the "Using a Custom Background Service (Advanced)"
    // section for more details.
    // .setBackgroundServiceClass(YourCustomBackgroundService::class.java)
    .build()

// To start the foreground service or provide default notification for a custom background service
// (if it uses the base implementation for notifications), configure DefaultNotificationConfiguration.
// This is not strictly needed if your custom background service *completely* overrides notification creation
// and does not rely on BaseTrackingService's defaultNotificationConfiguration property.
val notificationConfig = DefaultNotificationConfiguration(
    defaultIntentActivity = YourMainActivity::class.java, // Replace with your actual MainActivity
    contentTitle = "Live Tracking Active",
    contentText = "Your location is being tracked.",
    smallIcon = R.drawable.ic_notification, // Replace with your actual icon resource
    ticker = "Live Tracking"
)
// To make this configuration available to BaseTrackingService (either default or your custom one if it uses it),
// you would typically pass it to the LiveTrackingManager.Builder or ensure your custom service
// provides it in its `defaultNotificationConfiguration` override.
// For ForegroundTrackingManager or DefaultTrackingService, LiveTrackingManager handles passing this.
// For a custom service, see the example in "Using a Custom Background Service (Advanced)".
```

---

## Usage

### Listeners

You can add listeners to receive updates on location and tracking status.

#### ITrackingLocationListener

Implement `ITrackingLocationListener` to receive location updates.

```kotlin
val locationListener = object : ITrackingLocationListener {
    override fun onLocationChanged(location: Location) {
        // Handle new location
    }
}

liveTrackingManager.addTrackingLocationListener(locationListener)
```

#### ITrackingStatusListener

Implement `ITrackingStatusListener` to receive updates on the tracking state.

```kotlin
val statusListener = object : ITrackingStatusListener {
    override fun onTrackingStateChanged(state: TrackingState) {
        // Handle tracking state changes (e.g., STARTED, PAUSED, STOPPED)
    }
}

liveTrackingManager.addTrackingStatusListener(statusListener)
```

### Actions

Control the tracking process using the following actions:

- `onStartTracking()`: Starts the tracking service.
- `onResumeTracking()`: Resumes tracking if it was paused.
- `onPauseTracking()`: Pauses the tracking service.
- `onStopTracking()`: Stops the tracking service.

```kotlin
// Example: Start tracking
liveTrackingManager.onStartTracking()
```

### Using a Custom Background Service (Advanced)

For advanced scenarios where you need to implement a custom background service instead of relying on the SDK's default when using `LiveTrackingMode.BACKGROUND_SERVICE`, you can create your own service.

Your custom service would typically extend `com.kerberos.livetrackingsdk.services.BaseTrackingService` or implement the necessary location handling and service lifecycle management independently if `BaseTrackingService` is not suitable.

**Example:**
You can refer to `TripBackgroundService` in the showcase application as an example of how such a custom service might be implemented.

**Integration:**
When initializing the `LiveTrackingManager`, you can specify your custom service class using the `Builder`.

1.  **Create your custom service:**
    Ensure your service extends `com.kerberos.livetrackingsdk.services.BaseTrackingService` or handles its lifecycle and location updates correctly.

    ```kotlin
    // Example: YourCustomBackgroundService.kt
    import android.app.Notification
    import android.app.Service
    import android.os.Build
    import androidx.annotation.RequiresApi
    // Ensure you have an R file import for resources if you use them, e.g., your app's R file
    // import com.example.yourapp.R 
    // Or, if using SDK resources directly (less common for custom service icons):
    // import com.kerberos.livetrackingsdk.R
    import com.kerberos.livetrackingsdk.models.DefaultNotificationConfiguration
    import com.kerberos.livetrackingsdk.services.BaseTrackingService 
    // Replace YourMainActivity with your actual activity class
    // import com.example.yourapp.YourMainActivity 

    class YourCustomBackgroundService : BaseTrackingService() {

        override val serviceClassForRestart: Class<out Service>
            get() = YourCustomBackgroundService::class.java // Important for service restart

        // You MUST provide a DefaultNotificationConfiguration if you intend to use
        // the base class's notification creation logic (e.g., calling super.createNotification()).
        // If you fully override createNotification() with completely custom logic, this can be null,
        // BUT BaseTrackingService expects a non-null value if its createNotification is called.
        override val defaultNotificationConfiguration: DefaultNotificationConfiguration?
            get() = DefaultNotificationConfiguration(
                notificationChannelId = "custom_tracking_channel",
                notificationChannelName = "Custom Tracking Service",
                notificationChannelDescription = "Tracks location in background via custom service.",
                contentTitle = "Custom Tracking Active",
                contentText = "Your location is being tracked by a custom service.",
                smallIcon = android.R.drawable.ic_dialog_info, // Replace with your actual icon resource e.g., R.drawable.your_icon
                ticker = "Custom Tracking Started",
                defaultIntentActivity = null // Replace with YourMainActivity::class.java if needed
            )

        @RequiresApi(Build.VERSION_CODES.O)
        override fun createNotification(): Notification {
            // Option 1: Rely on base class logic (if defaultNotificationConfiguration is provided as above)
            // This is generally recommended if your notification needs are standard.
            // Ensure defaultNotificationConfiguration is not null if you call this.
            if (this.defaultNotificationConfiguration != null) {
                return super.createNotification()
            }

            // Fallback or error - should ideally not happen if configured correctly
            // Or, implement fully custom notification logic here if defaultNotificationConfiguration is intentionally null
            // and you are not calling super.createNotification().
            throw IllegalStateException("defaultNotificationConfiguration is null and createNotification was not fully overridden with custom logic.")
        }

        // Add other necessary overrides (like onStartCommand, onBind, etc. if needed)
        // and custom logic for your service.
        // BaseTrackingService provides a lot of the boilerplate for starting/stopping tracking
        // and managing notifications via onStartCommand actions.
    }
    // Note: Replace YourMainActivity::class.java and icon resources with your actual classes/resources.
    // Ensure necessary imports like your app's R file and YourMainActivity are correct.
    ```

2.  **Declare your service in `AndroidManifest.xml`:**

    ```xml
    <service android:name=".services.YourCustomBackgroundService" 
        android:enabled="true"
        android:exported="false"
        android:foregroundServiceType="location"
        android:stopWithTask="false" />
    ```
    *(Adjust the path `.services.YourCustomBackgroundService` as per your project structure)*

3.  **Configure the SDK Builder:**
    In your SDK initialization (as shown in [Step 3: Initialize the SDK](#step-3-initialize-the-sdk)), set `LiveTrackingMode.BACKGROUND_SERVICE` and provide your custom service class using `.setBackgroundServiceClass(YourCustomBackgroundService::class.java)`.

By doing this, the SDK will start and manage your `YourCustomBackgroundService` when `onStartTracking()` is called and the mode is `LiveTrackingMode.BACKGROUND_SERVICE`.

---

## API Reference

- **`LiveTrackingManager`**: The main class for interacting with the SDK.
- **`LiveTrackingManager.Builder`**: The builder class for initializing the SDK.
    - `setBackgroundServiceClass(Class<out Service>?)`: Allows specifying a custom background service when `LiveTrackingMode.BACKGROUND_SERVICE` is used.
- **`ITrackingLocationListener`**: Interface for receiving location updates.
- **`ITrackingStatusListener`**: Interface for receiving tracking status updates.
- **`LiveTrackingMode`**: Enum for setting the tracking mode (`FOREGROUND_SERVICE` or `BACKGROUND_SERVICE`).
- **`DefaultNotificationConfiguration`**: Data class for configuring the foreground service notification (used by `FOREGROUND_SERVICE` mode or if your custom background service doesn't manage its own notification via `BaseTrackingService` overrides).
- **`com.kerberos.livetrackingsdk.services.BaseTrackingService`**: Base service class that can be extended to create custom background services.

---

## Troubleshooting

- **Permissions not granted:** Ensure you have requested the necessary location permissions from the user at runtime.
- **GPS not enabled:** The SDK may throw a `GpsNotEnabledException` if the user's GPS is turned off. Prompt the user to enable it.
- **Foreground service not starting:** Make sure you have provided a valid `DefaultNotificationConfiguration` if using `LiveTrackingMode.FOREGROUND_SERVICE`.
- **Custom background service issues:**
    - Ensure your custom service is correctly declared in `AndroidManifest.xml`.
    - Verify that your custom service correctly handles its lifecycle and location updates, especially if not extending `BaseTrackingService`.
    - Check for any errors logged by your custom service or the SDK related to service instantiation.

---

## Demo

### Screenshots

- **Dashboard View**

| ![Screenshot 1: Dashboard View](./Docs/Screenshot_20250904_153316.png) | ![Screenshot 2: Dashboard View](./Docs/Screenshot_20250904_153340.png) | ![Screenshot 3: Dashboard View](./Docs/Screenshot_20250904_153354.png) |
|-----------------|-----------------|-----------------|
| ![Screenshot 4: Dashboard View](./Docs/Screenshot_20250904_153401.png) | ![Screenshot 5: Dashboard View](./Docs/Screenshot_20250904_153431.png) |                                 |
|-----------------|-----------------|-----------------|

### Videos

▶️ [Watch on YouTube](https://img.youtube.com/vi/OfhSMe3b7xg/maxresdefault.jpg)](https://youtu.be/OfhSMe3b7xg)

[![Watch the video](https://img.youtube.com/vi/OfhSMe3b7xg/maxresdefault.jpg)](https://youtu.be/OfhSMe3b7xg)


