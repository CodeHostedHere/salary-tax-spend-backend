package fyi.incomeoutcome.salarytaxspend.spendsource;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpendSourceRepository extends CrudRepository<SpendSource, Long> {
    SpendSource findById(long id);
}
