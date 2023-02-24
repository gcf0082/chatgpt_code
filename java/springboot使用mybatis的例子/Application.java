@SpringBootApplication

@MapperScan("com.example.mapper")

public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

    }

    @Bean

    @ConfigurationProperties(prefix = "spring.datasource")

    public DataSource dataSource() {

        return DataSourceBuilder.create().build();

    }

    @Bean

    public SqlSessionFactory sqlSessionFactory() throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSource());

        // 配置 MyBatis XML 映射文件

        Resource[] resources = new PathMatchingResourcePatternResolver()

                .getResources("classpath:mapper/*.xml");

        factoryBean.setMapperLocations(resources);

        return factoryBean.getObject();

    }

    @Bean

    public SqlSessionTemplate sqlSessionTemplate() throws Exception {

        return new SqlSessionTemplate(sqlSessionFactory());

    }

}
