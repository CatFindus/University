package com.example.repository;

import com.example.model.entities.Subject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.consts.ModelConstants.*;
import static com.example.consts.RepositoryCosntants.*;

public class SubjectsRepository {

    private final Session session;

    public SubjectsRepository(Session session) {
        this.session = session;
    }

    Optional<Subject> getById(Integer id) {
        String hql = "from Subject where id = :id";
        Query<Subject> query = session.createQuery(hql, Subject.class);
        query.setParameter(FIELD_ID, id);
        return query.uniqueResultOptional();
    }
    @SuppressWarnings("unused")
    List<Subject> getAll() {
        String hql = "from Subject";
        Query<Subject> query = session.createQuery(hql, Subject.class);
        return query.list();
    }

    boolean removeSubject(Integer id) {
        Optional<Subject> mayBeSubject = getById(id);
        if (mayBeSubject.isEmpty()) return false;
        else {
            session.remove(mayBeSubject.get());
            session.flush();
            session.evict(mayBeSubject.get());
            return true;
        }
    }

    Optional<Subject> getSubjectByRequestName(String requestName) {
        String hql = "FROM Subject WHERE requestName = :requestName";
        Query<Subject> query = session.createQuery(hql, Subject.class);
        query.setParameter(FIELD_REQUEST_NAME, requestName);
        return query.uniqueResultOptional();
    }

    public void add(Subject subject) {
        session.persist(subject);
    }

    public Subject updateSubject(Subject subject) {
        return session.merge(subject);
    }

    public List<Subject> getSubjects(Map<String, Object> requestMap, int limit, int offset) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Subject> cq = cb.createQuery(Subject.class);
        Root<Subject> subject = cq.from(Subject.class);
        List<Predicate> predicates = new ArrayList<>();
        for (String key : requestMap.keySet()) {
            Object value = requestMap.get(key);
            switch (key) {
                case RQ_NAME -> predicates.add(cb.equal(subject.get(RQ_NAME), value));
                case RQ_REQUEST_NAME -> predicates.add(cb.equal(subject.get(RQ_REQUEST_NAME), value));
                case RQ_ID -> predicates.add(cb.equal(subject.get(RQ_ID), value));
            }
        }
        cq.select(subject).where(predicates.toArray(Predicate[]::new));
        return session.createQuery(cq).setFirstResult(offset).setMaxResults(limit).list();
    }
}
