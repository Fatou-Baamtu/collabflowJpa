package com.collabflow.web.rest;

import static com.collabflow.domain.SubTaskAsserts.*;
import static com.collabflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.collabflow.IntegrationTest;
import com.collabflow.domain.SubTask;
import com.collabflow.domain.enumeration.Status;
import com.collabflow.repository.SubTaskRepository;
import com.collabflow.repository.UserRepository;
import com.collabflow.service.dto.SubTaskDTO;
import com.collabflow.service.mapper.SubTaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SubTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubTaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.TODO;
    private static final Status UPDATED_STATUS = Status.IN_PROGRESS;

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/sub-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubTaskRepository subTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubTaskMapper subTaskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubTaskMockMvc;

    private SubTask subTask;

    private SubTask insertedSubTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubTask createEntity() {
        return new SubTask().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).status(DEFAULT_STATUS).dueDate(DEFAULT_DUE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubTask createUpdatedEntity() {
        return new SubTask().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).dueDate(UPDATED_DUE_DATE);
    }

    @BeforeEach
    public void initTest() {
        subTask = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSubTask != null) {
            subTaskRepository.delete(insertedSubTask);
            insertedSubTask = null;
        }
    }

    @Test
    @Transactional
    void createSubTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);
        var returnedSubTaskDTO = om.readValue(
            restSubTaskMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubTaskDTO.class
        );

        // Validate the SubTask in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubTask = subTaskMapper.toEntity(returnedSubTaskDTO);
        assertSubTaskUpdatableFieldsEquals(returnedSubTask, getPersistedSubTask(returnedSubTask));

        insertedSubTask = returnedSubTask;
    }

    @Test
    @Transactional
    void createSubTaskWithExistingId() throws Exception {
        // Create the SubTask with an existing ID
        subTask.setId(1L);
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subTask.setTitle(null);

        // Create the SubTask, which fails.
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subTask.setStatus(null);

        // Create the SubTask, which fails.
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubTasks() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())));
    }

    @Test
    @Transactional
    void getSubTask() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.saveAndFlush(subTask);

        // Get the subTask
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, subTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subTask.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSubTask() throws Exception {
        // Get the subTask
        restSubTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubTask() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.saveAndFlush(subTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subTask
        SubTask updatedSubTask = subTaskRepository.findById(subTask.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubTask are not directly saved in db
        em.detach(updatedSubTask);
        updatedSubTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).dueDate(UPDATED_DUE_DATE);
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(updatedSubTask);

        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subTaskDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO))
            )
            .andExpect(status().isOk());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubTaskToMatchAllProperties(updatedSubTask);
    }

    @Test
    @Transactional
    void putNonExistingSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subTaskDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subTaskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubTaskWithPatch() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.saveAndFlush(subTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subTask using partial update
        SubTask partialUpdatedSubTask = new SubTask();
        partialUpdatedSubTask.setId(subTask.getId());

        partialUpdatedSubTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubTask))
            )
            .andExpect(status().isOk());

        // Validate the SubTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubTaskUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSubTask, subTask), getPersistedSubTask(subTask));
    }

    @Test
    @Transactional
    void fullUpdateSubTaskWithPatch() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.saveAndFlush(subTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subTask using partial update
        SubTask partialUpdatedSubTask = new SubTask();
        partialUpdatedSubTask.setId(subTask.getId());

        partialUpdatedSubTask.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).dueDate(UPDATED_DUE_DATE);

        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubTask))
            )
            .andExpect(status().isOk());

        // Validate the SubTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubTaskUpdatableFieldsEquals(partialUpdatedSubTask, getPersistedSubTask(partialUpdatedSubTask));
    }

    @Test
    @Transactional
    void patchNonExistingSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subTaskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subTask.setId(longCount.incrementAndGet());

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(subTaskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubTask() throws Exception {
        // Initialize the database
        insertedSubTask = subTaskRepository.saveAndFlush(subTask);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subTask
        restSubTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, subTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subTaskRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected SubTask getPersistedSubTask(SubTask subTask) {
        return subTaskRepository.findById(subTask.getId()).orElseThrow();
    }

    protected void assertPersistedSubTaskToMatchAllProperties(SubTask expectedSubTask) {
        assertSubTaskAllPropertiesEquals(expectedSubTask, getPersistedSubTask(expectedSubTask));
    }

    protected void assertPersistedSubTaskToMatchUpdatableProperties(SubTask expectedSubTask) {
        assertSubTaskAllUpdatablePropertiesEquals(expectedSubTask, getPersistedSubTask(expectedSubTask));
    }
}
