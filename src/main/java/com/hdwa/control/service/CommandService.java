package com.hdwa.control.service;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import java.util.Date;

public interface CommandService {

    String addCommand(Date startTime, String jobName, String jobGroupName, JobDataMap jobDataMap, String msg) throws SchedulerException;

    String deleteCommandAfterFlagDate(Date flagDate) throws SchedulerException;

}