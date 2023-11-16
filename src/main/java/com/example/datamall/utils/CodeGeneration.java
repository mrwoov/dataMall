package com.example.datamall.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

public class CodeGeneration {

    public static void main(String[] args) {
        Generation("user_order_goods");
    }

    /**
     * 根据表名生成相应结构代码
     *
     * @param tableName 表名
     */
    public static void Generation(String... tableName) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/data-mall?&useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai", "root", "5123580rt")
                .globalConfig(builder -> builder.author("woov")
                        //启用swagger
                        //.enableSwagger()
                        //指定输出目录
                        .outputDir("F:\\graduation\\springboot\\data-mall\\src\\main\\java")
                        .disableOpenDir())
                .packageConfig(builder -> builder.entity("entity")//实体类包名
                        .parent("com.example.datamall")//父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
                        .controller("controller")//控制层包名
                        .mapper("mapper")//mapper层包名
                        //.other("dto")//生成dto目录 可不用
                        .service("service")//service层包名
                        .serviceImpl("service.impl")//service实现类包名
                        //自定义mapper.xml文件输出目录
                        .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + "/src/main/resources/mapper")))

                .templateEngine(new VelocityTemplateEngine())
                .strategyConfig(builder -> {
                    //设置要生成的表名
                    builder.addInclude(tableName)
                            .addTablePrefix("sys_")//设置表前缀过滤
                            .entityBuilder()
                            .enableLombok()
                            .enableChainModel()
                            .naming(NamingStrategy.underline_to_camel)//数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                            .columnNaming(NamingStrategy.underline_to_camel)//表字段映射实体属性命名规则：默认null，不指定按照naming执行
                            .idType(IdType.AUTO)//添加全局主键类型
                            .formatFileName("%s")//格式化实体名称，%s取消首字母I,
                            .mapperBuilder()
                            .enableMapperAnnotation()//开启mapper注解
                            .enableBaseResultMap()//启用xml文件中的BaseResultMap 生成
                            .enableBaseColumnList()//启用xml文件中的BaseColumnList
                            .formatMapperFileName("%sMapper")//格式化Dao类名称
                            .formatXmlFileName("%sMapper")//格式化xml文件名称
                            .serviceBuilder()
                            .formatServiceFileName("%sService")//格式化 service 接口文件名称
                            .formatServiceImplFileName("%sServiceImpl")//格式化 service 接口文件名称
                            .controllerBuilder()
                            .enableRestStyle();
                })
                .execute();
    }
}