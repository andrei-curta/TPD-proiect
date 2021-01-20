package server.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.List;

public class BaseRepository<T> {
    private Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T get(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        return session.get(entityClass, id);
    }

    public List getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        return session.createQuery("from " + entityClass.getName()).list();
    }

    public T create(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(entity);
        transaction.commit();
        return entity;
    }

    public T update(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        T dbEntity = (T) session.merge(entity);
        transaction.commit();
        return dbEntity;
    }

    public void delete(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
    }

    public void deleteById(long entityId) {
        T entity = get(entityId);
        delete(entity);
    }
}
