package 设计模式.构建者模式;

/**
 * @Description
 * @Author ChinHeng-Chu
 * @Date 2020-09-11 14:55
 */
public class ComputerBuilder {

  private static Computer computer = new Computer();

  public void installDisplay(String display) {
    computer.setDisplay(display);
  }

  public void installMainUnit(String mainUnit) {
    computer.setMainUnit(mainUnit);
  }

  public void installMouse(String mouse) {
    computer.setMouse(mouse);
  }

  public void installKeyBoard(String keyBoard) {
    computer.setKeyBoard(keyBoard);
  }

  public Computer build() {
    return computer;
  }
}

