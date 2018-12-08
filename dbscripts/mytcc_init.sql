
-- tcc执行记录
drop table if exists my_tcc_execution;
create table my_tcc_execution (
  execution_id bigint unsigned not null auto_increment comment '主键id',
  tcc_id varchar(64) default null comment 'tcc事务id',
  app_name varchar(64) default null comment 'app名',
  parent_app_name varchar(64) default null comment 'tcc调用链路上父app名',
  interface_name varchar(256) not null comment '接口名',
  try_address varchar(256) not null comment 'try地址',
  cancel_address varchar(1024) not null comment 'cancel地址',
  confirm_address varchar(1024) not null comment 'confirm地址',
  args varbinary(8000) default null comment '方法参数',
  status tinyint unsigned default null comment '状态（0：try，1：tried，2：cancel，3：canceled，4：confirm，5：confirmed）',
  remote_type varchar(64) default null comment '远程调用方式',
  create_time timestamp not null default current_timestamp comment '创建时间',
  update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间',
  version bigint unsigned default '0' comment '版本号',
  is_deleted tinyint unsigned default '0' comment '是否删除（0：未删除，1：已删除）',
  primary key (execution_id)
) engine=innodb default charset=utf8;

-- job任务表
drop table if exists my_tcc_job_execution;
create table my_tcc_job_execution (
  job_execution_id bigint unsigned not null auto_increment comment '主键id',
  task_name varchar(64) not null comment '任务名',
  app_name varchar(64) not null comment '执行job应用名',
  ip varchar(64) default null comment '执行job的应用ip地址',
  status tinyint unsigned default 0 comment '状态（0：初始化，1：执行中，2：成功，3：失败）',
  switch_status tinyint unsigned default 0 comment '开关（0：关闭，1：开启）',
  create_time timestamp not null default current_timestamp comment '创建时间',
  update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间',
  version bigint unsigned default '0' comment '版本号',
  message varchar(2048) default null comment '附加信息',
  primary key (job_execution_id),
  unique key uk_task_name_app_name(task_name, app_name)
) engine=innodb default charset=utf8;
