package com.predictiveprocess.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kerwin on 3/11/17.
 */
@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
