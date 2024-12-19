package com.collabflow.service;

import com.collabflow.service.dto.SubTaskDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.collabflow.domain.SubTask}.
 */
public interface SubTaskService {
    /**
     * Save a subTask.
     *
     * @param subTaskDTO the entity to save.
     * @return the persisted entity.
     */
    SubTaskDTO save(SubTaskDTO subTaskDTO);

    /**
     * Updates a subTask.
     *
     * @param subTaskDTO the entity to update.
     * @return the persisted entity.
     */
    SubTaskDTO update(SubTaskDTO subTaskDTO);

    /**
     * Partially updates a subTask.
     *
     * @param subTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubTaskDTO> partialUpdate(SubTaskDTO subTaskDTO);

    /**
     * Get all the subTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubTaskDTO> findAll(Pageable pageable);

    /**
     * Get the "id" subTask.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubTaskDTO> findOne(Long id);

    /**
     * Delete the "id" subTask.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
