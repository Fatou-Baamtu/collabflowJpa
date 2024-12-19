package com.collabflow.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class SubTaskAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSubTaskAllPropertiesEquals(SubTask expected, SubTask actual) {
        assertSubTaskAutoGeneratedPropertiesEquals(expected, actual);
        assertSubTaskAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSubTaskAllUpdatablePropertiesEquals(SubTask expected, SubTask actual) {
        assertSubTaskUpdatableFieldsEquals(expected, actual);
        assertSubTaskUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSubTaskAutoGeneratedPropertiesEquals(SubTask expected, SubTask actual) {
        assertThat(expected)
            .as("Verify SubTask auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSubTaskUpdatableFieldsEquals(SubTask expected, SubTask actual) {
        assertThat(expected)
            .as("Verify SubTask relevant properties")
            .satisfies(e -> assertThat(e.getTitle()).as("check title").isEqualTo(actual.getTitle()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()))
            .satisfies(e -> assertThat(e.getDueDate()).as("check dueDate").isEqualTo(actual.getDueDate()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertSubTaskUpdatableRelationshipsEquals(SubTask expected, SubTask actual) {
        assertThat(expected)
            .as("Verify SubTask relationships")
            .satisfies(e -> assertThat(e.getTask()).as("check task").isEqualTo(actual.getTask()));
    }
}
