package com.takeiteasy.chatchat.model.chatting;

import com.takeiteasy.chatchat.model.ReponseStatus;

public interface ChattingMsgSetListener {
  void onComplete(ReponseStatus status);
  void onFailed(Exception e);
}
