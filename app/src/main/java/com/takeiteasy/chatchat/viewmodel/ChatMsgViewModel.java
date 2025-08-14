package com.takeiteasy.chatchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.play.core.integrity.x;
import com.takeiteasy.chatchat.model.ReponseStatus;
import com.takeiteasy.chatchat.model.chatting.ChattingGroup;
import com.takeiteasy.chatchat.model.chatting.ChattingMsg;
import com.takeiteasy.chatchat.model.chatting.ChattingMsgSetListener;
import com.takeiteasy.chatchat.model.chatting.repository.ChattingMsgRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatMsgViewModel extends ViewModel  {
  private MutableLiveData<ChattingGroup> data;
  private ChattingMsgRepository repository;

  public ChatMsgViewModel() {
    data = new MutableLiveData<>();
    repository = new ChattingMsgRepository();
  }

  public LiveData<ChattingGroup> getData() {
    return data;
  }

  public void setData(ChattingGroup setData) {
    data.postValue(setData);
  }

  public void fetchChattingMsg(String id) {
    repository.fetchChattingMsg(id, data::postValue);
  }

  public void sendMsg(String id, ChattingMsg msg, Consumer<ReponseStatus> listener) {
    repository.sendMsg(id, msg, listener::accept);
  }

  public void enteredChattingRoom(String userId, Consumer<ReponseStatus> listener) {
    List<String> onUsers = Objects.requireNonNull(data.getValue()).getOnUsers();
    List<String> list = new ArrayList<>();
    if(onUsers != null && !onUsers.isEmpty()) {
      list.addAll(onUsers.stream().filter(x -> !x.equals(userId)).collect(Collectors.toList()));
    }
    list.add(userId);
    repository.updateStatusInChattingRoom(data.getValue().getUid(), list, listener::accept);
  }

  public void leaveChattingRoom(String userId, Consumer<ReponseStatus> listener) {
    List<String> onUsers = Objects.requireNonNull(data.getValue()).getOnUsers();
    List<String> list = new ArrayList<>();
    if(onUsers != null && !onUsers.isEmpty()) {
      list.addAll(onUsers.stream().filter(x -> !x.equals(userId)).collect(Collectors.toList()));
    }
    repository.updateStatusInChattingRoom(data.getValue().getUid(), list, listener::accept);
  }
}
