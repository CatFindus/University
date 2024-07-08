package com.example.repository;

import com.example.model.entities.Subject;
import com.example.model.entities.Teacher;
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
import static com.example.consts.ModelConstants.RQ_BIRTHDAY;
import static com.example.consts.RepositoryCosntants.*;

public class TeachersRepository {
    private final Session session;

    public TeachersRepository(Session session) {
        this.session = session;
    }

    Optional<Teacher> getById(Integer id) {
        String hql = "FROM Teacher t WHERE id = :id";
        Query<Teacher> query = session.createQuery(hql, Teacher.class);
        query.setParameter(FIELD_ID, id);
        return query.uniqueResultOptional();
    }
    @SuppressWarnings("unused")
    List<Teacher> getAllTeachers() {
        String hql = "from Teacher";
        Query<Teacher> query = session.createQuery(hql, Teacher.class);
        return query.list();
    }

    public List<Teacher> getTeachers(Map<String, Object> parameterMap, int limit, int offset) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Teacher> criteria = cb.createQuery(Teacher.class);
        Root<Teacher> root = criteria.from(Teacher.class);
        List<Predicate> predicates = getPredicatesByParameterMap(parameterMap, root, cb);
        criteria.select(root).where(predicates.toArray(Predicate[]::new));
        return session.createQuery(criteria).setMaxResults(limit).setFirstResult(offset).list();
    }

    private List<Predicate> getPredicatesByParameterMap(Map<String, Object> parameterMap, Root<Teacher> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (String key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);
            switch (key) {
                case RQ_ID -> predicates.add(cb.equal(root.get(FIELD_ID), value));
                case RQ_FIRST_NAME ->predicates.add(cb.equal(root.get(FIELD_FIRSTNAME), value));
                case RQ_MIDDLE_NAME -> predicates.add(cb.equal(root.get(FIELD_MIDDLENAME), value));
                case RQ_SURNAME -> predicates.add(cb.equal(root.get(FIELD_SURNAME), value));
                case RQ_PHONE_NUMBER -> predicates.add(cb.equal(root.get(FIELD_PHONE), value));
                case RQ_BIRTHDAY -> predicates.add(cb.equal(root.get(FIELD_BIRTHDAY), value));
                case RQ_EXPERIENCE -> predicates.add(cb.equal(root.get(FIELD_EXPERIENCE), value));
                case RQ_SUBJECT -> predicates.add(cb.equal(root.get(FIELD_SUBJECTS).get(FIELD_REQUEST_NAME), value));
            }
        }
        return predicates;
    }

    void addTeacher(Teacher teacher) {
        session.persist(teacher);
    }

    boolean removeTeacher(Integer id)
    {
        Teacher teacher = session.get(Teacher.class, id);
        if (teacher==null) return false;
        session.remove(teacher);
        return true;
    }

    Teacher update(Teacher teacher) {
        return session.merge(teacher);
    }

    boolean addSubjectToTeacher(Subject subject, Integer teacherId) {
    Optional<Teacher> mayBeTeacher = getById(teacherId);
    if(mayBeTeacher.isEmpty() || subject==null) {
        return false;
    }
    Teacher teacher = mayBeTeacher.get();
    boolean added;
    if (teacher.getSubjects().contains(subject)) added = false;
    else {
        teacher.addSubject(subject);
        added = true;
    }
    return added;
    }

    boolean removeSubjectFromTeacher(Subject subject, Integer teacherId) {
        Optional<Teacher> mayBeTeacher = getById(teacherId);
        if(mayBeTeacher.isEmpty() || subject==null) {
            return false;
        }
        Teacher teacher = mayBeTeacher.get();
        boolean replaced;
        if (!teacher.getSubjects().contains(subject)) replaced = false;
        else {
            teacher.removeSubject(subject);
            replaced = true;
        }
        return replaced;
    }
}
