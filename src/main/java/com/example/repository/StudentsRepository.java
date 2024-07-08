package com.example.repository;

import com.example.model.entities.Student;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.consts.ModelConstants.*;
import static com.example.consts.RepositoryCosntants.*;

public class StudentsRepository {
    private final Session session;
    public StudentsRepository (Session session) {
        this.session = session;
    }
    Optional<Student> getStudentById(Integer id) {
        String hql = "from Student where id = :id";
        Query<Student> query = session.createQuery(hql, Student.class);
        query.setParameter(FIELD_ID , id);
        return query.uniqueResultOptional();
    }

    void add(Student student) {
        session.persist(student);
    }

    Student update(Student student) {
        return session.merge(student);
    }

    boolean remove(Integer id) {
        Optional<Student> mayBeStudent = getStudentById(id);
        if (mayBeStudent.isEmpty()) return false;
        else {
            session.remove(mayBeStudent.get());
            session.flush();
            session.evict(mayBeStudent.get());
            return true;
        }
    }

    @SuppressWarnings(value = "unused")
    List<Student> getAll() {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Student.class);
        var student = criteria.from(Student.class);
        criteria.select(student);
        return session.createQuery(criteria).list();
    }
    @SuppressWarnings(value = "unused")
    List<Student> findAllBySurName(String surName) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> criteria = cb.createQuery(Student.class);
        Root<Student> student = criteria.from(Student.class);
        criteria.select(student).where(
                cb.equal(student.get("surName"),surName)
        );
        return session.createQuery(criteria).list();
    }
    @SuppressWarnings(value = "unused")
    List<Student> findLimitedStudentsOrderedByBirthDay(int limit) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> criteria = cb.createQuery(Student.class);
        Root<Student> studentRoot = criteria.from(Student.class);
        criteria.select(studentRoot).orderBy(cb.asc(studentRoot.get(FIELD_BIRTHDAY)));
        return session.createQuery(criteria).setMaxResults(limit).list();
    }



    List<Student> getStudents(Map<String, Object> parameterMap, int limit, int offset) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> criteria = cb.createQuery(Student.class);
        Root<Student> studentRoot = criteria.from(Student.class);
        List<Predicate> predicates = getPredicatesFromParameterMap(parameterMap, studentRoot, cb);
        criteria.select(studentRoot).where(predicates.toArray(Predicate[]::new));
        return session.createQuery(criteria).setFirstResult(offset).setMaxResults(limit).list();
    }

    Integer getStudentsCountByGroup(Integer groupId) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Student> root = cq.from(Student.class);
        cq.select(cb.count(root)).where(cb.equal(root.get(FIELD_GROUP).get(FIELD_ID), groupId));
        Long count = session.createQuery(cq).uniqueResult();
        return Math.toIntExact(count);
    }

    private List<Predicate> getPredicatesFromParameterMap(Map<String, Object> parameterMap, Root<Student> studentRoot, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (String key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);
            if(value==null) continue;
            switch (key) {
                case RQ_ID -> predicates.add(cb.equal(studentRoot.get(FIELD_ID), value));
                case RQ_FIRST_NAME ->predicates.add(cb.equal(studentRoot.get(FIELD_FIRSTNAME), value));
                case RQ_MIDDLE_NAME -> predicates.add(cb.equal(studentRoot.get(FIELD_MIDDLENAME), value));
                case RQ_SURNAME -> predicates.add(cb.equal(studentRoot.get(FIELD_SURNAME), value));
                case RQ_PHONE_NUMBER -> predicates.add(cb.equal(studentRoot.get(FIELD_PHONE), value));
                case RQ_BIRTHDAY -> predicates.add(cb.equal(studentRoot.get(FIELD_BIRTHDAY), value));
                case RQ_GROUP_ID -> predicates.add(cb.equal(studentRoot.get(FIELD_GROUP).get(FIELD_ID), value));
                case RQ_GROUP_NUMBER -> predicates.add(cb.equal(studentRoot.get(FIELD_GROUP).get(FIELD_GROUP_NUMBER), value));
            }
        }
        return predicates;
    }

}
