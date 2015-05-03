package spittr.data;

import spittr.Spittle;

import java.util.List;

/**
 * Created by shawnritchie on 18/04/15.
 */
public interface SpittleRepositoryCustom {
    List<Spittle> findSpittles(long max, int count);
}
