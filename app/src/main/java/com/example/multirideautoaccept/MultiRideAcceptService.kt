package com.example.multirideautoaccept

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MultiRideAcceptService : AccessibilityService() {

    private var lastClickTime: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 3000) return

        val rootNode = rootInActiveWindow ?: return
        val buttonTexts = listOf("Accept", "Confirm", "Accept Ride", "સ્વીકારો")

        for (text in buttonTexts) {
            val nodes = rootNode.findAccessibilityNodeInfosByText(text)
            for (node in nodes) {
                if (clickNodeOrParent(node)) {
                    lastClickTime = System.currentTimeMillis()
                    return
                }
            }
        }
    }

    private fun clickNodeOrParent(node: AccessibilityNodeInfo?): Boolean {
        var current = node
        while (current != null) {
            if (current.isClickable) {
                return current.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            current = current.parent
        }
        return false
    }

    override fun onInterrupt() {}
}
