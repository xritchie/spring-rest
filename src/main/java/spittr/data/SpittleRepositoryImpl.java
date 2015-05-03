package spittr.data;

import spittr.Spittle;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by shawnritchie on 18/04/15.
 */
public class SpittleRepositoryImpl implements SpittleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Spittle> findSpittles(long max, int count) {
        return (List<Spittle>) entityManager.createQuery("select s from Spittle s where s.id<:maxId order by s.postedTime desc")
                .setParameter("maxId", max)
                .setMaxResults(count)
                .getResultList();
    }
}
