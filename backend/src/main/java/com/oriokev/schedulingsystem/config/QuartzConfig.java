package com.oriokev.schedulingsystem.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext context) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(autowiringJobFactory(context));
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        return factory;
    }

    private SpringBeanJobFactory autowiringJobFactory(ApplicationContext context) {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                Object job = super.createJobInstance(bundle);
                AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
                beanFactory.autowireBean(job);
                return job;
            }

            {
                setApplicationContext(context);
            }
        };
    }
}
