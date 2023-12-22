package com.hdwa.control.config;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * 报警定时任务配置
 */
@Configuration
@EnableScheduling
public class QuartzConfigurationControl {
    /**
     * 配置任务工厂实例
     */
    @Bean
    public JobFactory jobFactory2(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean(name = "quartzScheduler-control")
    public SchedulerFactoryBean schedulerFactoryBean2(DataSource dataSource, JobFactory jobFactory2,
                                                      Properties quartzProperties2) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // 将spring管理job自定义工厂交由调度器维护
        factory.setJobFactory(jobFactory2);
        factory.setSchedulerName("quartzScheduler-control");
        factory.setDataSource(dataSource);
        // 设置覆盖已存在的任务
        factory.setOverwriteExistingJobs(true);
        // 项目启动完成后，等待2秒后开始执行调度器初始化
        factory.setStartupDelay(20);
        // 设置调度器自动运行
        factory.setAutoStartup(true);
        // factory.setConfigLocation(new ClassPathResource("/quartz-control.properties"));
        factory.setQuartzProperties(quartzProperties2);

        return factory;
    }

    @Bean
    public Properties quartzProperties2() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz-control.properties"));
        // 在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();

        return propertiesFactoryBean.getObject();
    }

    /**
     * 继承org.springframework.scheduling.quartz.SpringBeanJobFactory 实现任务实例化方式
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        private transient AutowireCapableBeanFactory beanFactoryControl;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactoryControl = context.getAutowireCapableBeanFactory();
        }

        /**
         * 将job实例交给spring ioc托管 我们在job实例实现类内可以直接使用spring注入的调用被spring ioc管理的实例
         */
        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            /*
              将job实例交付给spring ioc
             */
            beanFactoryControl.autowireBean(job);
            return job;
        }
    }
}