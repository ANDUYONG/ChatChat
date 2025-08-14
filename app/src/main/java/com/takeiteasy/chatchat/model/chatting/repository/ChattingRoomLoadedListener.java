package com.takeiteasy.chatchat.model.chatting.repository;

import com.takeiteasy.chatchat.model.chatting.ChattingRoom;

import java.util.List;

public interface ChattingRoomLoadedListener {
  void onSuccess(List<ChattingRoom> response);
  void onFailure(Exception e);
}
