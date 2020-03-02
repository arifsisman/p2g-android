package vip.yazilim.p2g.android.api.generic

interface Callback<T> {
    fun onSuccess(obj: T)
    fun onError(msg: String)
}