package com.dreampany.lca.data.source.api;

import com.dreampany.frame.data.source.DataSource;
import com.dreampany.lca.data.model.Ico;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by Hawladar Roman on 6/22/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
public interface IcoDataSource extends DataSource<Ico> {

    List<Ico> getLiveItems(int limit);

    Maybe<List<Ico>> getLiveItemsRx(int limit);

    List<Ico> getUpcomingItems(int limit);

    Maybe<List<Ico>> getUpcomingItemsRx(int limit);

    List<Ico> getFinishedItems(int limit);

    Maybe<List<Ico>> getFinishedItemsRx(int limit);
}
