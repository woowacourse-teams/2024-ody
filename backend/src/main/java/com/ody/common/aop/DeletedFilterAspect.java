package com.ody.common.aop;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DeletedFilterAspect {

    @Autowired
    private EntityManager entityManager;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceClass() {
    }

    @Pointcut("@annotation(DisabledDeletedFilter)")
    public void methodWithDisabledFilter() {
    }

    @Before("serviceClass() && !methodWithDisabledFilter()")
    public void enableFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("deletedMemberFilter");
        session.enableFilter("deletedMateFilter");
    }

    @Before("methodWithDisabledFilter()")
    @After("serviceClass()")
    public void disableFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("deletedMemberFilter");
        session.disableFilter("deletedMateFilter");
    }
}
