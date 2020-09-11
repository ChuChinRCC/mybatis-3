package 设计模式.构建者模式;

/**
 * @Description
 * @Author ChinHeng-Chu
 * @Date 2020-09-11 15:03
 */
public class Test {

  public static void main(String[] args) {
    ComputerBuilder computerBuilder = new ComputerBuilder();
    computerBuilder.installDisplay("14940");
    computerBuilder.installMainUnit("190324");
    computerBuilder.installKeyBoard("131231");
    computerBuilder.installMouse("3123");
    System.out.println(computerBuilder.build().toString());
  }

}

