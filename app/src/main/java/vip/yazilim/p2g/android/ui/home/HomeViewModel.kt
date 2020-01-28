package vip.yazilim.p2g.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vip.yazilim.p2g.android.data.OperationCallback
import vip.yazilim.p2g.android.model.RoomDataSource
import vip.yazilim.p2g.android.model.p2g.RoomModel

class HomeViewModel(private val repository: RoomDataSource):ViewModel() {

    private val _roomModels = MutableLiveData<List<RoomModel>>().apply { value = emptyList() }
    val roomModels: LiveData<List<RoomModel>> = _roomModels

    private val _isViewLoading=MutableLiveData<Boolean>()
    val isViewLoading:LiveData<Boolean> = _isViewLoading

    private val _onMessageError=MutableLiveData<Any>()
    val onMessageError:LiveData<Any> = _onMessageError

    private val _isEmptyList=MutableLiveData<Boolean>()
    val isEmptyList:LiveData<Boolean> = _isEmptyList

    @Suppress("UNCHECKED_CAST")
    fun loadRooms(){
        _isViewLoading.postValue(true)
        repository.getRoomModels(object: OperationCallback {
            override fun onError(obj: Any?) {
                _isViewLoading.postValue(false)
                _onMessageError.postValue(obj)
            }

            override fun onSuccess(obj: Any?) {
                _isViewLoading.postValue(false)

                if(obj!=null && obj is List<*>){
                    if(obj.isEmpty()){
                        _isEmptyList.postValue(true)
                    }else{
                        _roomModels.value= obj as List<RoomModel>
                    }
                }
            }
        })
    }

}