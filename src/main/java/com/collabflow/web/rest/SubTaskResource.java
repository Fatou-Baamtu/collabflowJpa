package com.collabflow.web.rest;

import com.collabflow.repository.SubTaskRepository;
import com.collabflow.service.SubTaskService;
import com.collabflow.service.dto.SubTaskDTO;
import com.collabflow.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.collabflow.domain.SubTask}.
 */
@RestController
@RequestMapping("/api/sub-tasks")
public class SubTaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubTaskResource.class);

    private static final String ENTITY_NAME = "subTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubTaskService subTaskService;

    private final SubTaskRepository subTaskRepository;

    public SubTaskResource(SubTaskService subTaskService, SubTaskRepository subTaskRepository) {
        this.subTaskService = subTaskService;
        this.subTaskRepository = subTaskRepository;
    }

    /**
     * {@code POST  /sub-tasks} : Create a new subTask.
     *
     * @param subTaskDTO the subTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subTaskDTO, or with status {@code 400 (Bad Request)} if the subTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubTaskDTO> createSubTask(@Valid @RequestBody SubTaskDTO subTaskDTO) throws URISyntaxException {
        LOG.debug("REST request to save SubTask : {}", subTaskDTO);
        if (subTaskDTO.getId() != null) {
            throw new BadRequestAlertException("A new subTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        subTaskDTO = subTaskService.save(subTaskDTO);
        return ResponseEntity.created(new URI("/api/sub-tasks/" + subTaskDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, subTaskDTO.getId().toString()))
            .body(subTaskDTO);
    }

    /**
     * {@code PUT  /sub-tasks/:id} : Updates an existing subTask.
     *
     * @param id the id of the subTaskDTO to save.
     * @param subTaskDTO the subTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subTaskDTO,
     * or with status {@code 400 (Bad Request)} if the subTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubTaskDTO> updateSubTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubTaskDTO subTaskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SubTask : {}, {}", id, subTaskDTO);
        if (subTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        subTaskDTO = subTaskService.update(subTaskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subTaskDTO.getId().toString()))
            .body(subTaskDTO);
    }

    /**
     * {@code PATCH  /sub-tasks/:id} : Partial updates given fields of an existing subTask, field will ignore if it is null
     *
     * @param id the id of the subTaskDTO to save.
     * @param subTaskDTO the subTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subTaskDTO,
     * or with status {@code 400 (Bad Request)} if the subTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubTaskDTO> partialUpdateSubTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubTaskDTO subTaskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SubTask partially : {}, {}", id, subTaskDTO);
        if (subTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubTaskDTO> result = subTaskService.partialUpdate(subTaskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subTaskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sub-tasks} : get all the subTasks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SubTaskDTO>> getAllSubTasks(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of SubTasks");
        Page<SubTaskDTO> page = subTaskService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sub-tasks/:id} : get the "id" subTask.
     *
     * @param id the id of the subTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubTaskDTO> getSubTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SubTask : {}", id);
        Optional<SubTaskDTO> subTaskDTO = subTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subTaskDTO);
    }

    /**
     * {@code DELETE  /sub-tasks/:id} : delete the "id" subTask.
     *
     * @param id the id of the subTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SubTask : {}", id);
        subTaskService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
