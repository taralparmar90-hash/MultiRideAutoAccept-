package com.example.multirideautoaccept

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MultiRideAcceptService : AccessibilityService() {

    private var lastTapTime = 0L

    // Button texts to look for (exact match, case-insensitive)
    private val buttonTexts = listOf("accept", "accept ride", "confirm", "સ્વીકારો")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val now = System.currentTimeMillis()
        if (now - lastTapTime < 1500) return

        val root = rootInActiveWindow ?: return
        val target = findAcceptNode(root) ?: return

        val rect = Rect()
        target.getBoundsInScreen(rect)
        if (rect.isEmpty) return

        lastTapTime = now
        tapAt(rect.exactCenterX(), rect.exactCenterY())
    }

    private fun findAcceptNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        for (text in buttonTexts) {
            val nodes = root.findAccessibilityNodeInfosByText(text)
            for (n in nodes) {
                val label = (n.text ?: n.contentDescription)?.toString()?.trim()?.lowercase()
                if (label == text && n.isVisibleToUser) return n
            }
        }
        return null
    }

    // Real screen tap (works when performAction(ACTION_CLICK) does not)
    private fun tapAt(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 60)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                lastTapTime = System.currentTimeMillis()
            }
        }, null)
    }

    override fun onInterrupt() {}
}
