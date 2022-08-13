package fyi.incomeoutcome.salarytaxspend.repository.source;

import fyi.incomeoutcome.salarytaxspend.data.source.SpendSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpendSourceRepository extends CrudRepository<SpendSource, Long> {
    SpendSource findById(long id);
}
