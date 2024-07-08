package com.example.repository;

import com.example.model.entities.ScheduleUnit;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.consts.ModelConstants.*;
import static com.example.consts.RepositoryCosntants.*;

public class SchedulesRepository {
    private final Session session;

    public SchedulesRepository(Session session) {
        this.session = session;
    }

    List<ScheduleUnit> getSchedules(Map<String,Object> parameterMap, int limit, int offset) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ScheduleUnit> cq = cb.createQuery(ScheduleUnit.class);
        Root<ScheduleUnit> unit = cq.from(ScheduleUnit.class);
        List<Predicate> predicates = getPredicatesByParameterMap(parameterMap, cb, unit);
        cq.select(unit).where(predicates.toArray(Predicate[]::new));
        return session.createQuery(cq).setFirstResult(offset).setMaxResults(limit).list();
    }

    private List<Predicate> getPredicatesByParameterMap(Map<String, Object> parameterMap, CriteriaBuilder cb, Root<ScheduleUnit> unit) {
        List<Predicate> predicates = new ArrayList<>();
        for (String key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);

            switch (key) {
                case RQ_ID -> predicates.add(cb.equal(unit.get(FIELD_ID), value));
                case RQ_SUBJECT -> predicates.add(cb.equal(unit.get(FIELD_SUBJECT).get(FIELD_REQUEST_NAME), value));
                case RQ_TEACHER_ID -> predicates.add(cb.equal(unit.get(FIELD_TEACHER).get(FIELD_ID), value));
                case RQ_GROUP_ID -> predicates.add(cb.equal(unit.get(FIELD_GROUP).get(FIELD_ID), value));
                case RQ_GROUP_NUMBER -> predicates.add(cb.equal(unit.get(FIELD_GROUP).get(FIELD_GROUP_NUMBER), value));
                case RQ_BEGIN_DATE_TIME -> predicates.add(cb.greaterThanOrEqualTo(unit.get(FIELD_BEGIN), (LocalDateTime) value));
                case RQ_END_DATE_TIME -> predicates.add(cb.lessThanOrEqualTo(unit.get(FIELD_END), (LocalDateTime) value));
            }
        }
        return predicates;
    }

    List<ScheduleUnit> getSchedules(LocalDateTime begin, LocalDateTime end) {
        String hql = """
                    FROM ScheduleUnit WHERE
                    ScheduleUnit.begin between :begin1 and :end1
                    and ScheduleUnit.end between :begin2 and :end2
                    """;
        Query<ScheduleUnit> query = session.createQuery(hql, ScheduleUnit.class);
        query.setParameter("begin1", begin);
        query.setParameter("begin2", begin);
        query.setParameter("end1", end);
        query.setParameter("end2", end);
        return query.list();
    }
    void addSchedule(ScheduleUnit unit) {
        Transaction transaction = session.beginTransaction();
        session.persist(unit);
        transaction.commit();
    }

    boolean deleteSchedule(Long id) {
        Transaction transaction = session.beginTransaction();
        Optional<ScheduleUnit> mayBeUnit =getById(id);
        if (mayBeUnit.isPresent())
        {
            session.remove(mayBeUnit.get());
            transaction.commit();
            return true;
        } else {
            transaction.rollback();
            return false;
        }
    }

    Optional<ScheduleUnit> getById(Long id) {
        String hql = "FROM ScheduleUnit WHERE id = :id";
        Query<ScheduleUnit> query = session.createQuery(hql, ScheduleUnit.class);
        query.setParameter(FIELD_ID, id);
        return query.uniqueResultOptional();
    }

    List<ScheduleUnit> getAll() {
        String hql = "FROM ScheduleUnit";
        Query<ScheduleUnit> query = session.createQuery(hql, ScheduleUnit.class);
        return query.list();
    }

    ScheduleUnit update(ScheduleUnit unit) {
        return session.merge(unit);
    }

    Integer getSchedulesCountByGroupPerDay(Integer groupId, LocalDate date) {
        LocalDateTime begin = date.atStartOfDay();
        LocalDateTime end = begin.plusDays(1).minusSeconds(1);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ScheduleUnit> root = cq.from(ScheduleUnit.class);
        cq.select(cb.count(root)).where(
                cb.between(root.get(FIELD_BEGIN), begin, end),
                cb.between(root.get(FIELD_END), begin, end),
                cb.equal(root.get(FIELD_GROUP).get(FIELD_ID),groupId));
        Long count = session.createQuery(cq).getSingleResult();
        return Math.toIntExact(count);
    }

}
