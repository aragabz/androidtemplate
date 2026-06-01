package com.aragabz.androidtemplate.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_entities")
data class SampleEntity(
    @PrimaryKey
    val id: String,
    val data: String,
)
