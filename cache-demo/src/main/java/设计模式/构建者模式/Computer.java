package 设计模式.构建者模式;

/**
 * @Description
 * @Author ChinHeng-Chu
 * @Date 2020-09-11 14:46
 */
public class Computer {

  private String display;

  private String mainUnit;

  private String mouse;

  private String keyBoard;

  @Override
  public String toString() {
    return "Computer{" +
      "display='" + display + '\'' +
      ", mainUnit='" + mainUnit + '\'' +
      ", mouse='" + mouse + '\'' +
      ", keyBoard='" + keyBoard + '\'' +
      '}';
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public String getMainUnit() {
    return mainUnit;
  }

  public void setMainUnit(String mainUnit) {
    this.mainUnit = mainUnit;
  }

  public String getMouse() {
    return mouse;
  }

  public void setMouse(String mouse) {
    this.mouse = mouse;
  }

  public String getKeyBoard() {
    return keyBoard;
  }

  public void setKeyBoard(String keyBoard) {
    this.keyBoard = keyBoard;
  }
}

