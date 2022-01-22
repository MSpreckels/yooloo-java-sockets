package net.mspreckels.enums;

public enum AppState {
  STARTUP,
  CONNECTING, //Client only
  CONNECTED, //Client only
  ACCEPTING, //Server only
  SHUTDOWN,
  CLOSE
}
