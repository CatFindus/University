package com.example.repository;

import com.example.model.entities.Group;
import com.example.model.entities.Student;
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

import static com.example.consts.RepositoryCosntants.*;

public class GroupsRepository {
    private final Session session;

    public GroupsRepository(Session session) {
        this.session = session;
    }

    Optional<Group> getGroupById(int id) {
        String hql = "from Group where id = :id";
        Query<Group> query = session.createQuery(hql, Group.class);
        query.setParameter(FIELD_ID, id);
        return query.uniqueResultOptional();
    }

    List<Group> getGroups(Map<String, Object> parameterMap, int limit, int offset) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Group> cq = cb.createQuery(Group.class);
        Root<Group> group = cq.from(Group.class);
        List<Predicate> predicates = getPredicatesFromParameterMap(parameterMap, cb, group);
        cq.select(group).where(predicates.toArray(Predicate[]::new));
        return session.createQuery(cq).setFirstResult(offset).setMaxResults(limit).list();
    }

    private List<Predicate> getPredicatesFromParameterMap(Map<String, Object> parameterMap, CriteriaBuilder cb, Root<Group> group) {
        List<Predicate> predicates = new ArrayList<>();
        for (String key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);

            switch (key) {
                case FIELD_ID -> predicates.add(cb.equal(group.get(FIELD_ID), value));
                case FIELD_GROUP_NUMBER -> predicates.add(cb.equal(group.get(FIELD_GROUP_NUMBER), value));
            }
        }
        return predicates;
    }

    void add(Group group) {
        session.persist(group);
    }

    boolean remove(Integer id) {
        Optional<Group> mayBeGroup = getGroupById(id);
        if (mayBeGroup.isPresent()) {
            session.remove(mayBeGroup.get());
            return true;
        } else {
            return false;
        }
    }

    Group update(Group group) {
        return session.merge(group);
    }

    boolean removeStudentFromGroup(Student student, Integer groupId) {
        Optional<Group> mayBeGroup = getGroupById(groupId);
        if (mayBeGroup.isPresent()) {
            Group group = mayBeGroup.get();
            if (group.getStudents().contains(student)) {
                group.getStudents().remove(student);
                student.setGroup(null);
                return true;
            }
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    boolean addStudentToGroup(Student student, Integer groupId) {
        Optional<Group> mayBeGroup = getGroupById(groupId);
        if (mayBeGroup.isPresent()) {
            Group group = mayBeGroup.get();
            boolean added = group.addStudent(student);
            session.merge(group);
            return added;
        } else {
            return false;
        }
    }
}
