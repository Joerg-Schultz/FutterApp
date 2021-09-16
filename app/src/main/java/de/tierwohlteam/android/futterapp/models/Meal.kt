package de.tierwohlteam.android.futterapp.models

import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Meal(
    @Embedded
    val feeding: Feeding = Feeding(),
    @Relation(
        parentColumn = "id",
        entityColumn = "inFeeding"
    )
    val ingredients: MutableList<Ingredient> = mutableListOf()
) {
    fun addIngredient(food: Food, gram: Int) {
        ingredients.add(
            Ingredient(foodName = food.name, gram = gram, inFeeding = feeding.id))
    }
}

@Entity(
    tableName = "feeding"
)
data class Feeding(
    val time: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    @PrimaryKey
    val id: Uuid = uuid4()
)

/**
 * Ingredient of food
 * @property[food] Food
 * @property[gram] Int
 */
@Entity(
    tableName = "ingredient",
    foreignKeys = [
        ForeignKey(
            entity = Feeding::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("inFeeding")
        ),
    ForeignKey(
        entity = Food::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("foodName")
    )
    ]
)
data class Ingredient(
    val foodName: String,
    val gram: Int,
    @PrimaryKey
    val id: Uuid = uuid4(),
    val inFeeding: Uuid
    )