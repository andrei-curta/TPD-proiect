package server.repository;

import entities.FileEntity;
import entities.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.type.LongType;
import util.HibernateUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepository extends BaseRepository<UserEntity> {

    public UserRepository() {
        super(UserEntity.class);
    }



    public UserEntity getUserByUsername(String username) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            String sql = "select id from user where username = (?1)";
            Query query = session.createNativeQuery(sql).setParameter(1, username).addScalar("id", LongType.INSTANCE);
            List<Long> res = query.list();
            session.close();

            if (res.size() > 0) {
                Long id = res.get(0).longValue();
                UserEntity user = get(id);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
