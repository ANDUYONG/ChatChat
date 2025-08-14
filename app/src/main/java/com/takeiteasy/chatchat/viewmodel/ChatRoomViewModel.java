package com.takeiteasy.chatchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.repository.ChattingRoomRepository;

import java.util.List;

public class ChatRoomViewModel extends ViewModel {
  private MutableLiveData<List<ChattingRoom>> dataList;
  private ChattingRoomRepository repository;

  public ChatRoomViewModel() {
    dataList = new MutableLiveData<>();
    repository = new ChattingRoomRepository();
  }

  public LiveData<List<ChattingRoom>> getDataList() {
    return dataList;
  }

  public void setDataList(List<ChattingRoom> setData) {
    dataList.postValue(setData);
  }

  public void fetchChattingRooms(String userId) {
    repository.fetchChattingRooms(userId, dataList::postValue);
  }

  public void leaveChattingRooms(String userId, String uid, Action action) {
    repository.leaveChattingRoom(userId, uid, action);
  }
}
