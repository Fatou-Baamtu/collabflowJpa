package com.collabflow.service.impl;

import com.collabflow.domain.SubTask;
import com.collabflow.repository.SubTaskRepository;
import com.collabflow.service.SubTaskService;
import com.collabflow.service.dto.SubTaskDTO;
import com.collabflow.service.mapper.SubTaskMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.collabflow.domain.SubTask}.
 */
@Service
@Transactional
public class SubTaskServiceImpl implements SubTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(SubTaskServiceImpl.class);

    private final SubTaskRepository subTaskRepository;

    private final SubTaskMapper subTaskMapper;

    public SubTaskServiceImpl(SubTaskRepository subTaskRepository, SubTaskMapper subTaskMapper) {
        this.subTaskRepository = subTaskRepository;
        this.subTaskMapper = subTaskMapper;
    }

    @Override
    public SubTaskDTO save(SubTaskDTO subTaskDTO) {
        LOG.debug("Request to save SubTask : {}", subTaskDTO);
        SubTask subTask = subTaskMapper.toEntity(subTaskDTO);
        subTask = subTaskRepository.save(subTask);
        return subTaskMapper.toDto(subTask);
    }

    @Override
    public SubTaskDTO update(SubTaskDTO subTaskDTO) {
        LOG.debug("Request to update SubTask : {}", subTaskDTO);
        SubTask subTask = subTaskMapper.toEntity(subTaskDTO);
        subTask = subTaskRepository.save(subTask);
        return subTaskMapper.toDto(subTask);
    }

    @Override
    public Optional<SubTaskDTO> partialUpdate(SubTaskDTO subTaskDTO) {
        LOG.debug("Request to partially update SubTask : {}", subTaskDTO);

        return subTaskRepository
            .findById(subTaskDTO.getId())
            .map(existingSubTask -> {
                subTaskMapper.partialUpdate(existingSubTask, subTaskDTO);

                return existingSubTask;
            })
            .map(subTaskRepository::save)
            .map(subTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubTaskDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SubTasks");
        return subTaskRepository.findAll(pageable).map(subTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubTaskDTO> findOne(Long id) {
        LOG.debug("Request to get SubTask : {}", id);
        return subTaskRepository.findById(id).map(subTaskMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SubTask : {}", id);
        subTaskRepository.deleteById(id);
    }
}
