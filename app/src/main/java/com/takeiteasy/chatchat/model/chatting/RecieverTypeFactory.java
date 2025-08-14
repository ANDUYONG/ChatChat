package com.takeiteasy.chatchat.model.chatting;

public class RecieverTypeFactory {
  public static int from(String type) {
    int result = -999;
    switch (type) {
      case "me":
        result = RecieverType.ME.ordinal();
      break;
      case "you":
        result = RecieverType.YOU.ordinal();
      break;
      case "inout":
        result = RecieverType.INOUT.ordinal();
      break;
    }
    return result;
  }
}
