package com.hallen.rfilemanager.infraestructure.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable

interface UsbListener {
    fun usbDeviceAttached(usbDevice: UsbDevice?)
    fun usbDeviceDetached()

}

class UsbDeviceReceiver(private val usbListener: UsbListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
            usbListener.usbDeviceDetached()
        }

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
            val usbDevice =
                intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
            usbListener.usbDeviceAttached(usbDevice)
        }
    }
}