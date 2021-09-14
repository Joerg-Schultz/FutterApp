package de.tierwohlteam.android.futterapp.models

/**
 * The content of the fridge (only packs)
 * object, as there is only one fridge per app
 */
object Fridge {
    // Meat, gram, amount
    private val content: MutableMap<String, MutableMap<Int, Int>> = mutableMapOf()

    /**
     * add one or more packs to the fridge
     * @param[pack] What is added?
     * @param[amount] How much is added (default 1)
     */
    fun addPack(pack: Pack, amount: Int = 1) {
        val current = content.getOrDefault(pack.food.type, mutableMapOf(pack.size to 0)).getOrDefault(pack.size, 0)
        content[pack.food.type] = mutableMapOf<Int,Int>(pack.size to current + amount)
    }

    /**
     * get a pack from the fridge
     * @param[pack]
     */
    fun retrievePack(pack: Pack) {
        val current = content.getOrDefault(pack.food.type, mutableMapOf(pack.size to 0)).getOrDefault(pack.size, 0)
        content[pack.food.type] = mutableMapOf<Int,Int>(pack.size to if (current - 1 < 0) 0 else current - 1)
    }

    /**
     * get the content of the fridge
     * @returns List of Pair Pack and amount
     */
    fun content(): List<Pair<Pack,Int>> {
        val result = mutableListOf<Pair<Pack,Int>>()
        for ((meat, sizes) in content) {
            for ((size,amount) in sizes) {
                if (amount > 0)
                    result.add(Pair(Pack(meat,size), amount))
            }
        }
        return result
    }
}