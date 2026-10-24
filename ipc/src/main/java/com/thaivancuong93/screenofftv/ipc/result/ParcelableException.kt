package com.thaivancuong93.screenofftv.ipc.result

class ParcelableException(
    override val message: String?
) : RuntimeException(message) {
    companion object {
        val UnsupportedResultCode = ParcelableException("Unsupported resultCode")
    }
}