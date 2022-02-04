package net.mspreckels.logger;

public class Logger {

  private final String scope;

  public enum Level {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
  }

  private enum Color {
    RESET("\033[0m"),
    BLACK("\033[0;30m"),    // BLACK
    RED("\033[0;31m"),      // RED
    GREEN("\033[0;32m"),    // GREEN
    YELLOW("\033[0;33m"),   // YELLOW
    BLUE("\033[0;34m"),     // BLUE
    MAGENTA("\033[0;35m"),  // MAGENTA
    CYAN("\033[0;36m"),     // CYAN
    WHITE("\033[0;37m");    // WHITE

    private final String code;

    Color(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return code;
    }
  }

  public <T> Logger(Class<T> clazz) {
    this.scope = clazz.getSimpleName();
  }

  private void log(Level level, String message) {
    Color color = getColorForLevel(level);
    System.out.printf("%s [%s] %s:%s %s\n", color, scope, level, Color.RESET, message);
  }

  private Color getColorForLevel(Level level) {
    switch (level) {
      case INFO -> {
        return Color.MAGENTA;
      }
      case SUCCESS -> {
        return Color.GREEN;
      }
      case WARNING -> {
        return Color.YELLOW;
      }
      case ERROR -> {
        return Color.RED;
      }
      default -> {
        return Color.RESET;
      }
    }
  }

  public void log(Level level, String format, Object... args) {
    log(level, String.format(format, args));
  }
}
