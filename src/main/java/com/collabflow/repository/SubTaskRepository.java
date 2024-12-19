package com.collabflow.repository;

import com.collabflow.domain.SubTask;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SubTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    @Query("select subTask from SubTask subTask where subTask.assignee.login = ?#{authentication.name}")
    List<SubTask> findByAssigneeIsCurrentUser();
}
