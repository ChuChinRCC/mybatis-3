package mapper;

import entity.StudentEntity;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * https://tech.meituan.com/2018/01/19/mybatis-cache.html
 */
public class StudentMapperTest {

  private static SqlSessionFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new SqlSessionFactoryBuilder()
      .build(Resources.getResourceAsReader("mybatis-config.xml"));
//    initDb();
  }

  void initDb() throws IOException, SQLException {
    BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),
      "class.sql");
    System.out.println("db-init-class");
    BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),
      "classroom.sql");
    System.out.println("db-init-classroom");
    BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),
      "student.sql");
    System.out.println("db-init-student");
  }

  @Test
  public void showDefaultCacheConfiguration() {
    System.out.println("本地缓存范围: " + factory.getConfiguration().getLocalCacheScope());
    System.out.println("二级缓存是否被启用: " + factory.getConfiguration().isCacheEnabled());
  }

  /**
   * 只有第一次真正查询了数据库，后续的查询使用了一级缓存。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testLocalCache() throws Exception {
    SqlSession sqlSession = factory.openSession(true); // 自动提交事务
    StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

    System.out.println(studentMapper.getStudentById(1));
    System.out.println(studentMapper.getStudentById(1));
    System.out.println(studentMapper.getStudentById(1));

    sqlSession.close();
  }

  /**
   * 增加了对数据库的修改操作，验证在一次数据库会话中，如果对数据库发生了修改操作，一级缓存是否会失效。
   * 在修改操作后执行的相同查询，查询了数据库，一级缓存失效。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testLocalCacheClear() throws Exception {
    SqlSession sqlSession = factory.openSession(true); // 自动提交事务
    StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

    System.out.println(studentMapper.getStudentById(1));
    System.out.println("增加了" + studentMapper.addStudent(buildStudent()) + "个学生");
    System.out.println(studentMapper.getStudentById(1));

    sqlSession.close();
  }

  /**
   *开启两个SqlSession，在sqlSession1中查询数据，使一级缓存生效，
   * 在sqlSession2中更新数据库，验证一级缓存只在数据库会话内部共享 。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testLocalCacheScope() throws Exception {
    initDb();
    SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

    StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
    StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);

    System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));
    System.out.println("=======================================================");
    System.out.println("studentMapper2更新了" + studentMapper2.updateStudentName("小岑", 1) + "个学生的数据");
    System.out.println("=======================================================");
    System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

  }


  private StudentEntity buildStudent() {
    StudentEntity studentEntity = new StudentEntity();
    studentEntity.setName("明明");
    studentEntity.setAge(20);
    return studentEntity;
  }

  /**测试二级缓存效果，不提交事务，sqlSession1查询完数据后，sqlSession2相同的查询是否会从缓存中获取数据。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testCacheWithoutCommitOrClose() throws Exception {
    SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

    StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
    StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);

    System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

  }

  /**测试二级缓存效果，当提交事务时，sqlSession1查询完数据后，sqlSession2相同的查询是否会从缓存中获取数据。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testCacheWithCommitOrClose() throws Exception {
    SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

    StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
    StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);

    System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
    sqlSession1.close();
    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

  }

  /**测试update操作是否会刷新该namespace下的二级缓存。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testCacheWithUpdate() throws Exception {
    SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession3 = factory.openSession(true); // 自动提交事务

    StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
    StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
    StudentMapper studentMapper3 = sqlSession3.getMapper(StudentMapper.class);

    System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
    sqlSession1.close();
    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

    studentMapper3.updateStudentName("方方", 1);
    sqlSession3.commit();
    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));
  }

  /**验证MyBatis的二级缓存不适应用于映射文件中存在多表查询的情况。
   通常我们会为每个单表创建单独的映射文件，由于MyBatis的二级缓存是基于namespace的，多表查询语句所在的namspace
   无法感应到其他namespace中的语句对多表查询中涉及的表进行的修改，引发脏数据问题。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testCacheWithDiffererntNamespace() throws Exception {
    SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession3 = factory.openSession(true); // 自动提交事务

    StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
    StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
    ClassMapper classMapper = sqlSession3.getMapper(ClassMapper.class);

    System.out.println("studentMapper读取数据: " + studentMapper.getStudentByIdWithClassInfo(1));
    sqlSession1.close();

    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));

    classMapper.updateClassName("特色一班", 1);
    sqlSession3.commit();

    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));
  }

  /**为了解决实验4的问题呢，可以使用Cache ref，让ClassMapper引用StudenMapper命名空间，
   * 这样两个映射文件对应的SQL操作都使用的是同一块缓存了。
   * <setting name="localCacheScope" value="SESSION"/>
   * <setting name="cacheEnabled" value="true"/>
   */
  @Test
  public void testCacheWithDiffererntNamespaceWithCacheRef() throws Exception {
    SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务
    SqlSession sqlSession3 = factory.openSession(true); // 自动提交事务

    StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
    StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
    ClassMapper classMapper = sqlSession3.getMapper(ClassMapper.class);

    System.out.println("studentMapper读取数据: " + studentMapper.getStudentByIdWithClassInfo(1));
    sqlSession1.close();

    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));

    classMapper.updateClassName("特色一班", 1);
    sqlSession3.commit();

    System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));
  }


}
