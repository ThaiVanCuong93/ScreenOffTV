// IEventsListener.aidl
package com.thaivancuong93.screenofftv.shizuku;

// Declare any non-default types here with import statements
import com.thaivancuong93.screenofftv.ipc.result.EventResult;

interface IEventsListener {
    void onEvent(in EventResult event);
}