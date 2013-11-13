package com.mapabc.booking.listener;

import com.mapabc.booking.util.PropertiesUtil;
import com.mapabc.mas.db.HibernateSessionFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created with ChengLi
 * User: ChengLi
 * Date: 13-1-30
 * Time: 涓婂崍11:29
 * To change this template use File | Settings | File Templates.
 */
public class InitEmapListener implements ServletContextListener {
    //@Override
    public void contextInitialized(ServletContextEvent sce) {
        HibernateSessionFactory.getSession();
        PropertiesUtil.initProperties();
    }

    // @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
