package com.sd.laborator

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.repository.PageableRepository
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@JdbcRepository
interface Repository : PageableRepository<Entity, String> {
    @Query("""
        INSERT INTO button_counter (id_button, counter) VALUES (:id, :counter)
    """, nativeQuery = true)
    fun save(@NonNull @NotNull @Id id: String, @NonNull @NotBlank counter: Int)

    @Query("""
        UPDATE button_counter e SET e.counter = :counter WHERE e.id_button = :id
    """)
    fun update(@NonNull @NotNull @Id id: String, @NonNull @NotBlank counter: Int)

    @Query("""
        SELECT * FROM button_counter e where e.id_button = :id limit 1
    """)
    fun findOne(@NonNull @NotNull @Id id: String) : Optional<Entity>
    //fun findAll(): List<Entity>
}