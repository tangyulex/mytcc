
### 一、操作步骤：
- 创建或使用一个现有的数据库，执行/dbscripts/mytcc_init.sql
- 在Spring容器中配置一个com.tyxxp.mytcc.config.TccConfig，以及beanName为tccDataSource的javax.sql.DataSource
- Spring容器中还需包含dubbo的ApplicationConfig、RegistryConfig等配置
- 在try方法上使用@Try，指定cancel和confirm阶段的方法名
- 在cancel方法上使用@Cancel
- 在confirm方法上使用@Confirm

### 二、其他说明
- 默认的remoteType是dubbo，扩展请实现IRemote接口，并在Try注解中设定
- 如果使用的是dubbo，那么配置必须在Spring容器中，包括ApplicationConfig、RegistryConfig等
- 如果CallTypeEnum为BY_PARENT（默认），框架会使每个节点的父节点来调用这些节点的cancel或confirm，发起节点由自己调用
- 如果CallTypeEnum为BY_SPONSOR，框架会使发起节点调用所有节点的cancel或confirm
- 目前仅支持mysql

