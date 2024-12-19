package com.collabflow.domain;

import com.collabflow.domain.enumeration.Status;
import com.collabflow.service.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@EntityListeners(AuditListener.class)
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "assignee", "task" }, allowSetters = true)
    private Set<SubTask> subTasks = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "task" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Task title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public Task status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Task dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    public Task isCompleted(Boolean isCompleted) {
        this.setIsCompleted(isCompleted);
        return this;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Set<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(Set<SubTask> subTasks) {
        if (this.subTasks != null) {
            this.subTasks.forEach(i -> i.setTask(null));
        }
        if (subTasks != null) {
            subTasks.forEach(i -> i.setTask(this));
        }
        this.subTasks = subTasks;
    }

    public Task subTasks(Set<SubTask> subTasks) {
        this.setSubTasks(subTasks);
        return this;
    }

    public Task addSubTasks(SubTask subTask) {
        this.subTasks.add(subTask);
        subTask.setTask(this);
        return this;
    }

    public Task removeSubTasks(SubTask subTask) {
        this.subTasks.remove(subTask);
        subTask.setTask(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setTask(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setTask(this));
        }
        this.comments = comments;
    }

    public Task comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Task addComments(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
        return this;
    }

    public Task removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setTask(null);
        return this;
    }

    public User getAssignee() {
        return this.assignee;
    }

    public void setAssignee(User user) {
        this.assignee = user;
    }

    public Task assignee(User user) {
        this.setAssignee(user);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return getId() != null && getId().equals(((Task) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", isCompleted='" + getIsCompleted() + "'" +
            "}";
    }
}
