package com.likelionsns.final_project.utils;

import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class FilterManager {

    private final EntityManager entityManager;

    /**
     * 지정된 필터를 활성화하고 파라미터를 설정합니다.
     *
     * @param filterName  활성화할 필터의 이름
     * @param paramName   설정할 파라미터의 이름
     * @param paramValue  설정할 파라미터의 값
     */
    public void enableFilter(String filterName, String paramName, Object paramValue) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(filterName);
        filter.setParameter(paramName, paramValue);
    }

    /**
     * 지정된 필터를 비활성화합니다.
     *
     * @param filterName  비활성화할 필터의 이름
     */
    public void disableFilter(String filterName) {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter(filterName);
    }
}